package com.disney.xband.xbrms.server.managed;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.disney.xband.xbrms.common.model.*;
import com.disney.xband.xbrms.server.AuditHelper;
import com.disney.xband.xbrms.server.model.HealthItemDao;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.health.IDMSStatus;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.model.HealthItemDto;

public class IdmsSystem implements IHealthSystem
{
	private IdmsDto dto;
	
	private static Logger logger = Logger.getLogger(IdmsSystem.class);
	
	public IdmsSystem(IdmsDto dto)
	{
		this.dto = dto;
	}
	
	@Override
	public HealthItemDto getDto()
	{
		return dto;
	}

	@Override
	public void setDto(HealthItemDto dto)
	{
		this.dto = (IdmsDto)dto;
	}
	
	@Override
	public IDMSStatus refreshStatus() 
	{
        // No lock in here.. this could take a while.
        logger.trace("Trying to get status from IDMS " + dto.getIp() + ":" + dto.getPort());

        IDMSStatus status = null;
        InputStream is = null;

        try {
            XbrcService xbrcService = new XbrcService();
            is = xbrcService.makeRestfullGETRequest(dto.getIp(), dto.getPort(), "/IDMS/status", 0, 0);
            if (is == null)
            {
            	logger.warn("Request " + dto.getIp() + ":" + dto.getPort() + "/status" + " produced no response");
            	return status;
            }
            
            status = XmlUtil.convertToPojo(is, IDMSStatus.class);
        }
        catch (Exception e) {
            logger.error("Failed to get IDMS status: " + e.getMessage());
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                }
            }
        }

        Date now = new Date();
        final HealthStatusDto oldStatus = dto.getStatus();
        final HealthStatusDto newStatus = new HealthStatusDto(
                (status == null) || (status.getStatus() == null) ? null : status.getStatus().name(),
                (status == null) ? null : status.getStatusMessage());

        if (status != null) {
            if (status != null) {
                logger.trace("Got status from IDMS " + dto.getIp() + ":" + dto.getPort());

                // We were able to get status from idms so let's update it's last JMS time.
                dto.setLastDiscovery(now);
                dto.setNextDiscovery(new Date(now.getTime() + PkConstants.STATUS_REFRESH_MS));
                dto.setStatus(newStatus);
                dto.setVersion(status.getVersion());
                dto.setDatabaseURL(status.getDatabaseURL());
                dto.setDatabaseUserName(status.getDatabaseUserName());
                dto.setDatabaseVersion(status.getDatabaseVersion());
                dto.setReadOnly("" + status.isReadOnly());
                dto.setActive(Boolean.TRUE);
                dto.setStartTime(DateUtils.format(status.getStartTime().getTime()));

                AuditHelper.generateStatusChangeEvent(oldStatus, newStatus, dto);

                // persist changes
                if (dto.getId() != null) {
                    HealthItemDao.getInstance().update(dto);
                }
                else {
                    HealthItemDao.getInstance().insert(dto);
                }
            }
            else {
                logger.trace("Failed to get status from IDMS " + dto.getIp() + ":" + dto.getPort());

                dto.setStatus(new HealthStatusDto(StatusType.Red.name(), "Not Reachable"));
                dto.setNextDiscovery(new Date(now.getTime() + 2000));
                if (dto.getId() != null) {
                    HealthItemDao.getInstance().update(dto);
                }
            }

            return status;
        }

        AuditHelper.generateStatusChangeEvent(oldStatus, newStatus, dto);
        dto.setStatus(new HealthStatusDto(StatusType.Red.name(), "Not Reachable"));

        // We failed to get status, most likely the IDMS is dead
        dto.setNextDiscovery(new Date(1000));
        return status;
    }
	
	@Override
	public void update(HealthItemDto healthItem)
	{
		IdmsDto item = (IdmsDto)healthItem;
		
		//TODO use reflection to do this!
        if (dto.getId() == item.getId()) 
        {
        	// values common to all health items
        	if (item.getIp() != null)
        		dto.setIp(item.getIp());
        	if (item.getPort() != null)
        		dto.setPort(item.getPort());
        	if (item.getHostname() != null)
        		dto.setHostname(item.getHostname());
        	if (item.getName() != null)
        		dto.setName(item.getName());
            dto.setLastDiscovery(item.getLastDiscovery());
            dto.setNextDiscovery(item.getNextDiscovery());
            dto.setStatus(item.getStatus());
            dto.setActive(item.isActive());
            if (item.getVersion() != null)
            	dto.setVersion(item.getVersion());
            if (item.getVip() != null)
            	dto.setVip(item.getVip());
            if (item.getVport() != null)
            	dto.setVport(item.getVport());
            
            // values specific to this type of health item
            if (item.getStartTime() != null)
            	dto.setStartTime(item.getStartTime());
            if (item.getDatabaseURL() != null)
            	dto.setDatabaseURL(item.getDatabaseURL());
            if (item.getDatabaseURL() != null)
            	dto.setDatabaseUserName(item.getDatabaseUserName());
            if (item.getDatabaseVersion() != null)
            	dto.setDatabaseVersion(item.getDatabaseVersion());
        }
    }
}
