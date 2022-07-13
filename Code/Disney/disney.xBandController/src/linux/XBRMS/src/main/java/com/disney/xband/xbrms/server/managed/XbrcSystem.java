package com.disney.xband.xbrms.server.managed;

import java.util.Date;

import com.disney.xband.xbrms.common.model.*;
import com.disney.xband.xbrms.server.AuditHelper;
import com.disney.xband.xbrms.server.model.HealthItemDao;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrms.common.PkConstants;

public class XbrcSystem implements IHealthSystem
{
	private XbrcDto dto;
	
	private static Logger logger = Logger.getLogger(XbrcSystem.class);
	
	public XbrcSystem(XbrcDto dto)
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
		this.dto = (XbrcDto)dto;
	}
	
	@Override
	public XbrcStatus refreshStatus() {
        final XbrcService xbrcService = new XbrcService();

        // No lock in here.. this could take a while.
        logger.trace("Trying to get status from XBRC " + dto.getIp() + ":" + dto.getPort());
        final XbrcStatus status = xbrcService.getStatus(dto);

        if (status != null) {
            logger.trace("Got status from XBRC " + dto.getIp() + ":" + dto.getPort());
        }
        
        Date now = new Date();
        final HealthStatusDto oldStatus = dto.getStatus();
        final HealthStatusDto newStatus = new HealthStatusDto(
                (status == null) || (status.getStatus() == null) ? null : status.getStatus().name(),
                (status == null) ? null : status.getStatusMessage());

        if (status != null) {
            // We were able to get status from the XBRC so let's update it's last JMS time.
            dto.setLastDiscovery(now);
            dto.setNextDiscovery(new Date(now.getTime() + PkConstants.STATUS_REFRESH_MS));
            dto.setName(status.getName());
            dto.setFacilityId(status.getFacilityId());
            dto.setVersion(status.getVersion());
            dto.setActive(Boolean.TRUE);
            dto.setHaStatus(HAStatusEnum.getStatus(status.getHaStatus()).name());
            dto.setHaEnabled(Boolean.toString(status.isHaenabled()));
            dto.setStartPerfTime(status.getStartPerfTime());
            dto.setModel(status.getModel());
            dto.setVip(status.getVip());
            dto.setVport(status.getVport());
            dto.setStatus(newStatus);
            AuditHelper.generateStatusChangeEvent(oldStatus, newStatus, dto);

            // persist the updated status info
            if (dto.getId() != null) {
            	HealthItemDao.getInstance().update(dto);
            }
            else {
            	HealthItemDao.getInstance().insert(dto);
            }
            
            return status;
        }

        AuditHelper.generateStatusChangeEvent(oldStatus, newStatus, dto);

        // We failed to get status, most likely the XBRC is dead
        dto.setStatus(new HealthStatusDto(StatusType.Red.name(), "Not Reachable"));
        dto.setNextDiscovery(new Date(now.getTime() + 2000));
        if (dto.getId() != null)
        	HealthItemDao.getInstance().update(dto);
        
        return status;
    }
	
	@Override
	public void update(HealthItemDto healthItem)
	{
		XbrcDto item = (XbrcDto)healthItem;
		
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
            if (item.getFacilityId() != null)
        		dto.setFacilityId(item.getFacilityId());
            if (item.getHaStatus() != null)
            	dto.setHaStatus(item.getHaStatus());
            if (item.getModel() != null)
            	dto.setModel(item.getModel());
            dto.setStartPerfTime(item.getStartPerfTime());
            if (item.getHaEnabled() != null && !item.getHaEnabled().trim().isEmpty())
            	dto.setHaEnabled(item.getHaEnabled());
        }
    }
}
