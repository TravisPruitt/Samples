package com.disney.xband.xbrc.lib.model;

import java.util.Date;

public class XBioDiagnosticPacket {
	
	private int xBioDiagnosticPacketId; 
	private String xFPEId;
	private Date dateTimeStamp;
	private String v500DiagnosticPacket;
	private String xFPEDiagnosticPacket;
	private String xFP1DiagnosticPacket;
	
	
	public XBioDiagnosticPacket()
	{
		
	}
	
	public XBioDiagnosticPacket(String xFPEId, Date dateTimeStamp, String v500DiagnosticPacket, String xFPEDiagnosticPacket, String xFP1DiagnosticPacket )
	{

		this.setxFPEId(xFPEId);
		this.setDateTimeStamp(dateTimeStamp);
		this.setV500DiagnosticPacket(v500DiagnosticPacket);
		this.setxFPEDiagnosticPacket(xFPEDiagnosticPacket);
		this.setxFP1DiagnosticPacket(xFP1DiagnosticPacket);
		
	}

	/**
	 * @return the xBioDiagnosticPacketId
	 */
	public int getxBioDiagnosticPacketId() {
		return xBioDiagnosticPacketId;
	}

	/**
	 * @param xBioDiagnosticPacketId the xBioDiagnosticPacketId to set
	 */
	public void setxBioDiagnosticPacketId(int xBioDiagnosticPacketId) {
		this.xBioDiagnosticPacketId = xBioDiagnosticPacketId;
	}

	/**
	 * @return the xFPEId
	 */
	public String getxFPEId() {
		return xFPEId;
	}

	/**
	 * @param xFPEId the xFPEId to set
	 */
	public void setxFPEId(String xFPEId) {
		this.xFPEId = xFPEId;
	}

	/**
	 * @return the dateTimeStamp
	 */
	public Date getDateTimeStamp() {
		return dateTimeStamp;
	}

	/**
	 * @param dateTimeStamp the dateTimeStamp to set
	 */
	public void setDateTimeStamp(Date dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	/**
	 * @return the v500DiagnosticPacket
	 */
	public String getV500DiagnosticPacket() {
		return v500DiagnosticPacket;
	}

	/**
	 * @param v500DiagnosticPacket the v500DiagnosticPacket to set
	 */
	public void setV500DiagnosticPacket(String v500DiagnosticPacket) {
		this.v500DiagnosticPacket = v500DiagnosticPacket;
	}

	/**
	 * @return the xFPEDiagnosticPacket
	 */
	public String getxFPEDiagnosticPacket() {
		return xFPEDiagnosticPacket;
	}

	/**
	 * @param xFPEDiagnosticPacket the xFPEDiagnosticPacket to set
	 */
	public void setxFPEDiagnosticPacket(String xFPEDiagnosticPacket) {
		this.xFPEDiagnosticPacket = xFPEDiagnosticPacket;
	}

	/**
	 * @return the xFP1DiagnosticPacket
	 */
	public String getxFP1DiagnosticPacket() {
		return xFP1DiagnosticPacket;
	}

	/**
	 * @param xFP1DiagnosticPacket the xFP1DiagnosticPacket to set
	 */
	public void setxFP1DiagnosticPacket(String xFP1DiagnosticPacket) {
		this.xFP1DiagnosticPacket = xFP1DiagnosticPacket;
	}
	
	


	

}
