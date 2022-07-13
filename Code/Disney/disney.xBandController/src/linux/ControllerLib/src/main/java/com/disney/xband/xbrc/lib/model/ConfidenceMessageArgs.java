package com.disney.xband.xbrc.lib.model;

public class ConfidenceMessageArgs extends MessageArgs {
    private int confidence;

    public ConfidenceMessageArgs(EventAggregate ev, boolean bxPass,
                                 String GuestID, String PublicID, String LinkID, String LinkIDType, String BandType, int confidence) {
    	super(ev, bxPass, GuestID, PublicID, LinkID, LinkIDType, BandType);
    	this.confidence = confidence;
    }

    public int getConfidence() {
        return confidence;
    }
}
