package com.disney.xband.idms.lib.model.oneview;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.idms.lib.model.LinkCollection;

@XmlRootElement
@XmlType(propOrder={"links","role","relationship","lastname","firstname"})
public class CelebrationGuest 
{
	private LinkCollection links;
	private String role;
	private String relationship;
	private String firstname;
	private String lastname;

	public CelebrationGuest()
	{
		this.links = new LinkCollection();
	}
	
	@XmlElement(name="links")
	public LinkCollection getLinks()
	{
		return this.links;
	}
	
	@XmlElement(name="role")
	public String getRole() 
	{
		return this.role;
	}
	
	public void setRole(String role) 
	{
		this.role = role;
	}

	@XmlElement(name="relationship")
	public String getRelationship() 
	{
		return this.relationship;
	}
	
	public void setRelationship(String relationship) 
	{
		this.relationship = relationship;
	}
	
	@XmlElement(name="firstname")
	public String getFirstname() 
	{
		return this.firstname;
	}
	
	public void setFirstname(String firstname) 
	{
		this.firstname = firstname;
	}

	@XmlElement(name="lastname")
	public String getLastname() 
	{
		return this.lastname;
	}
	
	public void setLastname(String lastname) 
	{
		this.lastname = lastname;
	}

	public void setLinks(LinkCollection links)
	{
		this.links = links;
	}
}
