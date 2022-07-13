package com.disney.xband.xbrc.lib.entity;

public enum LocationType {
	Undefined,	// 0
	Entry,		// 1
	Queue,		// 2
	Exit,		// 3
	Load,		// 4
	InCar,		// 5 
	Merge,		// 6
	xPassEntry,	// 7
	Combo,		// 8 (Combo is xPassEntry + Merge)
	Waypoint;	// 9
	
	public static LocationType getByOrdinal(int ordinal){
		
		switch(ordinal){
			case 0: return LocationType.Undefined;
			case 1: return LocationType.Entry;
			case 2: return LocationType.Queue;
			case 3: return LocationType.Exit;
			case 4: return LocationType.Load;
			case 5: return LocationType.InCar;
			case 6: return LocationType.Merge;
			case 7: return LocationType.xPassEntry;
			case 8: return LocationType.Combo;
			case 9: return LocationType.Waypoint;
			default: return null;
		}
	}
}
