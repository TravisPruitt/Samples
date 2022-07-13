package com.disney.xband.xfpe;

import com.disney.xband.common.lib.PersistName;
import com.disney.xband.xbrc.lib.omni.OmniConst;

@PersistName("XfpeSimConfig")
public class XfpeSimConfig {
	private String controllerURL = "http://localhost:8080/ControllerServer";
	private int helloIntervalSec = 20;
	private int omniThreadPort = 9920;
	private int bioMatchThreshold = OmniConst.BIO_MATCH_THRESHOLD_HIGH;
	private int maxScanRetry = 1;
	private int guestQueueStartX = 0;
	private int guestQueueStartY = 7;
	private int guestEntryLoop = 1;
	private boolean reportReaderTestResults = false;
	private boolean simulateWalking = true;
	private String castMemberRfid = "008023C002AC9004";
	private boolean simulateClosedReaderOnTor = false;

	public String getControllerURL() {
		return controllerURL;
	}

	public void setControllerURL(String controllerURL) {
		this.controllerURL = controllerURL;
	}

	public int getHelloIntervalSec() {
		return helloIntervalSec;
	}

	public void setHelloIntervalSec(int helloIntervalSec) {
		this.helloIntervalSec = helloIntervalSec;
	}

	public int getOmniThreadPort() {
		return omniThreadPort;
	}

	public void setOmniThreadPort(int omniThreadPort) {
		this.omniThreadPort = omniThreadPort;
	}
	
	public int getBioMatchThreshold() {
		return bioMatchThreshold;
	}

	public void setBioMatchThreshold(int bioMatchThreshold) {
		this.bioMatchThreshold = bioMatchThreshold;
	}

	public int getMaxScanRetry() {
		return maxScanRetry;
	}

	public void setMaxScanRetry(int maxScanRetry) {
		this.maxScanRetry = maxScanRetry;
	}

	public int getGuestQueueStartX() {
		return guestQueueStartX;
	}

	public void setGuestQueueStartX(int guestQueueStartX) {
		this.guestQueueStartX = guestQueueStartX;
	}

	public int getGuestQueueStartY() {
		return guestQueueStartY;
	}

	public void setGuestQueueStartY(int guestQueueStartY) {
		this.guestQueueStartY = guestQueueStartY;
	}
	
	public int getGuestEntryLoop() {
		return guestEntryLoop;
	}
	
	public void setGuestEntryLoop(int guestEntryLoop) {
		this.guestEntryLoop = guestEntryLoop;
	}
	
	public boolean getReportReaderTestResults() {
		return reportReaderTestResults;
	}
	
	public void setReportReaderTestResults(boolean reportReaderTestResults) {
		this.reportReaderTestResults = reportReaderTestResults;
	}

	public boolean isSimulateWalking()
	{
		return simulateWalking;
	}

	public void setSimulateWalking(boolean simulateWalking)
	{
		this.simulateWalking = simulateWalking;
	}

	public String getCastMemberRfid()
	{
		return castMemberRfid;
	}

	public void setCastMemberRfid(String castMemberRfid)
	{
		this.castMemberRfid = castMemberRfid;
	}

	public boolean isSimulateClosedReaderOnTor()
	{
		return simulateClosedReaderOnTor;
	}

	public void setSimulateClosedReaderOnTor(boolean simulateClosedReaderOnTor)
	{
		this.simulateClosedReaderOnTor = simulateClosedReaderOnTor;
	}
}
