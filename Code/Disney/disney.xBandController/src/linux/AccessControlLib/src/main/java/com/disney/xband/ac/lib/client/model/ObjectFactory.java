
package com.disney.xband.ac.lib.client.model;

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

    private final static QName _AcModel_QNAME = new QName("", "AcModel");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.disney.xband.ac.server.auth.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Application }
     * 
     */
    public Application createApplication() {
        return new Application();
    }

    /**
     * Create an instance of {@link AcModel }
     * 
     */
    public AcModel createAcModel() {
        return new AcModel();
    }

    /**
     * Create an instance of {@link UrlPatternType }
     * 
     */
    public UrlPatternType createUrlPatternType() {
        return new UrlPatternType();
    }

    /**
     * Create an instance of {@link Asset }
     * 
     */
    public Asset createAsset() {
        return new Asset();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "AcModel")
    public JAXBElement<AcModel> createAcModel(AcModel value) {
        return new JAXBElement<AcModel>(_AcModel_QNAME, AcModel.class, null, value);
    }

}
