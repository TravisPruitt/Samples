package com.disney.xband.xbrms.client.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.IRestCall;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.HealthItem;
import com.disney.xband.xbrms.common.model.XbrcDto;

public class SystemHealthAjaxAction extends BaseAction {

	private Map<String,Collection<HealthItem>> inventory;
    private static Logger logger = Logger.getLogger(SystemHealthAjaxAction.class);
    
    private String err = null;
    
    private String healthItemType;
    private Long healthItemId;
    public String healthItemIp;
	public String healthItemPort;
	public HealthItem newHealthItem;

	@Override
	public String execute() throws Exception 
	{
        try {
        	Map<String,Collection<HealthItem>> allHealthItems = HealthItem.fromDtoMap(XbrmsUtils.getRestCaller().getHealthItemsInventory().getMap());
            
        	inventory = new HashMap<String,Collection<HealthItem>>();
        	
        	for (String key : allHealthItems.keySet())
        	{
        		if (!key.equals(XbrcDto.class.getName()))
        		{
        			inventory.put(
        					allHealthItems.get(key).iterator().next().getItem().getType(), 
        					allHealthItems.get(key)
        			);
        			continue;
        		}
        		
        		Map<String,HealthItem> masterXbrcs = new HashMap<String,HealthItem>();
                Map<String,HealthItem> slaveXbrcs = new HashMap<String,HealthItem>();
        		Map<String,HealthItem> otherXbrcs = new HashMap<String,HealthItem>();
        		
        		for (HealthItem item : allHealthItems.get(key))
        		{
        			XbrcDto xbrc = (XbrcDto)item.getItem();
        			
        			if (HAStatusEnum.slave.name().equals(xbrc.getHaStatus()))
        			{
        				slaveXbrcs.put(xbrc.getFacilityId(), item);
        			}
        			else
        			{
        				if (HAStatusEnum.master.name().equals(xbrc.getHaStatus()))
        				{
        					masterXbrcs.put(xbrc.getFacilityId(), item);
        				}
        				
        				otherXbrcs.put(xbrc.getIp() + "-" + xbrc.getFacilityId(), item);
        				
        				if (!inventory.containsKey(xbrc.getType()))
        					inventory.put(xbrc.getType(), otherXbrcs.values());
        			}
        		}

        		// pair up masters with their slaves
        		for (HealthItem hi : masterXbrcs.values())
        		{
        			HealthItem slave = slaveXbrcs.get(((XbrcDto)hi.getItem()).getFacilityId());
        			if (slave == null)
        				continue;
        			
        			hi.setSlave(slave.getItem());
        			hi.initSlavesDynamicFields();
        			
        			// 'remove' slaves that have a master
        			slave = null;
        			slaveXbrcs.put(((XbrcDto)hi.getItem()).getFacilityId(), null);
        		}
        		
        		// if there are any independent slaves left, try to pair them up with unknowns
        		for (HealthItem slave : slaveXbrcs.values())
        		{
        			if (slave == null)
        				continue;
        			
        			XbrcDto item = (XbrcDto)slave.getItem();
        			HealthItem other = otherXbrcs.get(item.getIp() + "-" + item.getFacilityId());
        			if (other == null || !HAStatusEnum.unknown.name().equals(((XbrcDto)other.getItem()).getHaStatus()))
        				continue;
        			
        			other.setSlave(slave.getItem());
        			other.initSlavesDynamicFields();
        		}
        	}
        }
        catch(Throwable t) {
            final String errorMessage = "Failed to retrieve health items inventory";
            
            err = errorMessage;
            
            if (logger.isDebugEnabled()) {
                logger.warn(errorMessage, t);
            }
            else {
                logger.warn(errorMessage);
            }
        }
		
		return super.execute();
	}
	
	public String addHealthItem() throws Exception {
        try {
            IRestCall caller = XbrmsUtils.getRestCaller();
            
            caller.addHealthItem(this.healthItemIp, this.healthItemPort, this.healthItemType);

            newHealthItem = new HealthItem(caller.getHealthItemByAddr(this.healthItemIp, this.healthItemPort));
            
            return super.execute();
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch(Exception e)
        {
            this.err = "Failed to add the health item " + this.healthItemIp + ":" + this.healthItemPort + " of type " + this.healthItemType + ". " + e.getMessage();

            if (logger.isDebugEnabled()) {
                logger.error(this.err, e);
            } else {
            	logger.error(this.err);
            }
        }

        return SUCCESS;
	}
	
	public String removeHealthItem() throws Exception {
		
		if (healthItemId != null && healthItemId >= 0) {
            try {
                XbrmsUtils.getRestCaller().deleteHealthItem(healthItemId.toString());
            }
            catch(Exception e) {
                final String error = "Failed to delete health item with ID=" + healthItemId;

                if (logger.isDebugEnabled()) {
                    logger.error(error, e);
                }
                else {
                    logger.error(error);
                }
            }
		}
		
		return SUCCESS;
	}
	
	public String inactivateHealthItem() throws Exception {
		
		if (healthItemId != null && healthItemId >= 0) {
            try {
                XbrmsUtils.getRestCaller().deactivateHealthItem(healthItemId.toString());
            }
            catch(Exception e) {
                final String error = "Failed to deactivate health item with ID=" + healthItemId;

                if (logger.isDebugEnabled()) {
                    logger.error(error, e);
                }
                else {
                    logger.error(error);
                }
            }
		}
		
		return SUCCESS;
	}
	
	public Map<String,Collection<HealthItem>> getInventory() {
		if (inventory == null || inventory.size() == 0)
			return null;
		
		return inventory;
	}

	public Long getHealthItemId()
	{
		return healthItemId;
	}

	public void setHealthItemId(Long id)
	{
		this.healthItemId = id;
	}

	public String getErr()
	{
		return err;
	}

	public void setErr(String error)
	{
		this.err = err;
	}

	public String getHealthItemType()
	{
		return healthItemType;
	}

	public void setHealthItemType(String healthItemType)
	{
		this.healthItemType = healthItemType;
	}

	public String getHealthItemIp()
	{
		return healthItemIp;
	}

	public String getHealthItemPort()
	{
		return healthItemPort;
	}

	public void setHealthItemIp(String xbrcIp)
	{
		this.healthItemIp = xbrcIp;
	}

	public void setHealthItemPort(String xbrcPort)
	{
		this.healthItemPort = xbrcPort;
	}

	public HealthItem getNewHealthItem()
	{
		return newHealthItem;
	}

	public void setNewHealthItem(HealthItem newHealthItem)
	{
		this.newHealthItem = newHealthItem;
	}
}
