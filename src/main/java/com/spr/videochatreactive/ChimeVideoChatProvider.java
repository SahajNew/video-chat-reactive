package com.spr.videochat.services.impl;

import static com.spr.props.dynamic.properties.CareDynamicProperty.ENABLE_VIDEO_CHAT_IAM_ROLE_BASED;
import static com.spr.utils.core.UserContextProvider.getCurrentPartnerId;
import static com.spr.videochat.util.VideoChatConstants.AWS_REGION;
import static com.spr.videochat.util.VideoChatConstants.CHIME_ARN;
import static com.spr.videochat.util.VideoChatConstants.PARTNER_TAG;
import static com.spr.videochat.util.VideoChatConstants.RECORDING_ARTIFACTS_BUCKET;
import static com.spr.videochat.util.VideoChatConstants.ROLE_ARN;
import static com.spr.videochat.util.VideoChatConstants.TOKEN_RESOURCE;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.chime.AmazonChime;
import com.amazonaws.services.chime.AmazonChimeClient;
import com.amazonaws.services.chime.AmazonChimeClientBuilder;
import com.amazonaws.services.chime.model.CreateAttendeeRequest;
import com.amazonaws.services.chime.model.CreateAttendeeResult;
import com.amazonaws.services.chime.model.CreateMediaCapturePipelineRequest;
import com.amazonaws.services.chime.model.CreateMediaCapturePipelineResult;
import com.amazonaws.services.chime.model.CreateMeetingRequest;
import com.amazonaws.services.chime.model.CreateMeetingResult;
import com.amazonaws.services.chime.model.DeleteAttendeeRequest;
import com.amazonaws.services.chime.model.DeleteAttendeeResult;
import com.amazonaws.services.chime.model.DeleteMediaCapturePipelineRequest;
import com.amazonaws.services.chime.model.DeleteMeetingRequest;
import com.amazonaws.services.chime.model.DeleteMeetingResult;
import com.amazonaws.services.chime.model.EngineTranscribeSettings;
import com.amazonaws.services.chime.model.GetMeetingRequest;
import com.amazonaws.services.chime.model.MediaPipelineSinkType;
import com.amazonaws.services.chime.model.MediaPipelineSourceType;
import com.amazonaws.services.chime.model.NotFoundException;
import com.amazonaws.services.chime.model.StartMeetingTranscriptionRequest;
import com.amazonaws.services.chime.model.StartMeetingTranscriptionResult;
import com.amazonaws.services.chime.model.Tag;
import com.amazonaws.services.chime.model.TranscribeLanguageCode;
import com.amazonaws.services.chime.model.TranscriptionConfiguration;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.spr.audience.process.ProcessTask;
import com.spr.audience.process.ProcessTaskType;
import com.spr.audience.process.SkipError;
import com.spr.beans.Account;
import com.spr.chat.auth.ChatContext;
import com.spr.core.config.partner.PartnerLevelConfigService;
import com.spr.core.content.util.SprMimeType;
import com.spr.core.logger.Logger;
import com.spr.core.logger.LoggerFactory;
import com.spr.enums.AccountType;
import com.spr.exceptions.UserVisibleException;
import com.spr.pipeline.beans.Status;
import com.spr.props.SprProperties;
import com.spr.props.dynamic.DynamicPropertyUtils;
import com.spr.utils.ExceptionUtils;
import com.spr.utils.SprinklrCollectionUtils;
import com.spr.utils.SprinklrUtils;
import com.spr.utils.core.UserContextProvider;
import com.spr.utils.core.managers.MongoTemplateFactory;
import com.spr.utils.performance.PerfTracker;
import com.spr.videochat.adapter.ChimeAttendeeAdapter;
import com.spr.videochat.adapter.ChimeMeetingAdapter;
import com.spr.videochat.beans.ChimeVideoChatConversationProviderDetails;
import com.spr.videochat.beans.ChimeVideoChatCredentialsProviderChain;
import com.spr.videochat.beans.ChimeVideoChatParticipantProviderDetails;
import com.spr.videochat.beans.ChimeVideoChatRecordingProcessTask;
import com.spr.videochat.beans.VideoChatConversation;
import com.spr.videochat.beans.VideoChatConversationProviderDetails;
import com.spr.videochat.beans.VideoChatParticipantProviderDetails;
import com.spr.videochat.beans.VideoChatProviderType;
import com.spr.videochat.beans.VideoChatRecordingState;
import com.spr.videochat.beans.VideoChatTranscriptionState;
import com.spr.videochat.beans.chime.Attendee;
import com.spr.videochat.beans.chime.Meeting;
import com.spr.videochat.services.VideoChatProvider;
import com.spr.videochat.util.VideoChatUtils;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User : dhruvthakker
 * Date : 2020-11-19
 * Time : 19:00
 */
