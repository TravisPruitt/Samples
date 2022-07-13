package com.disney.xband.xbrc.ui.bean;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.config.EventConfig;
import com.disney.xband.xbrc.lib.config.ReaderConfig;

public class ReaderValue {
	private String readerId;
	private Double gain;
	private Integer threshold;
	private Map<Integer, ReaderChannel> channels;
	private LocationBean location;
	private Double avgSignalStrength;
	private Double maxSignalStrength;
	
	private static Logger logger = Logger.getLogger(ReaderValue.class);
	
	public ReaderValue(String readerId, Double gain, Integer threshold, LocationBean location) throws IllegalArgumentException {
		if (readerId == null)
			throw new IllegalArgumentException("Reader ID is required!");
		if (gain == null)
			throw new IllegalArgumentException("Gain is required!");
		if (threshold == null)
			throw new IllegalArgumentException("Threshold is required!");
		if (location == null)
			throw new IllegalArgumentException("You can not have a reader without its location!");
		
		this.readerId = readerId;
		this.gain = gain;
		this.threshold = threshold;
		this.setLocation(location);
		
		channels = new HashMap<Integer, ReaderChannel>();
		/*
		 * There should always be two channels, even if they are not picking up any guests at the moment.
		 * The default channels will get replaced with the real thing, if present.
		 */
		Date monthAgo = new Date(System.currentTimeMillis() - 2592000000L);
		channels.put(0, new ReaderChannel(readerId, 0, 0, monthAgo, gain));
		channels.put(1, new ReaderChannel(readerId, 1, 0, monthAgo, gain));
	}
	
	public String getReaderId() {
		return readerId;
	}
	
	public void addChannel(ReaderChannel channel){
		channels.put(channel.getChannelId(), channel);
	}
	
	public ReaderChannel getChannel(String id){
		if (id == null || id.trim().isEmpty())
			return null;
		
		return channels.get(Integer.parseInt(id));
	}
	
	/**
	 * @return the chanels
	 */
	public Collection<ReaderChannel> getChannelCollection() {
		return channels.values();
	}
	
	public Map<Integer, ReaderChannel> getChannelsMap()	{
		return channels;
	}

	/**
	 * @return the gain
	 */
	public Double getGain() {
		return gain;
	}

	/**
	 * @param gain the gain to set
	 */
	public void setGain(Double gain) {
		this.gain = gain;
	}

	/**
	 * @return the threshold
	 */
	public Integer getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public Double getAvgSignalStrength() {
		return avgSignalStrength;
	}

	public void setAvgSignalStrength(Double avgSignalStrength) {
		this.avgSignalStrength = avgSignalStrength;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 59;
		int result = 1;
		result = prime * result
				+ ((channels == null) ? 0 : channels.hashCode());
		result = prime * result
				+ ((readerId == null) ? 0 : readerId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReaderValue other = (ReaderValue) obj;
		if (channels == null) {
			if (other.channels != null)
				return false;
		} else if (!channels.equals(other.channels))
			return false;
		if (readerId == null) {
			if (other.readerId != null)
				return false;
		} else if (!readerId.equals(other.readerId))
			return false;
		return true;
	}
	
	/*
	 * Calculates average of reader's channels signal strength.
	 */
	public void updateAmpifiedSignalStrength(){
		
		double signalStrenghtSum = 0.0;
		int numChannels = 0;
		
		logger.debug("Calculating signal strength average for reader: " + readerId);
		
		int minSS = EventConfig.getInstance().getSsaMin();
		int maxSS = EventConfig.getInstance().getSsaMax();
		maxSignalStrength = (double)minSS;

		for (ReaderChannel ch : channels.values()){
			
			double ampss = ch.getSignalStrength();
			boolean valueSet = ch.getSignalStrength() >= minSS && ch.getSignalStrength() <= maxSS;
				
			logger.debug("channes's id " + ch.getChannelId() + " signal strength = " + ampss);			
			
			if (valueSet){
				maxSignalStrength = Math.max(maxSignalStrength, ampss);
				//add up the products of channel strengths
				signalStrenghtSum += ampss;
				numChannels++;
			}
		}

		if (numChannels > 0)
			avgSignalStrength = signalStrenghtSum / numChannels;
		else
			avgSignalStrength = 0.0;
		
		logger.debug("Avg signal strength for reader id " + readerId + " = " + avgSignalStrength);
	}
	
	public int getFormattedSignalStrength() {
		int ss = 0;
		int count = 0;
		for (ReaderChannel ch : channels.values()) {
			if (ch.getFormattedSignalStrength() != 0) {
				ss += ch.getFormattedSignalStrength();
				count++;
			}
		}
		if (count > 0)
			ss = ss / count;
		
		return ss;
	}
	
	public int getFormattedMaxSignalStrength() {
		int ss = 0;
		for (ReaderChannel ch : channels.values()) {
			if (ch.getFormattedSignalStrength() != 0) {
				if (ss < ch.getFormattedSignalStrength())
					ss = ch.getFormattedSignalStrength();
			}
		}		
		
		return ss;
	}
	
	public long getScaledThreshold(){
	 
		
		long result = Math.round((((double)(threshold - ReaderConfig.THRESHOLD_MIN)) / (ReaderConfig.THRESHOLD_MAX - ReaderConfig.THRESHOLD_MIN)) * 100);
		
		return result;
	}

	public LocationBean getLocation() {
		return location;
	}

	public void setLocation(LocationBean location) {
		this.location = location;
	}

	public Double getMaxSignalStrength() {
		return maxSignalStrength;
	}
}
