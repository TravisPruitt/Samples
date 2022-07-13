package com.disney.xband.lib.xbrapi;

import com.disney.xband.common.lib.health.StatusType;


public class XfpDiagEvent extends XbrEvent {

	/** Ambient light reading. **/
	private Double amb;
	/** Internal temperature reading. **/
	private Double temp;
	/** Maximum temperature reading since program started **/
	private Double maxTemp;
	/** Status of a dap reader.  Either “Green”, “Yellow” or “Red”. **/
	private StatusType status;
	/** Reason for problem if status is other than “Green”, otherwise "Green". **/ 
	private String statusMsg;
	/** Status of RFID reader.  Either “okay” or some form of error description. **/
	private String rfidStatus;
	/** "Green" or a reason for the problem if status is other than "Green". **/
	private String rfidStatusMsg;
	/** Status of XBIO reader.  Either “okay” or some form of error description. **/
	private String xbioStatus;
	/** Reason for problem if xBio status is other than "Green" **/
	private String xbioStatusMsg;
	
	public XfpDiagEvent() {
		super(XbrEventType.XfpDiag);
	}
	
	@Override
	public String getID() {
		return null;
	}

	public Double getAmb() {
		return amb;
	}

	public void setAmb(Double amb) {
		this.amb = amb;
	}

	public Double getTemp() {
		return temp;
	}

	public void setTemp(Double temp) {
		this.temp = temp;
	}

	/**
	 * @return the status
	 */
	public StatusType getStatus() {
		return status;
	}

	/**
	 * @return the statusDetail
	 */
	public String getStatusMsg() {
		return statusMsg;
	}

	/**
	 * @return the rfidStatus
	 */
	public String getRfidStatus() {
		return rfidStatus;
	}

	/**
	 * @return the xbioStatus
	 */
	public String getXbioStatus() {
		return xbioStatus;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(StatusType status) {
		this.status = status;
	}

	/**
	 * @param statusDetail the statusDetail to set
	 */
	public void setStatusMsg(String statusDetail) {
		this.statusMsg = statusDetail;
	}

	/**
	 * @param rfidStatus the rfidStatus to set
	 */
	public void setRfidStatus(String rfidStatus) {
		this.rfidStatus = rfidStatus;
	}

	/**
	 * @param xbioStatus the xbioStatus to set
	 */
	public void setXbioStatus(String xbioStatus) {
		this.xbioStatus = xbioStatus;
	}
	
	public String getRfidStatusMsg() {
		return rfidStatusMsg;
	}

	public void setRfidStatusMsg(String rfidStatusMsg) {
		this.rfidStatusMsg = rfidStatusMsg;
	}

	public Double getMaxTemp()
	{
		return maxTemp;
	}

	public void setMaxTemp(Double maxTemp)
	{
		this.maxTemp = maxTemp;
	}

	public String getXbioStatusMsg()
	{
		return xbioStatusMsg;
	}

	public void setXbioStatusMsg(String xbioStatusMsg)
	{
		this.xbioStatusMsg = xbioStatusMsg;
	}
}
