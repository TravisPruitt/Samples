package com.disney.xband.xbrc.ui.edit.action;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_COMMAND;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.xbrc.lib.db.XbandCommandService;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.action.BaseAction;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class TransmitCommandAction extends ActionSupport implements Preparable {

	private static Logger logger = Logger
			.getLogger(TransmitCommandAction.class);

	private Location location;
	private Long locationId;
	private Long commandId;
	private String action;
	private boolean commandSaved = false;

	private Map<Long, String> locations;
	private LocationService locationService;
	private Set<String> selectedLocations;
	private List<String> recipient;
	private Map<Integer, XMIT_COMMAND> displayCommands;
	private Map<Integer, XMIT_MODE> displayTransmitModes;
	private Boolean enableThreshold;
	private Integer threshold = -127;
	private String timeout;
	private String frequency;
	private Map<XbrBandCommand, String> commands;

	@Override
	public void prepare() throws Exception {

		locationService = ServiceLocator.getInstance().createService(
				LocationService.class);
	}

	@Override
	public String execute() throws Exception {
		
		Connection conn = null;
		try {
			conn = UIConnectionPool.getInstance().getConnection();
			init(conn);
			
			if (action != null) {
				if (action.equals("save"))
					insertTransmitCommand();
			}
			
		} finally {
			if (conn != null)
				UIConnectionPool.getInstance().releaseConnection(conn);
		}

		return SUCCESS;
	}

	private void init(Connection conn) throws Exception {
		if (locationId != null)
            location = locationService.find(locationId);
		
		if (location == null)
			return;

		// get all but the UNKNOWN locations
		Collection<Location> allLocations = locationService.findAll(true);
		// build display list
		locations = new LinkedHashMap<Long, String>();
		for (Location l : allLocations) {
			locations.put(l.getId(), l.getName());
		}

		// transmit commands exposed through the UI
		displayCommands = new LinkedHashMap<Integer, XMIT_COMMAND>();
		displayCommands.put(XMIT_COMMAND.FAST_RX_ONLY.ordinal(),
				XMIT_COMMAND.FAST_RX_ONLY);
		displayCommands.put(XMIT_COMMAND.FAST_PING.ordinal(),
				XMIT_COMMAND.FAST_PING);
		displayCommands.put(XMIT_COMMAND.SLOW_PING.ordinal(),
				XMIT_COMMAND.SLOW_PING);

		// transmit modes exposed through the UI
		displayTransmitModes = new LinkedHashMap<Integer, XMIT_MODE>();
		displayTransmitModes.put(XMIT_MODE.REPLY.ordinal(), XMIT_MODE.REPLY);
		displayTransmitModes.put(XMIT_MODE.BROADCAST.ordinal(),
				XMIT_MODE.BROADCAST);

		// display commands currently configured for this reader
		Collection<XbrBandCommand> allCommands = null;

		allCommands = XbandCommandService
				.findByLocation(conn, location.getId());
		// display commands currently configured for this reader
		commands = new LinkedHashMap<XbrBandCommand, String>();
		selectedLocations = new HashSet<String>();
		if (allCommands != null) {
			for (XbrBandCommand cmd : allCommands) {

				if (cmd.getRecipients() != null) {
					StringBuffer recipients = new StringBuffer();
					for (Long recipientId : cmd.getRecipients()) {
						if (locations.containsKey(recipientId)) {
							if (recipients.length() != 0) {
								recipients.append(",");
							} else {
								recipients.append("All bands detected at: ");
							}

							recipients.append(locations.get(recipientId));

							selectedLocations.add(locations.get(recipientId));
						} else {
							logger.error("Invalid location id found in the recipient column for "
									+ cmd.toString());
						}
					}
					// add a REPLY command to be applied to a list of recipient
					commands.put(cmd, recipients.toString());
					// } else if (cmd.getMode() == XMIT_MODE.REPLY) {
					// // add a REPLY command to be applied to all recipient
					// within some signal strength transmit threshold
					// commands.put(cmd,
					// "Bands within signal strength threshold of " +
					// reader.getSignalStrengthTransitThreshold());
				} else {
					// add a BROADCAST command to be applied to everybody
					// visible
					commands.put(cmd, "All bands in the vicinity");
				}
			}
		}
	}

	public String insertTransmitCommand() throws Exception {

		if (locationId == null) {
			this.addActionError("The location ID has not been provided. Cannot save command.");
			return INPUT;
		}

		location = locationService.find(locationId);

		// save the new command
		HttpServletRequest request = ServletActionContext.getRequest();

		String commandTypeName = request.getParameter("commandTypes"); // select
																		// dropdown,
																		// never
																		// null
		XMIT_COMMAND command = XMIT_COMMAND.valueOf(commandTypeName);

		if (enableThreshold == null)
			enableThreshold = false;

		XbrBandCommand newBandCommand = new XbrBandCommand();
		newBandCommand.setCommand(command);
		newBandCommand.setThreshold(threshold);
		newBandCommand.setEnableThreshold(enableThreshold);

		if (command == XMIT_COMMAND.FAST_RX_ONLY) {
			newBandCommand.setMode(XMIT_MODE.BROADCAST);
		} else {
			String transmitMode = request.getParameter("transmitMode");
			try {
				if (transmitMode != null)
					newBandCommand.setMode(XMIT_MODE.valueOf(transmitMode));
				else
					newBandCommand.setMode(XMIT_MODE.REPLY);
			} catch (Exception e) {
				newBandCommand.setMode(XMIT_MODE.REPLY);
			}
		}

		/*
		 * The default grabbing logic for frequency and timeout is here because
		 * for some reason text field values set with jquery val() method don't
		 * come through on the request.
		 */
		
		String outOfRangeErrorMessage = "%s of %s is out of bounds. Use values between %d and %d %s.";
		
		if (command.isFrequencyProgrammable()) {
			if (frequency == null || frequency.isEmpty())
				newBandCommand.setInterval(command.getFrequencyMin());
			else {
				try {
					int freq = (int)Double.parseDouble(frequency);
					
					if (freq < command.getFrequencyMin())
						this.addActionError(String.format(outOfRangeErrorMessage, "Frequency", freq, command.getFrequencyMin(), command.getFrequencyMax(), "milliseconds"));
					else if (freq > command.getFrequencyMax())
						this.addActionError(String.format(outOfRangeErrorMessage, "Frequency", freq, command.getFrequencyMin(), command.getFrequencyMax(), "milliseconds"));
					
					newBandCommand.setInterval(freq);
				} catch (NumberFormatException e) {
					if (frequency.trim().length() > 0)
						this.addActionError(String.format(outOfRangeErrorMessage, "Frequency", frequency, command.getFrequencyMin(), command.getFrequencyMax(), "milliseconds"));
					else
						newBandCommand.setInterval(command.getFrequencyMin());
				}
			}
		} else {
			newBandCommand.setInterval(command.getFrequencyMin());
		}

		if (command.isTimeoutProgrammable()) {
            if (timeout == null || timeout.isEmpty())
				newBandCommand.setTimeout(command.getTimeoutMax());
			else {
				try {
					int tout = (int)Double.parseDouble(timeout) * 1000;
					
					if (tout < command.getTimeoutMin())
						this.addActionError(String.format(outOfRangeErrorMessage, "Timeout", tout/1000, command.getTimeoutMin()/1000, command.getTimeoutMax()/1000, "seconds"));
					else if (tout > command.getTimeoutMax())
						this.addActionError(String.format(outOfRangeErrorMessage, "Timeout", tout/1000, command.getTimeoutMin()/1000, command.getTimeoutMax()/1000, "seconds"));
					
					newBandCommand.setTimeout(tout);
				} catch (NumberFormatException e) {
					if (timeout.trim().length() > 0)
						this.addActionError(String.format(outOfRangeErrorMessage, "Timeout", timeout, command.getTimeoutMin()/1000, command.getTimeoutMax()/1000, "seconds"));
					else
						newBandCommand.setTimeout(command.getTimeoutMax());
				}
			}
		} else {
			newBandCommand.setTimeout(command.getTimeoutMax());
		}

		if (newBandCommand.getMode() != XMIT_MODE.BROADCAST
				&& recipient != null) {
			Set<Long> recipients = new HashSet<Long>();
			for (String str : recipient) {
				try {
					if (str.trim().length() == 0)
						continue;
					recipients.add(Long.parseLong(str));
				} catch (NumberFormatException e) {
				}
			}

			newBandCommand.setRecipients(recipients);
		}
		
		if (!this.getActionErrors().isEmpty())
			return INPUT;

		if (!newBandCommand.isValid()) {
			this.addActionError("Error: your new xBand transmit command has not been created. Invalid command.");
			return INPUT;
		}

		// persist the new command
		Connection conn = UIConnectionPool.getInstance().getConnection();

		try {
			XbandCommandService.create(conn, location.getId(), newBandCommand);
		} catch (Exception e) {
			this.addActionError("Cannot add the command: " + e.getLocalizedMessage());
			return INPUT;
		} finally {
			UIConnectionPool.getInstance().releaseConnection(conn);
		}

		commandSaved = true;
		
		return SUCCESS;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public Long getCommandId() {
		return commandId;
	}

	public void setCommandId(Long commandId) {
		this.commandId = commandId;
	}

	public Map<Long, String> getLocations() {
		return locations;
	}

	public Set<String> getSelectedLocations() {
		return selectedLocations;
	}

	public void setSelectedLocations(Set<String> selectedLocations) {
		this.selectedLocations = selectedLocations;
	}

	public void setRecipient(List<String> recipient) {
		this.recipient = recipient;
	}

	public List<String> getRecipient() {
		return this.recipient;
	}

	public Map<XbrBandCommand, String> getCommands() {
		return commands;
	}

	public Boolean getEnableThreshold() {
		return enableThreshold;
	}

	public void setEnableThreshold(Boolean enableThreshold) {
		this.enableThreshold = enableThreshold;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public Map<Integer, XMIT_COMMAND> getDisplayCommands() {
		return displayCommands;
	}

	public void setDisplayCommands(Map<Integer, XMIT_COMMAND> displayCommands) {
		this.displayCommands = displayCommands;
	}

	public Map<Integer, XMIT_MODE> getDisplayTransmitModes() {
		return displayTransmitModes;
	}

	public void setDisplayTransmitModes(Map<Integer, XMIT_MODE> displayTransmitModes) {
		this.displayTransmitModes = displayTransmitModes;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isCommandSaved() {
		return commandSaved;
	}

	public void setCommandSaved(boolean commandSaved) {
		this.commandSaved = commandSaved;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
}
