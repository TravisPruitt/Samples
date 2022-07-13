package com.disney.xband.common.lib.audit.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 4/30/13
 * Time: 4:03 PM
 */

/**
 * All events belong to one of two classes: audit or health.
 * Audit events contain security information related to access to xConnect components by different types of users.
 * All user actions will be audited to make it possible to later find out who did what and when. The events will be
 * marked with user Keystone ID and session ID. The latter can be used to correlate user-initiated operations spanning
 * multiple systems. Health events carry data related to the system operational status. They are not associated with
 * specific users and do not have user ID.
 *
 * Event message format:
 *
 * id -        Private event ID local to the system owning the events cache.
 *
 * aid -       Aggregation ID. This ID gets assigned to an event by the event aggregator as it pulls data from a child
 *             node. This ID equals the private ID of the event on the child node.
 *
 * type -      Aggregation ID. This ID gets assigned to an event by the event aggregator as it pulls data from a child
 *             node . This ID equals the private ID of the event on the child node.
 *
 * category -  For audit events is one of these: READ, WRITE, ACCESS, LOGIN, LOGOUT. For health events the category is
 *             application specific.
 *
 * dateTime -  Data and time of the event in the format adopted by WWW: yyyy-MM-dd'T'HH:mm:ss.SSSZ
 *
 * appClass -  Unique application name, e.g. xBRMS, xBRMSUI, xBRC, xBRCUI, xGreeterApp, xI, IDMS, etc.
 *
 * appId    -  Application instance ID, e.g. park ID for xBRMS.
 *
 * host     -  Host name/IP address of the event source.
 *
 * vHost    -  Virtual host name/VIP address of the event source.
 *
 * uid      -  User ID.
 *
 * sid      - HTTP session ID.
 *
 * desc     - Optional description.
 *
 * rid      - Optional resource ID, i.e. property path for audit WRITE events.
 *
 * rData    - New property value for audit WRITE events.
 *
 * client 	- IP address or host name of the server making the request.
 */
@XmlRootElement(name = "AuditEvent")
public class AuditEvent {
    public enum Category {READ, WRITE, ACCESS, STATUS, STATUS_CHANGE, LOGIN, LOGOUT}
    public enum Type 
    {	
    	AUDIT_FAILURE, AUDIT_SUCCESS, FATAL, ERROR, WARN, INFO;
    	
    	public static Type getByOrdinal(int ordinal)
    	{
    		for (Type type: values())
    		{
    			if (type.ordinal() == ordinal)
    				return type;
    		}
    		
    		return INFO;	// default
    	}
    }
    public enum AppClass {XBRC, UI, XBRMS, XBRMSUI, XI, IDMS, JSMLISTENER, GREETER_APP, UNKNOWN}

    private long id;
    private long aid = -1L;
    private String type;
    private String category;
    private String appClass;
    private String appId;
    private String host;
    private String vHost;
    private String uid;
    private String sid;
    private String desc;
    private String rid;
    private String rData;
    private String dateTime;
    private long dateTimeMillis;
    private String sourceTimeZone;
    private String collectorHost;
    private String client;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getAid() {
        return aid;
    }

    public void setAid(final long aid) {
        this.aid = aid;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getCategory() {
        return toEmptyStr(category);
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(final String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAppClass() {
        return appClass;
    }

    public void setAppClass(final String appClass) {
        this.appClass = appClass;
    }

    public String getAppId() {
        return toEmptyStr(appId);
    }

    public void setAppId(final String appId) {
        this.appId = appId;
    }

    public String getHost() {
        return toEmptyStr(host);
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getvHost() {
        return toEmptyStr(vHost);
    }

    public void setvHost(final String vHost) {
        this.vHost = vHost;
    }

    public String getUid() {
        return toEmptyStr(uid);
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getSid() {
        return toEmptyStr(sid);
    }

    public void setSid(final String sid) {
        this.sid = sid;
    }

    public String getDesc() {
        return toEmptyStr(desc);
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    public String getRid() {
        return toEmptyStr(rid);
    }

    public void setRid(final String rid) {
        this.rid = rid;
    }

    public String getrData() {
        return toEmptyStr(rData);
    }

    public void setrData(final String rData) {
        this.rData = rData;
    }
    
    public long getDateTimeMillis()
	{
		return dateTimeMillis;
	}

	public void setDateTimeMillis(long dateTimeMillis)
	{
		this.dateTimeMillis = dateTimeMillis;
	}

	public String getSourceTimeZone()
	{
		return sourceTimeZone;
	}

	public void setSourceTimeZone(String sourceTimeZone)
	{
		this.sourceTimeZone = sourceTimeZone;
	}

    public String getCollectorHost() {
        return collectorHost;
    }

    public void setCollectorHost(String collectorHost) {
        this.collectorHost = collectorHost;
    }

	public String getClient()
	{
		return client;
	}

	public void setClient(String client)
	{
		this.client = client;
	}

	public String toString()
    {
    	StringBuffer result = new StringBuffer("[");
    	result.append(id).append(",")
    			.append(aid).append(",")
    			.append(type).append(",")
    			.append(category).append(",")
    			.append(appClass).append(",")
    			.append(appId).append(",")
    			.append(host).append(",")
    			.append(vHost).append(",")
    			.append(uid).append(",")
    			.append(sid).append(",")
    			.append(desc).append(",")
    			.append(rid).append(",")
    			.append(rData).append(",")
    			.append(dateTime).append(",")
    			.append(dateTimeMillis).append(",")
                .append(collectorHost).append(",")
                .append(client).append(",")
                .append(sourceTimeZone).append("]");

    	return result.toString();
    }

    private static String toEmptyStr(final String s) {
        return s == null ? "" : s;
    }
}
