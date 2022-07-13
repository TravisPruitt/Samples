package com.disney.xband.xbrc.ui.edit.action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.disney.xband.ac.lib.IXbPrincipal;
import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.SchedulerItemParameterMetadata;
import com.disney.xband.common.scheduler.ui.SchedulerItemDisplay;
import com.disney.xband.xbrc.ui.UISchedulerClient;
import com.disney.xband.xbrc.ui.action.BaseAction;

public class SchedulerItemAction extends BaseAction {
	private UISchedulerClient client;
	private String itemKey;
	private String jobClassName;
	private SchedulerItem item;
	private SchedulerItemMetadata metadata;
	private SchedulerItemDisplay itemDisp;
	private Collection<SchedulerItem> items;
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		
		client = new UISchedulerClient();
	}
	
	@Override
	public String execute() throws Exception {
		super.execute();
		
		try
		{
			initialize();
		}
		catch(Exception e) {
			addActionError(e.getLocalizedMessage());
			return INPUT;
		}
		
		return SUCCESS;
	}
	
	private boolean validateInput(SchedulerItem newItem) {
		boolean ret = true;
		
		if (newItem.getDescription() == null || newItem.getDescription().isEmpty()) {
			addFieldError("description", "The description cannot be empty");
			ret = false;
		}
		
		if (newItem.getRunOnceDate() == null) {
			try {
				client.checkCronExpression(newItem.getSchedulingExpression());
			} catch (Exception e) {
				addFieldError("shcedulingExpression", e.getLocalizedMessage());
				ret = false;
			}
		}
		
		for (SchedulerItemParameter param : item.getParameters()) {
			String error = validateParameter(param);
			if (error != null) {
				addFieldError(param.getHtmlSafeName(), error);
				ret = false;
			}
		}
		
		return ret;
	}
	
	private IXbPrincipal getPrincipal() {
		HttpServletRequest request = ServletActionContext.getRequest();
		IXbPrincipal principal = (IXbPrincipal) request.getUserPrincipal();
		return principal;
	}
	
	public String saveItem() throws Exception {
		
		// Save off the item with fields populated by struts from the HTML form
		SchedulerItem newItem = item;
		
		try {
			initialize();
		}
		catch(Exception e) {
			addActionError(e.getLocalizedMessage());
			return INPUT;
		}
				
		HttpServletRequest request = ServletActionContext.getRequest();
		
		for (SchedulerItemParameter param : item.getParameters()) {
			String paramValue = request.getParameter(param.getHtmlSafeName());
			param.setValue(paramValue);
		}
		
		item.setDescription(newItem.getDescription());
		item.setSchedulingExpression(newItem.getSchedulingExpression());
		item.setEnabled(newItem.getEnabled());
		
		if (newItem.getRunOnceDate() != null)
			item.setRunOnceDate(newItem.getRunOnceDate());
		
		if (!validateInput(item)) {
			addActionError("The information provided in one or more input fields below is not correct." );
			return INPUT;
		}
		
		try {
			client.saveItem(item, getPrincipal() != null ? getPrincipal().getName() : null, null);
			return "saved";
		}
		catch(Exception e) {
			addActionError("Failed to update the Scheduler Task: " + e.getLocalizedMessage());
			return INPUT;
		}
	}
	
	public String deleteItem() throws Exception {
		
		try {
			initialize();
		}
		catch(Exception e) {
			addActionError(e.getLocalizedMessage());
			return INPUT;
		}
		
		try {
			client.deleteItem(itemKey, getPrincipal() != null ? getPrincipal().getName() : null, null);
			return "saved";
		}
		catch(Exception e) {
			addActionError("Failed to delete the Scheduler Task: " + e.getLocalizedMessage());
			return INPUT;
		}
	}
	
	private void initialize() throws Exception {
				
		if (isNewItem()) {
			
			items = client.getItems(null);
			
			for (SchedulerItem it : items) {
				if (it.getJobClassName().equals(jobClassName)) {
					addActionError("Please note that there already exists another scheduler task with description: \"" + it.getDescription() + 
									"\". Running two identical tasks at the same time may cause problems.");
					break;
				}
			}
			
			Collection<SchedulerItemMetadata> mdList = client.getMetadata(null);
			for (SchedulerItemMetadata md : mdList) {
				if (md.getJobClassName().equals(jobClassName)) {
					metadata = md;
					break;
				}
			}
			
			if (metadata == null) {
				addActionError("Internal system error: missing metadata for jobClassName = " + 
						jobClassName + ". Please contact support.");
				return;
			}
			
			item = new SchedulerItem();
			item.setDescription(metadata.getName());
			item.setItemKey(UUID.randomUUID().toString());
			item.setJobClassName(jobClassName);
			item.setMetadata(metadata);
			item.setEnabled(true);
			item.setSchedulingExpression(metadata.getDefaultSchedulingExpression());
			
			item.setParameters(new LinkedList<SchedulerItemParameter>());
			for (SchedulerItemParameterMetadata pmd : metadata.getParameters()) {
				SchedulerItemParameter param = new SchedulerItemParameter();
				param.setName(pmd.getName());
				param.setMetadata(pmd);
				param.setItemKey(item.getItemKey());
				param.setSequence(pmd.getSequence());
				param.setValue(pmd.getDefaultValue());
				item.getParameters().add(param);
			}
		}
		else if (itemKey != null && !itemKey.isEmpty()) {
			item = client.getItem(itemKey, null);
		}
		
		// TODO: check if the item has been deleted by someone else and issue a warning.
		
		if (item != null) {
			itemDisp = new SchedulerItemDisplay(item);
			metadata = item.getMetadata();
		}
	}
	
	private String validateParameter(SchedulerItemParameter param) {
		
		if (param.getValue() == null || param.getValue().isEmpty() && param.getMetadata().isRequired())
			return "Parameter " + param.getName() + " is required and cannot be empty";
		
		switch(param.getMetadata().getType()) {
		case BOOLEAN:
			if (param.getValue().equals("true") || param.getValue().equals("false"))
				return null;
			return "Invalid boolean expression. Please enter: true or false";
		case ENVPROPVALUE:
			if (param.getValue().trim().contains(" ") || param.getValue().trim().contains("\t"))
				return param.getName() + " cannot contain spaces";			
			break;
		case NUMBER:
			try {
				Float.parseFloat(param.getValue());
			}
			catch(NumberFormatException e) {
				return param.getName() + " must be a number (" + e.getLocalizedMessage() + ")";
			}
			break;
		case POSITIVENUMBER:
			Float fl = 0f;
			try {
				fl = Float.parseFloat(param.getValue());
			}
			catch(NumberFormatException e) {
				return param.getName() + " must be a number (" + e.getLocalizedMessage() + ")";
			}
			if (fl < 0)
				return param.getName() + " must be a non-negative number";
			break;
		case INTEGER:
			try {
				Integer.parseInt(param.getValue());
			}
			catch(NumberFormatException e) {
				return param.getName() + " must be an integer (" + e.getLocalizedMessage() + ")";
			}
			break;
		case POSITIVEINTEGER:
			Integer i = 0;
			try {
				i = Integer.parseInt(param.getValue());
			}
			catch(NumberFormatException e) {
				return param.getName() + " must be a non-negative integer (" + e.getLocalizedMessage() + ")";
			}
			if (i < 0)
				return param.getName() + " must be a non-negative integer";
			break;
		case STRING:
			break;
		}
		
		return null;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String key) {
		this.itemKey = key;
	}

	public SchedulerItem getItem() {
		return item;
	}

	public void setItem(SchedulerItem item) {
		this.item = item;
	}

	public SchedulerItemMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(SchedulerItemMetadata metadata) {
		this.metadata = metadata;
	}

	public SchedulerItemDisplay getItemDisp() {
		return itemDisp;
	}

	public void setItemDisp(SchedulerItemDisplay itemDisp) {
		this.itemDisp = itemDisp;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	
	private boolean isNewItem() {
		return jobClassName != null && !jobClassName.isEmpty();
	}
}
