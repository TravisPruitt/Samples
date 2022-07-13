package com.swedishchef.esm.stas.latest.artifacts;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enterprisehorizons.magma.designtime.artifact.GeoArtifact;

/* This is what is going to be available to the eco-system and spatial view 
 * for further processing
 */
public class AttractionArtifact extends GeoArtifact 
{

	public final static String CYLINDER_COORDINATES = "CC"; 
	public final static String SB_QUEUE_COUNT = "SB"; 
	public final static String XP_QUEUE_COUNT = "XP"; 
	public final static String SB_XP_QUEUE_COUNT = "XPSBC"; 
	public final static String XP_GUESTS_SERVED_COUNT = "XPGSC";
	public final static String SB_GUESTS_SERVED_COUNT = "SBGSC";
	public final static String AVERAGE_STANDBY_WAIT_COUNT = "ASBQWC"; //not a location but treating it as it were for the sake of simplicity
	public final static String AVERAGE_XPASS_WAIT_COUNT = "AXPQWC";
	public final static String SB_ARIVAL_RATE_COUNT = "SBARC";
	public final static String XP_ARIVAL_RATE_COUNT = "XPARC";
	public final static String ESTIMATED_SB_WAIT = "ESBW";
	public final static String ESTIMATED_XP_WAIT = "EXPW";
	private String attractionName;
	private String displayName;
	private String attractionStatus;
	private int xpqueuecountcap;
	private int sbqueuecountcap;
	
	private int standbyCount;
	private int xpassCount;
	private int standbyGuestsServed;
	private int xpassGuestsServed;
	
	public final static String[] allLocations = new String[]{SB_QUEUE_COUNT, XP_QUEUE_COUNT, XP_GUESTS_SERVED_COUNT, 
															 SB_GUESTS_SERVED_COUNT, AVERAGE_STANDBY_WAIT_COUNT, 
															 AVERAGE_XPASS_WAIT_COUNT, SB_XP_QUEUE_COUNT, SB_ARIVAL_RATE_COUNT,
															 XP_ARIVAL_RATE_COUNT, ESTIMATED_SB_WAIT, ESTIMATED_XP_WAIT};
	Map<String, QueueCountArtifact> sensordata = Collections.synchronizedMap(new HashMap<String, QueueCountArtifact>());

	public AttractionArtifact()
	{
		
	}
	public String getAttractionName() {
		return this.attractionName;
	}
	public void setAttractionName(String attrName) {
		this.attractionName = attrName;
	}
	public String getAttractionStatus() {
		return this.attractionStatus;
	}
	public void setAttractionStatus(String attrStatus) {
		this.attractionStatus = attrStatus;
	}
	
	public int getStandbyCount() {
		QueueCountArtifact qcArtifact = sensordata.get(SB_QUEUE_COUNT);
		if(qcArtifact!=null) 
			standbyCount = qcArtifact.getCount();
		return standbyCount;
	}
	
	public int getXpassCount() {
		QueueCountArtifact qcArtifact = sensordata.get(XP_QUEUE_COUNT);
		if(qcArtifact!=null) 
			xpassCount = qcArtifact.getCount();
		return xpassCount;
	}


	public int getXpassGuestsServed() {
		
		QueueCountArtifact qcArtifact = sensordata.get(XP_GUESTS_SERVED_COUNT);
		if(qcArtifact!=null) 
			xpassGuestsServed = qcArtifact.getCount();
		return xpassGuestsServed;
	}

	public int getStandbyGuestsServed() {
		QueueCountArtifact qcArtifact = sensordata.get(SB_GUESTS_SERVED_COUNT);
		if(qcArtifact!=null) 
			standbyGuestsServed = qcArtifact.getCount();
		return standbyGuestsServed;
	}
	
	public List<QueueCountArtifact> getSensordataAsList() {
		return new ArrayList<QueueCountArtifact>(sensordata.values());
	}
	public Map<String, QueueCountArtifact> getSensordata() {
		return sensordata;
	}
	public void addSensordata(QueueCountArtifact queueCount) {
		sensordata.put(queueCount.getLocation(), queueCount);
	}
	public int getXpqueuecountcap() {
		return this.xpqueuecountcap;
	}
	public void setXpqueuecountcap(int XPqueuecountcap) {
		this.xpqueuecountcap = XPqueuecountcap;
	}
	public int getSbqueuecountcap() {
		return this.sbqueuecountcap;
	}
	public void setSbqueuecountcap(int SBqueuecountcap) {
		this.sbqueuecountcap = SBqueuecountcap;
	}
	
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(this.getAttractionName() + " Object {" + NEW_LINE);
		result.append(" XPCAP: " + getXpqueuecountcap() + NEW_LINE);
		result.append(" SBCAP: " + getSbqueuecountcap() + NEW_LINE);
		result.append(" STATUS: " + getAttractionStatus() + NEW_LINE);
		result.append("}");

		    return result.toString();
		  }
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
