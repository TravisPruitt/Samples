package com.disney.xband.lib.xbrapi;

/*
 * Possible values for the type field of the xBR events. 
 */
public enum XbrEventType {

	Unknown("Unknown"),
	LRR("LRR"),
	RFID("RFID"),
	BioEnroll("bio-enroll"),
	BioMatch("bio-match"),
	BioImage("bio-image"),
	BioScanError("bio-scan-error"),
	XfpDiag("xfp-diagnostics"),
	XbioDiag("xbio-diagnostics"),
	XbioFWUpgrade("xbio-fw-upgrade"),
	VID("VID"),
	XtpGpio("xtp-gpio"),
	Shutdown("reader-shutdown"),
	XbrDiag("xbr-diagnostics");
	
	// NOTE: don't forget to change the fromType() function below if you add a new type!!

	private String type;
	
	XbrEventType(String type) {
		this.type = type;
	}
	
	public static XbrEventType fromType(String type) {
		if (type == null)
			return Unknown;
					
		if (type.equals(LRR.getType()))
			return LRR;
		if (type.equals(RFID.getType()))
			return RFID;
		if (type.equals(BioEnroll.getType()))
			return BioEnroll;
		if (type.equals(BioMatch.getType()))
			return BioMatch;
		if (type.equals(XfpDiag.getType()))
			return XfpDiag;
		if (type.equals(XbioDiag.getType()))
			return XbioDiag;
		if (type.equals(BioImage.getType()))
			return BioImage;
		if (type.equals(BioScanError.getType()))
			return BioScanError;
		if (type.equals(VID.getType()))
			return VID;
		if (type.equals(XtpGpio.getType()))
			return XtpGpio;
		if (type.equals(Shutdown.getType()))
			return Shutdown;
		if (type.equals(XbrDiag.getType()))
			return XbrDiag;
			
		return Unknown;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type;
	}
}
