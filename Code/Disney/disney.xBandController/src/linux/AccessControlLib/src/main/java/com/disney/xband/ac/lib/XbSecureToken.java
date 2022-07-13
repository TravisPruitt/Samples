package com.disney.xband.ac.lib;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/18/12
 * Time: 5:22 PM
 */
@XmlRootElement(name = "AuthToken")
public class XbSecureToken implements Serializable {

    public enum AuthType {NONE, FORM, HTTP_BASIC, X509_CERT}

    private String logonName;
    private String displayName;
    private AuthType authType;
    private boolean isAuthenticated;
    private long authTime;
    private String sid;
    private String initSid;
    private String authData;
    private List<String> roles;
    private List<String> entities;
    private List<String> abilities;
    private String tokenRefId;
    private String omniUser;
    private String omniPw;
    private String pwHash;
    private boolean offLineMode;

    public XbSecureToken() {
        this.authType = AuthType.NONE;
        this.roles = new ArrayList(0);
        this.entities = new ArrayList(0);
        this.abilities = new ArrayList(0);
    }

    public static XbSecureToken fromJson(final String s) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
		    return mapper.readValue(s, XbSecureToken.class);
	    }
        catch (Exception e) {
		    // e.printStackTrace();
            System.out.println(e);
	    }

        return null;
    }

    public static XbSecureToken fromJson(final InputStream s) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
		    return mapper.readValue(s, XbSecureToken.class);
	    }
        catch (Exception e) {
		    // e.printStackTrace();
            // System.out.println(e);
	    }

        return null;
    }

    public static String encode(XbSecureToken token) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = null;

        try {
            o = new ObjectOutputStream(b);
            o.writeObject(token);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(o != null) {
                try {
                    o.close();
                }
                catch(Exception ignore) {
                }
            }
        }

        final String encoded = Base64.encodeBase64String(b.toByteArray());
        // System.out.println("After encode: " + encoded);
        return encoded;
    }

    public static XbSecureToken decode(String token) {
        // System.out.println("Before decode: " + token);

        ObjectInputStream o = null;

        try {
            ByteArrayInputStream b = new ByteArrayInputStream(Base64.decodeBase64(token));
            o = new ObjectInputStream(b);

            return (XbSecureToken) o.readObject();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(o != null) {
                try {
                    o.close();
                }
                catch(Exception ignore) {
                }
            }
        }
    }

    /*
    public static String encode(XbSecureToken token) {
        try {
            return Base64.encodeBase64String(XbUtils.objectToXmlString(token, XbSecureToken.class).getBytes());
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static XbSecureToken decode(String token) {
        try {
            return (XbSecureToken) XbUtils.objectFromXmlString(new String(Base64.decodeBase64(token)), XbSecureToken.class);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    */

    public String getLogonName() {
        return this.logonName;
    }

    public void setLogonName(String logonName) {
        this.logonName = logonName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AuthType getAuthType() {
        return this.authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
    }

    public long getAuthTime() {
        return this.authTime;
    }

    public void setAuthTime(long authTime) {
        this.authTime = authTime;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getOmniUser() {
        return this.omniUser;
    }

    public void setOmniUser(String val) {
        this.omniUser = val;
    }

    public String getOmniPw() {
        return this.omniPw;
    }

    public void setOmniPw(String val) {
        this.omniPw = val;
    }

    @JsonIgnore
    @XmlTransient
    public String getAuthData() {
        return this.authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public String getTokenRefId() {
        if(this.tokenRefId == null) {
            return this.sid;
        }
        else {
            return this.tokenRefId;
        }
    }

    public void setTokenRefId(String refId) {
        this.tokenRefId = refId;
    }

    @XmlElement(name = "roles")
    public List<String> getRoles() {
        return this.roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @XmlElement(name = "functionalAbilities")
    public List<String> getFunctionalAbilities() {
        return this.abilities;
    }

    public void setFunctionalAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    @XmlElement(name = "securedEntities")
    public List<String> getSecuredEntities() {
        return this.entities;
    }

    public void setSecuredEntities(List<String> resources) {
        this.entities = resources;
    }

    public String getInitSid() {
        return initSid;
    }

    public void setInitSid(String initSid) {
        this.initSid = initSid;
    }

    @JsonIgnore
    public String getPwHash() {
        return pwHash;
    }

    public void setPwHash(String pwHash) {
        this.pwHash = pwHash;
    }

    @JsonIgnore
    public boolean isOffLineMode() {
        return offLineMode;
    }

    public void setOffLineMode(boolean offLineMode) {
        this.offLineMode = offLineMode;
    }

    public String toJson() {
        final ObjectMapper mapper = new ObjectMapper();

        try {
		    return mapper.writeValueAsString(this);
	    }
        catch (Exception e) {
		    // e.printStackTrace();
	    }

        return null;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("logonName: ");
        sb.append(this.logonName);
        sb.append("\n");

        sb.append("displayName: ");
        sb.append(this.displayName);
        sb.append("\n");

        sb.append("authTime: ");
        sb.append(this.authTime);
        sb.append("\n");

        sb.append("authType: ");
        sb.append(this.authType);
        sb.append("\n");

        sb.append("sid: ");
        sb.append(this.sid);
        sb.append("\n");

        sb.append("initSid: ");
        sb.append(this.initSid);
        sb.append("\n");

        sb.append("roles: ");

        Iterator<String> it;

        if(this.roles != null) {
            it = this.roles.iterator();

            while(it.hasNext()) {
               sb.append(it.next());
               sb.append(" ");
            }
        }

        sb.append("\n");

        sb.append("abilities: ");

        if(this.abilities != null) {
            it = this.abilities.iterator();

            while(it.hasNext()) {
                sb.append(it.next());
              sb.append(" ");
          }
        }

        sb.append("\n");

        sb.append("entities:\n");

        if(this.entities != null) {
            it = this.entities.iterator();

            while(it.hasNext()) {
               sb.append("\t");
              sb.append(it.next());
             sb.append("\n");
            }
        }

        return sb.toString();
    }

    /*
    public static void main(String[] args) throws Exception {

        XbSecureToken t = new XbSecureToken();
        t.setAuthenticated(true);
        t.setLogonName("slavam@yahoo.com");
        t.setDisplayName("Vyacheslav Minyailov");
        List<String> roles = new ArrayList<String>(10);
        roles.add("administrator0");
        roles.add("administrator1");
        roles.add("administrator2");
        t.setRoles(roles);
        t.setAuthType(AuthType.FORM);
        t.setAuthTime(System.currentTimeMillis());
        t.setSid("ses12345");

	    System.out.println(t.toJson());

        t = XbSecureToken.fromJson(t.toJson());
        System.out.println(t.getSid());
        System.out.println(t.getRoles().get(0));
        String token = XbSecureToken.encode(t);

        System.out.println("Token length: " + token.length());
        System.out.println(token);
        t = XbSecureToken.decode(token);
        System.out.println(t.getDisplayName());

        //final String str = URLDecoder.decode("rO0ABXNyACVjb20uZGlzbmV5LnhiYW5kLmFjLmxpYi5YYlNlY3VyZVRva2VuKSS9PG5Cp5ICAAdKAAhhdXRoVGltZVoAD2lzQXV0aGVudGljYXRlZEwACGF1dGhUeXBldAAwTGNvbS9kaXNuZXkveGJhbmQvYWMvbGliL1hiU2VjdXJlVG9rZW4kQXV0aFR5cGU7TAALZGlzcGxheU5hbWV0ABJMamF2YS9sYW5nL1N0cmluZztMAAlsb2dvbk5hbWVxAH4AAkwABXJvbGVzdAAQTGphdmEvdXRpbC9MaXN0O0wAA3NpZHEAfgACeHAAAAE4L%2BGJ0AF%2BcgAuY29tLmRpc25leS54YmFuZC5hYy5saWIuWGJTZWN1cmVUb2tlbiRBdXRoVHlwZQAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQABEZPUk10ABRWeWFjaGVzbGF2IE1pbnlhaWxvdnQAEHNsYXZhbUB5YWhvby5jb21zcgATamF2YS51dGlsLkFycmF5TGlzdHiB0h2Zx2GdAwABSQAEc2l6ZXhwAAAAAncEAAAACnQACXhicmNhZG1pbnQACnhicm1zYWRtaW54dAAgQkI5M0E3Q0E5QUM2MUNBRDYyMkJFNURFMUYwMzMxMjE%3D", "UTF-8");
        //System.out.println("STR: " + str);
        //t= XbSecureToken.decode(str);

        System.out.println(t.getSid());

        String s = "rO0ABXNyACVjb20uZGlzbmV5LnhiYW5kLmFjLmxpYi5YYlNlY3VyZVRva2VuIMkBicdfypgCABBKAAhhdXRoVGltZVoAD2lzQXV0aGVudGljYXRlZFoAC29mZkxpbmVNb2RlTAAJYWJpbGl0aWVzdAAQTGphdmEvdXRpbC9MaXN0O0wACGF1dGhEYXRhdAASTGphdmEvbGFuZy9TdHJpbmc7TAAIYXV0aFR5cGV0ADBMY29tL2Rpc25leS94YmFuZC9hYy9saWIvWGJTZWN1cmVUb2tlbiRBdXRoVHlwZTtMAAtkaXNwbGF5TmFtZXEAfgACTAAIZW50aXRpZXNxAH4AAUwAB2luaXRTaWRxAH4AAkwACWxvZ29uTmFtZXEAfgACTAAGb21uaVB3cQB+AAJMAAhvbW5pVXNlcnEAfgACTAAGcHdIYXNocQB+AAJMAAVyb2xlc3EAfgABTAADc2lkcQB+AAJMAAp0b2tlblJlZklkcQB+AAJ4cAAAAT9eVJzHAABzcgATamF2YS51dGlsLkFycmF5TGlzdHiB0h2Zx2GdAwABSQAEc2l6ZXhwAAAAAHcEAAAAAHh0AAhjaGFuZ2VpdH5yAC5jb20uZGlzbmV5LnhiYW5kLmFjLmxpYi5YYlNlY3VyZVRva2VuJEF1dGhUeXBlAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAAKSFRUUF9CQVNJQ3QAEHhjb25uZWN0LXNlcnZpY2VzcQB+AAUAAAAAdwQAAAAAeHBxAH4ADHBwcHNxAH4ABQAAAAF3BAAAAApxAH4ADHhwcA==";
        t = XbSecureToken.decode(s);
        System.out.println(t.getLogonName());
    }
    */
}
