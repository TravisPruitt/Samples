package com.disney.xband.xbrc.ui.edit.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.lib.db.OmniServerService;
import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.OmniServer;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.model.XbrcModel;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.XbrcDataCache;
import com.disney.xband.xbrc.ui.action.BaseAction;
import com.disney.xband.xbrc.ui.db.Data;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class CreateReaderAction extends BaseAction {

	private static Logger logger = Logger.getLogger(CreateReaderAction.class);
	private LocationService locationService;
	private Reader reader;
	private ReaderConfig readerConfig;
	private Map<Integer, String> readerTypes;
	private int readerType;
	private Map<Integer, String> locationTypes;
	private Map<Long, String> locations;
	private Long selectedLocation;
	private String status;
	
	private List<Integer> readerDefaultChannelGroups;
	private List<Integer> readerChosenChannelGroups;
	private Map<Integer, String> readerAvailableChannelGroups;
	
	/*
	 * Park entry specific
	 */
	private Collection<OmniServer> omnisForGrabs;
	private Collection<String> addedOmniServers;
	
	
    private void updateChannelGroups(Reader reader)
    {
		readerDefaultChannelGroups = new LinkedList<Integer>();
		readerAvailableChannelGroups = new LinkedHashMap<Integer, String>();
		if (reader == null || reader.getAntennas() == null)
		{
			// xBR4 has 8 antennas (channels), which can be turned off in pairs, except the first pair which cannot be turned off.
			
			for (int i = 0; i < 4; i++) // 3 antenna groups. Group 0: [2, 3], Group 1: [4, 5], Group 2: [6, 7]
			{
				String str = "";
				for (int j = 0; j < 2; j++)
				{
					int value = i * 2 + j;
					if (j != 0)
						str = str + ", ";
					str = str + "Rx " + value;
				}
				readerAvailableChannelGroups.put(i, str);
				readerDefaultChannelGroups.add(i);
			}
		}
		else if (reader.getAntennas() != null)
		{
			List<Boolean> antennas = reader.getAntennas();
		
			for (int i = 0; i < antennas.size(); i += 2) // xBR4
			{
				String str = "";
				for (int j = 0; j < 2; j++)
				{
					int value = i + j;
					if (j != 0)
						str = str + ", ";
					str = str + "Rx " + value;
				}
				readerAvailableChannelGroups.put(i / 2, str);
				
				Boolean channel = false;
				if (i == 0)
					channel = true;
				else
				{
					Boolean value1 = antennas.get(i);
					Boolean value2 = antennas.get(i + 1);
					if (value1 == null || Boolean.TRUE.equals(value1) || value2 == null || Boolean.TRUE.equals(value2))
							channel = true;
				}
				
				if (Boolean.TRUE.equals(channel))
				{
					readerDefaultChannelGroups.add(i / 2);
				}
			}
		}
    }
    
    @Override
    public void validate() {
    	super.validate();
    }
    
	@Override
	public void prepare() throws Exception {
		super.prepare();
		
		status = UIProperties.getInstance().getControllerInfo().getModel();
		
		locationService = ServiceLocator.getInstance().createService(LocationService.class);
		
		locationTypes = locationService.getLocationTypes(true);
		
		readerConfig = ReaderConfig.getInstance();
		readerConfig.initialize(UIConnectionPool.getInstance());
		
		// list of existing locations
		Collection<Location> locations = locationService.findAll(false);
		if (locations != null && locations.size() > 0){
			this.locations = new LinkedHashMap<Long, String>();
			for (Location l : locations)
				this.locations.put(l.getId(), l.getName());
		}
		
		readerTypes = new LinkedHashMap<Integer, String>();	
		for (ReaderType rt : ReaderType.values())
			readerTypes.put(rt.ordinal(), rt.getDescription());
	
		updateChannelGroups(reader);
	}
	
	@Override
	public String execute() throws Exception {
		
		reader = new Reader();
		reader.setGroup("UNKNOWN");
		
		locationTypes = locationService.getLocationTypes(false);
		
		if (isParkEntry())
		{
			Connection conn = null;
			try {
				conn = UIConnectionPool.getInstance().getConnection();
				
				omnisForGrabs = OmniServerService.getAllMissingFromReader(conn, null);
				
			} finally {
				UIConnectionPool.getInstance().releaseConnection(conn);
			}
		}
		
		return SUCCESS;
	}
	
	public String saveNewReader() throws Exception {
		
		try 
		{	
			// check for name's uniqueness
			boolean nameTaken = true;
			
			Connection conn = UIConnectionPool.getInstance().getConnection();
			try
			{
				nameTaken = ReaderService.isNameTaken(conn, reader.getReaderId());
			}
			catch (Exception e)
			{
				this.addActionMessage("Failed verify reader's name.");
			}
			finally
			{
				UIConnectionPool.getInstance().releaseConnection(conn);
			}
			
			if (!nameTaken){
				// set selected type
				reader.setType(ReaderType.getByOrdinal(readerType));
			
				//set selected location
				reader.setLocationId(selectedLocation);
				
				try {
					List<Boolean> antennas = new ArrayList<Boolean>(8);
					
					antennas.add(0, true);
					antennas.add(1, true);
					for (Integer i = 1; i < 4; i++)
					{
						if (readerChosenChannelGroups != null && readerChosenChannelGroups.contains(i))
						{
							antennas.add(i * 2, true);
							antennas.add(i * 2 + 1, true);
						}
						else
						{
							antennas.add(i * 2, false);
							antennas.add(i * 2 + 1, false);
						}
					}
					reader.setAntennas(antennas);
				}
				catch (Exception ex)
				{
				}
				
				
				// save the reader
				conn = UIConnectionPool.getInstance().getConnection();
				try
				{
					ReaderService.save(conn, reader);
				}
				catch (Exception e)
				{
					logger.error("Failed to save reader " + reader.getMacAddress(), e);
					this.addActionError("Failed to save reader: " + e.getLocalizedMessage());
					return INPUT;
				}
				finally
				{
					UIConnectionPool.getInstance().releaseConnection(conn);
				}
				
				// associate omni servers
				if (isParkEntry()) 
				{
					if (this.addedOmniServers != null && this.addedOmniServers.size() > 0)
					{
						conn = UIConnectionPool.getInstance().getConnection();
						
						try {
							Map<Integer, Integer> omnis = new HashMap<Integer, Integer>();
							int colenIndex = -1;
							for (String key : this.addedOmniServers)
							{
								colenIndex = key.indexOf(':');
								omnis.put(
									Integer.parseInt(key.substring(0, colenIndex)), 
									Integer.parseInt(key.substring(colenIndex + 1)));
							}
							
							OmniServerService.associateReader(conn, omnis, reader.getId());
						} catch (Exception e){
							this.addActionMessage("Failed to associate omni servers.");
						} finally {
							UIConnectionPool.getInstance().releaseConnection(conn);
						}
					}
				}
			} else {
				this.addActionError("The name " + reader.getReaderId() + 
						" is already taken. Please choose a different name for the new reader.");
				reader.setReaderId(null);
				
				return INPUT;
			}
				
		} catch (Exception e){
			this.addActionError("Failed to add a reader.");
			return INPUT;
		}
		
		return SUCCESS;
	}

	/**
	 * @param location the location to set
	 */
	public void setReader(Reader reader) {
		this.reader = reader;
	}
	
	public Reader getReader(){
		return reader;
	}
	
	public Map<Integer, String> getReaderTypes(){
		return readerTypes;
	}
	
	public Map<Integer, String> getLocationTypes(){
		return locationTypes;
	}

	public ReaderConfig getReaderConfig() {
		return readerConfig;
	}

	public void setReaderConfig(ReaderConfig readerConfig) {
		this.readerConfig = readerConfig;
	}

	/**
	 * @return the locations
	 */
	public Map<Long, String> getLocations() {
		return locations;
	}

	public Long getSelectedLocation() {
		return selectedLocation;
	}

	public void setSelectedLocation(Long selectedLocation) {
		this.selectedLocation = selectedLocation;
	}

	public int getReaderType() {
		return readerType;
	}

	public void setReaderType(int readerType) {
		this.readerType = readerType;
	}

	public Collection<OmniServer> getOmnisForGrabs()
	{
		return omnisForGrabs;
	}
	
	public boolean isParkEntry(){
		if (status != null && XbrcModel.PARKENTRY.getXbrcModelClass().equals(status)){
			return true;
		}
		
		return false;
	}

	public void setAddedOmniServers(Collection<String> addedOmniServers)
	{
		this.addedOmniServers = addedOmniServers;
	}
	
	public Map<Integer, String> getReaderAvailableChannelGroups()
	{
		return readerAvailableChannelGroups;
	}
	
	public void setReaderChosenChannelGroups(List<Integer> collection)
	{
		readerChosenChannelGroups = collection;
	}
	
	public List<Integer> getReaderChosenChannelGroups()
	{
		return readerChosenChannelGroups;
	}

	public List<Integer> getReaderDefaultChannelGroups()
	{
		return readerDefaultChannelGroups;
	}
}
