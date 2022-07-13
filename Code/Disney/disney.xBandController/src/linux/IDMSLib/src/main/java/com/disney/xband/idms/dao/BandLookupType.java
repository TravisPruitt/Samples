package com.disney.xband.idms.dao;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum BandLookupType 
{
	XBANDID(0),
	BANDID(1),
	LRID(2),
	TAPID(3),
	SECUREID(4),
	UID(5),
	PUBLICID(6);
	
	private final int value;
	
	BandLookupType(int value)
	{
		this.value = value;
	}
	
	private static final Map<Integer,BandLookupType> lookup = 
			new HashMap<Integer,BandLookupType>();

	static 
	{
	    for(BandLookupType item : EnumSet.allOf(BandLookupType.class))
	    {
	         lookup.put(item.getValue(), item);
	    }
	}
	
	public int getValue() 
	{ 
		return value; 
	}

    public static BandLookupType get(int value) 
    { 
         return lookup.get(value); 
    }
}
