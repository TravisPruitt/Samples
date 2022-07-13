package com.disney.xband.xbrc.Controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.disney.xband.common.lib.audit.Auditor;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.xbrc.Controller.model.ESBInfo;
import com.disney.xband.xbrc.Controller.model.ReadersInfo;
import com.disney.xband.xbrc.Controller.model.StoredStatus;
import com.disney.xband.xbrc.lib.db.StoredConfigService;
import com.disney.xband.xbrc.lib.entity.StoredConfiguration;
import com.disney.xband.xbrc.lib.model.ControllerInfo;
import com.disney.xband.xbrc.lib.model.LocationInfo;

public class ConfigOptions
{
	// singleton
	public static final ConfigOptions INSTANCE = new ConfigOptions();
	public static Logger logger = Logger.getLogger(ConfigOptions.class);
	
	public static final String AUDIT_STORED_CONFIG_NAME = "CurrentAudit";
	public static final String AUDIT_STORED_CONFIG_DESC = "DO NOT REMOVE. Last stored configuration compiled for Audit purposes.";

	// class data
	private ReadersInfo ri;
	private ControllerInfo ci;
	private ESBInfo ei;
    private AuditConfig ac;
	private StoredStatus storedStatus;
	private Date dtConfigurationChanged;

	private ConfigOptions()
	{
		// create the reader info
		ri = new ReadersInfo();
		ci = new ControllerInfo();
		ei = new ESBInfo();
		storedStatus = new StoredStatus();
		dtConfigurationChanged = new Date();
        ac = new AuditConfig();
	}
	
	public Date getConfigurationChangedDate()
	{
		return dtConfigurationChanged;
	}
	
	public void setConfigurationChangedDate(Date dt)
	{
		this.dtConfigurationChanged = dt;
	}

	public ReadersInfo getReadersInfo()
	{
		return ri;
	}

	public ControllerInfo getControllerInfo()
	{
		return ci;
	}

	public ESBInfo getESBInfo()
	{
		return ei;
	}
	
	public AuditConfig getAuditConfig()
	{
		return ac;
	}

	public StoredStatus getStoredStatus()
	{
		return storedStatus;
	}

	public Map<Integer, LocationInfo> getLocationInfo()
	{
		return ri.getLocationInfo();
	}

	public void readConfigurationOptionsFromDatabase() throws Exception
	{
		Connection conn = null;

		try
		{
			conn = Controller.getInstance().getPooledConnection();

			// process the Configurations table
			ci.processConfigurationTable(conn, Controller.getInstance().getProperties());

			// and location information
			ri.processLocationTable(conn);

			// now reader Reader info
			ri.processReadersTable(conn);

			// and process the esb info
			ei.processConfigurationTable(conn, Controller.getInstance()
                    .getProperties());

			// configuration related to the Audit system
            ac.populateAuditConfigFromDb(conn);
			
			// and the status table
			storedStatus.processStatusTable(conn);
			
			// reset date
			dtConfigurationChanged = new Date();
		}
		catch (Exception ex)
		{
			logger.error("Failed to read configuration file: "
					+ ex.getLocalizedMessage());
			throw ex;
		}
		finally
		{
			if (conn != null)
				try {
				Controller.getInstance().releasePooledConnection(conn); } catch (Exception ignore) {}
		}
	}

	public void refreshConfigurationTable() throws Exception
	{
		logger.debug("Re-reading configuration from database");
		readConfigurationOptionsFromDatabase();
	}

	public void auditConfigurationChanges(boolean oldAuditIsEnabled) throws Exception
	{
		if (Processor.INSTANCE.getModel() == null)
			return; //first time, model not yet loaded
		
		final IAudit auditor = Auditor.getInstance().getAuditor();
		
		if (auditor.isAuditEnabled() == oldAuditIsEnabled)
			if (!auditor.isAuditEnabled())
				return;
		
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			if (conn == null)
				return;
			
			final boolean internal = true;
			final String model = Processor.INSTANCE.getModel().getClass().getName();

			InputStream previous = null;
			InputStream current = null;

			// get the previous internal stored configuration from the database
			StoredConfiguration previousConfig = null;
			try
			{
				previousConfig = StoredConfigService.find(conn, 
						AUDIT_STORED_CONFIG_NAME,
						internal,
						Processor.INSTANCE.getModel().getClass().getName());
			}
			catch (Exception e)
			{
				logger.error("Failed to retrieve previously stored configuration " + AUDIT_STORED_CONFIG_NAME);
			}

			if (previousConfig != null && previousConfig.getXml().trim().length() > 0)
			{
				previous = new ByteArrayInputStream(previousConfig.getXml().trim().getBytes());
			}

			// construct the current configuration
			StringBuffer currentConfig = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
			try
			{
				currentConfig.append(WebServer.INSTANCE.serializeCurrentConfiguration("current"));
			}
			catch (Exception e)
			{
				logger.error("Failed to AUDIT! Failed to serialize current configuration.", e);
				return;
			}

			current = new ByteArrayInputStream(currentConfig.toString().getBytes());

			// diff the two
			List<CurrentConfigDiffItem> delta = CurrentConfigDiff.diff(previous, current);

			// replace the previous persisted internal stored configuration with the current
			if (previousConfig == null)
			{
				previousConfig = new StoredConfiguration();
				previousConfig.setName(AUDIT_STORED_CONFIG_NAME);
				previousConfig.setModel(model);
				previousConfig.setXml(currentConfig.toString());
				previousConfig.setDescription(AUDIT_STORED_CONFIG_DESC);
				previousConfig.setInternal(true);
			}
			else
			{
				previousConfig.setXml(currentConfig.toString());
			}

			try
			{
				StoredConfigService.update(conn, previousConfig);
			}
			catch (Exception e)
			{
				logger.error("Failed to update stored configuration " + AUDIT_STORED_CONFIG_NAME + " in model " + model);
			}

			// persist the delta
			if (delta == null || delta.size() == 0)
				return;

			List<AuditEvent> changes = new LinkedList<AuditEvent>();
			for (CurrentConfigDiffItem item : delta)
			{
				String resourceData = item.getCurrValue();
				
				switch (item.getResult())
				{
					case ADDED: 
						resourceData = "ADDED [Was: <not set>, Now: " + item.getCurrValue() + "]"; 
						break;
					case DELETED: 
						resourceData = "DELETED [Was: " + item.getPrevValue() + ", Now: <not set>]"; 
						break; 
					case MODIFIED: 
						resourceData = "MODIFIED [Was: " + item.getPrevValue() + ", Now: " + item.getCurrValue() + "]"; 
						break;
				}
				
				changes.add(auditor.createWriteSuccess(
						item.getResult().name(), 
						item.getKey(), 
						resourceData));
			}

			auditor.audit(changes);
		}
		finally
		{
			if (conn != null)
			{
				try
				{
					Controller.getInstance().releasePooledConnection(conn);
				}
				catch (Exception e){}
			}
		}
	}
}
