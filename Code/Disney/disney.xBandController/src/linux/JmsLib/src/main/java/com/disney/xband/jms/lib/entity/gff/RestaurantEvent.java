//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.26 at 12:57:25 PM PDT 
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
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="timeStampGMT" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="openingTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="closingTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceURI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tableOccupancy" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="available" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="occupied" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="dirty" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="closed" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="seatOccupancy" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="totalSeats" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="occupied" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlRootElement(name = "restaurantevent")
public class RestaurantEvent {

    private BigInteger eventTypeId;
    private String event;
    private String facilityId;
    private String source;
    private String timeStamp;
    private String timeStampGMT;
    private String openingTime;
    private String closingTime;
    private String serviceURI;
    private TableOccupancy tableOccupancy;
    private SeatOccupancy seatOccupancy;
    private String facilityOneSourceId;

    public RestaurantEvent() {}

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


    @XmlElement(name = "facilityid")
    public String getFacilityId() {
        if(facilityId == null) {
            return facilityOneSourceId;
        }
        return facilityId;
    }

    public void setFacilityId(String value) {
        this.facilityId = value;
    }

    @XmlElement(name = "facilityonesourceid")
    public String getFacilityOneSourceId() {
        return facilityOneSourceId;
    }

    public void setFacilityOneSourceId(String value) {
        this.facilityOneSourceId = value;
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


    @XmlElement(name = "timestampgmt")
    public String getTimeStampGMT() {
        return timeStampGMT;
    }


    public void setTimeStampGMT(String value) {
        this.timeStampGMT = value;
    }


    @XmlElement(name = "openingtime")
    public String getOpeningTime() {
        return openingTime;
    }


    public void setOpeningTime(String value) {
        this.openingTime = value;
    }


    @XmlElement(name = "closingtime")
    public String getClosingTime() {
        return closingTime;
    }


    public void setClosingTime(String value) {
        this.closingTime = value;
    }


    @XmlElement(name = "serviceuri")
    public String getServiceURI() {
        return serviceURI;
    }


    public void setServiceURI(String value) {
        this.serviceURI = value;
    }

    @XmlElement(name = "tableoccupancy")
    public RestaurantEvent.TableOccupancy getTableOccupancy() {
        return tableOccupancy;
    }


    public void setTableOccupancy(RestaurantEvent.TableOccupancy value) {
        this.tableOccupancy = value;
    }

    @XmlElement(name = "seatoccupancy")
    public RestaurantEvent.SeatOccupancy getSeatOccupancy() {
        return seatOccupancy;
    }


    public void setSeatOccupancy(RestaurantEvent.SeatOccupancy value) {
        this.seatOccupancy = value;
    }


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
     *         &lt;element name="totalSeats" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="occupied" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    public static class SeatOccupancy {

        private int totalSeats;
        private int occupied;

        @XmlElement(name = "totalseats")
        public int getTotalSeats() {
            return totalSeats;
        }

        public void setTotalSeats(int value) {
            this.totalSeats = value;
        }

        @XmlElement(name = "occupied")
        public int getOccupied() {
            return occupied;
        }

        public void setOccupied(int value) {
            this.occupied = value;
        }

    }


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
     *         &lt;element name="available" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="occupied" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="dirty" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="closed" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    public static class TableOccupancy {
        private int available;
        private int occupied;
        private int dirty;
        private int closed;

        @XmlElement(name="available")
        public int getAvailable() {
            return available;
        }


        public void setAvailable(int value) {
            this.available = value;
        }


        @XmlElement(name="occupied")
        public int getOccupied() {
            return occupied;
        }


        public void setOccupied(int value) {
            this.occupied = value;
        }


        @XmlElement(name="dirty")
        public int getDirty() {
            return dirty;
        }


        public void setDirty(int value) {
            this.dirty = value;
        }


        @XmlElement(name="closed")
        public int getClosed() {
            return closed;
        }


        public void setClosed(int value) {
            this.closed = value;
        }

    }

}
