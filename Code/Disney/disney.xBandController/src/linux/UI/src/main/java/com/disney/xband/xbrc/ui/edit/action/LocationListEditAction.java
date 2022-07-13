package com.disney.xband.xbrc.ui.edit.action;

import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.lib.config.UnlinkReader;
import com.disney.xband.xbrc.lib.config.UpdateConfig;
import com.disney.xband.xbrc.lib.db.EventsLocationConfigService;
import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.db.VaLocationConfigService;
import com.disney.xband.xbrc.lib.db.XbandCommandService;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.model.ControllerInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.net.NetInterface;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.action.BaseAction;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import com.disney.xband.xbrc.ui.exception.LocationCanNotBeDeletedException;
import com.disney.xband.xbrc.ui.httpclient.Controller;

public class LocationListEditAction extends BaseAction {

	private ReaderConfig readerConfig;
	private LocationService locationService;
	private Map<Location, Collection<Reader>> locMap;
	private Collection<Reader> unlinkedReaders;
	private ControllerInfo controllerInfo;
	
	/* Location opened by default when page renders */
	private Location location;
	private Long locationId;
	private Long readerId;
	private Long newReaderId;
	private Long commandId;
	private boolean showTransmitTab = false;
	
	private static final int READER_PRONOUNCED_DEAD_AFTER_MIN = 2;
	private static final long READER_IDENTIFICATION_SEQUENCE_TIMEOUT = 30000;
	
	private static Logger logger = Logger.getLogger(LocationListEditAction.class);
	
	@Override
	public void prepare() throws Exception {
		super.prepare();

		locationService = ServiceLocator.getInstance().createService(LocationService.class);
		
		readerConfig = ReaderConfig.getInstance();
		readerConfig.initialize(UIConnectionPool.getInstance());
		
        controllerInfo = new ControllerInfo();
        controllerInfo.initialize(UIConnectionPool.getInstance());
	}
	
