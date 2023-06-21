package com.spr.videochat.beans.chime;

/**
 * User : dhruvthakker
 * Date : 2021-03-13
 * Time : 20:23
 */

import java.io.Serializable;

public class MediaPlacement implements Serializable, Cloneable {

    /**
     * <p>
     * The audio host URL.
     * </p>
     */
    private String audioHostUrl;
    /**
     * <p>
     * The audio fallback URL.
     * </p>
     */
    private String audioFallbackUrl;
    /**
     * <p>
     * The screen data URL.
     * </p>
     */
    private String screenDataUrl;
    /**
     * <p>
     * The screen sharing URL.
     * </p>
     */
    private String screenSharingUrl;
    /**
     * <p>
     * The screen viewing URL.
     * </p>
     */
    private String screenViewingUrl;
    /**
     * <p>
     * The signaling URL.
     * </p>
     */
    private String signalingUrl;
    /**
     * <p>
     * The turn control URL.
     * </p>
     */
    private String turnControlUrl;

    /**
     * <p>
     * The audio host URL.
     * </p>
     *
     * @return The audio host URL.
     */

    public String getAudioHostUrl() {
        return this.audioHostUrl;
    }

    /**
     * <p>
     * The audio host URL.
     * </p>
     *
     * @param audioHostUrl The audio host URL.
     */

    public void setAudioHostUrl(String audioHostUrl) {
        this.audioHostUrl = audioHostUrl;
    }

    /**
     * <p>
     * The audio host URL.
     * </p>
     *
     * @param audioHostUrl The audio host URL.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public MediaPlacement withAudioHostUrl(String audioHostUrl) {
        setAudioHostUrl(audioHostUrl);
        return this;
    }

    /**
     * <p>
     * The audio fallback URL.
     * </p>
     *
     * @return The audio fallback URL.
     */

    public String getAudioFallbackUrl() {
        return this.audioFallbackUrl;
    }

    /**
     * <p>
     * The audio fallback URL.
     * </p>
     *
     * @param audioFallbackUrl The audio fallback URL.
     */

    public void setAudioFallbackUrl(String audioFallbackUrl) {
        this.audioFallbackUrl = audioFallbackUrl;
    }

    /**
     * <p>
     * The audio fallback URL.
     * </p>
     *
     * @param audioFallbackUrl The audio fallback URL.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public MediaPlacement withAudioFallbackUrl(String audioFallbackUrl) {
        setAudioFallbackUrl(audioFallbackUrl);
        return this;
    }

    /**
     * <p>
     * The screen data URL.
     * </p>
     *
     * @return The screen data URL.
     */

    public String getScreenDataUrl() {
        return this.screenDataUrl;
    }

    /**
     * <p>
     * The screen data URL.
     * </p>
     *
     * @param screenDataUrl The screen data URL.
     */

    public void setScreenDataUrl(String screenDataUrl) {
        this.screenDataUrl = screenDataUrl;
    }

    /**
     * <p>
     * The screen data URL.
     * </p>
     *
     * @param screenDataUrl The screen data URL.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public MediaPlacement withScreenDataUrl(String screenDataUrl) {
        setScreenDataUrl(screenDataUrl);
        return this;
    }

    /**
     * <p>
     * The screen sharing URL.
     * </p>
     *
     * @return The screen sharing URL.
     */

    public String getScreenSharingUrl() {
        return this.screenSharingUrl;
    }

    /**
     * <p>
     * The screen sharing URL.
     * </p>
     *
     * @param screenSharingUrl The screen sharing URL.
     */

    public void setScreenSharingUrl(String screenSharingUrl) {
        this.screenSharingUrl = screenSharingUrl;
    }

    /**
     * <p>
     * The screen sharing URL.
     * </p>
     *
     * @param screenSharingUrl The screen sharing URL.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public MediaPlacement withScreenSharingUrl(String screenSharingUrl) {
        setScreenSharingUrl(screenSharingUrl);
        return this;
    }

    /**
     * <p>
     * The screen viewing URL.
     * </p>
     *
     * @return The screen viewing URL.
     */

    public String getScreenViewingUrl() {
        return this.screenViewingUrl;
    }

    /**
     * <p>
     * The screen viewing URL.
     * </p>
     *
     * @param screenViewingUrl The screen viewing URL.
     */

    public void setScreenViewingUrl(String screenViewingUrl) {
        this.screenViewingUrl = screenViewingUrl;
    }

    /**
     * <p>
     * The screen viewing URL.
     * </p>
     *
     * @param screenViewingUrl The screen viewing URL.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public MediaPlacement withScreenViewingUrl(String screenViewingUrl) {
        setScreenViewingUrl(screenViewingUrl);
        return this;
    }

    /**
     * <p>
     * The signaling URL.
     * </p>
     *
     * @return The signaling URL.
     */

    public String getSignalingUrl() {
        return this.signalingUrl;
    }

    /**
     * <p>
     * The signaling URL.
     * </p>
     *
     * @param signalingUrl The signaling URL.
     */

    public void setSignalingUrl(String signalingUrl) {
        this.signalingUrl = signalingUrl;
    }

