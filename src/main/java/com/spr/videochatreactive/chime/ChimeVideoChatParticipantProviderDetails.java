package com.spr.videochatreactive.chime;

import com.spr.videochatreactive.beans.VideoChatParticipantProviderDetails;

public class ChimeVideoChatParticipantProviderDetails extends VideoChatParticipantProviderDetails {

    public static final String MEETING_ID = "meetingId";
    public static final String ATTENDEE_ID = "attendeeId";

    private String meetingId;
    private String attendeeId;
    private Attendee attendee;

    public ChimeVideoChatParticipantProviderDetails() {
        this.provider = "AMAZON_CHIME";
    }

    public ChimeVideoChatParticipantProviderDetails(String meetingId, String attendeeId, Attendee attendee) {
        this.provider = "AMAZON_CHIME";
        this.meetingId = meetingId;
        this.attendeeId = attendeeId;
        this.attendee = attendee;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }
}
