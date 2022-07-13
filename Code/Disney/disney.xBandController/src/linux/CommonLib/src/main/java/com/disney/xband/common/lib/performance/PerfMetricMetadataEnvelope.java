package com.disney.xband.common.lib.performance;

import java.util.Collection;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class PerfMetricMetadataEnvelope
{
	// elements
	private Collection<PerfMetricMetadata> metadata;
	
	// envelope attributes
	private String time;
	private String name;
	private String facilityId;
	
	public PerfMetricMetadataEnvelope(){
		setMetadata(new LinkedList<PerfMetricMetadata>());
	}

	public Collection<PerfMetricMetadata> getMetadata()
	{
		return metadata;
	}

	public void setMetadata(Collection<PerfMetricMetadata> metadata)
	{
		this.metadata = metadata;
	}
	
	public void add(PerfMetricMetadata meta)
	{
		metadata.add(meta);
	}
	
	public void remove(PerfMetricMetadata meta)
	{
		metadata.remove(meta);
	}
	
	public void clear()
	{
		metadata.clear();
	}

	/**
	 * @return the time
	 */
	@XmlAttribute
	public String getTime()
	{
		return time;
	}

	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName()
	{
		return name;
	}

	/**
	 * @return the facilityId
	 */
	@XmlAttribute
	public String getFacilityId()
	{
		return facilityId;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time)
	{
		this.time = time;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param facilityId the facilityId to set
	 */
	public void setFacilityId(String facilityId)
	{
		this.facilityId = facilityId;
	}
}
