package com.swedishchef.esm.stas.latest.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.swedishchef.esm.stas.latest.artifacts.AttractionArtifact;
import com.swedishchef.esm.stas.latest.artifacts.ParkArtifact;
import com.swedishchef.esm.stas.latest.artifacts.QueueCountArtifact;


 
public class DataManager {
	
	private static Map<String, Map<String, double[]>> sensorpointsbylocation = Collections.synchronizedMap(new HashMap<String, Map<String, double[]>>());
	private static Map<String,AttractionArtifact> attractionMap = Collections.synchronizedMap(new HashMap<String,AttractionArtifact>());
	private static Map<String,ParkArtifact> parkMap = Collections.synchronizedMap(new HashMap<String,ParkArtifact>());
	
	private static DataManager _instance;
	public static ArrayList<String> attractionNames = new ArrayList<String>(); //marked for removal
	private static Object[][] attractionConstants = null;
	
	private DataManager(){
		createSensorPoints();
	}

	public static DataManager getInstance(){
		if(_instance ==null){
			_instance = new DataManager();
		}
		return _instance;
	}
	
	private void createSensorPoints(){

		HashMap<String, double[]> sensorlocationsatattraction1 = new HashMap<String, double[]>();
		sensorlocationsatattraction1.put(AttractionArtifact.CYLINDER_COORDINATES,new double[]{-122.3355061638373,47.61166018512169});
		sensorlocationsatattraction1.put(AttractionArtifact.SB_GUESTS_SERVED_COUNT,new double[]{-122.335336796281,47.61154205674247});
		sensorlocationsatattraction1.put(AttractionArtifact.XP_GUESTS_SERVED_COUNT,new double[]{-122.3354256920788,47.61150018308143});
		sensorlocationsatattraction1.put(AttractionArtifact.SB_QUEUE_COUNT,new double[]{-122.3354794101293,47.61176547879237});
		sensorlocationsatattraction1.put(AttractionArtifact.XP_QUEUE_COUNT,new double[]{-122.3355614585027,47.61184534166546});
		sensorpointsbylocation.put("xCoaster", sensorlocationsatattraction1);
			
		HashMap<String, double[]> sensorlocationsatattraction2 = new HashMap<String, double[]>();
		sensorlocationsatattraction2.put(AttractionArtifact.CYLINDER_COORDINATES,new double[]{-122.3407052344062,47.60898504329208});
		sensorlocationsatattraction2.put(AttractionArtifact.SB_GUESTS_SERVED_COUNT,new double[]{-122.3407691265624,47.6090869755602});
		sensorlocationsatattraction2.put(AttractionArtifact.XP_GUESTS_SERVED_COUNT,new double[]{-122.3406735640006,47.60912481078636});
		sensorlocationsatattraction2.put(AttractionArtifact.SB_QUEUE_COUNT,new double[]{-122.3409321918613,47.60902971998597});
		sensorlocationsatattraction2.put(AttractionArtifact.XP_QUEUE_COUNT,new double[]{-122.3408767992288,47.60904806366418});
		sensorpointsbylocation.put("xCoaster1", sensorlocationsatattraction2);

		HashMap<String, double[]> sensorlocationsatattraction3 = new HashMap<String, double[]>();
		sensorlocationsatattraction3.put(AttractionArtifact.CYLINDER_COORDINATES,new double[]{-122.3357319721069,47.60319705176055});
		sensorlocationsatattraction3.put(AttractionArtifact.SB_GUESTS_SERVED_COUNT,new double[]{-122.3365865982717,47.60259775090557});
		sensorlocationsatattraction3.put(AttractionArtifact.XP_GUESTS_SERVED_COUNT,new double[]{-122.3364395214999,47.60245733276219});
		sensorlocationsatattraction3.put(AttractionArtifact.SB_QUEUE_COUNT,new double[]{-122.3361980106259,47.60285322964045});
		sensorlocationsatattraction3.put(AttractionArtifact.XP_QUEUE_COUNT,new double[]{-122.3359137691557,47.60305093229357});
		sensorpointsbylocation.put("xCoaster2", sensorlocationsatattraction3);

	}
	
	
	public List<QueueCountArtifact> attractionLookup(String selectedEcosystem)
	{
		
		//System.out.println("In DataManager.attractionLookup(" + selectedEcosystem + ")");
		//System.out.println(attractionMap.get("xCoaster").toString());
		return attractionMap.get(selectedEcosystem).getSensordataAsList();
	}
	
