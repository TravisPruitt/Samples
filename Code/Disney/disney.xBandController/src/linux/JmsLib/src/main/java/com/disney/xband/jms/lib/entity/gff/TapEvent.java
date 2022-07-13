//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.26 at 12:57:42 PM PDT 
//

package com.disney.xband.jms.lib.entity.gff;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventTypeId" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="event" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="facilityId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xBandId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="timeStampGMT" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="orderNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gxpLinkId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="terminal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lane" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="xPassId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="partySize" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="sourceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isBlueLaned" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
/*
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "eventtypeid",
    "event",
    "facilityid",
    "xbandid",
    "source",
    "timestamp",
    "timestampgmt",
    "ordernumber",
    "gxplinkid",
    "terminal",
    "lane",
    "xpassid",
    "partysize",
    "sourcetype",
    "isbluelaned"
})*/
@XmlRootElement(name = "tapevent")
public class TapEvent {

    private BigInteger eventTypeId;
    private String event;
    private String facilityId;



    private String facilityOneSourceId;
    private String xBandId;
    private String source;
    private String timeStamp;
    private String timeStampGMT;
    private String orderNumber;
    private String gxpLinkId;
    private String terminal;
    private String lane;
    private String xPassId;
    private boolean isBlueLaned;
    private BigInteger partySize;
    private String sourceType;

    public TapEvent() {}

    @XmlElement(name = "eventtypeid")
    public BigInteger getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(BigInteger value) {
        this.eventTypeId = value;
    }

    @XmlElement(name = "event")
    public String getEvent() {
        return event;
    }

    public void setEvent(String value) {
        this.event = value;
    }

    @XmlElement(name = "facilityonesourceid")
    public String getFacilityOneSourceId() {
        return facilityOneSourceId;
    }

    public void setFacilityOneSourceId(String facilityOneSourceId) {
        this.facilityOneSourceId = facilityOneSourceId;
    }


    @XmlElement(name = "facilityid")
    public String getFacilityId() {
        if(facilityId == null){
            return facilityOneSourceId;
        }
        return facilityId;
    }

    public void setFacilityId(String value) {
        this.facilityId = value;
    }

    @XmlElement(name = "xbandid")
    public String getXBandId() {
        return xBandId;
    }

    public void setXBandId(String value) {
        this.xBandId = value;
    }

    @XmlElement(name = "source")
    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    @XmlElement(name = "timestamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String value) {
        this.timeStamp = value;
    }

    @XmlElement(name = "timestamp")
    public String getTimeStampGMT() {
        return timeStampGMT;
    }


    public void setTimeStampGMT(String value) {
        this.timeStampGMT = value;
    }

    @XmlElement(name = "ordernumber")
    public String getOrderNumber() {
        return orderNumber;
    }


    public void setOrderNumber(String value) {
        this.orderNumber = value;
    }

    @XmlElement(name = "gxplinkid")
    public String getGxpLinkId() {
        return gxpLinkId;
    }

    public void setGxpLinkId(String value) {
        this.gxpLinkId = value;
    }

    @XmlElement(name = "terminal")
    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String value) {
        this.terminal = value;
    }

    @XmlElement(name = "lane")
    public String getLane() {
        return lane;
    }


    public void setLane(String value) {
        this.lane = value;
    }

    @XmlElement(name =  "xpassid")
    public String getXPassId() {
        return xPassId;
    }

    public void setXPassId(String value) {
        this.xPassId = value;
    }

    @XmlElement(name = "partysize")
    public BigInteger getPartySize() {
        return partySize;
    }

    public void setPartySize(BigInteger value) {
        this.partySize = value;
    }

    @XmlElement(name = "sourcetype")
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String value) {
        this.sourceType = value;
    }

    @XmlElement(name = "isbluelaned")
    public boolean isBlueLaned() {
        return isBlueLaned;
    }

    public void setBlueLaned(boolean value) {
        this.isBlueLaned = value;
    }

}
