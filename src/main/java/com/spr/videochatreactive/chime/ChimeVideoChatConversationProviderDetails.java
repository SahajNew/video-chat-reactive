package com.spr.videochatreactive;

import com.spr.videochatreactive.chime.Meeting;

public class ChimeVideoChatConversationProviderDetails extends VideoChatConversationProviderDetails {

    private String meetingId;
    private Meeting meeting;

    public ChimeVideoChatConversationProviderDetails(String meetingId, Meeting meeting) {
        this.provider = "AMAZON_CHIME";
        this.meetingId = meetingId;
        this.meeting = meeting;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    @Override
    public String toString() {
        return "ChimeVideoChatConversationProviderDetails{" +
                "meetingId='" + meetingId + '\'' +
                ", meeting=" + meeting +
                '}';
    }
}
