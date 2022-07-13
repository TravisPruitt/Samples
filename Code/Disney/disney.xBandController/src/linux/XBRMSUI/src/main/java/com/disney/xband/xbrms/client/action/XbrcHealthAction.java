package com.disney.xband.xbrms.client.action;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.client.model.MonitoredAppDpo;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.Comparators;
import com.disney.xband.xbrms.common.model.HealthItem;
import com.disney.xband.xbrms.common.model.HealthItemType;
import com.disney.xband.xbrms.common.model.HealthSchema;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.opensymphony.xwork2.Preparable;

public class XbrcHealthAction extends BaseAction implements Preparable {
    private static Logger logger = Logger.getLogger(XbrcHealthAction.class);

	private List<MonitoredAppDpo> inventory;
	private Collection<HealthItemType> healthItemTypes;
	private Long id;

	@Override
	public void prepare() throws Exception 
	{
		inventory = new LinkedList<MonitoredAppDpo>();
		healthItemTypes = Arrays.asList(HealthItemType.values());
	}
	
	private String buildInventory() throws Exception {
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

		List<HealthItem> current = null;

		for (String key : healthItems.keySet()) {
			if (key.equals(XbrcDto.class.getName())){
				// sort xBRC according to venue id and then master/slave ha indication
				current = (List<HealthItem>)healthItems.get(key);
				
				if (current != null && current.size() > 0) {
					Collections.sort(current, new Comparators.XbrcByVenueAndHAStatusComparator());
                }
			}
		}
		
		for (Map.Entry<String, Collection<HealthItem>> entry : healthItems.entrySet()) {
			MonitoredAppDpo ma = new MonitoredAppDpo();
			ma.setValues(entry.getValue());
			ma.setType(entry.getValue().iterator().next().getItem().getType());			
			ma.setDefs(HealthSchema.getInstance().getFields(entry.getKey()));
			inventory.add(ma);
		}

        Collections.sort(inventory);
        return SUCCESS;
	}

	@Override
	public String execute() throws Exception {		
		return buildInventory();
	}
	
	public String removeXbrc() throws Exception {
		
		if (id != null && id >= 0) {
            try {
                XbrmsUtils.getRestCaller().deleteHealthItem(id.toString());
            }
            catch(Exception e) {
                final String errorMessage = "Failed to delete health item with ID=" + id;
                this.addActionError(errorMessage);

                if (logger.isDebugEnabled()) {
                    logger.error(errorMessage, e);
                }
                else {
                    logger.error(errorMessage);
                }
            }
		}
	
		return buildInventory();
	}
	
	public String inactivateXbrc() throws Exception {
		
		if (id != null && id >= 0) {
            try {
                XbrmsUtils.getRestCaller().deactivateHealthItem(id.toString());
            }
            catch(Exception e) {
                final String errorMessage = "Failed to deactivate health item with ID=" + id;
                this.addActionError(errorMessage);

                if (logger.isDebugEnabled()) {
                    logger.error(errorMessage, e);
                }
                else {
                    logger.error(errorMessage);
                }
            }
		}
	
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
