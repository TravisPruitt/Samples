package com.disney.xband.xbrc.Controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.ReaderExecutor;
import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.TransmitLocationStatus;
import com.disney.xband.xbrc.lib.entity.TransmitReaderState;
import com.disney.xband.xbrc.lib.entity.TransmitReaderStatus;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

/*
 * The transmit manager is responsible for keeping track of which reader is 
 * currently transmitting at each location capable of transmitting. It responds
 * to events such as readers saying hello and readers not responding.
 */
public class TransmitManager 
{
	private static Logger logger = Logger.getLogger(TransmitManager.class);
	
	private class TransmitLocation
	{
		private LocationInfo location;
		// List of transmitters in HA priority order
		private List<ReaderInfo> readers;
		private ReaderInfo currentTransmitter;
		
		public LocationInfo getLocation() 
		{
			return location;
		}
		public void setLocation(LocationInfo location) 
		{
			this.location = location;
		}
		public List<ReaderInfo> getReaders() 
		{
			return readers;
		}
		public void setReaders(List<ReaderInfo> readers) 
		{
			this.readers = readers;
		}
		public ReaderInfo getCurrentTransmitter() 
		{
			return currentTransmitter;
		}
		public void setCurrentTransmitter(ReaderInfo currentTransmitter) 
		{
			this.currentTransmitter = currentTransmitter;
		}
	}
	
	private class HaPriorityComparator implements Comparator<ReaderInfo>
	{
		@Override
		public int compare(ReaderInfo arg0, ReaderInfo arg1)
		{
			// handle null cases
			if (arg0 == null && arg1 != null)
				return -1;
			else if (arg0 != null && arg1 == null)
				return 1;
			else if (arg0 == null && arg1 == null)
				return 0;

			return new Integer(arg0.getTransmitterHaPriority()).compareTo(arg1.getTransmitterHaPriority());
		}
	}
	
	// All locations capable of transmitting
	private HashMap<String, TransmitLocation> locations = new HashMap<String, TransmitLocation>();
	private Date initTime = null;
	private Long helloWaitTime = 70000l;
	// Need to keep track of which readers we have cleared the transmit commands, in case they die
	// while transmitting and come back to life later on.
	private HashMap<String, Boolean> resetReaders = new HashMap<String, Boolean>();
	
	public static TransmitManager INSTANCE = new TransmitManager();
	private TransmitManager() {}
	
	/* 
	 * This should be called every time configuration is read
	 * from the database. This will almost certainly be called from the 
	 * xBRC main process thread.
	 */
	public void reinitialize(Map<Integer, LocationInfo> locInfos)
	{
		synchronized(this)
		{
			initTime = new Date();
			locations.clear();
			resetReaders.clear();
			
			//
			// Find all locations and readers that are capable of transmitting.
			//
			
			for (LocationInfo li: locInfos.values())
			{
				if (li.getTransmitCommands() == null || li.getTransmitCommands().isEmpty())
					continue;
				
				if (li.getReaders() == null)
					continue;
				
				List<ReaderInfo> readers = new LinkedList<ReaderInfo>();
				for (ReaderInfo ri : li.getReaders())
				{
					if (!ri.getType().equals(ReaderType.lrr) || !ri.isTransmitter())
						continue;
					readers.add(ri);
				}
				
				if (readers.isEmpty())
				{
					logger.warn("There are reader transmit commands configured at location " + li.getName() + 
							", but there are no transmit readers available/enabled at this location.");
					continue;
				}
				
				logger.trace("Found location (" + li.getName() + ") configured for transmitter mode");
				
				Collections.sort(readers, new HaPriorityComparator());
				
				TransmitLocation tl = new TransmitLocation();
				tl.setLocation(li);
				tl.setReaders(readers);
				
				locations.put(li.getName(), tl);
			}
		}
	}
	
