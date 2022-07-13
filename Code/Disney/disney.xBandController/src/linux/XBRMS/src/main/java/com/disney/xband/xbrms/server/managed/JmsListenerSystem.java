package com.disney.xband.xbrms.server.managed;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.disney.xband.xbrms.server.AuditHelper;
import com.disney.xband.xbrms.server.model.HealthItemDao;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.health.ListenerStatus;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.model.HealthStatusDto;
import com.disney.xband.xbrms.common.model.HealthItemDto;
import com.disney.xband.xbrms.common.model.IHealthSystem;
import com.disney.xband.xbrms.common.model.JmsListenerDto;

public class JmsListenerSystem implements IHealthSystem
{
	private JmsListenerDto dto;
	
	private static Logger logger = Logger.getLogger(JmsListenerSystem.class);
	
	public JmsListenerSystem(JmsListenerDto dto)
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
		this.dto = (JmsListenerDto)dto;
	}
	
	@Override
	public ListenerStatus refreshStatus() {
        // No lock in here.. this could take a while.
        logger.trace("Trying to get status from JMSListener " + dto.getIp() + ":" + dto.getPort());

        ListenerStatus status = null;

        InputStream is = null;
        try {
            XbrcService xbrcService = new XbrcService();
            is = xbrcService.makeRestfullGETRequest(dto.getIp(), dto.getPort(), "status", 0, 0);
            if (is == null)
            {
            	logger.warn("Request " + dto.getIp() + ":" + dto.getPort() + "/status" + " produced no response");
            	return status;
            }
            
            status = XmlUtil.convertToPojo(is, ListenerStatus.class);
        }
        catch (Exception e) {
            logger.error("Failed to get JmsListener status: " + e.getMessage());
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
                logger.trace("Got status from JmsListener " + dto.getIp() + ":" + dto.getPort());

                // We were able to get status from idms so let's update it's last JMS time.
                dto.setLastDiscovery(now);
                dto.setNextDiscovery(new Date(now.getTime() + PkConstants.STATUS_REFRESH_MS));
                dto.setStatus(newStatus);
                dto.setVersion(status.getVersion());
                dto.setBootTime(DateUtils.format(status.getBootTime().getTime()));
                dto.setXiDatabaseVersion(status.getXiDatabaseVersion());
                dto.setGffDatabaseVersion(status.getGffDatabaseVersion());
                dto.setXbrcMessagesSinceStart("" + status.getXbrcMessagesSinceStart());
                dto.setGffMessagesSinceStart("" + status.getGffMessagesSinceStart());
                dto.setXbmsMessagesSinceStart("" + status.getXbmsMessagesSinceStart());
                dto.setGxpMessagesSinceStart("" + status.getGxpMessagesSinceStart());
                dto.setCacheSize("" + status.getCacheSize());
                dto.setCacheCapacity("" + status.getCacheCapacity());
                dto.setActive(Boolean.TRUE);

                AuditHelper.generateStatusChangeEvent(oldStatus, newStatus, dto);

                // persist
                if (dto.getId() != null) {
                    HealthItemDao.getInstance().update(dto);
                }
                else {
                    HealthItemDao.getInstance().insert(dto);
                }
            }
            else {
                logger.trace("Failed to get status from JmsListener " + dto.getIp() + ":" + dto.getPort());

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

        // We failed to get status, most likely JmsListenerDto is dead
        dto.setNextDiscovery(new Date(1000));

        return status;
    }
	
	@Override
	public void update(HealthItemDto healthItem)
	{
		JmsListenerDto item = (JmsListenerDto)healthItem;
		
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
            if (item.getBootTime() != null)
            	dto.setBootTime(item.getBootTime());
            if (item.getXiDatabaseVersion() != null)
            	dto.setXiDatabaseVersion(item.getXiDatabaseVersion());
            if (item.getXbrcMessagesSinceStart() != null)
            	dto.setXbrcMessagesSinceStart(item.getXbrcMessagesSinceStart());
            if (item.getGxpMessagesSinceStart() != null)
            	dto.setGxpMessagesSinceStart(item.getGxpMessagesSinceStart());
            if (item.getXbmsMessagesSinceStart() != null)
            	dto.setXbmsMessagesSinceStart(item.getXbmsMessagesSinceStart());
            if (item.getGffMessagesSinceStart() != null)
            	dto.setGffMessagesSinceStart(item.getGffMessagesSinceStart());
            if (item.getCacheCapacity() != null)
            	dto.setCacheCapacity(item.getCacheCapacity());
            if (item.getCacheSize() != null)
            	dto.setCacheSize(item.getCacheSize());
        }
    }
}