@Service
public class ChimeVideoChatProvider implements VideoChatProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChimeVideoChatProvider.class);
    private final MongoTemplateFactory mongoTemplateFactory;

    @Autowired
    public ChimeVideoChatProvider(MongoTemplateFactory mongoTemplateFactory) {
        this.mongoTemplateFactory = mongoTemplateFactory;
    }

    @Override
    public VideoChatConversationProviderDetails createVideoChatConversation(Account account) {
        AmazonChime chimeClient = getChimeApiClient(account);
        Tag partnerTag = new Tag().withKey(PARTNER_TAG).withValue(String.valueOf(getCurrentPartnerId()));
        CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest().withExternalMeetingId(new ObjectId().toString()).withTags(partnerTag);
        Map<String, String> propertiesMap = account.getPropertiesMap();
        if (propertiesMap != null && propertiesMap.containsKey(AWS_REGION)) {
            createMeetingRequest.setMediaRegion(propertiesMap.get(AWS_REGION));
        }
        PerfTracker.in("createChimeMeeting");
        CreateMeetingResult meetingResult = chimeClient.createMeeting(createMeetingRequest);
        PerfTracker.out("createChimeMeeting");
        Meeting meeting = ChimeMeetingAdapter.getInstance().createSprMeeting(meetingResult.getMeeting());
        return new ChimeVideoChatConversationProviderDetails(meetingResult.getMeeting().getMeetingId(), meeting);
    }

    @Override
    public VideoChatParticipantProviderDetails getDummyParticipantProviderDetails() {
        return new ChimeVideoChatParticipantProviderDetails();
    }

    @Override
    public VideoChatParticipantProviderDetails addVideoChatParticipant(Account account,
                                                                       VideoChatConversationProviderDetails conversationProviderDetails,
                                                                       String participantId) {
        try {
            VideoChatUtils.throwUserVisibleExceptionIfFalse(conversationProviderDetails instanceof ChimeVideoChatConversationProviderDetails,
                                                            "Invalid conversation provider details");
            String meetingId = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeetingId();
            AmazonChime chimeClient = getChimeApiClient(account);
            PerfTracker.in("addChimeAttendee");
            CreateAttendeeResult attendeeResult = chimeClient.createAttendee(new CreateAttendeeRequest().withMeetingId(meetingId)
                                                                                                        .withExternalUserId(participantId));
            PerfTracker.out("addChimeAttendee");
            Attendee attendee = ChimeAttendeeAdapter.getInstance().createSprAttendee(attendeeResult.getAttendee());
            return new ChimeVideoChatParticipantProviderDetails(meetingId, attendeeResult.getAttendee().getAttendeeId(), attendee);
        } catch (NotFoundException e) {
            throw new UserVisibleException("Meeting Already Ended");
        }
    }

    @Override
    public VideoChatParticipantProviderDetails rejoinVideoChatParticipant(Account account,
                                                                          VideoChatConversationProviderDetails conversationProviderDetails,
                                                                          String participantId) {
        try {
            VideoChatUtils.throwUserVisibleExceptionIfFalse(conversationProviderDetails instanceof ChimeVideoChatConversationProviderDetails,
                                                            "Invalid conversation provider details");
            String meetingId = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeetingId();
            AmazonChime chimeClient = getChimeApiClient(account);
            PerfTracker.in("addChimeAttendee");
            CreateAttendeeResult attendeeResult = chimeClient.createAttendee(new CreateAttendeeRequest().withMeetingId(meetingId)
                                                                                                        .withExternalUserId(participantId));
            PerfTracker.out("addChimeAttendee");
            Attendee attendee = ChimeAttendeeAdapter.getInstance().createSprAttendee(attendeeResult.getAttendee());
            return new ChimeVideoChatParticipantProviderDetails(meetingId, attendeeResult.getAttendee().getAttendeeId(), attendee);
        } catch (NotFoundException e) {
            throw new UserVisibleException("Meeting Already Ended");
        }
    }

    @Override
    public boolean removeParticipantFromConversation(Account account, VideoChatParticipantProviderDetails participantProviderDetails) {
        try {
            VideoChatUtils.throwUserVisibleExceptionIfFalse(participantProviderDetails instanceof ChimeVideoChatParticipantProviderDetails,
                                                            "Invalid participant provider details");
            String meetingId = ((ChimeVideoChatParticipantProviderDetails) participantProviderDetails).getMeetingId();
            String attendeeId = ((ChimeVideoChatParticipantProviderDetails) participantProviderDetails).getAttendeeId();
            AmazonChime chimeClient = getChimeApiClient(account);
            DeleteAttendeeRequest attendeeRequest = new DeleteAttendeeRequest();
            attendeeRequest.setMeetingId(meetingId);
            attendeeRequest.setAttendeeId(attendeeId);
            DeleteAttendeeResult deleteAttendeeResult = chimeClient.deleteAttendee(attendeeRequest);
            return deleteAttendeeResult.getSdkHttpMetadata().getHttpStatusCode() == 204;
        } catch (NotFoundException e) {
            //Do Nothing
            return false;
        }
    }

    @Override
    public boolean deleteConversation(Account account, VideoChatConversationProviderDetails conversationProviderDetails) {
        VideoChatUtils.throwUserVisibleExceptionIfFalse(conversationProviderDetails instanceof ChimeVideoChatConversationProviderDetails,
                                                        "Invalid conversation provider details");
        try {
            String meetingId = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeetingId();
            AmazonChime chimeClient = getChimeApiClient(account);
            DeleteMeetingRequest deleteMeetingRequest = new DeleteMeetingRequest();
            deleteMeetingRequest.setMeetingId(meetingId);

            DeleteMeetingResult deleteMeetingResult = chimeClient.deleteMeeting(deleteMeetingRequest);

            return deleteMeetingResult.getSdkHttpMetadata().getHttpStatusCode() == 204;
        } catch (NotFoundException e) {
            return true;
        } catch (Exception e) {
            LOGGER.error("[VIDEO_CHAT_ALERT] Exception in deleting the meeting", e);
            return false;
        }
    }

    @Override
    public boolean conversationEnded(Account account, VideoChatConversationProviderDetails conversationProviderDetails) {
        VideoChatUtils.throwUserVisibleExceptionIfFalse(conversationProviderDetails instanceof ChimeVideoChatConversationProviderDetails,
                                                        "Invalid conversation provider details");
        try {
            Meeting meeting = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeeting();
            AmazonChime apiClient = getChimeApiClient(account);
            apiClient.getMeeting(new GetMeetingRequest().withMeetingId(meeting.getMeetingId()));
            return false;
        } catch (NotFoundException e) {
            return true;
        } catch (Exception e) {
            LOGGER.error("[VIDEO_CHAT_ALERT] Error while fetching meeting " + conversationProviderDetails);
        }
        return false;
    }

    @Override
    public VideoChatRecordingState startRecording(Account account, VideoChatRecordingState recordingState,
                                                  VideoChatConversation videoChatConversation) {
        CreateMediaCapturePipelineResult pipelineResult = createMediaPipeline(account, videoChatConversation, recordingState.getStartTime());
        recordingState.setRecordingProviderTaskId(pipelineResult.getMediaCapturePipeline().getMediaPipelineId());
        return recordingState;
    }

    @Override
    public void endRecording(Account account, VideoChatRecordingState recordingState) {
        deleteMediaCapturePipeline(account, recordingState.getRecordingProviderTaskId());
    }

    @Override
    public void processRecordings(Long accountId, VideoChatRecordingState recordingState, boolean isAudioOnly) {
        ChimeVideoChatRecordingProcessTask recordingProcessTask = new ChimeVideoChatRecordingProcessTask();
        recordingProcessTask.setRecordingStateId(recordingState.getId());
        recordingProcessTask.setVideoAccountId(accountId);
        recordingProcessTask.setOnlyAudioRecordingEnabled(isAudioOnly);

        ProcessTask processTask = ProcessTask.newInstance();
        processTask.setPartnerId(UserContextProvider.getCurrentPartnerId());
        processTask.setTaskType(ProcessTaskType.CHIME_VIDEO_RECORDING.name());
        processTask.setRequestId(recordingState.getId());
        processTask.setConfig(recordingProcessTask.toJson());
        processTask.setStatus(Status.NEW);
        processTask.setPriority(-1);
        processTask.setRetries(5);
        processTask.setProcessAfter(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
        mongoTemplateFactory.getAudienceMongoTemplate().save(processTask);
    }

    @Override
    public String getRecordingFile(VideoChatRecordingState recordingState, Account account) {
        InputStream inputStream = null;
        try {
            AmazonS3Client amazonS3Client = new AmazonS3Client(getAwsCredentialsProvider(account));
            String recordingFileName = recordingState.getRecordingFileName();
            Map<String, String> propertiesMap = account.getPropertiesMap();
            S3Object object = amazonS3Client.getObject(propertiesMap.get(RECORDING_ARTIFACTS_BUCKET), recordingFileName + ".mp4");
            inputStream = object.getObjectContent();
            String url = SprinklrUtils.uploadMediaOnPrivateCloudAndGetURL(inputStream, recordingFileName, SprMimeType.VIDEO_MP4.getMimeType());
            recordingState.setRecordingFileUrl(url);
            return url;
        } catch (Exception e) {
            if (e instanceof AmazonS3Exception) {
                int statusCode = ((AmazonS3Exception) e).getStatusCode();
                if (statusCode == 404) {
                    LOGGER.error("Retrying fetch file for task: " + recordingState.getId());
                    throw new SkipError();
                }
            }
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public VideoChatProviderType getProviderType() {
        return VideoChatProviderType.AMAZON_CHIME;
    }

    @Override
    public String startMediaPipelineForTranscription(Account account, VideoChatConversation videoChatConversation, Long startTime) {
        return createMediaPipeline(account, videoChatConversation, startTime).getMediaCapturePipeline().getMediaPipelineId();
    }

    @Override
    public void endMediaPipelineForTranscription(Account account, VideoChatTranscriptionState videoChatTranscriptionState) {
        deleteMediaCapturePipeline(account, videoChatTranscriptionState.getRecordingProviderTaskId());
    }

    // Method to hit Amazon Chime API for starting transcription
    @Override
    public boolean startTranscription(Account account, VideoChatConversation videoChatConversation) {

        AmazonChime chimeClient = getChimeApiClient(account);

        ChimeVideoChatConversationProviderDetails providerDetails = videoChatConversation.providerDetails();

        /*
            The structure of the request is as follows:
            startMeetingTranscriptionRequest:
                - meetingId
                - transcriptionConfiguration:
                    - engineTranscribeSettings:
                        - LanguageCode
                        - Region
                        [Vocab Filters etc also exist, we aren't using these for now]
                    [engineTranscribeMedicalSettings - we aren't using this]
         */
        StartMeetingTranscriptionRequest startMeetingTranscriptionRequest = new StartMeetingTranscriptionRequest();
        startMeetingTranscriptionRequest.withMeetingId(providerDetails.getMeetingId());

        EngineTranscribeSettings engineTranscribeSettings = new EngineTranscribeSettings();
        Map<String, String> propertiesMap = account.getPropertiesMap();
        if (propertiesMap.containsKey(AWS_REGION)) {
            engineTranscribeSettings.withRegion(propertiesMap.get(AWS_REGION));
        }
        engineTranscribeSettings.withLanguageCode(fetchTranscriptionLanguageFromLocale(videoChatConversation.getId()));
        TranscriptionConfiguration transcriptionConfiguration =
            new TranscriptionConfiguration().withEngineTranscribeSettings(engineTranscribeSettings);
        startMeetingTranscriptionRequest.withTranscriptionConfiguration(transcriptionConfiguration);

        StartMeetingTranscriptionResult startMeetingTranscriptionResult = chimeClient.startMeetingTranscription(startMeetingTranscriptionRequest);
        if (startMeetingTranscriptionResult.getSdkHttpMetadata().getHttpStatusCode() != 200) {
            LOGGER.alertError("[VIDEO_CHAT_TRANSCRIPTION_ALERT] meeting did not start, failed with error code: " + startMeetingTranscriptionResult);
            return false;
        }

        return true;
    }

    //--------------------------------------------------------------- Private Methods ----------------------------------------------------------------

    private CreateMediaCapturePipelineResult createMediaPipeline(Account account, VideoChatConversation videoChatConversation, Long startTime) {

        AmazonChime chimeClient = getChimeApiClient(account);

        ChimeVideoChatConversationProviderDetails providerDetails = videoChatConversation.providerDetails();

        CreateMediaCapturePipelineRequest createMediaCapturePipelineRequest = new CreateMediaCapturePipelineRequest();
        createMediaCapturePipelineRequest.withSourceType(MediaPipelineSourceType.ChimeSdkMeeting);
        createMediaCapturePipelineRequest
            .withSourceArn("arn:aws:chime::" + getChimeArn(account) + ":meeting:" + providerDetails.getMeetingId());
        createMediaCapturePipelineRequest.withSinkType(MediaPipelineSinkType.S3Bucket);
        String recordingArtifactsFolder =
            "recording_artifacts" + "/" + getCurrentPartnerId() + "/" + videoChatConversation.getId() + "_" + startTime;
        String recordingArtifactsBucket = account.getPropertiesMap().get(RECORDING_ARTIFACTS_BUCKET);
        createMediaCapturePipelineRequest.withSinkArn("arn:aws:s3:::" + recordingArtifactsBucket + "/" + recordingArtifactsFolder);

        return chimeClient.createMediaCapturePipeline(createMediaCapturePipelineRequest);
    }


    private void deleteMediaCapturePipeline(Account account, String recordingProviderTaskId) {

        AmazonChime chimeClient = getChimeApiClient(account);

        try {
            chimeClient.deleteMediaCapturePipeline(new DeleteMediaCapturePipelineRequest().withMediaPipelineId(recordingProviderTaskId));
        } catch (Exception e) {
            if (ExceptionUtils.chainContains(e, NotFoundException.class)) {
                return;
            }
            throw e;
        }
    }


    private AmazonChime getChimeApiClient(Account account) {
        AWSCredentialsProvider credentialsProvider = getAwsCredentialsProvider(account);
        AmazonChimeClientBuilder builder = AmazonChimeClient.builder();
        builder.setCredentials(credentialsProvider);
        if (SprinklrCollectionUtils.isNotEmpty(account.getPropertiesMap()) && account.getPropertiesMap().containsKey(AWS_REGION)) {
            builder.setRegion(account.getPropertiesMap().get(AWS_REGION));
        }
        return builder.build();
    }

    private AWSCredentialsProvider getAwsCredentialsProvider(Account account) {
        if (account == null || !AccountType.AWS_CHIME.name().equals(account.getAccountType())) {
            throw new UserVisibleException("Video Chat Account Not found");
        }
        AWSCredentialsProvider credentialsProvider;
        if (DynamicPropertyUtils.getBooleanDynamicPropertyValue(ENABLE_VIDEO_CHAT_IAM_ROLE_BASED)) {
            credentialsProvider = new ChimeVideoChatCredentialsProviderChain(account.getAccessToken(), account.getAccessSecret(),
                                                                             MapUtils.getString(account.getPropertiesMap(), ROLE_ARN),
                                                                             MapUtils.getString(account.getPropertiesMap(), TOKEN_RESOURCE));
        } else {
            credentialsProvider =
                new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccessToken(), account.getAccessSecret()));
        }
        return credentialsProvider;
    }

    protected String getChimeArn(Account account) {
        return MapUtils.getString(account.getPropertiesMap(), CHIME_ARN, account.getAccountUserId());
    }

    // We need to map locale language to AWS chime lang enum
    private TranscribeLanguageCode fetchTranscriptionLanguageFromLocale(String videoConversationId) {
        Locale locale = ChatContext.current().getLocale();
        TranscribeLanguageCode transcribeLanguageCode = TranscribeLanguageCode.EnUS;
        try {
            transcribeLanguageCode = TranscribeLanguageCode.fromValue(locale.getLanguage() + "-" + locale.getCountry());
        } catch (Exception e1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                    "[VIDEO_CHAT_TRANSCRIPTION_ALERT] Chime transcription language config from locale failed for conversation: " + videoConversationId,
                    e1);
            }
            locale = Locale.getDefault();
            try {
                transcribeLanguageCode = TranscribeLanguageCode.fromValue(locale.getLanguage() + "-" + locale.getCountry());
            } catch (Exception e2) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("[VIDEO_CHAT_TRANSCRIPTION_ALERT] Chime transcription language config from default locale failed for conversation: "
                                 + videoConversationId, e2);
                }
            }
        }
        return transcribeLanguageCode;
    }
}
