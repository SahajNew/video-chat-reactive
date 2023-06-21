package com.spr.videochatreactive;

public class VideoChatRecordingState {

    private String id;
    private String videoChatConversationId;
    private Status status;
    private VideoChatParticipantProviderDetails botParticipantProviderDetails;
    private Long startTime;
    private Long modifiedTime;
    private Long endTime;
    private String processedBy;
    private String recordingFileName;
    private String recordingFileUrl;
    private String failureReason;
    private String recordingProviderTaskId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoChatConversationId() {
        return videoChatConversationId;
    }

    public void setVideoChatConversationId(String videoChatConversationId) {
        this.videoChatConversationId = videoChatConversationId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public VideoChatParticipantProviderDetails getBotParticipantProviderDetails() {
        return botParticipantProviderDetails;
    }

    public void setBotParticipantProviderDetails(VideoChatParticipantProviderDetails botParticipantProviderDetails) {
        this.botParticipantProviderDetails = botParticipantProviderDetails;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public String getRecordingFileName() {
        return recordingFileName;
    }

    public void setRecordingFileName(String recordingFileName) {
        this.recordingFileName = recordingFileName;
    }

    public String getRecordingFileUrl() {
        return recordingFileUrl;
    }

    public void setRecordingFileUrl(String recordingFileUrl) {
        this.recordingFileUrl = recordingFileUrl;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getRecordingProviderTaskId() {
        return recordingProviderTaskId;
    }

    public void setRecordingProviderTaskId(String recordingProviderTaskId) {
        this.recordingProviderTaskId = recordingProviderTaskId;
    }

    public enum Status {
        REQUESTED, STARTED, IN_PROGRESS, ENDED, FAILED, PROCESSING_RECORDING, RECORDING_PROCESSED, RECORDING_RECEIVED
    }
}
