package com.disney.xband.xbrms.client.action;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.client.model.XbrcConfigurationDpo;
import com.disney.xband.xbrms.client.model.XbrcDpo;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.XbrcConfigurationListDto;
import com.disney.xband.xbrms.common.model.XbrcDto;

public class StoredConfigAction extends BaseAction
{
	private Collection<XbrcConfigurationDpo> inventory;
	private File xmlfile;
	private String xmlfileContentType;
	private String xmlfileFileName;
	private String action;
	private String venueCode;
	private String venueName;
	private String name;
	private String description;
	private String id;
	private String xbrcId;
	private Collection<XbrcDpo> xbrcList;
	private Collection<XbrcDpo> targetXbrcList;
	
	private static Logger logger = Logger.getLogger(StoredConfigAction.class);
	
	@Override
	public String execute() throws Exception
	{
		if (action != null)
		{
			if (action.equals("delete")) {
				doDeleteConfiguration();
            }
			else {
                if (action.equals("uploadFromFile") || action.equals("uploadFromXbrc")) {
				    doConfigurationUpload();
                }
            }
		}
		
		try 
		{
			// configuration should only be selectable from running xBRCs
			final Collection<XbrcDto> allXbrcDtos = XbrmsUtils.getRestCaller().getFacilities().getFacility();

			xbrcList = new LinkedList<XbrcDpo>();
			targetXbrcList = new LinkedList<XbrcDpo>();
			if (allXbrcDtos != null)
			{
				for (XbrcDto xbrcDto : allXbrcDtos){
					if (xbrcDto.isAlive() && xbrcDto.isValidHaStatus()) {
						
						// Allow getting the stored configuration from solo or master
						if (HAStatusEnum.getStatus(xbrcDto.getHaStatus()) == HAStatusEnum.solo || 
							HAStatusEnum.getStatus(xbrcDto.getHaStatus()) == HAStatusEnum.master) {
							xbrcList.add(new XbrcDpo(xbrcDto));
						}
						
						// Allow pushing the stored configuration to solo, master or slave
						if (HAStatusEnum.getStatus(xbrcDto.getHaStatus()) == HAStatusEnum.solo || 
							HAStatusEnum.getStatus(xbrcDto.getHaStatus()) == HAStatusEnum.master || 
							HAStatusEnum.getStatus(xbrcDto.getHaStatus()) == HAStatusEnum.slave ||
							HAStatusEnum.getStatus(xbrcDto.getHaStatus()) == HAStatusEnum.unknown) {
							targetXbrcList.add(new XbrcDpo(xbrcDto));
						}
					}
				}
			}
			
			inventory = new LinkedList<XbrcConfigurationDpo>();

            final XbrcConfigurationListDto list = XbrmsUtils.getRestCaller().getStoredXbrcConfigs();

            if((list != null) && (list.getXbrcConfiguration() != null)) {
			    for (XbrcConfiguration conf : list.getXbrcConfiguration()) {
				    inventory.add(new XbrcConfigurationDpo(conf));
			    }
            }
		}
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
		catch (Exception e)
		{
            final String errorMessage = "Failed to read a list of stored configurations from the database. " + e.getMessage();
            this.addActionError(errorMessage);

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }

			return INPUT;
		}

		return super.execute();
	}
	
	private String doDeleteConfiguration()
	{
		try
		{			
			XbrmsUtils.getRestCaller().deleteXbrcConfig(id);
			
			return SUCCESS;
		}
		catch(Exception e)
		{
            final String errorMessage = "Failed to delete stored configuration record. " + e.getMessage();
            this.addActionError(errorMessage);

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }
			
			return INPUT;
		}
	}
	
	private String doConfigurationUpload()
	{
		try
		{			
			processConfigurationUpload();

			return SUCCESS;
		}
		catch(Exception e)
		{
			final String errorMessage = "Failed to upload a stored configuration record. " + e.getMessage();
			this.addActionError(errorMessage);
			
			if (logger.isDebugEnabled()) {
				logger.error(errorMessage, e);
            }
			else {
				logger.error(errorMessage);
            }
			
			return INPUT;
		}
	}
	
	private void processConfigurationUpload() throws Exception
	{	
		final XbrcConfiguration conf;

		if (xmlfile != null) {
			final String xml = XbrmsUtils.readFileAsString(xmlfile.getAbsolutePath());
			xmlfile.delete();
            conf = XbrmsUtils.getRestCaller().parseXbrcConfigXml(xml);
		}
		else {
            conf = new XbrcConfiguration();

            if (xbrcId == null) {
                if ("uploadFromFile".equals(action)) {
                    throw new Exception("Failed to read file content. Make sure the file type is either .xml or .txt");
                }

                if ("uploadFromXbrc".equals(action)) {
                    throw new Exception("Failed to get xBRC configuration");
                }

			    throw new Exception("xBRC ID is null");
		    }
        }

        conf.setName(name);
        conf.setDescription(description);
        conf.setVenueCode(venueCode);
        conf.setVenueName(venueName);

		XbrmsUtils.getRestCaller().addXbrcConfig(xbrcId, conf);
	}
	
	public Collection<XbrcDpo> getXbrcList()
	{
		return xbrcList;
	}

	public Collection<XbrcConfigurationDpo> getInventory()
	{
		return inventory;
	}

	public File getXmlfile()
	{
		return xmlfile;
	}

	public void setXmlfile(File xmlfile)
	{
		this.xmlfile = xmlfile;
	}

	public String getXmlfileContentType()
	{
		return xmlfileContentType;
	}

	public void setXmlfileContentType(String xmlfileContentType)
	{
		this.xmlfileContentType = xmlfileContentType;
	}

	public String getXmlfileFileName()
	{
		return xmlfileFileName;
	}

	public void setXmlfileFileName(String xmlfileFileName)
	{
		this.xmlfileFileName = xmlfileFileName;
	}

	public String getVenueCode()
	{
		return venueCode;
	}

	public void setVenueCode(String venueCode)
	{
		this.venueCode = venueCode;
	}

	public String getVenueName()
	{
		return venueName;
	}

	public void setVenueName(String venueName)
	{
		this.venueName = venueName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getXbrcId()
	{
		return xbrcId;
	}

	public void setXbrcId(String xbrcId)
	{
		this.xbrcId = xbrcId;
	}

	public Collection<XbrcDpo> getTargetXbrcList() {
		return targetXbrcList;
	}
}
