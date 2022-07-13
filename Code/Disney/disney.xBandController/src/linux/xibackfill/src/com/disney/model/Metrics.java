package com.disney.model;

/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 6/25/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Metrics {

    public Metrics()
    {}


    private int looksupsCompleted;
    private int SFOneViewLookups;
    private int SFOneViewLookupAttempts;

    private int IDMSLookups;
    private int IDMSLookupAttempts;

    private int staleRecords;

    private long averageIDMSLookupTime;
    private long averageSFOVLookupTime;
    private long averageGXPLookupTime;
    private long averageGXPCacheLookupTime;


    private long gxpInCache;


    public long getAverageGXPCacheLookupTime()
    {
        return averageGXPCacheLookupTime;
    }

    public void setAverageGXPCacheLookupTime(long averageGXPCacheLookupTime)
    {
        this.averageGXPCacheLookupTime = averageGXPCacheLookupTime;
    }




    public long getGxpInCache()
    {
        return gxpInCache;
    }

    public void setGxpInCache(long gxpInCache)
    {
        this.gxpInCache = gxpInCache;
    }


    public int getLooksupsCompleted() {
        return looksupsCompleted;
    }

    public void setLooksupsCompleted(int looksupsCompleted) {
        this.looksupsCompleted = looksupsCompleted;
    }

    public int getSFOneViewLooksups() {
        return SFOneViewLookups;
    }

    public void setSFOneViewLooksups(int SFOneViewLookups) {
        this.SFOneViewLookups = SFOneViewLookups;
    }

    public int getIDMSLookups() {
        return IDMSLookups;
    }

    public void setIDMSLookups(int IDMSLookups) {
        this.IDMSLookups = IDMSLookups;
    }

    public int getStaleRecords() {
        return staleRecords;
    }

    public void setStaleRecords(int staleRecords) {
        this.staleRecords = staleRecords;
    }

    public long getAverageIDMSLookupTime() {
        return averageIDMSLookupTime;
    }

    public void setAverageIDMSLookupTime(long averageIDMSLookupTime) {
        this.averageIDMSLookupTime = averageIDMSLookupTime;
    }

    public long getAverageSFOVLookupTime() {
        return averageSFOVLookupTime;
    }

    public void setAverageSFOVLookupTime(long averageSFOVLookupTime) {
        this.averageSFOVLookupTime = averageSFOVLookupTime;
    }

    public long getAverageGXPLookupTime() {
        return averageGXPLookupTime;
    }

    public void setAverageGXPLookupTime(long averageGXPLookupTime) {
        this.averageGXPLookupTime = averageGXPLookupTime;
    }

    public int getSFOneViewLookupAttempts() {
        return SFOneViewLookupAttempts;
    }

    public void setSFOneViewLookupAttempts(int SFOneViewLookupAttempts) {
        this.SFOneViewLookupAttempts = SFOneViewLookupAttempts;
    }

    public int getIDMSLookupAttempts() {
        return IDMSLookupAttempts;
    }

    public void setIDMSLookupAttempts(int IDMSLookupAttempts) {
        this.IDMSLookupAttempts = IDMSLookupAttempts;
    }
}
