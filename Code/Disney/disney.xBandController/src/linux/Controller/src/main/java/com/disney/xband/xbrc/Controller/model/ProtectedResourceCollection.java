package com.disney.xband.xbrc.Controller.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/13/13
 * Time: 11:29 PM
 */
@XmlRootElement(name = "resourceCollection")
public class ProtectedResourceCollection {
    private boolean accessControlDisabled;
    private List<ProtectedResource> resource;

    @XmlAttribute
    public boolean getAccessControlDisabled() {
        return accessControlDisabled;
    }

    public void setAccessControlDisabled(boolean accessControlDisabled) {
        this.accessControlDisabled = accessControlDisabled;
    }

    public List<ProtectedResource> getResource() {
        return resource;
    }

    public void setResource(List<ProtectedResource> resource) {
        this.resource = resource;
    }
}
