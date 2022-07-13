package com.disney.xband.jms.lib.pxc;

import com.disney.xband.jms.lib.entity.common.GuestIdentifier;
import com.disney.xband.jms.lib.entity.xbms.Owner;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/*
{
  "owner":{},
  "type":"combination",
  "timestamp":"2013-09-12T17:56:13Z",
   "resultingGuestIdentifiers":[],
   "previousGuestIdentifiers":[]
}
*/

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 9/13/13
 * Time: 4:59 PM
 */
@XmlRootElement
@XmlType(propOrder = {"owner", "type", "timestamp", "resultingGuestIdentifiers", "previousGuestIdentifiers"})
public class Combination {
    private Owner owner;
    private String type;
    private String timestamp;
    private List<GuestIdentifier> resultingGuestIdentifiers;
    private List<GuestIdentifier> previousGuestIdentifiers;

    @XmlElement(name = "owner")
    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "setTimestamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @XmlElement(name = "getResultingGuestIdentifiers")
    public List<GuestIdentifier> getResultingGuestIdentifiers() {
        return resultingGuestIdentifiers;
    }

    public void setResultingGuestIdentifiers(List<GuestIdentifier> resultingGuestIdentifiers) {
        this.resultingGuestIdentifiers = resultingGuestIdentifiers;
    }

    @XmlElement(name = "getPreviousGuestIdentifiers")
    public List<GuestIdentifier> getPreviousGuestIdentifiers() {
        return previousGuestIdentifiers;
    }

    public void setPreviousGuestIdentifiers(List<GuestIdentifier> previousGuestIdentifiers) {
        this.previousGuestIdentifiers = previousGuestIdentifiers;
    }
}
