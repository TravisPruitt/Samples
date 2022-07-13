package com.disney.xband.lib.xbrapi;

public class XbioFWUpgradeEvent extends XbrEvent {

	// Status of the upgrade.  Can be “success” or “failed”
	private String status;
	// Version number reported by the firmware after the upgrade.
	private String version;
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

}
