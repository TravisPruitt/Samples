package com.disney.xband.xbrms.common.model;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class HealthItem {

    public class HealthFieldInt {
	    private IHealthField desc;
    	private String value;
    	private String name;

	    public IHealthField getDesc()
	    {
		    return desc;
	    }

	    public void setDesc(IHealthField desc)
	    {
		    this.desc = desc;
	    }

	    public String getValue()
	    {
		    return value;
	    }

	    public void setValue(String value)
	    {
		    this.value = value;
	    }

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getId()
		{
			return this.desc.id();
		}

		public void setId(String id){
		}
		
		public String getType()
		{
			return this.desc.type().name();
		}
		
		public void setType(String type){
		}
		
		public boolean getMandatory(){
			return this.desc.mandatory();
		}
		
		public void setMandatory(){
		}
    }

	private static String dataFormat = "d MMM yyyy HH:mm:ss";
	private static Logger logger = Logger.getLogger(HealthItem.class);
	private static transient Pattern illegalChars = Pattern.compile("\\s|\r\n|<|>");
	private HealthItemDto item;
	private List<HealthFieldInt> fields;
	private HealthItemDto slave;
	private List<HealthFieldInt> slaveFields;
	
	public HealthItem(HealthItemDto item) throws IllegalArgumentException
	{
		if (item == null)
			throw new IllegalArgumentException("Can not create a HealthItem object with null HealthItemDto.");
		
		this.item = item;
		
		fields = new LinkedList<HealthFieldInt>();
		for (HealthField fi : HealthSchema.getInstance().getFields(item.getClass().getName()))
		{
			HealthFieldInt fd = new HealthFieldInt();
			
			fd.setDesc(fi.getDesc());
						
			try {
				String value = (String)fi.getMethod().invoke(item, new Object[]{});
				fd.setValue(value);
				
				String name = fi.getMethod().getName().substring(3);
				fd.setName(name);
			}
			catch (Exception e) {
				logger.error("Failed to invoke function " + fi.getDesc().name() + " on Class " + item.getClass().getName(), e);
			}
			
			fields.add(fd);
		}
	}

	public HealthItemDto getItem() {
		return item;
	}
	
	public List<HealthFieldInt> getFields() {
		return fields;
	}
	
	public List<HealthFieldInt> getSlaveFields(){
		return slaveFields;
	}

	public void setXbrc(XbrcDto xbrcDto) {
		this.item = xbrcDto;
	}
	
	public String getLastDiscoveryAgo(){
		if (item.getLastDiscovery() == null)
			return "";
			
		return formatAgo(item.getLastDiscovery().getTime());
		
	}
	
	public String getSlaveLastDiscoveryAgo(){
		if (slave == null)
			return "";
		
		if (slave.getLastDiscovery() == null)
			return "";
			
		return formatAgo(slave.getLastDiscovery().getTime());
		
	}
	
	public String getLastDiscovery(){
		if (item.getLastDiscovery() != null)
			return format(item.getLastDiscovery().getTime());
		return "";
	}
	
	public String getNextDiscovery(){
		if (item.getNextDiscovery() != null)
			return format(item.getNextDiscovery().getTime());
		return "";
	}
	
	public boolean isAlive(){
		return item.isAlive();
	}
	
	public String getStatusColor()
	{
		if (!isAlive())
			return "Red";
		if (item.getStatus() == null)
			return "Green";
		
		return item.getStatus().getStatus().toString();
	}
	
	public String getSlaveStatusColor()
	{
		if (slave == null)
		return "";
		
		if (!slave.isAlive())
			return "Red";
		if (slave.getStatus() == null)
			return "Green";
	
		return slave.getStatus().getStatus().toString();
	}
	
	public String getStatusIcon() {
		String color = getStatusColor();		
		if (color.equals("Green"))
			return "green-light.png";
		if (color.equals("Red"))
			return "red-light.png";
		if (color.equals("Yellow"))
			return "yellow-light.png";
		return "";
	}
	
	public int getStatusWeight()
	{
		if (item == null || item.status == null || item.status.getStatus() == null)
			return -1;
		
		return item.status.getStatus().ordinal();
	}
	
	public int getSlaveStatusWeight()
	{
		if (slave == null || slave.status == null || slave.status.getStatus() == null)
			return -1;
		
		return slave.status.getStatus().ordinal();
	}
	
	public String getSlaveStatusIcon(){
		String color = getSlaveStatusColor();		
		if (color.equals("Green"))
			return "green-light.png";
		if (color.equals("Red"))
			return "red-light.png";
		if (color.equals("Yellow"))
			return "yellow-light.png";
		return "";
	}
	
	public String getStatusMessage() {
		if (!isAlive())
			return "Not running";
		
		if (item.getStatus() == null || item.getStatus().getMessage() == null)
			return "";
		
		// browsers blow up on new line c
		return illegalChars.matcher(item.getStatus().getMessage()).replaceAll(" ");
	}
	
	private String format(Long date){
		SimpleDateFormat format = new SimpleDateFormat(dataFormat);
		return format.format(date);
	}
	
	private String formatAgo(Long date) {
		if (date == 0L)
			return "never";
		
		long time = (new Date().getTime() - date) / 1000;
		long months = time / 60 / 60 / 24 / 30;
		if (months != 0l){
			return months + " month(s) ago";
		}
		
		long weeks = time / 60 / 60 / 24 / 7;
		if (weeks != 0l){
			return weeks + " week(s) ago";
		}
		
		long days = time / 60 / 60 / 24;
		if (days != 0l){
			return days + " day(s) ago";
		}
		
		long hours = time / 60 / 60;
		if (hours != 0l){
			return hours + "h ago";
		}
		
		long minutes = time / 60;
		if (minutes != 0l){
			return minutes + "m ago";
		}
		
		return time + "s ago";
				
	}
	
	public String getHostname()
	{
		return normalizeAddress(item.getHostname());
	}
	
	public String getAddress()
	{
		return item.getIp() + ":" + item.getPort();
	}
	
	public String getSlaveHostname()
	{
		if (slave == null)
			return "";
		
		return normalizeAddress(slave.getHostname());
	}
	
	public String getSlaveAddress()
	{
		if (slave == null)
			return "";
		
		return slave.getIp() + ":" + slave.getPort();
	}
	
	private static String normalizeAddress(String name) {
        if(name != null && name.length() != 0) {
        	/*
        	 *  According to RFC952, a hostname can not begin with a number.
        	 *  This rule isn't always respected, but there is nothing we can do about that.
        	 */
        	if (Character.isDigit(name.charAt(0)))
        		return name;	// don't truncate an ip
        	
        	// must be a hostname, remove the domain part
            final int ind = name.indexOf(".");
            if(ind > 0) {
            	return name.substring(0, ind);
            }
        }

        return name;
    }

    public static Map<String, Collection<HealthItem>> fromDtoMap(final Map<String, HealthItemListDto> map) {
        final LinkedHashMap<String, Collection<HealthItem>> ret = new LinkedHashMap<String, Collection<HealthItem>>();

        for(String key : map.keySet()) {
            final LinkedList<HealthItem> list = new LinkedList<HealthItem>();

            for(HealthItemDto dto : map.get(key).getHealthItem()) {
                list.add(new HealthItem(dto));
            }

            ret.put(key, list);
        }

        return ret;
    }

	public HealthItemDto getSlave()
	{
		return slave;
	}

	public void setSlave(HealthItemDto slave)
	{
		this.slave = slave;
	}
	
	public String getSlaveAsString()
	{
		if (slave == null)
			return "";
		
		StringBuffer slaveInfo = new StringBuffer();
		slaveInfo.append(slave.getId()).append(',')
			.append(slave.getHostname()).append(',')
			.append(slave.getPort()).append(',')
			.append(slave.getVip()).append(',')
			.append(slave.getVersion()).append(',')
			.append(slave != null ? formatAgo(slave.getLastDiscovery().getTime()) : "").append(',')
			.append(getSlaveStatusIcon()).append(',');
		
		if (slave.getStatus().getMessage() == null || slave.getStatus().getMessage().isEmpty())
			slave.getStatus().setMessage("Message not available.");
		
		slaveInfo.append(slave.getStatus().getMessage());
		
		return slaveInfo.toString();
	}

	public void initSlavesDynamicFields()
	{
		if (slave == null)
			return;
		
		slaveFields = new LinkedList<HealthFieldInt>();
		for (HealthField fi : HealthSchema.getInstance().getFields(slave.getClass().getName()))
		{
			HealthFieldInt fd = new HealthFieldInt();
			
			fd.setDesc(fi.getDesc());
						
			try {
				String value = (String)fi.getMethod().invoke(slave, new Object[]{});
				fd.setValue(value);
				
				String name = fi.getMethod().getName().substring(3);
				fd.setName(name);
			}
			catch (Exception e) {
				logger.error("Failed to invoke function " + fi.getDesc().name() + " on Class " + slave.getClass().getName(), e);
			}
			
			slaveFields.add(fd);
		}
	}
}
