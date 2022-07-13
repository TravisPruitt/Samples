package com.disney.xband.xbrc.lib.config;

import org.apache.log4j.Logger;

// ha status - NOTE: these MUST be kept in sort order so that the compare function works properly!
public enum HAStatusEnum
{
	unknown,
	solo,
	master,
	slave;
	
	private static Logger log = Logger.getLogger(HAStatusEnum.class);
	
	public static HAStatusEnum getStatus(String name)
	{
		if (name == null || name.trim().isEmpty())
			return unknown;
		
		try 
		{
			HAStatusEnum haStatus = valueOf(name.toLowerCase());
			
			if (haStatus == null)
				haStatus = unknown;
			
			return haStatus;
		}
		catch (IllegalArgumentException e)
		{
			log.warn("Attempt at creating an undefined HAStatusEnum " + name + ". Using EAStatusEnum.unknown in its stead");
			return unknown;
		}
	}
	
	/**
	 * Natural order of this enum is: null, unknown, master, slave
	 * 
	 * @param status1
	 * @param status2
	 * @return
	 */
	public static int compare(HAStatusEnum status1, HAStatusEnum status2)
	{
		// deal with null cases
		if (status1 == null && status2 != null)
			return -1;
		if (status1 != null && status2 == null)
			return 1;
		if (status1 == null && status2 == null)
			return 0;
		
		// compare ordinals
		int nStatus1 = status1.ordinal();
		int nStatus2 = status2.ordinal();
		if (nStatus1 < nStatus2)
			return -1;
		else if (nStatus1 == nStatus2)
			return 0;
		else
			return 1;
	}
}