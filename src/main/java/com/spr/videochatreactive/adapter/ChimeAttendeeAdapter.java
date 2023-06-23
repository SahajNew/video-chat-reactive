package com.spr.videochatreactive.adapter;

import com.spr.videochatreactive.chime.Attendee;

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