	public List<AttractionArtifact> returnAttractionArtifacts() {
		//System.out.println(attractionMap.values());
		return new ArrayList<AttractionArtifact>(attractionMap.values());
	}
	
	public void buildAttraction(String attractionName,
								int SBQueueCount, 
			                    int XPQueueCount,
			                    int SBGuestsServedCount,
			                    int XPGuestsServedCount, 
			                    double AvgSBQueueWait, 
			                    double AvgXPQueueWait,
			                    int SBArrivalRate,
			                    int XPArrivalRate,
			                    double estSBQueueWait,
			                    double estXPQueueWait)
	{
		AttractionArtifact attrArtifact = attractionMap.get(attractionName);
		double[] coordinates;
		if(attrArtifact == null){
			attrArtifact = new AttractionArtifact();

			Object[] attractionValues = returnAttractionFor(attractionName);
			attrArtifact.setAttractionName(attractionName);
			attrArtifact.setAttractionStatus((String)attractionValues[1]);
			attrArtifact.setXpqueuecountcap((Integer)attractionValues[2]);
			attrArtifact.setSbqueuecountcap((Integer)attractionValues[3]);
			attrArtifact.setDisplayName((String)attractionValues[4]);
			coordinates = sensorpointsbylocation.get(attractionName).get("CC"); 
			attrArtifact.setCoordinates(coordinates[1], coordinates[0]);
			attractionMap.put(attractionName, attrArtifact);
			//System.out.println(attractionMap.get(attractionName).toString());

		}

		Calendar cal = Calendar.getInstance();
		Date currTimestamp = cal.getTime();
		//figure out sensor locations as well and distinguish between wait and regular counts
		for(String sensorlocation : AttractionArtifact.allLocations)
		{
			QueueCountArtifact sensorlocationartifact = attrArtifact.getSensordata().get(sensorlocation);
			if(sensorlocationartifact==null){
				sensorlocationartifact = new QueueCountArtifact();
				sensorlocationartifact.setLocation(sensorlocation);
				attrArtifact.addSensordata(sensorlocationartifact);
			}
			
			if(sensorlocation.equals(AttractionArtifact.AVERAGE_STANDBY_WAIT_COUNT))
			{ 
				sensorlocationartifact.setAvewait(AvgSBQueueWait);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "wait");
			}
			
			if(sensorlocation.equals(AttractionArtifact.AVERAGE_XPASS_WAIT_COUNT))
			{
				sensorlocationartifact.setAvewait(AvgXPQueueWait);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "wait");
			}
			
