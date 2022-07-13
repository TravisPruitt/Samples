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
import org.apache.struts2.ServletActionContext;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.Sequence;
import com.disney.xband.common.lib.Sequences;
import com.disney.xband.lib.controllerapi.ReaderSequence;
import com.disney.xband.lib.controllerapi.ReaderSequenceInfo;
import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.lib.db.EventsLocationConfigService;
import com.disney.xband.xbrc.lib.db.OmniServerService;
import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.db.VaLocationConfigService;
import com.disney.xband.xbrc.lib.db.XbandCommandService;
import com.disney.xband.xbrc.lib.entity.AnnotationHelper;
import com.disney.xband.xbrc.lib.entity.EventsLocationConfig;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.OmniReaderAgregate;
import com.disney.xband.xbrc.lib.entity.OmniServer;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.VaLocationConfig;
import com.disney.xband.xbrc.lib.entity.VaLocationConfig.VaAlgorithm;
import com.disney.xband.xbrc.lib.model.ControllerInfo;
import com.disney.xband.xbrc.lib.model.XbrcModel;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import com.disney.xband.xbrc.ui.httpclient.Controller;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class LocationListEditAjaxAction extends ActionSupport implements Preparable {

    private LocationService locationService;
    private Long locationId;
    private Location location;
    private VaLocationConfig vaLocationConfig;
    private boolean vaLocationConfigEnabled = false;
    private EventsLocationConfig eventsLocationConfig;
    private boolean locationEventsEnabled = false;
    private ControllerInfo controllerInfo;

    private ReaderConfig readerConfig;
    private Long readerId;
    private Reader reader;
    private Map<Integer, String> locationTypes;

    private Map<Integer, String> readerTypes;
    private int readerType;

    private ReaderSequenceInfo sequenceInfo;
    private ReaderSequenceInfo sequenceInfoNoColors;

    private boolean readerIsTransmitter;

    private Map<Long, String> locations;
    private String status;
    private boolean showTransmitTab = false;

    /*
      * Commands mapped with their recipient location's names,
      * since displaying location ids is not very informative.
      */
    private Map<XbrBandCommand, String> commands;
    private Long commandId;
    
    /*
     * List of long range readers used as transmitters at this location.
     * The list is sorted according to "isTransmitter" and "transmitterHaPriority".
     */
    private Collection<Reader> transmitReaders;
    private Long[] transmitterId;
    private Integer[] transmitterHaPriority;
    private Long[] transmitter;

    /*
      * Park entry specific
      */
    private Collection<OmniServer> omnisForGrabs;
    private Collection<OmniReaderAgregate> omnisAssociated;
    private Collection<String> addedOmniServers;

    private Collection<String> successSequences;

    /*
     * Choose which antennas are active on an xBR4.
     */
	private List<Integer> readerDefaultChannelGroups;
	private List<Integer> readerChosenChannelGroups;
	private Map<Integer, String> readerAvailableChannelGroups;

    private static Logger logger = Logger.getLogger(LocationListEditAjaxAction.class);

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
    public void prepare() throws Exception {
        status = UIProperties.getInstance().getControllerInfo().getModel();

        locationService = ServiceLocator.getInstance().createService(LocationService.class);
        
        readerConfig = ReaderConfig.getInstance();
        readerConfig.initialize(UIConnectionPool.getInstance());
        controllerInfo = new ControllerInfo();
        controllerInfo.initialize(UIConnectionPool.getInstance());

        readerTypes = new LinkedHashMap<Integer, String>();
        for (ReaderType rt : ReaderType.values())
            readerTypes.put(rt.ordinal(), rt.getDescription());

        try
        {
            sequenceInfo = Controller.getInstance().getReaderSequenceInfo(true);
            sequenceInfoNoColors = Controller.getInstance().getReaderSequenceInfo(false);
        }
        catch(Exception e)
        {
            logger.error("Failed to retrieve the reader media sequence names from the xBRC: ", e);
        }
        
		updateChannelGroups(this.reader);
    }

    /**
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @Override
    public String execute() throws Exception {
        if (locationId == null && readerId == null)
            return INPUT;        

        if (locationId != null)
            location = locationService.find(locationId);
        
        // If we cannot connect to the xBRC we must at least populate the sequence with the ones we already use
        if (sequenceInfo == null)
            fixSequenceNames();
        
        if (sequenceInfoNoColors == null)
        	fixSequenceNamesNoColors();

        Connection conn = null;
        try
        {
        	conn = UIConnectionPool.getInstance().getConnection();
        
        	if (isAttractionModel())
        	{
        		if (location != null)
                {
        			vaLocationConfig = VaLocationConfigService.find(conn, location.getId());
                    eventsLocationConfig = EventsLocationConfigService.find(conn, location.getId());
                }
	        
        		vaLocationConfigEnabled = vaLocationConfig != null;
                locationEventsEnabled = eventsLocationConfig != null;
	        
		        // must have vaLocationConfig object to populate the form with default values
		        if (vaLocationConfig == null)
		        	vaLocationConfig = new VaLocationConfig();
                if ( eventsLocationConfig == null )
                    eventsLocationConfig = new EventsLocationConfig();
        	}
        	
	        if (readerId != null){	
				try
				{
					reader = ReaderService.find(conn, readerId);
					readerType = reader.getType().ordinal();
					readerIsTransmitter = reader.isTransmitter();
					updateChannelGroups(reader);
				}
				catch (Exception e)
				{
					this.addActionMessage("Failed verify reader's name.");
				}
	
	            if (isParkEntry())
	            {
                    omnisAssociated = OmniServerService.getAllForReader(conn, readerId);
                    omnisForGrabs = OmniServerService.getAllMissingFromReader(conn, readerId);
	            }	            
	        }
	        
	        if (location != null)
        		initLrrTransmitPage(conn);
        } 
        finally 
        {
        	if (conn != null)
        		UIConnectionPool.getInstance().releaseConnection(conn);
        }
	
        locationTypes = locationService.getLocationTypes(true);

        return SUCCESS;
    }

    private void fixSequenceNames()
    {
        sequenceInfo = new ReaderSequenceInfo();
        if (location == null)
        {
            sequenceInfo.getReaderSequences().add(new ReaderSequence("null", false));
            return;
        }

        ReaderSequence sequence;
        if (location.getSuccessSequence() != null)
        {
            // We need to parse existing success sequence names just as we
            // do elsewhere.
            Sequences successSequences = Sequences.parseSequenceNames(location.getSuccessSequence());
            for ( Sequence successSequence : successSequences ) {
                sequence = new ReaderSequence(successSequence.getName(), false);
                if ( !sequenceInfo.getReaderSequences().contains(sequence) )
                    sequenceInfo.getReaderSequences().add(sequence);
            }
        }
        if (location.getFailureSequence() != null)
        {
            sequence = new ReaderSequence(location.getFailureSequence(), false);
            if ( !sequenceInfo.getReaderSequences().contains(sequence) )
                sequenceInfo.getReaderSequences().add(sequence);
        }
        if (location.getErrorSequence() != null)
        {
            sequence = new ReaderSequence(location.getErrorSequence(), false);
            if ( !sequenceInfo.getReaderSequences().contains(sequence) )
                sequenceInfo.getReaderSequences().add(sequence);
        }
    }
    
    private void fixSequenceNamesNoColors()
    {
        sequenceInfoNoColors = new ReaderSequenceInfo();
        if (location == null)
        {
        	sequenceInfoNoColors.getReaderSequences().add(new ReaderSequence("null", false));
            return;
        }

        ReaderSequence sequence;       
        if (location.getTapSequence() != null)
        {
            sequence = new ReaderSequence(location.getTapSequence(), false);
            if ( !sequenceInfoNoColors.getReaderSequences().contains(sequence) )
                sequenceInfoNoColors.getReaderSequences().add(sequence);
        }
        
        if (location.getIdleSequence() != null)
        {
            sequence = new ReaderSequence(location.getIdleSequence(), false);
            if ( !sequenceInfoNoColors.getReaderSequences().contains(sequence) )
                sequenceInfoNoColors.getReaderSequences().add(sequence);
        }
    }
   
    public static String sanitizeLocationName(String name, int maxLength) {
         name = name.trim();

         // Strip HTML tags from the location name.
         name = name.replaceAll("\\<.*?\\>", "");

         // Remove invalid characters.
         name = name.replaceAll("[<>]", "");

         // Trim the name, if necessary.
         if (name.length() > maxLength)
         {
        	 name = name.substring(0, maxLength - 1);
         }
         
         return name;
    }
    
    public String saveLocation() throws Exception {
        if (location == null || location.getName() == null)
            return INPUT;

        //prevent data too long DB exception
        if (location != null && location.getName() != null)
        {
        	String name = sanitizeLocationName(location.getName(), AnnotationHelper.getColumnLength(location, "name"));
            
        	// Once trimmed, we need to make sure that we've got something
            // to save. We don't need to check for NULL since it's been
            // done.
            if ( name.isEmpty() ) {
            	this.addActionError("Invalid location name has been specified.");
            	return INPUT;
            }

            location.setName(name);
        }
		
        Collection<Location> existingLocations = locationService.findAll(true);
        
		for (Location loc : existingLocations) {
			if (loc.getName().equalsIgnoreCase(location.getName()) && !loc.getId().equals(location.getId())) {
				this.addActionError("Location with name: " + location.getName() + " already exists. Please enter a different name.");
				return INPUT;
			}
			
			if (location.getLocationId() != null && !location.getLocationId().isEmpty() && 
				location.getLocationId().equalsIgnoreCase(loc.getLocationId()) && !loc.getId().equals(location.getId()))
			{
				this.addActionError("The location ID " + location.getLocationId() + " is already used by another location " + loc.getName() + ". Please enter a unique value for location ID.");
				return INPUT;
			}
		}

        // Update the location success sequences based on the input form.
        updateSuccessSequences();

        // change the "null" string to null
        if (location.getSuccessSequence() != null && location.getSuccessSequence().equals("null"))
            location.setSuccessSequence(null);
        if (location.getSuccessTimeout() == null)
            location.setSuccessTimeout(0l);
        if (location.getFailureSequence() != null && location.getFailureSequence().equals("null"))
            location.setFailureSequence(null);
        if (location.getFailureTimeout() == null)
            location.setFailureTimeout(0l);
        if (location.getErrorSequence() != null && location.getErrorSequence().equals("null"))
            location.setErrorSequence(null);
        if (location.getErrorTimeout() == null)
            location.setErrorTimeout(0l);
        if (location.getTapSequence() != null && location.getTapSequence().equals("null"))
            location.setTapSequence(null);
        if (location.getTapTimeout() == null)
            location.setTapTimeout(0l);
        if (location.getIdleSequence() != null && location.getIdleSequence().equals("null"))
            location.setIdleSequence(null);

        String useSecureId = ServletActionContext.getRequest().getParameter("location.useSecureId");

        String useSecureIdLower = useSecureId.toLowerCase();
        if (useSecureIdLower.equals("true") || useSecureIdLower.equals("false"))
            // this value will overwrite the global configuration setting
            location.setUseSecureId(Boolean.parseBoolean(useSecureId));
        else
            // global configuration setting will be used
            location.setUseSecureId(null);

		locationService.save(location);

		Connection conn = null;
		try {
			conn = UIConnectionPool.getInstance().getConnection();
			
			if (isAttractionModel()) {
				if (vaLocationConfig != null) {
					vaLocationConfig.setLocationId(location.getId());

					if (vaLocationConfigEnabled) {
						if (VaLocationConfigService
								.find(conn, location.getId()) != null)
							VaLocationConfigService.update(conn,
									vaLocationConfig);
						else
							VaLocationConfigService
									.save(conn, vaLocationConfig);
					} else {
						VaLocationConfigService.delete(conn, location.getId());
					}

				}

                if (eventsLocationConfig != null)
                {
                    eventsLocationConfig.setLocationId(location.getId());
                   
                    if (EventsLocationConfigService.find(conn, location.getId()) != null)
                        EventsLocationConfigService.update(conn, eventsLocationConfig);
                    else
                        EventsLocationConfigService.save(conn, eventsLocationConfig);
                }
			}

			updateTransmitters(conn);
		} catch (Exception e) {
			this.addActionMessage("Failed save location " + e.getLocalizedMessage());
		} finally {
			if (conn != null)
				UIConnectionPool.getInstance().releaseConnection(conn);
		}
        
        return SUCCESS;
    }

    private void updateSuccessSequences() {
        if ( location == null )
            return;

        if ( successSequences != null && !successSequences.isEmpty() ) {
            // Loop through the sequences and build a string to store.
            String fullSequences = "";

            for ( String sequenceString : successSequences) {
                Sequences sequences = Sequences.parseSequenceNames(sequenceString);
                fullSequences += sequences.get(0).getName() + "," +
                    sequences.get(0).getTimeout() + "," +
                    sequences.get(0).getRatio() + ";";
            }

            location.setSuccessSequence(fullSequences);
        }
        else {
            location.setSuccessSequence(null);
        }
    }
    
    private void updateTransmitters(Connection conn) throws Exception {
    	if (transmitterId == null || transmitterId.length == 0)
    		return;
    	
    	for (int i = 0; i < transmitterId.length; i++) {
    		
    		Long id = transmitterId[i];
    		Integer haPriority = transmitterHaPriority[i];
    		boolean isTranstmitter = false;
    		if (transmitter != null) {
    			for (Long id2 : transmitter) {
    				if (id.equals(id2)) {
    					isTranstmitter = true;
    					break;
    				}
    			}
    		}
    		
    		Reader reader = new Reader();
    		reader.setId(id);
    		reader.setTransmitterHaPriority(haPriority);
    		reader.setTransmitter(isTranstmitter);
    		ReaderService.saveTransmitInfo(conn, reader);
    	}
    }

    public String saveReader() throws Exception {
        if (reader == null)
            return INPUT;

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
        
        
        
        reader.setGroup(reader.getGroup() != null ? reader.getGroup().toLowerCase() : null);
        reader.setType(ReaderType.getByOrdinal(readerType));

        //prevent data too long DB exception
        trimReaderValues();

        Connection conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			ReaderService.save(conn, reader);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed save reader " + reader.getMacAddress());
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}

        if (isParkEntry())
        {
            if (this.addedOmniServers != null && this.addedOmniServers.size() > 0)
            {
                // this reader should have some omnis associated with it
                try {
                    conn = UIConnectionPool.getInstance().getConnection();
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
            else
            {
                // this reader either never had any omnis associated, or they were all just deleted
                try {
                    conn = UIConnectionPool.getInstance().getConnection();
                    OmniServerService.disassociateAllOmniServers(conn, reader.getId());
                } catch (Exception e){
                    this.addActionMessage("Failed to disassociate omni servers.");
                } finally {
                    UIConnectionPool.getInstance().releaseConnection(conn);
                }
            }
        }

        return SUCCESS;
    }

    private void trimReaderValues() throws Exception {
        if (reader != null && reader.getReaderId() != null) {

            int columnLength = AnnotationHelper.getColumnLength(reader, "readerId");

            String trimed = reader.getReaderId().trim();
            if (trimed.length() > columnLength) {
                reader.setReaderId(trimed.substring(0, columnLength - 1));
                logger.info("Forced to trim readerId for reader id: " + reader.getId()
                        + " from " + trimed + " to " + reader.getReaderId());
            }
        }

        if (reader != null && reader.getIpAddress() != null) {

            int columnLength = AnnotationHelper.getColumnLength(reader, "ipAddress");

            String trimed = reader.getIpAddress().trim();
            if (trimed.length() > columnLength) {
                reader.setReaderId(trimed.substring(0, columnLength - 1));
                logger.error("Forced to trim ipAddress for reader id: " + reader.getId()
                        + " from " + trimed + " to " + reader.getIpAddress());
            }
        }
    }

    private void initLrrTransmitPage(Connection conn) throws Exception {
    	if (location == null)
    		return;
    	
    	transmitReaders = ReaderService.findTransmittersByLocation(conn, locationId);
    	
        // get all but the UNKNOWN locations
        Collection<Location> allLocations = locationService.findAll(true);
        // build display list
        locations = new LinkedHashMap<Long, String>();
        for (Location l : allLocations){
            locations.put(l.getId(), l.getName());
        }

        // display commands currently configured for this reader
        Collection<XbrBandCommand> allCommands = null;

        allCommands = XbandCommandService.findByLocation(conn, location.getId());
        // display commands currently configured for this reader
        commands = new LinkedHashMap<XbrBandCommand, String>();
        if (allCommands != null) {
            for (XbrBandCommand cmd : allCommands){

                if (cmd.getRecipients() != null){
                    StringBuffer recipients = new StringBuffer();
                    for (Long recipientId : cmd.getRecipients())
                    {
                        if (locations.containsKey(recipientId)){
                            if (recipients.length() != 0) {
                                recipients.append(",");
                            } else {
                                recipients.append("All bands detected at: ");
                            }

                            recipients.append(locations.get(recipientId));
                        } else {
                            logger.error("Invalid location id found in the recipient column for " + cmd.toString());
                        }
                    }
                    
                    if (cmd.getEnableThreshold())
                    	recipients.append(" within signal strength threshold of " + cmd.getThreshold());
                    
                    // add a REPLY command to be applied to a list of recipient
                    commands.put(cmd, recipients.toString());
                } else if (cmd.getMode() == XMIT_MODE.REPLY) {
                	if (cmd.getEnableThreshold())
                		commands.put(cmd, "Bands within signal strength threshold of " + cmd.getThreshold());
                	else
                		commands.put(cmd, "All bands from which the reader received a ping");
                } else {
                    // add a BROADCAST command to be applied to everybody visible
                    commands.put(cmd, "All bands in the vicinity");
                }
            }
        }
    }

    /**
     * @return the locationId
     */
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

    public Map<Integer, String> getLocationTypes(){
        return locationTypes;
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

    /**
     * @return the reader
     */
    public Reader getReader() {
        return reader;
    }

    /**
     * @param reader the reader to set
     */
    public void setReader(Reader reader) {
        this.reader = reader;
        updateChannelGroups(reader);
    }

    public String getReaderTimeLastHello() {
        if (reader == null || reader.getTimeLastHello() == null)
            return "";

        return DateUtils.format(reader.getTimeLastHello().getTime(), "yyyy-MM-dd HH:mm:ss.SSS z");
    }

    public Map<Integer, String> getReaderTypes() {
        return readerTypes;
    }

    public void setReaderTypes(Map<Integer, String> readerTypes) {
        this.readerTypes = readerTypes;
    }

    public ReaderConfig getReaderConfig() {
        return readerConfig;
    }

    public int getReaderType() {
        return readerType;
    }

    public void setReaderType(int readerType) {
        this.readerType = readerType;
    }

    public ReaderSequenceInfo getSequenceInfo()
    {
        return sequenceInfo;
    }

    public Map<Long, String> getLocations()
    {
        return locations;
    }

    public boolean getReaderIsTransmitter()
    {
        return readerIsTransmitter;
    }

    public void setReaderIsTransmitter(boolean readerIsTransmitter)
    {
        this.readerIsTransmitter = readerIsTransmitter;
    }

    public Map<XbrBandCommand, String> getCommands()
    {
        return commands;
    }

    public Long getCommandId()
    {
        return commandId;
    }

    public void setCommandId(Long commandId)
    {
        this.commandId = commandId;
    }
    
    public List<String> getSortedSequenceNames() {
        List<String> sequenceNames = new ArrayList<String>();

        // Add the package sequenceInfo.
        for (ReaderSequence sequence: sequenceInfo.getReaderSequences()) {
            if ( sequence.getIsReaderDefault() == false ) {
                sequenceNames.add(sequence.getName());
            }
        }

        // Add the default sequenceInfo.
        for (ReaderSequence sequence: sequenceInfo.getReaderSequences()) {
            if ( sequence.getIsReaderDefault() ) {
                sequenceNames.add(sequence.getName() + '*');
            }
        }

        return sequenceNames;
    }
    
    public List<String> getSortedSequenceNamesNoColors() {
        List<String> sequenceNames = new ArrayList<String>();

        // Add the package sequenceInfo.
        for (ReaderSequence sequence: sequenceInfoNoColors.getReaderSequences()) {
            if ( sequence.getIsReaderDefault() == false ) {
                sequenceNames.add(sequence.getName());
            }
        }

        // Add the default sequenceInfo.
        for (ReaderSequence sequence: sequenceInfoNoColors.getReaderSequences()) {
            if ( sequence.getIsReaderDefault() ) {
                sequenceNames.add(sequence.getName() + '*');
            }
        }        

        return sequenceNames;
    }

    public boolean getUseCustomSequenceList() {
        return location.getSuccessSequence() != null &&
                !location.getSuccessSequence().equals("null") &&
                !location.getSuccessSequence().isEmpty();
    }

    public Sequences getSuccessSequences() {
        Sequences sequences = Sequences.parseSequenceNames(location.getSuccessSequence());

        for ( Sequence sequence : sequences ) {
            sequence.setName(decorateIfDefault(sequence.getName()));
        }

        return sequences;
    }

    public String getFailureSequence() {
        return decorateIfDefault(location.getFailureSequence());
    }

    public String getErrorSequence() {
        return decorateIfDefault(location.getErrorSequence());
    }

    public String getIdleSequence() {
        return decorateIfDefault(location.getIdleSequence());
    }
    
    public String getTapSequence() {
        return decorateIfDefault(location.getTapSequence());
    }

    private String decorateIfDefault( String selectedSequence ) {
        // Add the asterisk if this is a default.
        for ( ReaderSequence sequence : sequenceInfo.getReaderSequences()) {
            if ( sequence.getName().equals(selectedSequence)) {
                if ( sequence.getIsReaderDefault() ) {
                    selectedSequence += "*";
                }
            }
        }

        return selectedSequence;
    }

    public Collection<OmniServer> getOmnisForGrabs()
    {
        return omnisForGrabs;
    }

    public Collection<OmniReaderAgregate> getOmnisAssociated()
    {
        return omnisAssociated;
    }

    public boolean isParkEntry(){
        if (status != null && XbrcModel.PARKENTRY.getXbrcModelClass().equals(status))
        {
            return true;
        }

        return false;
    }
    
    public boolean isAttractionModel() {
    	  if (status != null && XbrcModel.ATTRACTION.getXbrcModelClass().equals(status))
          {
              return true;
          }

          return false;
    }
    
    public boolean isSpaceModel() {
  	  if (status != null && XbrcModel.SPACE.getXbrcModelClass().equals(status))
        {
            return true;
        }

        return false;
  }

    public void setAddedOmniServers(Collection<String> addedOmniServers)
    {
        this.addedOmniServers = addedOmniServers;
    }

    public void setSuccessSequences(Collection<String> locationSuccessSequences) {
        successSequences = locationSuccessSequences;
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

	public VaLocationConfig getVaLocationConfig() {
		return vaLocationConfig;
	}

	public void setVaLocationConfig(VaLocationConfig vaLocationConfig) {
		this.vaLocationConfig = vaLocationConfig;
	}
	
	public Collection<String> getVaAlgorithmChoices(){
		Collection<String> choices = new LinkedList<String>();
		for (VaAlgorithm vaAlgorithm: VaLocationConfig.VaAlgorithm.values())
		{
			choices.add(vaAlgorithm.name());
		}
		return choices;
	}
	
	public boolean isVaLocationConfigEnabled()
	{
		return vaLocationConfigEnabled;
	}
	
	public void setVaLocationConfigEnabled(boolean enabled)
	{ 
		vaLocationConfigEnabled = enabled;
	}

	public ControllerInfo getControllerInfo() {
		return controllerInfo;
	}

	public void setControllerInfo(ControllerInfo controllerInfo) {
		this.controllerInfo = controllerInfo;
	}

	public boolean getShowTransmitTab() {
		return showTransmitTab;
	}

	public void setShowTransmitTab(boolean showTransmitTab) {
		this.showTransmitTab = showTransmitTab;
	}

	public Collection<Reader> getTransmitReaders() {
		return transmitReaders;
	}

	public void setTransmitReaders(Collection<Reader> transmitReaders) {
		this.transmitReaders = transmitReaders;
	}

	public Integer[] getTransmitterHaPriority() {
		return transmitterHaPriority;
	}

	public void setTransmitterHaPriority(Integer[] transmitterHaPriority) {
		this.transmitterHaPriority = transmitterHaPriority;
	}

	public Long[] getTransmitter() {
		return transmitter;
	}

	public void setTransmitter(Long[] transmitter) {
		this.transmitter = transmitter;
	}

	public Long[] getTransmitterId() {
		return transmitterId;
	}

	public void setTransmitterId(Long[] transmitterId) {
		this.transmitterId = transmitterId;
	}

    public EventsLocationConfig getEventsLocationConfig() {
        return eventsLocationConfig;
    }

    public void setEventsLocationConfig(EventsLocationConfig eventsLocationConfig) {
        this.eventsLocationConfig = eventsLocationConfig;
    }

    public boolean getLocationEventsEnabled()
    {
        return locationEventsEnabled;
    }

    public void setLocationEventsEnabled(boolean enabled)
    {
        locationEventsEnabled = enabled;
    }
}
