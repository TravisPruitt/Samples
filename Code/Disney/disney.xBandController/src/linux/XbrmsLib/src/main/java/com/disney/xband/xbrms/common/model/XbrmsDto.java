package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 2/11/13
 * Time: 7:06 PM
 */
@XmlRootElement(name = "xbrmsInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class XbrmsDto implements IDataTransferObject, Comparable<XbrmsDto> {
    private String url;
    private String desc;
    private String logo;
    private int id;
    private String addr;
    private String hostAlias;
    private String fqdnHostAlias;
    private boolean global;
    private boolean isAlive;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the value of the global property.
     *
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Sets the value of the global property.
     *
     */
    public void setGlobal(boolean value) {
        this.global = value;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getHostAlias() {
        return hostAlias;
    }

    public void setHostAlias(String hostAlias) {
        this.hostAlias = hostAlias;
    }

    public String getFqdnHostAlias() {
        return fqdnHostAlias;
    }

    public void setFqdnHostAlias(String fqdnHostAlias) {
        this.fqdnHostAlias = fqdnHostAlias;
    }

    @XmlTransient
    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

	@Override
	public int compareTo(XbrmsDto other)
	{
		if (this.global)
			return -1;
		else if (other.isGlobal())
			return 1;
		
		return this.desc.compareTo(other.getDesc());
	}
}