    /**
     * <p>
     * The signaling URL.
     * </p>
     *
     * @param signalingUrl The signaling URL.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public MediaPlacement withSignalingUrl(String signalingUrl) {
        setSignalingUrl(signalingUrl);
        return this;
    }

    /**
     * <p>
     * The turn control URL.
     * </p>
     *
     * @return The turn control URL.
     */

    public String getTurnControlUrl() {
        return this.turnControlUrl;
    }

    /**
     * <p>
     * The turn control URL.
     * </p>
     *
     * @param turnControlUrl The turn control URL.
     */

    public void setTurnControlUrl(String turnControlUrl) {
        this.turnControlUrl = turnControlUrl;
    }

    /**
     * <p>
     * The turn control URL.
     * </p>
     *
     * @param turnControlUrl The turn control URL.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public MediaPlacement withTurnControlUrl(String turnControlUrl) {
        setTurnControlUrl(turnControlUrl);
        return this;
    }

    /**
     * Returns a string representation of this object. This is useful for testing and debugging. Sensitive data will be
     * redacted from this string using a placeholder value.
     *
     * @return A string representation of this object.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getAudioHostUrl() != null) {
            sb.append("AudioHostUrl: ").append(getAudioHostUrl()).append(",");
        }
        if (getAudioFallbackUrl() != null) {
            sb.append("AudioFallbackUrl: ").append(getAudioFallbackUrl()).append(",");
        }
        if (getScreenDataUrl() != null) {
            sb.append("ScreenDataUrl: ").append(getScreenDataUrl()).append(",");
        }
        if (getScreenSharingUrl() != null) {
            sb.append("ScreenSharingUrl: ").append(getScreenSharingUrl()).append(",");
        }
        if (getScreenViewingUrl() != null) {
            sb.append("ScreenViewingUrl: ").append(getScreenViewingUrl()).append(",");
        }
        if (getSignalingUrl() != null) {
            sb.append("SignalingUrl: ").append(getSignalingUrl()).append(",");
        }
        if (getTurnControlUrl() != null) {
            sb.append("TurnControlUrl: ").append(getTurnControlUrl());
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (obj instanceof MediaPlacement == false) {
            return false;
        }
        MediaPlacement other = (MediaPlacement) obj;
        if (other.getAudioHostUrl() == null ^ this.getAudioHostUrl() == null) {
            return false;
        }
        if (other.getAudioHostUrl() != null && other.getAudioHostUrl().equals(this.getAudioHostUrl()) == false) {
            return false;
        }
        if (other.getAudioFallbackUrl() == null ^ this.getAudioFallbackUrl() == null) {
            return false;
        }
        if (other.getAudioFallbackUrl() != null && other.getAudioFallbackUrl().equals(this.getAudioFallbackUrl()) == false) {
            return false;
        }
        if (other.getScreenDataUrl() == null ^ this.getScreenDataUrl() == null) {
            return false;
        }
        if (other.getScreenDataUrl() != null && other.getScreenDataUrl().equals(this.getScreenDataUrl()) == false) {
            return false;
        }
        if (other.getScreenSharingUrl() == null ^ this.getScreenSharingUrl() == null) {
            return false;
        }
        if (other.getScreenSharingUrl() != null && other.getScreenSharingUrl().equals(this.getScreenSharingUrl()) == false) {
            return false;
        }
        if (other.getScreenViewingUrl() == null ^ this.getScreenViewingUrl() == null) {
            return false;
        }
        if (other.getScreenViewingUrl() != null && other.getScreenViewingUrl().equals(this.getScreenViewingUrl()) == false) {
            return false;
        }
        if (other.getSignalingUrl() == null ^ this.getSignalingUrl() == null) {
            return false;
        }
        if (other.getSignalingUrl() != null && other.getSignalingUrl().equals(this.getSignalingUrl()) == false) {
            return false;
        }
        if (other.getTurnControlUrl() == null ^ this.getTurnControlUrl() == null) {
            return false;
        }
        if (other.getTurnControlUrl() != null && other.getTurnControlUrl().equals(this.getTurnControlUrl()) == false) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getAudioHostUrl() == null) ? 0 : getAudioHostUrl().hashCode());
        hashCode = prime * hashCode + ((getAudioFallbackUrl() == null) ? 0 : getAudioFallbackUrl().hashCode());
        hashCode = prime * hashCode + ((getScreenDataUrl() == null) ? 0 : getScreenDataUrl().hashCode());
        hashCode = prime * hashCode + ((getScreenSharingUrl() == null) ? 0 : getScreenSharingUrl().hashCode());
        hashCode = prime * hashCode + ((getScreenViewingUrl() == null) ? 0 : getScreenViewingUrl().hashCode());
        hashCode = prime * hashCode + ((getSignalingUrl() == null) ? 0 : getSignalingUrl().hashCode());
        hashCode = prime * hashCode + ((getTurnControlUrl() == null) ? 0 : getTurnControlUrl().hashCode());
        return hashCode;
    }

    @Override
    public MediaPlacement clone() {
        try {
            return (MediaPlacement) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() " + "even though we're Cloneable!", e);
        }
    }
}