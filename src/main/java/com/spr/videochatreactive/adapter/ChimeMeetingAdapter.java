package com.spr.videochatreactive.adapter;

import com.spr.videochatreactive.chime.MediaPlacement;
import com.spr.videochatreactive.chime.Meeting;

public class ChimeMeetingAdapter {

    private static final ChimeMeetingAdapter INSTANCE = new ChimeMeetingAdapter();

    private ChimeMeetingAdapter() {

    }

    public static ChimeMeetingAdapter getInstance() {
        return INSTANCE;
    }

    public Meeting createSprMeeting(com.amazonaws.services.chime.model.Meeting chimeMeeting) {
        com.amazonaws.services.chime.model.MediaPlacement mediaPlacement = chimeMeeting.getMediaPlacement();
        MediaPlacement sprMediaPlacement = null;
        if (mediaPlacement != null) {
            sprMediaPlacement = new MediaPlacement();
            sprMediaPlacement.setAudioFallbackUrl(mediaPlacement.getAudioFallbackUrl());
            sprMediaPlacement.setAudioHostUrl(mediaPlacement.getAudioHostUrl());
            sprMediaPlacement.setScreenDataUrl(mediaPlacement.getScreenDataUrl());
            sprMediaPlacement.setScreenSharingUrl(mediaPlacement.getScreenSharingUrl());
            sprMediaPlacement.setSignalingUrl(mediaPlacement.getSignalingUrl());
            sprMediaPlacement.setTurnControlUrl(mediaPlacement.getTurnControlUrl());
        }

        Meeting sprMeeting = new Meeting();
        sprMeeting.setExternalMeetingId(chimeMeeting.getExternalMeetingId());
        sprMeeting.setMediaPlacement(sprMediaPlacement);
        sprMeeting.setMediaRegion(chimeMeeting.getMediaRegion());
        sprMeeting.setMeetingId(chimeMeeting.getMeetingId());
        return sprMeeting;
    }
}
