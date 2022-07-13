package com.disney.xband.xfpe.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.catalina.Server;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11AprProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.coyote.http11.Http11Protocol;
import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.BioEvent;
import com.disney.xband.lib.xbrapi.HelloMsg;
import com.disney.xband.lib.xbrapi.TapEvent;
import com.disney.xband.lib.xbrapi.UpdateStream;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.lib.xbrapi.XbrEventType;
import com.disney.xband.lib.xbrapi.XbrEvents;
import com.disney.xband.lib.xbrapi.XbrJsonMapper;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.net.NetInterface;
import com.disney.xband.xfpe.ServiceLocator;
import com.disney.xband.xfpe.Utils;
import com.disney.xband.xfpe.XfpeProperties;
import com.disney.xband.xfpe.bean.XfpeLocation;
import com.disney.xband.xfpe.db.LocationService;
import com.disney.xband.xfpe.db.ReaderService;
import com.disney.xband.xfpe.model.XfpeListener;
import com.disney.xband.xfpe.model.XfpeReader;
import com.disney.xband.xfpe.model.XfpeReaderLight;
import com.disney.xband.xfpe.model.XfpeReaderSequence;
import com.disney.xband.xfpe.model.XfpeScanMode;
import com.disney.xband.xfpe.model.XfpeScanTemplate;
import com.disney.xband.xfpe.simulate.Simulator;
import com.disney.xband.xfpe.simulate.SimulatorEvents;

public class XfpeController {
	
	private static Logger logger = Logger.getLogger(XfpeController.class);
	
	// Mapped by readerId
	private LinkedHashMap<String,XfpeReader> readers;
	private Map<Long,XfpeLocation> locations;
	private Timer timer;
	private String bioImage = null;
	
	private static class SingletonHolder { 
		public static final XfpeController instance = new XfpeController();
	}
	
	public static XfpeController getInstance() {
		return SingletonHolder.instance;
	}
	
	private XfpeController() {
		readers = new LinkedHashMap<String,XfpeReader>();
		locations = new HashMap<Long,XfpeLocation>();
		
		// setup some large bioImage data...
		StringBuffer bioImages = new StringBuffer();
		// Generate a large string to simulate the test xbio images
		for (int i = 0; i < 2000144; i++) {
			bioImages.append("*");
		}
		bioImage = bioImages.toString();
		
		try {
			
			// Read all the locations
			LocationService ls = ServiceLocator.getInstance().createService(LocationService.class);
			Collection<Location> locList = ls.findAll();
			for (Location l : locList) {
				locations.put(l.getId(), new XfpeLocation(l));
			}			

			// Read all TAP readers from the readers table.
			ReaderService rsi = ServiceLocator.getInstance().createService(ReaderService.class);
			Collection<Reader> readersCol = rsi.findAll();
			for (Reader r : readersCol) {
				// Note: at some point we may actually include LRR readers as well.
				if (ReaderType.isTapReader(r.getType())) {
					XfpeReader xr = new XfpeReader();
					xr.setReaderId(r.getReaderId());
					xr.setReader(r);					
					XfpeLocation l = locations.get(r.getLocationId());
					
					if (l == null) {
						logger.warn("Ignoring reader " + xr.getReaderId() + " (id=" + r.getId() + ") because it is not assigned to a location");
						continue;
					}
					
					// We ignore real readers based on the fact that they will not have X and Y positions set.
					if (r.getX() == null || r.getY() == null || (r.getX() == 0 && r.getY() == 0)) {
						logger.warn("Ignoring reader " + xr.getReaderId() + " (id=" + r.getId() + ") because it does not have X and Y position set");
						continue;
					}					
					
					l.getReaders().add(xr);
					readers.put(r.getReaderId(), xr);
					
					// Since XBRC will not send the update stream for the mobile reaer, we must set it ourselves.
					if (xr.getReader().getType() == ReaderType.mobileGxp) {
						String xbrcUrl = XfpeProperties.getInstance().getXfpeSimConfig().getControllerURL();
						if (r.getTransmitPayload() != null && !r.getTransmitPayload().isEmpty())
							xbrcUrl = r.getTransmitPayload();
						if (xbrcUrl.lastIndexOf("/") == (xbrcUrl.length() -1))
							xbrcUrl += "stream";
						else
							xbrcUrl += "/stream";
						List<String> urls = new LinkedList<String>();
						urls.add(xbrcUrl);
						onCommandSetUpdateStream(r.getReaderId(), urls, 10l, 1l, 0l);
					}
				}
			}
			
			// Periodically send hello 
			timer = new Timer();
			timer.schedule(new TimerTask(){
				  public void run(){
					  XfpeController.getInstance().sendHello();
				  }
				  }, 1000, // Start after one second
				  	 XfpeProperties.getInstance().getXfpeSimConfig().getHelloIntervalSec() * 1000);
			
		} catch (Exception e) {
			logger.error("Failed to read objects from the database", e);
		}
	}
	