			if(sensorlocation.equals(AttractionArtifact.XP_ARIVAL_RATE_COUNT))
			{
				sensorlocationartifact.setCount(XPArrivalRate);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "count");
			}
			
			if(sensorlocation.equals(AttractionArtifact.SB_XP_QUEUE_COUNT))
			{
				sensorlocationartifact.setCount((XPGuestsServedCount+SBGuestsServedCount));
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "count");
			}
		
			if(sensorlocation.equals(AttractionArtifact.SB_QUEUE_COUNT))
			{
				coordinates = sensorpointsbylocation.get(attractionName).get(sensorlocation); 
				sensorlocationartifact.setCoordinates(coordinates[1], coordinates[0]);
				sensorlocationartifact.setCount(SBQueueCount);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "count");
			}
			
			if(sensorlocation.equals(AttractionArtifact.XP_QUEUE_COUNT))
			{
				coordinates = sensorpointsbylocation.get(attractionName).get(sensorlocation); 
				sensorlocationartifact.setCoordinates(coordinates[1], coordinates[0]);
				sensorlocationartifact.setCount(XPQueueCount);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "count");
			}
			
			if(sensorlocation.equals(AttractionArtifact.XP_GUESTS_SERVED_COUNT))
			{
				coordinates = sensorpointsbylocation.get(attractionName).get(sensorlocation); 
				sensorlocationartifact.setCoordinates(coordinates[1], coordinates[0]);
				sensorlocationartifact.setCount(XPGuestsServedCount);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "count");
			}
			
			if(sensorlocation.equals(AttractionArtifact.SB_GUESTS_SERVED_COUNT))
			{
				coordinates = sensorpointsbylocation.get(attractionName).get(sensorlocation); 
				sensorlocationartifact.setCoordinates(coordinates[1], coordinates[0]);
				sensorlocationartifact.setCount(SBGuestsServedCount);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "count");
			}
			
			if(sensorlocation.equals(AttractionArtifact.SB_ARIVAL_RATE_COUNT))
			{
				sensorlocationartifact.setCount(SBArrivalRate);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "count");
			}
			
			if(sensorlocation.equals(AttractionArtifact.XP_ARIVAL_RATE_COUNT))
			{
				sensorlocationartifact.setCount(XPArrivalRate);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "count");
			}
			
			if(sensorlocation.equals(AttractionArtifact.ESTIMATED_SB_WAIT))
			{
				sensorlocationartifact.setAvewait(estSBQueueWait);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "wait");
			}
			
			if(sensorlocation.equals(AttractionArtifact.ESTIMATED_XP_WAIT))
			{
				sensorlocationartifact.setAvewait(estXPQueueWait);
				sensorlocationartifact.addTimeTrendValue(currTimestamp, "wait");
			}
		}

		
	}
	
	public void addAttraction(int index, 
									   String attractionName,
									   String attractionStatus,
									   int sbqueuecountcap,
									   int xpqueuecountcap,
									   String displayName){

		if(attractionStatus==null)
		{
			attractionStatus = "Open";
		}
		attractionConstants[index][0] = attractionName;
		attractionConstants[index][1] = attractionStatus;
		attractionConstants[index][2] = xpqueuecountcap;
		attractionConstants[index][3] = sbqueuecountcap;
		attractionConstants[index][4] = displayName;
	}
	
	public boolean hasAttractionConstants(){
		if(attractionConstants==null)
			return false;
		return attractionConstants.length>0;
	}
	
	public void instantiateAttractionConstants(){
		attractionConstants = new Object[3][5];
	}
	
	public Object[][] getAttractionConstants(){
		return attractionConstants;
	}
	
	public Object[] getAttractionConstantsAt(int index){
		
		return attractionConstants[index];
	}
	
	public Object[] returnAttractionFor(String attractionName){
		//System.out.println("Returning Attraction For " + attractionName);
		for(int i = 0 ; i<attractionConstants.length; i++)
			if(attractionConstants[i][0].toString().equals(attractionName))
			{
				return attractionConstants[i];
			}
		return null;
	}
	
	public void addPark(String name, int guestCount, int forecastedGuestCount){
		ParkArtifact parkArtifact = parkMap.get(name);
		if(parkArtifact==null){
			parkArtifact = new ParkArtifact();
			parkArtifact.setCoordinates(47.61166018512169,-122.3355061638373);
			parkMap.put(name, parkArtifact);
		}
		parkArtifact.setParkName(name);
		parkArtifact.setGuestCount(guestCount);
		parkArtifact.setForecastedGuestCount(forecastedGuestCount);
	}
	
	public ParkArtifact getPark(String name){
		return parkMap.get(name);
	}
	
	public List<ParkArtifact> getParks(){
		return new ArrayList<ParkArtifact>(parkMap.values());
	}

}
