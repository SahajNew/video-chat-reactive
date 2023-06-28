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
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.spr.videochatreactive.VideoChatConstants.*;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;

public class ChimeVideoChatProvider implements VideoChatProvider {


    @Override
    public Mono<VideoChatConversationProviderDetails> createVideoChatConversation(Account account) {

        return Mono.fromCallable(()->getChimeApiClient(account))
                .flatMap(chimeClient->{

                    Tag partnerTag = new Tag().withKey("PARTNER_TAG").withValue(String.valueOf(getCurrentPartnerId()));
                    CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest().withExternalMeetingId(UUID.randomUUID().toString()).withTags(partnerTag);
                    Map<String, String> propertiesMap = account.getPropertiesMap();
                    if (propertiesMap != null && propertiesMap.containsKey(AWS_REGION)) {
                        createMeetingRequest.setMediaRegion(propertiesMap.get(AWS_REGION));
                    }

//                    Mono<CreateMeetingResult> meetingResult=
                    return Mono.fromCallable(()->chimeClient.createMeeting(createMeetingRequest))
                            .map(meetingResult->{
                                Meeting meeting=ChimeMeetingAdapter.getInstance().createSprMeeting(meetingResult.getMeeting());
                                return new  ChimeVideoChatConversationProviderDetails(meetingResult.getMeeting().getMeetingId(), meeting);
                            });
                });
    }

    @Override
    public VideoChatParticipantProviderDetails getDummyParticipantProviderDetails() {
        return new ChimeVideoChatParticipantProviderDetails();
    }

