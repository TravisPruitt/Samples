package com.disney.xband.xbrms.client.action;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.client.model.MonitoredAppDpo;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.HealthItem;
import com.disney.xband.xbrms.common.model.HealthItemType;
import com.disney.xband.xbrms.common.model.HealthSchema;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.opensymphony.xwork2.Preparable;

public class SystemHealthAction extends BaseAction implements Preparable {
    private static Logger logger = Logger.getLogger(SystemHealthAction.class);

	private List<MonitoredAppDpo> inventory;
	private Collection<HealthItemType> healthItemTypes;
	private Long id;

	@Override
	public void prepare() throws Exception 
	{
		inventory = new LinkedList<MonitoredAppDpo>();
		healthItemTypes = Arrays.asList(HealthItemType.values());
	}
	
	private String buildInventory() throws Exception 
	{
        inventory.clear();
        Map<String,Collection<HealthItem>> healthItems = null;

        try {
            healthItems = HealthItem.fromDtoMap(XbrmsUtils.getRestCaller().getHealthItemsInventory().getMap());

            if(healthItems == null) {
                throw new RuntimeException();
            }
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch(Exception e) {
            final String errorMessage = "Failed to retrieve health items inventory";
            this.addActionError(errorMessage);

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }

            return SUCCESS;
        }
		
		for (Map.Entry<String, Collection<HealthItem>> entry : healthItems.entrySet()) 
		{
			if (!entry.getKey().equals(XbrcDto.class.getName())){
				MonitoredAppDpo ma = new MonitoredAppDpo();
				ma.setValues(entry.getValue());
				ma.setType(entry.getValue().iterator().next().getItem().getType());			
				ma.setDefs(HealthSchema.getInstance().getFields(entry.getKey()));
				inventory.add(ma);
				continue;
			}

			// weed out the xbrc slaves
			Collection<HealthItem> nonSlaveXbrcs = new LinkedList<HealthItem>();	
			for (HealthItem hi : entry.getValue())
			{
				XbrcDto xbrc = (XbrcDto)hi.getItem();

				// handle cases of xbrcs with ambiguous or none master/slave relationship
				if (!xbrc.isValidHaStatus() || !Boolean.parseBoolean(xbrc.getHaEnabled()))
				{
					nonSlaveXbrcs.add(hi);
					continue;
				}

				// non-slave xbrcs get to have their own row in the health status table
				if (HAStatusEnum.valueOf(xbrc.getHaStatus()) != HAStatusEnum.slave)
				{
					nonSlaveXbrcs.add(hi);
				}
			}

			if (nonSlaveXbrcs.size() > 0)
			{
				MonitoredAppDpo ma = new MonitoredAppDpo();
				ma.setValues(nonSlaveXbrcs);
				ma.setType(entry.getValue().iterator().next().getItem().getType());			
				ma.setDefs(HealthSchema.getInstance().getFields(entry.getKey()));
				inventory.add(ma);
			}
		}

        return SUCCESS;
	}

	@Override
	public String execute() throws Exception {		
		return buildInventory();
	}
	
	public Collection<MonitoredAppDpo> getInventory() {
		return inventory;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Collection<HealthItemType> getHealthItemTypes()
	{
		return healthItemTypes;
	}
}
