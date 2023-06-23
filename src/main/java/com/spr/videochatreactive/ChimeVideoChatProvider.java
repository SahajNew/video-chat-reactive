package com.spr.videochatreactive;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.chime.AmazonChime;
import com.amazonaws.services.chime.AmazonChimeClient;
import com.amazonaws.services.chime.AmazonChimeClientBuilder;
import com.amazonaws.services.chime.model.*;
import com.spr.videochatreactive.adapter.ChimeAttendeeAdapter;
import com.spr.videochatreactive.beans.*;
import com.spr.videochatreactive.beans.Account;
import com.spr.videochatreactive.chime.ChimeVideoChatConversationProviderDetails;
import com.spr.videochatreactive.chime.ChimeVideoChatParticipantProviderDetails;
import com.spr.videochatreactive.chime.Meeting;
import com.spr.videochatreactive.chime.Attendee;
import com.spr.videochatreactive.adapter.ChimeMeetingAdapter;
import org.apache.commons.collections4.MapUtils;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.spr.videochatreactive.VideoChatConstants.*;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;

public class ChimeVideoChatProvider implements VideoChatProvider {


    @Override
    public VideoChatConversationProviderDetails createVideoChatConversation(com.spr.videochatreactive.beans.Account account) {
        AmazonChime chimeClient = getChimeApiClient(account);
        Tag partnerTag = new Tag().withKey("PARTNER_TAG").withValue(String.valueOf(getCurrentPartnerId()));
        CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest().withExternalMeetingId(UUID.randomUUID().toString()).withTags(partnerTag);
        Map<String, String> propertiesMap = account.getPropertiesMap();
        if (propertiesMap != null && propertiesMap.containsKey(AWS_REGION)) {
            createMeetingRequest.setMediaRegion(propertiesMap.get(AWS_REGION));
        }

        CreateMeetingResult meetingResult = chimeClient.createMeeting(createMeetingRequest);

        Meeting meeting = ChimeMeetingAdapter.getInstance().createSprMeeting(meetingResult.getMeeting());
        return new ChimeVideoChatConversationProviderDetails(meetingResult.getMeeting().getMeetingId(), meeting);
    }

    @Override
    public VideoChatParticipantProviderDetails getDummyParticipantProviderDetails() {
        return new ChimeVideoChatParticipantProviderDetails();
    }

    @Override
    public VideoChatParticipantProviderDetails addVideoChatParticipant(com.spr.videochatreactive.beans.Account account,
                                                                       VideoChatConversationProviderDetails conversationProviderDetails,
                                                                       String participantId) {
        try {
            if (!(conversationProviderDetails instanceof ChimeVideoChatConversationProviderDetails)) {
                throw new RuntimeException("Invalid conversation provider details");
            }
            String meetingId = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeetingId();
            AmazonChime chimeClient = getChimeApiClient(account);

            CreateAttendeeResult attendeeResult = chimeClient.createAttendee(new CreateAttendeeRequest().withMeetingId(meetingId)
                    .withExternalUserId(participantId));

            Attendee attendee = ChimeAttendeeAdapter.getInstance().createSprAttendee(attendeeResult.getAttendee());
            return new ChimeVideoChatParticipantProviderDetails(meetingId, attendeeResult.getAttendee().getAttendeeId(), attendee);
        } catch (NotFoundException e) {
            throw new RuntimeException("Meeting Already Ended");
        }
    }

    @Override
    public VideoChatParticipantProviderDetails rejoinVideoChatParticipant(com.spr.videochatreactive.beans.Account account,
                                                                          VideoChatConversationProviderDetails conversationProviderDetails,
                                                                          String participantId) {
        try {
            if (!(conversationProviderDetails instanceof ChimeVideoChatConversationProviderDetails)) {
                throw new RuntimeException("Invalid conversation provider details");
            }
            String meetingId = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeetingId();
            AmazonChime chimeClient = getChimeApiClient(account);

            CreateAttendeeResult attendeeResult = chimeClient.createAttendee(new CreateAttendeeRequest().withMeetingId(meetingId)
                    .withExternalUserId(participantId));

            Attendee attendee = ChimeAttendeeAdapter.getInstance().createSprAttendee(attendeeResult.getAttendee());
            return new ChimeVideoChatParticipantProviderDetails(meetingId, attendeeResult.getAttendee().getAttendeeId(), attendee);
        } catch (NotFoundException e) {
            throw new RuntimeException("Meeting Already Ended");
        }
    }

