package com.disney.xband.xbrc.lib.model;

import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.xbrc.lib.entity.HAMessage;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;

public interface IXBRCModel 
{
	// Ask model to initialize itself. Called only once.
	void initialize();
	
	// Allow the model to do their own processing, regardless if there are any events.
	void beforeProcessEvents() throws Exception;
	
	// Ask model to process raw events
	void processEvents(List<XbrEvent> alEvents) throws Exception;
	
	// Allow the model to do their own processing, regardless if there are any events.
	void afterProcessEvents() throws Exception;
	
	// Ask model to return list of known bands from readers at a given set of locations
	public List<String> getBandsPresentAtLocations(Set<Long> locationIds);
	
	// Ask model to read it's configuration
	void readConfiguration();
	
	// Whether to log the Reader event into the EKG file.
	boolean isEventLogged(String bandId);
	
	// ask model to store its configuration to xml
	String storeConfiguration(int cIndentLevel) throws Exception;
	
	// ask model to restore its configuration from xml
	void restoreConfiguration(Connection conn, String sXML) throws Exception;
	
	// Ask model to clean up any dependencies prohibiting the controller from deleting data from a table
	// The keys may be null if all the rows are deleted. Otherwise they contain the keys of the records
	// about to be deleted.
	void beforeConfigurationDeleteFromTable(Connection conn, String tableName, Collection<Object> keys) throws Exception;
	
	// ask model to provide status information
	void formatStatus(XbrcStatus status);
	
	// ask model to provide reader status information
	void formatReaderStatus(ReaderInfo readerInfo);
	
	// ask model to process configuration requests
	void handlePropertiesRead(XbrcConfig config, Connection conn) throws Exception;
	void handlePropertiesWrite(XbrcConfig config, Connection conn) throws Exception;
	
	// ask model to persist/reload its state
	void storeState(Connection conn);
	void restoreState(Connection conn, Date dtLastDataStore);
	String serializeStateToXML(String sGuestID);
	void deserializeStateFromXML(String sXML);
	
	// clear the GST and any other state
	void clear();
	
	// process incoming message from external system (must set response code!)
	void processExternal(Request request, Response response, String sPath);
	
	// process HA messages
	void processHAMessages(HAMessage[] aHA);
	
	// return the required Mayhem database schema version
	String getRequiredSchemaVersion();
	
	// Return the count of resolver threads if the model is using asynchronous IDMS lookups.
	int getIDMSResolverThreads();
	
	// ask model if the tap sequence is to be automatically enabled for the reader
	boolean isTapSequenceEnabled(ReaderInfo ri);
	
	// ask model if the idle sequence is to be automatically enabled for the reader
	boolean isIdleSequenceEnabled(ReaderInfo ri);
	
	// ask model if the location is of a type WAYPOINT
	boolean isWaypointLocationType(LocationInfo li);
	
	// ask model to flatten the model specific section of /currentconfiguration xml
	void flattenModel(org.jdom.Element images, Map<String, String> results, Set<String> exclude, char pathSeparator);
	
	// ask model to register any scheduling item metadata
	void registerSchedulerItemsMetadata(XconnectScheduler scheduler) throws Exception;
	
	// ask model to add any default scheduling items
	void addDefaultSchedulerItems(XconnectScheduler scheduler) throws Exception;
	
	// let the model know that the xBRC is shutting down
	void onShutdown();
}
