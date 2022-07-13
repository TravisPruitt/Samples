package com.disney.xband.xfpe.model;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import com.disney.xband.lib.xbrapi.BioImageEvent;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xfpe.simulate.TestThread;

public class XfpeReader {
	private int maxQueueSize = 100000;
	private String readerId;
	private LinkedList<XbrEvent> events;
	private Long eno = 0L;	
	private XfpeReaderLight light;
	private Date lightExpiry;
	private XfpeScanMode scanMode;
	private XfpeScanTemplate[] templates;
	private ArrayList<XfpeListener> listeners;
	private Reader reader;
	private boolean fpLightOn = false;
	private TestThread currentTest;
	private boolean testMode = false;
	private String xbioImages;
	private boolean connected = false;
	private BigInteger loggedInUserId;

	public XfpeReader() {
		events = new LinkedList<XbrEvent>();
		listeners = new ArrayList<XfpeListener>();
		light = XfpeReaderLight.off;
		scanMode = XfpeScanMode.NONE;
	}	
	public Long getEno() {
		return eno;
	}
	public void setEno(Long eno) {
		this.eno = eno;
	}
	public String getReaderId() {
		return readerId;
	}
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}
	public Long incrementEno() {
		return eno++;
	}
	public void enqueEvent(XbrEvent e) {
		if (events.size() > maxQueueSize)
			events.remove();
		events.add(e);
	}
	public XbrEvent dequeEvent() {
		return events.remove();
	}
	public boolean hasEvents() {
		return !events.isEmpty();
	}
	public XfpeReaderLight getLight() {
		return light;
	}
	public XfpeReaderLight getCurrentLight() {
		if (light != XfpeReaderLight.off) {
			if (lightExpiry != null && lightExpiry.getTime() < new Date().getTime())
				light = XfpeReaderLight.off;
		}
		return light;
	}
	public void setLight(XfpeReaderLight light) {
		this.light = light;
	}
	public void setSequence(XfpeReaderSequence sequence) {
		// For now just map these sequences to some lights.
		switch(sequence) {
			case exception:
				setLight(XfpeReaderLight.blue);
				break;
			case login_ok:
				setLight(XfpeReaderLight.green);
				break;
			case entry_retry:
				setFpLightOn(true);
				break;
			case entry_start_scan:
				setFpLightOn(true);
				break;
			case success:
				setLight(XfpeReaderLight.green);
				break;
			case off:
				setLight(XfpeReaderLight.off);
				break;
		}
	}
	public Date getLightExpiry() {
		return lightExpiry;
	}
	public void setLightExpiry(Date lightExpiry) {
		this.lightExpiry = lightExpiry;
	}	
	public XfpeScanMode getScanMode() {
		return scanMode;
	}
	public void setScanMode(XfpeScanMode scanMode) {
		this.scanMode = scanMode;
	}
	public XfpeScanTemplate[] getTemplates() {
		return templates;
	}
	public void setTemplates(XfpeScanTemplate[] templates) {
		this.templates = templates;
	}
	public LinkedList<XbrEvent> getEvents() {
		return events;
	}
	public Reader getReader() {
		return reader;
	}
	public void setReader(Reader reader) {
		this.reader = reader;
	}
	public boolean isFpLightOn() {
		return fpLightOn;
	}
	public void setFpLightOn(boolean fpLightOn) {
		this.fpLightOn = fpLightOn;
	}
	public TestThread getCurrentTest() {
		synchronized(this) {
			return currentTest;
		}
	}
	public void setCurrentTest(TestThread currentTest) {
		synchronized(this) {
			this.currentTest = currentTest;
		}
	}
	public boolean isTestMode() {
		return testMode;
	}
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
	
	// We are asked to send the last image of the fingerprint scan to the XBRC
	public void sendBioImage(String RFID, Long transactionId, Long templateId) {
		if (xbioImages == null)
			return;
		
		BioImageEvent e = new BioImageEvent();
		e.setReaderName(reader.getReaderId());
		e.setEno(this.incrementEno());
		e.setXbioImages(xbioImages);
		e.setUid(RFID);
		e.setTransactionId(transactionId);
		e.setTemplateId(templateId);
		e.setTime(new Date());
		
		enqueEvent(e);
		
		// this operation clears the xbioImage
		xbioImages = null;
	}
	public String getXbioImages() {
		return xbioImages;
	}
	public void setXbioImages(String xbioImages) {
		this.xbioImages = xbioImages;
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	public boolean isLoggedIn() {
		return loggedInUserId != null;
	}
	public BigInteger getLoggedInUserId() {
		return loggedInUserId;
	}
	public void setLoggedInUserId(BigInteger loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}
	public String getHardwareType()
	{
		// Here is a hack to test the different hardware types.
		int i = reader.getReaderId().indexOf("xTP");
		if (i > 0)
			return reader.getReaderId().substring(i);
		
		if (ReaderType.isTapReader(reader.getType()))
			return "xTP";
		
		return "xBR";
	}
	public ArrayList<XfpeListener> getListeners() {
		return listeners;
	}
	public void setListeners(ArrayList<XfpeListener> listeners) {
		this.listeners = listeners;
	}
	
}