	/** 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		
		// all locations
		Connection conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			locMap = ReaderService.getByLocation(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed retrive readers by location.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}

		if (locMap == null){
			this.addActionMessage("Not able to connect to the database.");
		}
				
		//first page load, not a refresh, open the first location on the list
		if (locationId == null){
			if (locMap.values().size() > 0){
				locationId = locMap.keySet().iterator().next().getId();
			}
			else 
				return INPUT;	//TODO figure out a location to get
		}
		
		//opened location
		location = locationService.find(locationId);
		
		//readers that are not yet assigned to a location
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			unlinkedReaders = ReaderService.findUnlinked(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to find unlinked readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return SUCCESS;
	}
	
	public String removeReader() throws Exception 
	{
        Reader reader = null;
        boolean hasError = false;

        Connection conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			reader = ReaderService.find(conn, readerId);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to find a reader by id " + readerId);
			hasError = true;
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}

        if(reader == null) {
            logger.error("Failed to find reader by id=" + readerId + " for deletion.");
            this.addActionError("Failed to remove reader from location.");
			hasError = true;
        }

        //dissociate reader from its location
        conn = UIConnectionPool.getInstance().getConnection();
		try {
			UpdateConfig updateConfig = new UpdateConfig();
			UnlinkReader unlinkReader = new UnlinkReader();

            if(reader != null) {
			    unlinkReader.setReaderId(reader.getId());
            }

			updateConfig.addUpdateConfigItem(unlinkReader);
			
			Controller.getInstance().notifyXbrcOfConfigChange(updateConfig);
		} catch (IllegalArgumentException e) {
			logger.error("Reader ID missing. Failed to remove location association from reader.", e);
			this.addActionError("Failed to remove reader from location.");
			hasError = true;
		} catch (Exception e) {
			logger.error("Failed to remove location association from reader ID: " + (readerId != null ? readerId : ""), e);
			this.addActionError("Failed to remove reader from location.");
			hasError = true;
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}

		//all locations
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			locMap = ReaderService.getByLocation(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to retrieve a list of readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		//opened location
		location = locationService.find(locationId);
		
		//readers that are not yet assigned to a location
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			unlinkedReaders = ReaderService.findUnlinked(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to find unlinked readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return SUCCESS;
	}
	
	public String replaceReader() throws Exception 
	{
		
		Connection conn = UIConnectionPool.getInstance().getConnection();
		
		try {
			//dissociate reader from its location
			ReaderService.replaceReader(conn, readerId, newReaderId, getXbrcAddresses());
		} catch (IllegalArgumentException e) {
			logger.error("Failed to replace reader old id: " + readerId + " new id: " + newReaderId, e);
			this.addActionError("Failed to replace reader (" + e.getLocalizedMessage() + ")");
		} catch (Exception e) {
			logger.error("Failed to replace reader old id: " + readerId + " new id: " + newReaderId, e);
			this.addActionError("Failed to replace reader (" + e.getLocalizedMessage() + ")");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		//all locations
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			locMap = ReaderService.getByLocation(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to retrieve a list of readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		//opened location
		location = locationService.find(locationId);
		
		//readers that are not yet assigned to a location
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			unlinkedReaders = ReaderService.findUnlinked(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to retrieve a list of unlinked readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return SUCCESS;
	}
	
	public String deleteReader() throws Exception 
	{
		Connection conn = UIConnectionPool.getInstance().getConnection();
		
		try {
			// permanently delete reader from the DB
			ReaderService.delete(conn, readerId, true, getXbrcAddresses());
		} catch (IllegalArgumentException e) {
			logger.error("Reader ID missing. Failed to remove location association from reader.", e);
			this.addActionError("Failed to remove reader from location.");
		} catch (SQLException e) {
			logger.error("Failed to remove location association from reader ID: " + (readerId != null ? readerId : ""), e);
			this.addActionError("Failed to remove reader from location.");
		} catch(Throwable e) {
			logger.error("Failed to remove location association from reader ID: " + (readerId != null ? readerId : ""), e);
			this.addActionError("Failed to remove reader from location.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		//all locations
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			locMap = ReaderService.getByLocation(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to retrieve a list of readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		//opened location
		location = locationService.find(locationId);
		
		//readers that are not yet assigned to a location
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			unlinkedReaders = ReaderService.findUnlinked(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to find unlinked readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return SUCCESS;
	}
	
	public String addReader() throws Exception {
		if (readerId == null || locationId == null)
			return INPUT;
		
		//opened location
		location = locationService.find(locationId);
		
		Connection conn = UIConnectionPool.getInstance().getConnection();
		
		Reader addReader = new Reader();
		try {
			//retrieve the reader being added to a location
			try
			{
				addReader = ReaderService.find(conn, readerId);
			}
			catch (Exception e)
			{
				this.addActionMessage("Failed to find reader id " + readerId);
			}
			finally
			{
				UIConnectionPool.getInstance().releaseConnection(conn);
			}
			
			//associate reader and location
			addReader.setLocationId(locationId);
			
			conn = UIConnectionPool.getInstance().getConnection();
			try
			{
				ReaderService.save(conn, addReader);
			}
			catch (Exception e)
			{
				this.addActionMessage("Failed save reader id " + addReader.getMacAddress());
			}
			finally
			{
				UIConnectionPool.getInstance().releaseConnection(conn);
			}
			
		} catch (IllegalArgumentException e) {
			logger.error("Reader ID missing. Failed to add a reader to a location.", e);
			this.addActionError("Failed to add the reader to the location.");
		} catch (SQLException e) {
			logger.error("Failed to add a reader ID " + (readerId != null ? readerId : "") + " to a location ID: " + (locationId != null ? locationId : ""), e);
			this.addActionError("Failed to add reader " + (addReader.getMacAddress()) + " to location " + location.getName());
		}
		
		//all locations
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			locMap = ReaderService.getByLocation(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to retrieve a list of readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		//readers that are not yet assigned to a location
		conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			unlinkedReaders = ReaderService.findUnlinked(conn);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to find unlinked readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
				
		return SUCCESS;
	}
	
	public String deleteLocation() throws Exception {
		if (locationId == null)
			return INPUT;

		//all locations
		Connection conn = UIConnectionPool.getInstance().getConnection();
		
		try
		{
			try {
				if (isAttractionModel())
                {
					VaLocationConfigService.delete(conn, locationId);
                    EventsLocationConfigService.delete(conn, locationId);
                }
				locationService.delete(locationId, getXbrcAddresses());
			} catch (LocationCanNotBeDeletedException e){
				
				String message = e.getMessage() != null ? e.getMessage() : 
								"Exception deleting the location id " + locationId + " (" + e.getMessage() + ")";;
				
				this.addActionMessage(message);
				
			} catch (Exception e) {
				
				String errorMessage = "Exception deleting the location id " + locationId + " (" + e.getMessage() + ")";
				
				logger.error(errorMessage, e);
				
				this.addActionError(errorMessage);
			} 
					
			try
			{
				locMap = ReaderService.getByLocation(conn);
			}
			catch (Exception e)
			{
				this.addActionMessage("Failed to retrieve a list of readers.");
			}
			
			//readers that are not yet assigned to a location
			conn = UIConnectionPool.getInstance().getConnection();
			try
			{
				unlinkedReaders = ReaderService.findUnlinked(conn);
			}
			catch (Exception e)
			{
				this.addActionMessage("Failed to find unlinked readers.");
			}
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		//opened location
		if (this.hasActionErrors()){
			// location failed to delete, leave it opened
			location = locationService.find(locationId);
			return INPUT;
		} else if (locMap.keySet().size() > 0){
			// open the first location on the list
			locationId = locMap.keySet().iterator().next().getId();
			location = locationService.find(locationId);
		}
				
		return SUCCESS;
	}
	
	 public String deleteTransmitCommand() throws Exception {
			if (commandId == null)
				return INPUT;
			
			showTransmitTab = true;

			// delete the command
			Connection conn = null;

			try {
				conn = UIConnectionPool.getInstance().getConnection();
				XbandCommandService.deleteCommand(conn, commandId, true);
			} catch (Exception e) {
				this.addActionError("Failed to delete the command: " + e.getLocalizedMessage());
			} finally {
				UIConnectionPool.getInstance().releaseConnection(conn);
			}

			return execute();
		}
	
	public String notifyXbrc() throws Exception {
		
		try
		{
			Controller.getInstance().notifyXbrcOfConfigChange(null);
			this.addActionMessage("The xBRC has been updated with your changes.");
		}
		catch (Exception e)
		{
			String errorMessage = "Could not connect to the xBRC using url " + UIProperties.getInstance().getUiConfig().getControllerURL();			
			logger.error(errorMessage, e);			
			this.addActionError(errorMessage);
		}
		
		return execute();		
	}
	
	public String identifyReader() throws Exception {
		if (readerId == null || locationId == null) {
			this.addActionError("Action aborted. Can not identify a reader. Reader id or location id missing.");
			return INPUT;
		}
		
		Reader reader = null;
		Connection conn = UIConnectionPool.getInstance().getConnection();
		try {
			reader = ReaderService.find(conn, readerId);
		} catch (Exception e) {
			this.addActionError("Action aborted. Reader not found.");
			return INPUT;
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		if (reader == null) {
			this.addActionError("Action aborted. Reader not found.");
			return INPUT;
		}
		
		if (reader.getType() != null && !reader.getType().hasLight())
			return INPUT;
		
		Location location = null;
		try {
			location = locationService.find(locationId);
		} catch (Exception e) {
			this.addActionError("Action aborted. Location not found.");
			return INPUT;
		}
		
		boolean deadReader = (reader.getTimeLastHello() == null || 
				System.currentTimeMillis() - reader.getTimeLastHello().getTime() > (READER_PRONOUNCED_DEAD_AFTER_MIN * 60 * 1000));
		
		if (deadReader)
		{
			this.addActionMessage("Action aborted. This reader is not responsive at the moment. Please try again later.");
			return INPUT;
		}

		ReaderInfo readerInfo = new ReaderInfo();
		readerInfo.setId(reader.getId().intValue());
		readerInfo.setIpAddress(reader.getIpAddress());
		readerInfo.setPort(reader.getPort());
		readerInfo.setType(reader.getType());
		
		int httpStatusCode = ReaderApi.readerPlayIdentificationSequence(
				readerInfo, 
				"identify", 
				READER_IDENTIFICATION_SEQUENCE_TIMEOUT);
		
		if (httpStatusCode < 400)
			this.addActionMessage("Request to reader succeeded.");
		else
			this.addActionMessage("Request to reader failed with http status code " + httpStatusCode);
		
		return SUCCESS;
	}
	
	public String restartReader() throws Exception {
		if (readerId == null || locationId == null) {
			this.addActionError("Action aborted. Can not restart the reader. Reader id or location id missing.");
			return INPUT;
		}
		
		Reader reader = null;
		Connection conn = UIConnectionPool.getInstance().getConnection();
		try {
			reader = ReaderService.find(conn, readerId);
		} catch (Exception e) {
			this.addActionError("Action aborted. Reader not found.");
			return INPUT;
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		if (reader == null) {
			this.addActionError("Action aborted. Reader not found.");
			return INPUT;
		}
		
		try {
			location = locationService.find(locationId);
		} catch (Exception e) {
			this.addActionError("Action aborted. Location not found.");
			return INPUT;
		}
		
		boolean deadReader = (reader.getTimeLastHello() == null || 
				System.currentTimeMillis() - reader.getTimeLastHello().getTime() > (READER_PRONOUNCED_DEAD_AFTER_MIN * 60 * 1000));
		
		if (deadReader)
		{
			this.addActionMessage("Action aborted. This reader is not responsive at the moment. Please try again later.");
			return INPUT;
		}

		ReaderInfo readerInfo = new ReaderInfo();
		readerInfo.setId(reader.getId().intValue());
		readerInfo.setIpAddress(reader.getIpAddress());
		readerInfo.setPort(reader.getPort());
		readerInfo.setType(reader.getType());
		
		int httpStatusCode = ReaderApi.sendRestartReader(readerInfo);
		
		if (httpStatusCode < 400)
			this.addActionMessage("Request to reader succeeded.");
		else
			this.addActionMessage("Request to reader failed with http status code " + httpStatusCode);
		
		return SUCCESS;
	}
	
	public String rebootReader() throws Exception {
		if (readerId == null || locationId == null) {
			this.addActionError("Action aborted. Can not reboot the reader. Reader id or location id missing.");
			return INPUT;
		}
		
		Reader reader = null;
		Connection conn = UIConnectionPool.getInstance().getConnection();
		try {
			reader = ReaderService.find(conn, readerId);
		} catch (Exception e) {
			this.addActionError("Action aborted. Reader not found.");
			return INPUT;
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		if (reader == null) {
			this.addActionError("Action aborted. Reader not found.");
			return INPUT;
		}
		
		try {
			location = locationService.find(locationId);
		} catch (Exception e) {
			this.addActionError("Action aborted. Location not found.");
			return INPUT;
		}
		
		boolean deadReader = (reader.getTimeLastHello() == null || 
				System.currentTimeMillis() - reader.getTimeLastHello().getTime() > (READER_PRONOUNCED_DEAD_AFTER_MIN * 60 * 1000));
		
		if (deadReader)
		{
			this.addActionMessage("Action aborted. This reader is not responsive at the moment. Please try again later.");
			return INPUT;
		}

		ReaderInfo readerInfo = new ReaderInfo();
		readerInfo.setId(reader.getId().intValue());
		readerInfo.setIpAddress(reader.getIpAddress());
		readerInfo.setPort(reader.getPort());
		readerInfo.setType(reader.getType());
		
		int httpStatusCode = ReaderApi.sendRebootReader(readerInfo);
		
		if (httpStatusCode < 400)
			this.addActionMessage("Request to reader succeeded.");
		else
			this.addActionMessage("Request to reader failed with http status code " + httpStatusCode);
		
		return SUCCESS;
	}

	public Map<Location, Collection<Reader>> getLocMap() {
		return locMap;
	}

	public Long getLocationId() {
		return locationId;
	}
	
	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the readerId
	 */
	public Long getReaderId() {
		return readerId;
	}