	public Map<String, XfpeReader> getReaders() {
		return readers;
	}
	
	public Map<Long,XfpeLocation> getLocations() {
		return locations;
	}
	
	// This should return the port number that tomcat is configured to listen on.
	private int getOurPort() {
		Server server = ServerFactory.getServer();
        Service[] services = server.findServices();
        for (Service service : services) {
            for (Connector connector : service.findConnectors()) {
                ProtocolHandler protocolHandler = connector.getProtocolHandler();
                if (protocolHandler instanceof Http11Protocol
                    || protocolHandler instanceof Http11AprProtocol
                    || protocolHandler instanceof Http11NioProtocol) {
                    return connector.getPort();                   
                }
            }
        }
        
        return 8080;
	}
	
	public void sendHello() {
		
		String xbrcUrl = "";		
		
		for (XfpeReader reader : readers.values()) 
		{
			try
			{
				synchronized(reader) {
					xbrcUrl = XfpeProperties.getInstance().getXfpeSimConfig().getControllerURL();
					if (reader.getReader().getTransmitPayload() != null && !reader.getReader().getTransmitPayload().isEmpty())
						xbrcUrl = reader.getReader().getTransmitPayload();
					
					sendReaderHello(reader, xbrcUrl);
				}
			}
			catch(Exception e) {
				logger.error("Failed to send hello from reader to " + 
								xbrcUrl + "/hello", e );
			}
		}		
	}
	
	private void sendReaderHello(XfpeReader reader, String xbrcUrl) throws Exception {
		
		Collection<String> macList = NetInterface.getOwnMacAddress(null);
		String mac = macList.isEmpty() ? "127.0.0.1" : macList.iterator().next();
		
		HelloMsg hm = new HelloMsg();		
		//hm.setMac(mac);
		hm.setMac(reader.getReader().getMacAddress());
		hm.setLinuxVersion("XfpeEmulator");	// must set this so that Controller can build a proper URL back to us
		hm.setNextEno(reader.getEno());
		hm.setPort(getOurPort());
		hm.setReaderName(reader.getReaderId());
		hm.setReaderType(reader.getReader().getType().getType());
		hm.setReaderVersion(reader.getReader().getVersion());
		hm.setMinXbrcVersion(reader.getReader().getMinXbrcVersion());
		hm.setHardwareType(reader.getHardwareType());
		hm.setUpdateStream(new ArrayList<UpdateStream>());
		hm.setTime(new Date());
		
		if (reader.getListeners() != null) {
			for (XfpeListener l : reader.getListeners()) {
				UpdateStream us = new UpdateStream(l.getUrl());
				hm.getUpdateStream().add(us);
			}
		}
		
		String str = XbrJsonMapper.serializeHello(hm);
		
		int rCode = Utils.doPutHttpRequest(xbrcUrl + "/hello", str);
	
	    if (rCode > 300) {
	    	logger.error("Received a " + rCode + " return code from the Controller trying to send hello.");
	    }
	    //else {		
	    //	logger.trace("Sent hello: " + str);
	    //}
	}

	public XfpeReader getReaderByDeviceId(Integer deviceId) {
		for (XfpeReader r : readers.values()) {
			if (r.getReader().getDeviceId().equals(deviceId))
				return r;
		}
		return null;
	}

	public void handleGuestTap(String readerId, String uid, String pid, String sid, String iin) throws Exception
	{
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}		
		
		synchronized(reader) {
			TapEvent e = new TapEvent();
			e.setReaderName(reader.getReaderId());
			e.setEno(reader.incrementEno());
			e.setUid(uid);
			e.setPid(pid);
			e.setSid(sid);
			e.setIin(iin);
			e.setTime(new Date());
			
			reader.enqueEvent(e);
		}
		
