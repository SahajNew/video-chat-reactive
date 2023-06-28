package com.spr.videochatreactive;


import com.spr.videochatreactive.beans.*;
import reactor.core.publisher.Mono;

public interface VideoChatProvider {

    Mono<VideoChatConversationProviderDetails> createVideoChatConversation(Account account);

    VideoChatParticipantProviderDetails getDummyParticipantProviderDetails();

    Mono<VideoChatParticipantProviderDetails> addVideoChatParticipant(Account account, VideoChatConversationProviderDetails conversationProviderDetails,
                                                                      String participantId);

    Mono<VideoChatParticipantProviderDetails> rejoinVideoChatParticipant(Account account, VideoChatConversationProviderDetails conversationProviderDetails,
                                                                         String participantId);

    Mono<Boolean> removeParticipantFromConversation(Account account, VideoChatParticipantProviderDetails participantProviderDetails);

    Mono<Boolean> deleteConversation(Account account, VideoChatConversationProviderDetails conversationProviderDetails);

    Mono<Boolean> conversationEnded(Account account, VideoChatConversationProviderDetails videoChatConversationProviderDetails);

    Mono<VideoChatRecordingState> startRecording(Account account, VideoChatRecordingState recordingState,
                                                 VideoChatConversation videoChatConversation);

    Mono<Void> endRecording(Account account, VideoChatRecordingState recordingState);

    Mono<String> startMediaPipelineForTranscription(Account account, VideoChatConversation videoChatConversation, Long startTime);

    Mono<Boolean> startTranscription(Account account, VideoChatConversation videoChatConversation);
}
