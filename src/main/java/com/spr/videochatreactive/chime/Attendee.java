package com.spr.videochatreactive.chime;

import java.io.Serializable;

public class Attendee implements Serializable, Cloneable {

    /**
     * <p>
     * The Amazon Chime SDK external user ID. An idempotency token. Links the attendee to an identity managed by a
     * builder application.
     * </p>
     */
    private String externalUserId;
    /**
     * <p>
     * The Amazon Chime SDK attendee ID.
     * </p>
     */
    private String attendeeId;
    /**
     * <p>
     * The join token used by the Amazon Chime SDK attendee.
     * </p>
     */
    private String joinToken;

    /**
     * <p>
     * The Amazon Chime SDK external user ID. An idempotency token. Links the attendee to an identity managed by a
     * builder application.
     * </p>
     *
     * @param externalUserId The Amazon Chime SDK external user ID. An idempotency token. Links the attendee to an identity managed by
     *                       a builder application.
     */

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    /**
     * <p>
     * The Amazon Chime SDK external user ID. An idempotency token. Links the attendee to an identity managed by a
     * builder application.
     * </p>
     *
     * @return The Amazon Chime SDK external user ID. An idempotency token. Links the attendee to an identity managed by
     * a builder application.
     */

    public String getExternalUserId() {
        return this.externalUserId;
    }

    /**
     * <p>
     * The Amazon Chime SDK external user ID. An idempotency token. Links the attendee to an identity managed by a
     * builder application.
     * </p>
     *
     * @param externalUserId The Amazon Chime SDK external user ID. An idempotency token. Links the attendee to an identity managed by
     *                       a builder application.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public Attendee withExternalUserId(String externalUserId) {
        setExternalUserId(externalUserId);
        return this;
    }

    /**
     * <p>
     * The Amazon Chime SDK attendee ID.
     * </p>
     *
     * @param attendeeId The Amazon Chime SDK attendee ID.
     */

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    /**
     * <p>
     * The Amazon Chime SDK attendee ID.
     * </p>
     *
     * @return The Amazon Chime SDK attendee ID.
     */

    public String getAttendeeId() {
        return this.attendeeId;
    }

    /**
     * <p>
     * The Amazon Chime SDK attendee ID.
     * </p>
     *
     * @param attendeeId The Amazon Chime SDK attendee ID.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public Attendee withAttendeeId(String attendeeId) {
        setAttendeeId(attendeeId);
        return this;
    }

    /**
     * <p>
     * The join token used by the Amazon Chime SDK attendee.
     * </p>
     *
     * @param joinToken The join token used by the Amazon Chime SDK attendee.
     */

    public void setJoinToken(String joinToken) {
        this.joinToken = joinToken;
    }

    /**
     * <p>
     * The join token used by the Amazon Chime SDK attendee.
     * </p>
     *
     * @return The join token used by the Amazon Chime SDK attendee.
     */

    public String getJoinToken() {
        return this.joinToken;
    }

    /**
     * <p>
     * The join token used by the Amazon Chime SDK attendee.
     * </p>
     *
     * @param joinToken The join token used by the Amazon Chime SDK attendee.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public Attendee withJoinToken(String joinToken) {
        setJoinToken(joinToken);
        return this;
    }

    /**
     * Returns a string representation of this object. This is useful for testing and debugging. Sensitive data will be
     * redacted from this string using a placeholder value.
     *
     * @return A string representation of this object.
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getExternalUserId() != null)
            sb.append("ExternalUserId: ").append("***Sensitive Data Redacted***").append(",");
        if (getAttendeeId() != null)
            sb.append("AttendeeId: ").append(getAttendeeId()).append(",");
        if (getJoinToken() != null)
            sb.append("JoinToken: ").append("***Sensitive Data Redacted***");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof Attendee == false)
            return false;
        Attendee other = (Attendee) obj;
        if (other.getExternalUserId() == null ^ this.getExternalUserId() == null)
            return false;
        if (other.getExternalUserId() != null && other.getExternalUserId().equals(this.getExternalUserId()) == false)
            return false;
        if (other.getAttendeeId() == null ^ this.getAttendeeId() == null)
            return false;
        if (other.getAttendeeId() != null && other.getAttendeeId().equals(this.getAttendeeId()) == false)
            return false;
        if (other.getJoinToken() == null ^ this.getJoinToken() == null)
            return false;
        if (other.getJoinToken() != null && other.getJoinToken().equals(this.getJoinToken()) == false)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getExternalUserId() == null) ? 0 : getExternalUserId().hashCode());
        hashCode = prime * hashCode + ((getAttendeeId() == null) ? 0 : getAttendeeId().hashCode());
        hashCode = prime * hashCode + ((getJoinToken() == null) ? 0 : getJoinToken().hashCode());
        return hashCode;
    }

    @Override
    public Attendee clone() {
        try {
            return (Attendee) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() " + "even though we're Cloneable!", e);
        }
    }
}