	/**
	 * @param readerId the readerId to set
	 */
	public void setReaderId(Long readerId) {
		this.readerId = readerId;
	}

	public Collection<Reader> getUnlinkedReaders() {
		return unlinkedReaders;
	}

	public ReaderConfig getReaderConfig() {
		return readerConfig;
	}

	public Long getNewReaderId()
	{
		return newReaderId;
	}

	public void setNewReaderId(Long newReaderId)
	{
		this.newReaderId = newReaderId;
	}
	
	public boolean isAttractionModel()
	{
		return controllerInfo.getModelName().contains("attraction");
	}

	public Long getCommandId() {
		return commandId;
	}

	public void setCommandId(Long commandId) {
		this.commandId = commandId;
	}

	public boolean getShowTransmitTab() {
		return showTransmitTab;
	}

	public void setShowTransmitTab(boolean showTransmitTab) {
		this.showTransmitTab = showTransmitTab;
	}
	
	private Collection<String> getXbrcAddresses() {
		
		Collection<String> myAddresses = new LinkedList<String>();
		Collection<String> ips = null;
		String hostname = null;
		
		try {
			ips = NetInterface.getOwnIpAddress(null);
		} catch (SocketException e) {
			logger.error("Failed to retrieve own IP addresses", e);
		}
		
		try {
			hostname = NetInterface.getHostname();
		} catch (Exception e) {
			logger.error("Failed to retrieve own hostname", e);
		}
		
		myAddresses = new LinkedList<String>();
		if (ips != null)
			myAddresses.addAll(ips);
		if (hostname != null)
			myAddresses.add(hostname);
		
		Collection<String> ret = new LinkedList<String>();
		ret.addAll(myAddresses);
		
		if (controllerInfo.getVipAddress() != null && !controllerInfo.getVipAddress().isEmpty()
				&& !controllerInfo.getVipAddress().equals("#"))
			ret.add(controllerInfo.getVipAddress());
		
		return ret;
	}
}
