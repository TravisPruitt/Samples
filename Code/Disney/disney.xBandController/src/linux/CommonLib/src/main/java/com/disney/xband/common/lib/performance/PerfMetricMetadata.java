package com.disney.xband.common.lib.performance;

/**
 * When calling the constructor of this object, pass in an appropriate version number.
 * 
 * IMPORTANT: Increase the version whenever the type of data collected by the perf metric 
 * described by this PerfMetricMetadata changes.
 * 
 * @author Iwona Glabek
 */
public class PerfMetricMetadata implements Cloneable
{
	/**
	 * Name of a variable of type PerfMetric defined on the XbrcStatus object.
	 */
	private String name = "";
	/**
	 * More friendly and descriptive name for this metric to be used as a title in the UI.
	 * The name should describe the type of data collected by the perf metric.
	 */
	private String displayName = "";
	/**
	 * Description of data collected by the perf metric to be displayed as help in the UI.
	 */
	private String description = "";
	/**
	 * Version of this metadata. Used to ensure that the historical meaning of data collected 
	 * by this perf metrics is not lost when type of data collected changes.
	 */
	private String version = "1.0"; 
	/**
	 * Units of collected data.
	 */
	private PerfMetricType type = PerfMetricType.milliseconds;
	
	public PerfMetricMetadata(){}
	
	public PerfMetricMetadata(String name, PerfMetricType type, String displayName, String description, String version)
	{
		this.name = name;
		this.type = type;
		this.displayName = displayName;
		this.description = description;
		this.version = version;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public PerfMetricType getType()
	{
		return type;
	}

	public void setType(PerfMetricType type)
	{
		this.type = type;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	public PerfMetricMetadata clone()
	{
		return new PerfMetricMetadata(this.name, this.type, this.displayName, this.description, this.version);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PerfMetricMetadata other = (PerfMetricMetadata) obj;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (displayName == null)
		{
			if (other.displayName != null)
				return false;
		}
		else if (!displayName.equals(other.displayName))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (version == null)
		{
			if (other.version != null)
				return false;
		}
		else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	public String toString()
	{
		StringBuffer value = new StringBuffer();
		value.append("name:").append(name);
		value.append("displayName:").append(displayName);
		value.append("description:").append(description);
		value.append("units:").append(type.name());
		value.append("version:").append(version);
		
		return value.toString();
	}
}
