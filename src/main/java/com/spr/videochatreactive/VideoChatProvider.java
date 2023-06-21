package com.spr.videochat.services;

import com.spr.beans.Account;
import com.spr.videochat.beans.VideoChatConversation;
import com.spr.videochat.beans.VideoChatConversationProviderDetails;
import com.spr.videochat.beans.VideoChatParticipantProviderDetails;
import com.spr.videochat.beans.VideoChatProviderType;
import com.spr.videochat.beans.VideoChatRecordingState;
import com.spr.videochat.beans.VideoChatTranscriptionState;

/**
 * User : dhruvthakker
 * Date : 2020-11-19
 * Time : 19:49
 */
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
    void endMediaPipelineForTranscription(Account account, VideoChatTranscriptionState videoChatTranscriptionState);
    void processRecordings(Long accountId, VideoChatRecordingState recordingState, boolean isAudioOnly);

    String getRecordingFile(VideoChatRecordingState recordingState, Account account);

    VideoChatProviderType getProviderType();

    boolean startTranscription(Account account, VideoChatConversation videoChatConversation);
}
