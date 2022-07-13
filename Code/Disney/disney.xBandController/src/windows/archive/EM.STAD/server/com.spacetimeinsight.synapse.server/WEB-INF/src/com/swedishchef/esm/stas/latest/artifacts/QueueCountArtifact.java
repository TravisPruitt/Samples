package com.swedishchef.esm.stas.latest.artifacts;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.enterprisehorizons.magma.designtime.artifact.GeoArtifact;

/* This is what is going to be available to the eco-system and spatial view 
 * for further processing
 */
public class QueueCountArtifact extends GeoArtifact{  
	private int count;
	private double avewait;
	private String location;
	private List<Object[]> timeTrend = new ArrayList<Object[]>();
	
	public QueueCountArtifact()
	{
		
	}
	
	public QueueCountArtifact(int count, String location)
	{
		setCount(count);
		this.location = location;
	}
	
	public QueueCountArtifact(double avewait, String location)
	{
		setAvewait(avewait);
		this.location = location;
	}
	
	public void setAvewait(double avewait) {
		this.avewait = avewait;
	}
	
	public double getAvewait() {
		return avewait;
	}
	
	
	
	public int getCount() {
		return count;
	}
	
	public void addTimeTrendValue(Date timestamp, String type){
		if(type.equals("count"))
			timeTrend.add(new Object[]{timestamp,this.count});
		else
		{
			timeTrend.add(new Object[]{timestamp,this.avewait});
		}
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public List<Object[]> getTimeTrend() {
		return timeTrend;
	}
}
