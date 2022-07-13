package com.disney.xband.xbrc.lib.entity;


public class EventsLocationConfig {

    private Long locationId;
    private int csecAbandonmentTimeout = 3000;
    private long castMemberDetectDelay = 1000;
    private long puckDetectDelay = 1000;
    private long guestDetectDelay = 10000;
    private int confidenceDelta = 10;
    private int locationEventRatio = 70;
    private long msecChirpRate = 1000;
    private boolean sendTapToJMS = false;
    private boolean enableConfProcessing = false;
    private boolean sendConfToJMS = true;
    private boolean sendDeltaConfToJMS = false;
    private boolean sendTapToHTTP = false;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public int getAbandonmentTimeout()
    {
        return csecAbandonmentTimeout;
    }

    public void setAbandonmentTimeout(int csecAbandonmentTimeout)
    {
        this.csecAbandonmentTimeout = csecAbandonmentTimeout;
    }

    public long getCastMemberDetectDelay() {
        return castMemberDetectDelay;
    }

    public void setCastMemberDetectDelay( long castMemberDetectDelay ) {
        this.castMemberDetectDelay = castMemberDetectDelay;
    }

    public long getPuckDetectDelay() {
        return puckDetectDelay;
    }

    public void setPuckDetectDelay( long puckDetectDelay ) {
        this.puckDetectDelay = puckDetectDelay;
    }

    public long getGuestDetectDelay() {
        return guestDetectDelay;
    }

    public void setGuestDetectDelay( long guestDetectDelay ) {
        this.guestDetectDelay = guestDetectDelay;
    }

    public int getConfidenceDelta() {
        return confidenceDelta;
    }

    public void setConfidenceDelta( int confidenceDelta ) {
        this.confidenceDelta = confidenceDelta;
    }

    public int getLocationEventRatio() {
        return locationEventRatio;
    }

    public void setLocationEventRatio( int locationEventRatio ) {
        this.locationEventRatio = locationEventRatio;
    }

    public long getChirpRate() {
        return msecChirpRate;
    }

    public void setChirpRate( long msecChirpRate ) {
        this.msecChirpRate = msecChirpRate;
    }

	public boolean isSendTapToJMS() {
		return sendTapToJMS;
	}

	public boolean isEnableConfProcessing() {
		return enableConfProcessing;
	}

	public boolean isSendConfToJMS() {
		return sendConfToJMS;
	}

	public boolean isSendDeltaConfToJMS() {
		return sendDeltaConfToJMS;
	}

	public void setSendTapToJMS(boolean sendTapToJMS) {
		this.sendTapToJMS = sendTapToJMS;
	}

	public void setEnableConfProcessing(boolean enableConfProcessing) {
		this.enableConfProcessing = enableConfProcessing;
	}

	public void setSendConfToJMS(boolean sendConfToJMS) {
		this.sendConfToJMS = sendConfToJMS;
	}

	public void setSendDeltaConfToJMS(boolean sendDeltaConfToJMS) {
		this.sendDeltaConfToJMS = sendDeltaConfToJMS;
	}

	public boolean isSendTapToHTTP() {
		return sendTapToHTTP;
	}

	public void setSendTapToHTTP(boolean sendTapToHTTP) {
		this.sendTapToHTTP = sendTapToHTTP;
	}
}
