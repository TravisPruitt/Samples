
package com.disney.xband.ac.lib.auth.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.disney.xband.ac.server.auth.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _XbUsers_QNAME = new QName("", "XbUsers");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.disney.xband.ac.server.auth.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XbUsersType }
     * 
     */
    public XbUsersType createXbUsersType() {
        return new XbUsersType();
    }

    /**
     * Create an instance of {@link XbUserType }
     * 
     */
    public XbUserType createXbUserType() {
        return new XbUserType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XbUsersType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "XbUsers")
    public JAXBElement<XbUsersType> createXbUsers(XbUsersType value) {
        return new JAXBElement<XbUsersType>(_XbUsers_QNAME, XbUsersType.class, null, value);
    }

}
