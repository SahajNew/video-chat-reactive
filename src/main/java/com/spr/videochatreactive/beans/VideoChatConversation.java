package com.spr.videochatreactive.beans;

public class VideoChatConversation {

    private String id;
    private Long videoChatAccountId;
    private VideoChatConversationProviderDetails providerDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVideoChatAccountId() {
        return videoChatAccountId;
    }

    public void setVideoChatAccountId(Long videoChatAccountId) {
        this.videoChatAccountId = videoChatAccountId;
    }

    public VideoChatConversationProviderDetails getProviderDetails() {
        return providerDetails;
    }

    public void setProviderDetails(VideoChatConversationProviderDetails providerDetails) {
        this.providerDetails = providerDetails;
    }

    public <T extends VideoChatConversationProviderDetails> T providerDetails() {
        //noinspection unchecked
        return (T) providerDetails;
    }
}