    @Override
    public Mono<VideoChatParticipantProviderDetails> addVideoChatParticipant(com.spr.videochatreactive.beans.Account account,
                                                                             VideoChatConversationProviderDetails conversationProviderDetails,
                                                                             String participantId) {

        return Mono.just(conversationProviderDetails)
                .filter(details -> details instanceof ChimeVideoChatConversationProviderDetails)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid conversation provider details")))
                .flatMap(details->Mono.fromCallable(()-> getChimeApiClient(account)))
                .flatMap(chimeClient->{

                    String meetingId = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeetingId();
                    return Mono.fromCallable(()->chimeClient.createAttendee(new CreateAttendeeRequest().withMeetingId(meetingId).withExternalUserId(participantId)))
//                             .map(attendeeResult-> Tuples.of(meetingId,attendeeResult))
                            .map(attendeeResult->{
                                Attendee attendee = ChimeAttendeeAdapter.getInstance().createSprAttendee(attendeeResult.getAttendee());
                                VideoChatParticipantProviderDetails participantDetails = new ChimeVideoChatParticipantProviderDetails(meetingId, attendeeResult.getAttendee().getAttendeeId(), attendee);
                                return (participantDetails);
                            });

                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        return Mono.error(new RuntimeException("Meeting already ended"));
                    } else {
                        return Mono.error(throwable);
                    }
                });


    }

    @Override
    public Mono<VideoChatParticipantProviderDetails> rejoinVideoChatParticipant(Account account,
                                                                                VideoChatConversationProviderDetails conversationProviderDetails,
                                                                                String participantId) {

        return Mono.just(conversationProviderDetails)
                .filter(details->details instanceof ChimeVideoChatConversationProviderDetails)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid conversation provider details")))
                .flatMap(details->Mono.fromCallable(()->getChimeApiClient(account)))
                .flatMap(chimeClient->{

                    String meetingId=((ChimeVideoChatConversationProviderDetails) conversationProviderDetails).getMeetingId();

                    return Mono.fromCallable(()-> chimeClient.createAttendee(new CreateAttendeeRequest().withMeetingId(meetingId).withExternalUserId(participantId)))

                            .map(attendeeResult ->{
                                Attendee attendee = ChimeAttendeeAdapter.getInstance().createSprAttendee(attendeeResult.getAttendee());
                                VideoChatParticipantProviderDetails videoChatParticipantProviderDetails= new ChimeVideoChatParticipantProviderDetails(meetingId, attendeeResult.getAttendee().getAttendeeId(), attendee);
                                return  videoChatParticipantProviderDetails;
                            } );

                })

                .onErrorResume(throwable -> {

                    if (throwable instanceof NotFoundException) {return Mono.error(new RuntimeException("Meeting already ended"));}
                    else {return Mono.error(throwable);}
                });
    }

    @Override
    public Mono<Boolean> removeParticipantFromConversation(Account account, VideoChatParticipantProviderDetails participantProviderDetails) {

        return Mono.just(participantProviderDetails)
                .filter(participantProviderDetailsMono -> participantProviderDetailsMono instanceof ChimeVideoChatParticipantProviderDetails)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid conversation provider details")))
                .flatMap(participantProviderDetailsMono -> {

                    String meetingId = ((ChimeVideoChatParticipantProviderDetails) participantProviderDetailsMono).getMeetingId();
                    String attendeeId = ((ChimeVideoChatParticipantProviderDetails) participantProviderDetailsMono).getAttendeeId();
                    DeleteAttendeeRequest attendeeRequest = new DeleteAttendeeRequest();
                    attendeeRequest.setMeetingId(meetingId);
                    attendeeRequest.setAttendeeId(attendeeId);

                    return Mono.fromCallable(() -> getChimeApiClient(account))
                            .map(chimeClient -> chimeClient.deleteAttendee(attendeeRequest));
                })
                .map(deleteAttendeeResult -> {
                    return deleteAttendeeResult.getSdkHttpMetadata().getHttpStatusCode()==204;
                })
                .onErrorResume(NotFoundException.class, e -> Mono.just(false));
    }



    @Override
    public Mono<Boolean> deleteConversation(Account account, VideoChatConversationProviderDetails conversationProviderDetails) {

        return Mono.just(conversationProviderDetails)
                .filter(conversationProviderDetailsMono->conversationProviderDetailsMono instanceof ChimeVideoChatConversationProviderDetails)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid conversation provider details")))
                .flatMap(conversationProviderDetailsMono->{

                    String meetingId = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetailsMono).getMeetingId();
                    DeleteMeetingRequest deleteMeetingRequest = new DeleteMeetingRequest();
                    deleteMeetingRequest.setMeetingId(meetingId);

                    return  Mono.fromCallable(()->getChimeApiClient(account))
                            .map(chimeClient->chimeClient.deleteMeeting(deleteMeetingRequest));

                })
                .map(deleteMeetingResult ->deleteMeetingResult.getSdkHttpMetadata().getHttpStatusCode() == 204 )
                .onErrorResume(NotFoundException.class, e -> Mono.just(false))
                .onErrorResume(Exception.class,e->Mono.just(false));


    }

    @Override
    public Mono<Boolean> conversationEnded(Account account, VideoChatConversationProviderDetails conversationProviderDetails) {

        return Mono.just(conversationProviderDetails)
                .filter(conversationProviderDetailsMono-> conversationProviderDetailsMono  instanceof ChimeVideoChatConversationProviderDetails )
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid conversation provider details")))
                .flatMap(conversationProviderDetailsMono->{
                    Meeting meeting = ((ChimeVideoChatConversationProviderDetails) conversationProviderDetailsMono).getMeeting();

                    return Mono.fromCallable(()->getChimeApiClient(account))
                            .map(apiClient-> apiClient.getMeeting(new GetMeetingRequest().withMeetingId(meeting.getMeetingId())));

                })
                .map(d-> false)
                .onErrorResume(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    //Doubt about exception;
    @Override
    public Mono<VideoChatRecordingState> startRecording(Account account, VideoChatRecordingState recordingState,
                                                        VideoChatConversation videoChatConversation) {

        return createMediaPipeline(account, videoChatConversation, recordingState.getStartTime())
                .map(pipelineResult -> {
                    recordingState.setRecordingProviderTaskId(pipelineResult.getMediaCapturePipeline().getMediaPipelineId());
                    return recordingState;
                });

    }

    @Override
    public Mono<Void> endRecording(Account account, VideoChatRecordingState recordingState) {
        return deleteMediaCapturePipeline(account, recordingState.getRecordingProviderTaskId());
    }

    @Override
    public Mono<String> startMediaPipelineForTranscription(Account account, VideoChatConversation videoChatConversation, Long startTime) {

        return createMediaPipeline(account, videoChatConversation, startTime)
                .map(CreateMediaCapturePipelineResult::getMediaCapturePipeline)
                .map(MediaCapturePipeline::getMediaPipelineId);
    }

    // Method to hit Amazon Chime API for starting transcription
    @Override
    public Mono<Boolean> startTranscription(Account account, VideoChatConversation videoChatConversation) {

        return Mono.fromCallable(()->getChimeApiClient(account))
                .map(chimeClient->{

                    ChimeVideoChatConversationProviderDetails providerDetails = videoChatConversation.providerDetails();
                    StartMeetingTranscriptionRequest startMeetingTranscriptionRequest = new StartMeetingTranscriptionRequest();
                    startMeetingTranscriptionRequest.withMeetingId(providerDetails.getMeetingId());
                    EngineTranscribeSettings engineTranscribeSettings = new EngineTranscribeSettings();
                    Map<String, String> propertiesMap = account.getPropertiesMap();

                    if (propertiesMap.containsKey(AWS_REGION)) {
                        engineTranscribeSettings.withRegion(propertiesMap.get(AWS_REGION));
                    }
                    engineTranscribeSettings.withLanguageCode(fetchTranscriptionLanguageFromLocale(videoChatConversation.getId()));

                    TranscriptionConfiguration transcriptionConfiguration = new TranscriptionConfiguration().withEngineTranscribeSettings(engineTranscribeSettings);
                    startMeetingTranscriptionRequest.withTranscriptionConfiguration(transcriptionConfiguration);

                    return chimeClient.startMeetingTranscription(startMeetingTranscriptionRequest);
                })
                .map(startMeetingTranscriptionResult ->{

                    if (startMeetingTranscriptionResult.getSdkHttpMetadata().getHttpStatusCode() != 200)
                        return (false);
                    else
                        return (true);

                });


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

    }

    //--------------------------------------------------------------- Private Methods ----------------------------------------------------------------

    private Mono<CreateMediaCapturePipelineResult>createMediaPipeline(com.spr.videochatreactive.beans.Account account, VideoChatConversation videoChatConversation, Long startTime) {

//        AmazonChime chimeClient = getChimeApiClient(account);
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

        return Mono.fromCallable(()->getChimeApiClient(account))
                .map(chimeClient->chimeClient.createMediaCapturePipeline(createMediaCapturePipelineRequest));
//        return Mono.fromCallable(()-> chimeClient.createMediaCapturePipeline(createMediaCapturePipelineRequest));

    }


    private Mono<Void> deleteMediaCapturePipeline(com.spr.videochatreactive.beans.Account account, String recordingProviderTaskId) {

//
        return Mono.fromCallable(()->getChimeApiClient(account))
                .map(chimeClient->chimeClient.deleteMediaCapturePipeline(new DeleteMediaCapturePipelineRequest().withMediaPipelineId(recordingProviderTaskId)))
                .onErrorResume(Exception.class,e->Mono.error(e))
                .then();

    }


    private  AmazonChime getChimeApiClient(com.spr.videochatreactive.beans.Account account) {


        AWSCredentialsProvider credentialsProvider = getAwsCredentialsProvider(account);
        AmazonChimeClientBuilder builder = AmazonChimeClient.builder();
        builder.setCredentials(credentialsProvider);


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
