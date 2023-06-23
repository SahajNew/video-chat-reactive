package com.spr.videochatreactive;


import com.spr.videochatreactive.beans.*;

public interface VideoChatProvider {

    VideoChatConversationProviderDetails createVideoChatConversation(Account account);

    VideoChatParticipantProviderDetails getDummyParticipantProviderDetails();

    VideoChatParticipantProviderDetails addVideoChatParticipant(Account account, VideoChatConversationProviderDetails conversationProviderDetails,
                                                                String participantId);

    VideoChatParticipantProviderDetails rejoinVideoChatParticipant(Account account, VideoChatConversationProviderDetails conversationProviderDetails,
                                                                   String participantId);

    boolean removeParticipantFromConversation(Account account, VideoChatParticipantProviderDetails participantProviderDetails);

    boolean deleteConversation(Account account, VideoChatConversationProviderDetails conversationProviderDetails);

    boolean conversationEnded(Account account, VideoChatConversationProviderDetails videoChatConversationProviderDetails);

    VideoChatRecordingState startRecording(Account account, VideoChatRecordingState recordingState,
                                           VideoChatConversation videoChatConversation);

    void endRecording(Account account, VideoChatRecordingState recordingState);

    String startMediaPipelineForTranscription(Account account, VideoChatConversation videoChatConversation, Long startTime);

    boolean startTranscription(Account account, VideoChatConversation videoChatConversation);
}
