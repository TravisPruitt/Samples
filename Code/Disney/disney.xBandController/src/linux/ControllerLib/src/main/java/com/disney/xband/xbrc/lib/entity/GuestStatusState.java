package com.disney.xband.xbrc.lib.entity;

public enum GuestStatusState {
	INDETERMINATE,		// a bad, unexpected, state
	NEW,				// a newly added guest in the GST
	HASENTERED,			// guest has entered the attraction
	HASMERGED,			// xpass guest has tapped the merge DAP
	LOADING,			// waiting for a car
	RIDING,				// ride in progress
	EXITED, 			// left the attractsion
	DELETABLE			// exited a long time ago - delete from CST
}
