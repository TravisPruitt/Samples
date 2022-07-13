package com.disney.xband.jms.lib.entity.xbms;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"xpasses","resortReservations","admissionReservations",
		"celebrations","dmeReservations","levelNEntitlements","admissionEntitlements","paymentAccounts"})
public class Entitlements 
{
	private List<Entitlement> xpasses;
	private List<Entitlement> resortReservations;
	private List<Entitlement> admissionReservations;
	private List<Entitlement> celebrations;
	private List<Entitlement> dmeReservations;
	private List<Entitlement> levelNEntitlements;
	private List<Entitlement> admissionEntitlements;
	private List<Entitlement> paymentAccounts;

	@XmlElement(name="xpasses")
	public List<Entitlement> getXpasses()
	{
		return this.xpasses;
	}

	public void setXpasses(List<Entitlement> xpasses)
	{
		this.xpasses = xpasses;
	}

	@XmlElement(name="resortReservations")
	public List<Entitlement> getResortReservations()
	{
		return this.resortReservations;
	}

	public void setResortReservations(List<Entitlement> resortReservations)
	{
		this.resortReservations = resortReservations;
	}

	@XmlElement(name="admissionReservations")
	public List<Entitlement> getadmissionReservations()
	{
		return this.admissionReservations;
	}

	public void setadmissionReservations(List<Entitlement> admissionReservations)
	{
		this.admissionReservations = admissionReservations;
	}

	@XmlElement(name="celebrations")
	public List<Entitlement> getCelebrations()
	{
		return this.celebrations;
	}

	public void setCelebrations(List<Entitlement> celebrations)
	{
		this.celebrations = celebrations;
	}

	@XmlElement(name="dmeReservations")
	public List<Entitlement> getDmeReservations()
	{
		return this.dmeReservations;
	}

	public void setDmeReservations(List<Entitlement> dmeReservations)
	{
		this.dmeReservations = dmeReservations;
	}

	@XmlElement(name="levelNEntitlements")
	public List<Entitlement> getLevelNEntitlements()
	{
		return this.levelNEntitlements;
	}

	public void setLevelNEntitlements(List<Entitlement> levelNEntitlements)
	{
		this.levelNEntitlements = levelNEntitlements;
	}

	@XmlElement(name="admissionEntitlements")
	public List<Entitlement> getAdmissionEntitlements()
	{
		return this.admissionEntitlements;
	}

	public void setAdmissionEntitlements(List<Entitlement> admissionEntitlements)
	{
		this.admissionEntitlements = admissionEntitlements;
	}

	@XmlElement(name="paymentAccounts")
	public List<Entitlement> getpaymentAccounts()
	{
		return this.paymentAccounts;
	}

	public void setpaymentAccounts(List<Entitlement> paymentAccounts)
	{
		this.paymentAccounts = paymentAccounts;
	}

}
