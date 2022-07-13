
package com.disney.xband.ac.lib.auth.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for XbUsersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XbUsersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DefaultRole" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="XbUser" type="{}XbUserType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name="XbUsers")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XbUsersType", propOrder = {
    "defaultRole",
    "xbUser"
})
public class XbUsersType {

    @XmlElement(name = "DefaultRole", required = true)
    protected String defaultRole;
    @XmlElement(name = "XbUser")
    protected List<XbUserType> xbUser;

    /**
     * Gets the value of the defaultRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultRole() {
        return defaultRole;
    }

    /**
     * Sets the value of the defaultRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultRole(String value) {
        this.defaultRole = value;
    }

    /**
     * Gets the value of the xbUser property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xbUser property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXbUser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XbUserType }
     * 
     * 
     */
    public List<XbUserType> getXbUser() {
        if (xbUser == null) {
            xbUser = new ArrayList<XbUserType>();
        }
        return this.xbUser;
    }

}