		publishEvents(reader);	
	}
	
	/*
	 * Triggered by the guest.
	 */
	public void handleGuestTap(String readerId, String rfid) throws Exception {
		handleGuestTap(readerId, rfid, rfid, rfid, rfid);
	}
	
	/*
	 * Triggered by the guest.
	 */
	public void handleGuestScan(String readerId, String inputBioInfo) throws Exception {
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}		
		
		synchronized(reader) {
			BioEvent e = new BioEvent(reader.getScanMode() == XfpeScanMode.MATCH ? XbrEventType.BioMatch : XbrEventType.BioEnroll);
			e.setReaderName(reader.getReaderId());
			e.setEno(reader.incrementEno());
			e.setXbioTemplate(inputBioInfo);
			
			reader.setXbioImages(bioImage);
			
			e.setTime(new Date());
			reader.enqueEvent(e);
		}
		
		reader.setFpLightOn(false);
		
		publishEvents(reader);
	}
	
	/*
	 * Handled by the Omni administrator..
	 */
	public void handleReaderLogout(String readerId) throws Exception {
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		if (!reader.isLoggedIn()) {
			logger.warn("Cannot logout cast member from reader because no one is logged in.");
			return;
		}
		
		Simulator.getInstance().logoutReader(reader.getReader().getDeviceId());
		
		synchronized(reader) {
			reader.setLoggedInUserId(null);
		}
 	}
	
	/*
	 * Received from the XBRC to turn the light certain color.
	 */
	public void onCommandChangeLight(String readerId, XfpeReaderLight light, Long timeoutMs) throws Exception {
		logger.info("onCommandChangeLight for reader " + readerId + " light=" + light.name() + " timeoutMs=" + timeoutMs);
		
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		synchronized(reader) {
			reader.setLight(light);
			if (timeoutMs > 0)
				reader.setLightExpiry(new Date(new Date().getTime() + timeoutMs));
			else
				reader.setLightExpiry(null);
		}
		
		SimulatorEvents ev = SimulatorEvents.TapLightOff;
		switch(light) {
		case green:
			ev = SimulatorEvents.TapLightGreen;
			Simulator.getInstance().notifyOfReaderEvent(readerId, ev, "Tap read light changed to " + light.name());
			break;
		case blue:
			ev = SimulatorEvents.TapLightBlue;
			Simulator.getInstance().notifyOfReaderEvent(readerId, ev, "Tap read light changed to " + light.name());
			break;
		case off:
			ev = SimulatorEvents.TapLightOff;
			break;
		}		
	}
	
	public void onCommandSimulateTap(String readerId, String uid, String pid, String sid, String iin) throws Exception
	{
		logger.info("onCommandSimulateTap for reader " + readerId + " uid=" + uid + " pid=" + pid + " sid=" + sid + "iin=" + iin);
		
		handleGuestTap(readerId,uid,pid,sid,iin);
	}
	
	public void onCommandSimulateBioScan(String readerId, String template) throws Exception
	{
		logger.info("onCommandSimulateBioScan for reader " + readerId + " template=" + template);
		
		handleGuestScan(readerId,template);
	}
	
	/*
	 * Received from the XBRC to turn the light certain color.
	 */
	public void onCommandPlaySequence(String readerId, XfpeReaderSequence sequence, Long timeoutMs) throws Exception {
		logger.info("onCommandPlaySequence for reader " + readerId + " sequence=" + sequence.name() + " timeoutMs=" + timeoutMs);
		
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		synchronized(reader) {
			reader.setSequence(sequence);
			
			if (timeoutMs == 0) {
				// for some sequences we set the expiry ourselves
				switch(sequence) {
					case login_ok:
					case success:
						timeoutMs = 5000l;
						break;
				}
			}
			
			if (timeoutMs > 0)
				reader.setLightExpiry(new Date(new Date().getTime() + timeoutMs));
			else
				reader.setLightExpiry(null);
		}
		
		SimulatorEvents ev = SimulatorEvents.TapLightOff;
		switch(sequence) {
		case success:
		case login_ok:
			ev = SimulatorEvents.TapLightGreen;
			break;
		case exception:
			ev = SimulatorEvents.TapLightBlue;
			break;
		case off:
			ev = SimulatorEvents.TapLightOff;
			break;
		case entry_start_scan:
			ev = SimulatorEvents.EntryStartScan;
			break;
		}
		
		Simulator.getInstance().notifyOfReaderEvent(readerId, ev, "Tap reader sequence set to " + sequence.name());
	}
	
	/*
	 * Received from the XBRC to set the scan mode to either enorll or match
	 */
	public void onCommandSetScanMode(String readerId, XfpeScanMode scanMode, XfpeScanTemplate[] templates) throws Exception {
		logger.trace("onCommandSetScanMode for reader " + readerId);
		
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		synchronized(reader) {
			reader.setScanMode(scanMode);
			if (scanMode == XfpeScanMode.MATCH)
				reader.setTemplates(templates);
		}
	}
	
	/*
	 * Received from the XBRC to set the scan mode to either enorll or match
	 */
	public void onCommandBiometricCancel(String readerId) throws Exception {
		logger.trace("onCommandBiometricCancel for reader " + readerId);
		
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		synchronized(reader) {
			reader.setFpLightOn(false);
		}
	}
	
	/*
	 * Received from the XBRC to set the mode to either test or normal.
	 */
	public void onCommandSetOptions(String readerId, String imageCapture, String testLoop) throws Exception {
		logger.trace("onCommandSetOptions for reader " + readerId + " imageCapture = " + imageCapture + " testLoop = " + testLoop);
		
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		synchronized(reader) {
			if (imageCapture != null)
				reader.setTestMode(imageCapture.equalsIgnoreCase("on"));
			// TODO: implement the testLoop for the reader...
		}
	}
	
	/*
	 * Received from the XBRC to set the scan mode to either enorll or match
	 */
	public void onCommandBiometricEnroll(String readerId) throws Exception {
		logger.trace("onCommandBiometricEnroll for reader " + readerId);
		
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		synchronized(reader) {
			// Just turn the reader light on.
			reader.setFpLightOn(true);
		}
		Simulator.getInstance().notifyOfReaderEvent(readerId, SimulatorEvents.FpLightOn, "Biometric reader is waiting for scan.");
	}
	
	/*
	 * Received from the XBRC to initiate publishing of events.
	 */
	public void onCommandSetUpdateStream(String readerId, List<String> urls, Long interval, Long max, Long after) throws Exception {
		//logger.trace("onCommandSetUpdateStream for reader " + readerId);		
		
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		synchronized(reader) {
			reader.getListeners().clear();
			
			if (urls != null && !urls.isEmpty())
			{
				for (String url : urls)
				{
					// This will throw an exception if URL is invalid
					new URL(url);
					XfpeListener listener = new XfpeListener(url,interval, max, after);
					reader.getListeners().add(listener);
				}
			}
		}
		
		publishEvents(reader);
	}
	
	/*
	 * Received from the XBRC to set the mode to either test or normal.
	 */
	public void onCommandBiometricImageSend(String readerId, String RFID, Long templateId, Long transactionId) throws Exception {
		logger.trace("onCommandBiometricImageSend for reader " + readerId);
		
		XfpeReader reader = readers.get(readerId);
		if (reader == null) {
			throw new Exception("Could not find reader with readerId: " + readerId);
		}
		
		synchronized(reader) {
			reader.sendBioImage(RFID, transactionId, templateId);
		}
		
		publishEvents(reader);
	}
	
	/*
	 * Publish pending events to the listener.
	 */
	public void publishEvents(XfpeReader reader) {
		
		if (reader.getListeners() == null)
			return;
		
		ArrayList<XfpeListener> listeners;
		
		synchronized(reader) {
			if (reader.getListeners().isEmpty())
				return;
			listeners = new ArrayList<XfpeListener>(reader.getListeners().size());
			listeners.addAll(reader.getListeners());
		}
		
		XbrEvents events = null;	
		
		events = getNextBatchOfEvents(reader, 100l);
		if (events == null)
			return;
		
		String json = null;
		try {
			json = XbrJsonMapper.serializeEvents(events);
		} catch (Exception e) {
			logger.error("Failed to serialize events to JSON string. Events have been discarded.", e);
			return;
		}
		
		for (XfpeListener listener : listeners) {	
			try {
				// NOTE: We do not synchronize(reader) while talking to the listener to avoid
				// locking the reader object. Other events such as tap event may still be processed in other threads.				
				
				int rCode = Utils.doPutHttpRequest(listener.getUrl(), json);
				
			    if (rCode > 300) {
			    	logger.error("Received a " + rCode + " return code from the Controller while sending events for reader " + reader.getReaderId());
			    }
				
			} catch (MalformedURLException e) {
				logger.error("Bad listener URL: " + listener.getUrl() + ". Stopping publishing events.", e);
				synchronized(reader) { reader.getListeners().remove(listener); }
				if (events != null)
					ungetNextBatchOfEvents(reader,events);
			} catch (IOException e) {
				logger.error("Failed to send to URL: " + listener.getUrl() + ". Stopping publishing events.", e);
				synchronized(reader) { reader.getListeners().remove(listener); }
				if (events != null)
					ungetNextBatchOfEvents(reader,events);
			}
		}
	}
	
	/*
	 * Get pending events.
	 */
	private XbrEvents getNextBatchOfEvents(XfpeReader reader, Long max) {		
		XbrEvents xbrEvents = null;
		synchronized(reader) {
			if (reader.hasEvents()) {
				xbrEvents = new XbrEvents();
				xbrEvents.setReaderName(reader.getReaderId());
				if (max == null)
					max = new Long(reader.getEvents().size());
				while(max-- > 0 && reader.hasEvents())
					xbrEvents.getEvents().add(reader.dequeEvent());
			}
		}
		return xbrEvents;
	}
	
	/*
	 * Puts the events back at the head of the list.
	 */
	private void ungetNextBatchOfEvents(XfpeReader reader, XbrEvents events) {
		synchronized(reader) {
			Iterator<XbrEvent> it = events.getEvents().descendingIterator();
			while(it.hasNext())
				reader.getEvents().addFirst(it.next());			
		}
	}
	
}