	/*
	 * This should be called every time a reader says hello. This will be called
	 * 
	 */
	public void onReaderSaysHello(ReaderInfo ri, boolean firstTime)
	{	
		TransmitLocation tl = null;
		
		// Be careful not do any external (REST) calls from inside the synchronized block
		// because the same lock is used by the xBRC main processing thread when calling
		// to obtain the currently transmitting reader for a location.
		
		synchronized(this)
		{
			// we must be initialized to continue
			if (initTime == null)
				return;
			
			if (!ri.getType().equals(ReaderType.lrr))
				return;
			
			tl = locations.get(ri.getLocation().getName());			
						
			if (tl == null || !ri.isTransmitter())
			{
				// We clear long range reader commands for readers that are not transmitters since it is possible that the reader
				// transmit flag was recently turned off or the reader was moved to a location with no transmit commands.
				checkResetReader(ri);
				return;
			}
			
			// First see if we already know about this reader. Add it if not.
			if (!tl.getReaders().contains(ri))
				tl.getReaders().add(ri);
			
			// Wait for hello from all readers before making any decisions.
			if (waitingForHello(tl))
				return;
			
			if (tl.getCurrentTransmitter() != null)
			{
				// Check to see if we are currently using this reader as a transmitter.
				if (tl.getCurrentTransmitter() == ri)
				{
					// When reader says Hello for the first time after being restarted we need to send it the transmit commands
					if (firstTime)
						ReaderExecutor.getInstance().sendBandCommands(tl.getLocation().getTransmitCommands(), ri, null, ri.getLocation().getTransmitZoneGroup());
					
					return;
				}
				
				if (Controller.getInstance().isReaderAlive(tl.getCurrentTransmitter()))
				{
					// Check to see the current working transmitter is the one of higher priority.
					if (tl.getCurrentTransmitter().getTransmitterHaPriority() <= ri.getTransmitterHaPriority())
					{
						checkResetReader(ri);
						return;
					}
				
					logger.info("A higher priority transmitter said hello. Current transmitter: " + 
							tl.getCurrentTransmitter().getName() + " New transmitter: " + ri.getName());
				}
				else
				{
					logger.warn("A trasmitting reader " + tl.getCurrentTransmitter().getName() + " at " + 
							tl.getCurrentTransmitter().getIpAddress() + 
							" stopped communicating at location " + tl.getLocation().getName() );
				}
			}
			
			tl.setCurrentTransmitter(null);
			
			for (ReaderInfo r : tl.getReaders())
			{
				if (Controller.getInstance().isReaderAlive(r))
				{
					tl.setCurrentTransmitter(r);
					break;
				}
			}
			
			if (tl.getCurrentTransmitter() == null)
			{
				logger.error("There are currenlty no transmit readers that are alive at location " + 
						tl.getLocation().getName());
				return;
			}
			
			logger.info("Switching transmitting to reader " + ri.getName() + " at " + ri.getIpAddress() + 
					" at location " + tl.getLocation().getName());
			
			// We will be resetting all readers except the new current transmitter.
			resetReaders.clear();
			
			for (ReaderInfo r : tl.getReaders())
			{	
				if (r == tl.getCurrentTransmitter())
					ReaderExecutor.getInstance().sendBandCommands(tl.getLocation().getTransmitCommands(), r, null, r.getLocation().getTransmitZoneGroup());
				else
				{
					if (Controller.getInstance().isReaderAlive(r))
						checkResetReader(r);
				}
			}
		}
	}
	
	/*
	 * Send commands to the appropriate reader at a location.
	 */
	public boolean sendCommands(List<XbrBandCommand> commands, LocationInfo loc, XMIT_MODE mode, String group)
	{
		synchronized(this)
		{
			TransmitLocation tl = locations.get(loc.getName());
			if (tl == null)
				return false;
			
			ReaderInfo ri = tl.getCurrentTransmitter();
			
			if (ri == null)
				return false;
			
			ReaderExecutor.getInstance().sendBandCommands(commands, ri, XMIT_MODE.REPLY, null);
			return true;
		}
	}
	
	public TransmitReaderStatus getTransmitReaderStatus()
	{
		TransmitReaderStatus status = new TransmitReaderStatus();
		
		synchronized(this)
		{
			if (initTime == null)
				return status;
			
			for (TransmitLocation tl : locations.values())
			{
				TransmitLocationStatus tls = new TransmitLocationStatus(tl.getLocation());
				status.getLocations().add(tls);
				
				if (tl.getCurrentTransmitter() == null)
				{	
					if (waitingForHello(tl))
					{
						Date now = new Date();
						tls.setStatusMessage("Waiting for all readers to say hello for another " + 
								(initTime.getTime() + helloWaitTime - now.getTime())/1000 + " seconds");
					}
					else
					{
						tls.setStatusMessage("There are currently no transmit readers responding at this location.");
					}
				}
				else
					tls.setCurrentTransmitter(tl.getCurrentTransmitter().getName());
				
				for (ReaderInfo ri : tl.getReaders())
				{
					TransmitReaderState trs = new TransmitReaderState(ri);
					trs.setAlive(Controller.getInstance().isReaderAlive(ri));
					trs.setCurrentTransmitter(ri == tl.getCurrentTransmitter());
					tls.getReaders().add(trs);
				}
			}
			
			return status;
		}
	}
	
	/*
	 * Reset the transmit commands for a reader if we have not done so already.
	 */
	private void checkResetReader(ReaderInfo ri)
	{
		if (resetReaders.containsKey(ri.getName()))
			return;
			
		try {
			ReaderExecutor.getInstance().sendDeleteBandCommands(ri);
			resetReaders.put(ri.getName(), true);
		} catch (Exception e) {
			logger.warn("Failed to clear band commands for a reader: " + ri.getName(), e);
		}
	}
	
	private boolean waitingForHello(TransmitLocation tl)
	{
		// We must give readers at least a minute to make sure they all said hello
		// before deciding which reader will be the current transmitter. The only
		// exception is if there is only one transmitter in a location or the reader
		// with the highest priority is alive.
					
		Date now = new Date();
		if (tl.getReaders().size() > 1)
		{
			if (now.getTime() - initTime.getTime() < helloWaitTime)
			{
				// If the primary reader is alive then we are good to go.
				return !Controller.getInstance().isReaderAlive(tl.getReaders().get(0));
			}
		}
		
		return false;
	}
}