    @Override
    public boolean removeParticipantFromConversation(com.spr.videochatreactive.beans.Account account, VideoChatParticipantProviderDetails participantProviderDetails) {
        try {
            if (!(participantProviderDetails instanceof ChimeVideoChatParticipantProviderDetails)) {
                throw new RuntimeException("Invalid conversation provider details");
            }
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
    public boolean deleteConversation(com.spr.videochatreactive.beans.Account account, VideoChatConversationProviderDetails conversationProviderDetails) {
        if (!(conversationProviderDetails instanceof ChimeVideoChatConversationProviderDetails)) {
            throw new RuntimeException("Invalid conversation provider details");
        }
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
            return false;
        }
    }

    @Override
    public boolean conversationEnded(com.spr.videochatreactive.beans.Account account, VideoChatConversationProviderDetails conversationProviderDetails) {
        if (!(conversationProviderDetails instanceof ChimeVideoChatConversationProviderDetails)) {
            throw new RuntimeException("Invalid conversation provider details");
        }
        try {
            Meeting meeting = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeeting();
            AmazonChime apiClient = getChimeApiClient(account);
            apiClient.getMeeting(new GetMeetingRequest().withMeetingId(meeting.getMeetingId()));
            return false;
        } catch (NotFoundException e) {
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public VideoChatRecordingState startRecording(com.spr.videochatreactive.beans.Account account, VideoChatRecordingState recordingState,
                                                  VideoChatConversation videoChatConversation) {
        CreateMediaCapturePipelineResult pipelineResult = createMediaPipeline(account, videoChatConversation, recordingState.getStartTime());
        recordingState.setRecordingProviderTaskId(pipelineResult.getMediaCapturePipeline().getMediaPipelineId());
        return recordingState;
    }

    @Override
    public void endRecording(com.spr.videochatreactive.beans.Account account, VideoChatRecordingState recordingState) {
        deleteMediaCapturePipeline(account, recordingState.getRecordingProviderTaskId());
    }

    @Override
    public String startMediaPipelineForTranscription(com.spr.videochatreactive.beans.Account account, VideoChatConversation videoChatConversation, Long startTime) {
        return createMediaPipeline(account, videoChatConversation, startTime).getMediaCapturePipeline().getMediaPipelineId();
    }

    // Method to hit Amazon Chime API for starting transcription
    @Override
    public boolean startTranscription(com.spr.videochatreactive.beans.Account account, VideoChatConversation videoChatConversation) {

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
            return false;
        }

        return true;
    }

    //--------------------------------------------------------------- Private Methods ----------------------------------------------------------------

    private CreateMediaCapturePipelineResult createMediaPipeline(com.spr.videochatreactive.beans.Account account, VideoChatConversation videoChatConversation, Long startTime) {

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


    private void deleteMediaCapturePipeline(com.spr.videochatreactive.beans.Account account, String recordingProviderTaskId) {

        AmazonChime chimeClient = getChimeApiClient(account);

        try {
            chimeClient.deleteMediaCapturePipeline(new DeleteMediaCapturePipelineRequest().withMediaPipelineId(recordingProviderTaskId));
        } catch (NotFoundException ne) {
        } catch (Exception e) {
            throw e;
        }
    }


    private AmazonChime getChimeApiClient(com.spr.videochatreactive.beans.Account account) {
        AWSCredentialsProvider credentialsProvider = getAwsCredentialsProvider(account);
        AmazonChimeClientBuilder builder = AmazonChimeClient.builder();
        builder.setCredentials(credentialsProvider);
        if (isNotEmpty(account.getPropertiesMap()) && account.getPropertiesMap().containsKey(AWS_REGION)) {
            builder.setRegion(account.getPropertiesMap().get(AWS_REGION));
        }
        return builder.build();
    }

    private AWSCredentialsProvider getAwsCredentialsProvider(com.spr.videochatreactive.beans.Account account) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccessToken(), account.getAccessSecret()));
        return credentialsProvider;
    }

    protected String getChimeArn(Account account) {
        return MapUtils.getString(account.getPropertiesMap(), CHIME_ARN, account.getAccountUserId());
    }

    // We need to map locale language to AWS chime lang enum
    private TranscribeLanguageCode fetchTranscriptionLanguageFromLocale(String videoConversationId) {
        Locale locale = getLocale();
        TranscribeLanguageCode transcribeLanguageCode = TranscribeLanguageCode.EnUS;
        try {
            transcribeLanguageCode = TranscribeLanguageCode.fromValue(locale.getLanguage() + "-" + locale.getCountry());
        } catch (Exception e1) {
            locale = Locale.getDefault();
            try {
                transcribeLanguageCode = TranscribeLanguageCode.fromValue(locale.getLanguage() + "-" + locale.getCountry());
            } catch (Exception e2) {
            }
        }
        return transcribeLanguageCode;
    }

    //--------------------------------------------------------------- Dummy Methods ----------------------------------------------------------------

    private Long getCurrentPartnerId() {
        return 123L;
    }


    private Locale getLocale() {
        return Locale.US;
    }
}
