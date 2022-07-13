package com.disney.model;

import java.util.List;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 6/11/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuestLookup {

    private String businessEventId;
    private String referenceId;
    private String guestId;
    private String gxpId;
    private String xmbsId;
    private Date lastRetryDateTime;
    private Date eventStartDate;
    private int retryCount;
    private boolean stale;
    private boolean savedGXP;

    private List<String> publicId;


    public GuestLookup()
    {
        businessEventId = "";
        referenceId = "";
        guestId = "";
        gxpId = "";
        xmbsId = "";
        publicId = null;
        retryCount = 0;
        lastRetryDateTime = new Date();  // This marks the record with a DateTime of NOW.
        stale = false;
        savedGXP = false;
    }


    public boolean getSavedGXP()
    {
        return savedGXP;
    }

    public void setSavedGXP(boolean savedGXP)
    {
        this.savedGXP = savedGXP;
    }

    public boolean getStale()
    {
        return stale;
    }

    public void setStale(boolean stale)
    {
        this.stale = stale;
    }


    public int getRetryCount()
    {
        return retryCount;
    }

    public void setRetryCount(int retryCount)
    {
        this.retryCount = retryCount;
    }

    public Date getLastRetryDateTime()
    {
        return lastRetryDateTime;
    }

    public void setLastRetryDateTime(Date lastRetryDateTime)
    {
       this.lastRetryDateTime = lastRetryDateTime;
    }


    public List<String> getPublicId()
    {
        return publicId;
    }

    public void setPublicId(List<String> publicId)
    {
        this.publicId = publicId;
    }

    public String getBusinessEventId() {
        return businessEventId;
    }

    public void setBusinessEventId(String businessEventId) {
        this.businessEventId = businessEventId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getGxpId() {
        return gxpId;
    }

    public void setGxpId(String gxpId) {
        this.gxpId = gxpId;
    }

    public String getXmbsId() {
        return xmbsId;
    }

    public void setXmbsId(String xmbsId) {
        this.xmbsId = xmbsId;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }
}
