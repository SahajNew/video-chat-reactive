package com.spr.videochat.adapter;


import com.spr.videochat.beans.chime.Attendee;

/**
 * User : dhruvthakker
 * Date : 2021-03-13
 * Time : 20:16
 */
public class ChimeAttendeeAdapter {

    public static final ChimeAttendeeAdapter INSTANCE = new ChimeAttendeeAdapter();

    private ChimeAttendeeAdapter() {

    }

    public static ChimeAttendeeAdapter getInstance() {
        return INSTANCE;
    }

    public Attendee createSprAttendee(com.amazonaws.services.chime.model.Attendee chimeAttendee) {
        Attendee sprAttendee = new Attendee();
        sprAttendee.setAttendeeId(chimeAttendee.getAttendeeId());
        sprAttendee.setExternalUserId(chimeAttendee.getExternalUserId());
        sprAttendee.setJoinToken(chimeAttendee.getJoinToken());
        return sprAttendee;
    }
}
