package com.disney.xband.lib.xbrapi;

import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class HelloMsg
{		
	private String readerName;	
	private String readerType;
	private String mac;
	private Integer port;
	private Long nextEno;
	private String readerVersion;	
	private Collection<UpdateStream> updateStream;
	private Date time;	
	private String minXbrcVersion;	
	private String linuxVersion;	
	private String hardwareType;	
	private Integer LocationId;	
	private String mediaHash;
	
	private transient boolean simulated = false;
	private transient boolean listens = false;
	private transient boolean supportsMultipleStreams = false; 
	
	@JsonProperty("reader name")
	@XmlElement(name="reader name")	
	public String getReaderName() {
		return readerName;
	}
	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}
	@JsonProperty("reader type")
	@XmlElement(name="reader type")
	public String getReaderType() {
		return readerType;
	}
	public void setReaderType(String readerType) {
		this.readerType = readerType;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	@JsonProperty("next eno")
	@XmlElement(name="next eno")
	public Long getNextEno() {
		return nextEno;
	}
	public void setNextEno(Long nextEno) {
		this.nextEno = nextEno;
	}
	@JsonProperty("reader version")
	@XmlElement(name="reader version")
	public String getReaderVersion() {
		return readerVersion;
	}
	public void setReaderVersion(String readerVersion) {
		this.readerVersion = readerVersion;
	}
	@JsonProperty("update stream")
	@XmlElement(name="update stream")
	public Collection<UpdateStream> getUpdateStream() {
		return updateStream;
	}
	public void setUpdateStream(Collection<UpdateStream> updateStream) {
		this.updateStream = updateStream;
	}
	@JsonProperty("min xbrc version")
	@XmlElement(name="min xbrc version")
	public String getMinXbrcVersion() {
		return minXbrcVersion;
	}
	public void setMinXbrcVersion(String minXbrcVersion) {
		this.minXbrcVersion = minXbrcVersion;
	}
	@JsonProperty("linux version")
	@XmlElement(name="linux version")
	public String getLinuxVersion() {
		return linuxVersion;
	}
	public void setLinuxVersion(String linuxVersion) {
		this.linuxVersion = linuxVersion;
	}
	@JsonProperty("HW type")
	@XmlElement(name="HW type")
	public String getHardwareType() {
		return hardwareType;
	}
	public void setHardwareType(String hardwareType) {
		this.hardwareType = hardwareType;
	}
	@JsonProperty("location id")
	@XmlElement(name="location id")
	public Integer getLocationId() {
		return LocationId;
	}
	public void setLocationId(Integer locationId) {
		LocationId = locationId;
	}
	@JsonProperty("media hash")
	@XmlElement(name="media hash")
	public String getMediaHash() {
		return mediaHash;
	}
	public void setMediaHash(String mediaHash) {
		this.mediaHash = mediaHash;
	}
	
	@XmlTransient
	@JsonIgnore
	public ReaderType getType() {
		return ReaderType.getByType(readerType);
	}
	@XmlTransient
	@JsonIgnore
	public boolean isSimulated() {
		return simulated;
	}
	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}
	@XmlTransient
	@JsonIgnore
	public boolean isListens() {
		return listens;
	}
	public void setListens(boolean listens) {
		this.listens = listens;
	}
	@XmlTransient
	@JsonIgnore
	public boolean isSupportsMultipleStreams() {
		return supportsMultipleStreams;
	}
	public void setSupportsMultipleStreams(boolean supportsMultipleStreams) {
		this.supportsMultipleStreams = supportsMultipleStreams;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
}
