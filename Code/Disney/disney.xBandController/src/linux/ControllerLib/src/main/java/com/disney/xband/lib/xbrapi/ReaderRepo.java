package com.disney.xband.lib.xbrapi;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"name","path","weight"})
public class ReaderRepo
{
	private String name;
	private String path;
	private String weight;

	public ReaderRepo()
	{
	}
	
	public ReaderRepo(String name, String path, String weight)
	{
		super();
		this.name = name;
		this.path = path;
		this.weight = weight;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getPath()
	{
		return path;
	}
	public void setPath(String path)
	{
		this.path = path;
	}
	public String getWeight()
	{
		return weight;
	}
	public void setWeight(String weight)
	{
		this.weight = weight;
	}
	protected ReaderRepo clone()
	{
		return new ReaderRepo(this.name, this.path, this.weight);
	}
}
