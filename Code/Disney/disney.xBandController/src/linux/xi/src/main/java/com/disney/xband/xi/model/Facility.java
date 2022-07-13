package com.disney.xband.xi.model;

public class Facility {

    private int facilityId;
    private int parkId;
    private String name;
    private String shortName;
    private String facilityConfiguration;
    private int facilityConfigurationId;

    public String getFacilityConfiguration() {
        return facilityConfiguration;
    }

    public void setFacilityConfiguration(String facilityConfiguration) {
        this.facilityConfiguration = facilityConfiguration;
    }


    public int getFacilityConfigurationId() {
        return facilityConfigurationId;
    }

    public void setFacilityConfigurationId(int facilityConfigurationId) {
        this.facilityConfigurationId = facilityConfigurationId;
    }

    public Facility()
    {
    }

    public Facility(int facilityId, int parkId, String fName,
                    String fSName, String fConfiguration,
                    int iFConfiguration) {
        this.facilityId = facilityId;
        this.name = fName;
        this.shortName =fSName;
        this.parkId = parkId;
        this.facilityConfiguration = fConfiguration;
        this.facilityConfigurationId = iFConfiguration;
    }

    public int getParkId() {
        return parkId;
    }

    public void setParkId(int parkId) {
        this.parkId = parkId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {

        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {

        return name;
    }

    public void setFacilityId(int facilityId) {

        this.facilityId = facilityId;
    }



}
