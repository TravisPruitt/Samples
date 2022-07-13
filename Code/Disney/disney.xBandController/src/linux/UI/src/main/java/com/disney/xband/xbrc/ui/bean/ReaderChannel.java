package com.disney.xband.xbrc.ui.bean;

import java.util.Date;

import com.disney.xband.common.lib.ConnectionPool;
import com.disney.xband.xbrc.lib.config.EventConfig;
import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;

public class ReaderChannel {
	private String readerId;
	private Integer channelId;
	private Integer signalStrength;
	private Date timestamp;
	private EventConfig eventConfig;
	
	public ReaderChannel(String readerId, Integer channelId, Integer signalStrength, 
							Date timestamp, Double gain){
		this.readerId = readerId;
		this.channelId = channelId;
		this.signalStrength = signalStrength;
		this.timestamp = timestamp;
		
		ConnectionPool cp = UIConnectionPool.getInstance();
		
		eventConfig = EventConfig.getInstance();
		eventConfig.initialize(cp);
	}
	
	public String getReaderId() {
		return readerId;
	}

	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}

	/**
	 * @return the id
	 */
	public Integer getChannelId() {
		return channelId;
	}

	/**
	 * @param id the id to set
	 */
	public void setChannelId(Integer id) {
		this.channelId = id;
	}

	/**
	 * @return the signalStrength
	 */
	public Integer getSignalStrength() {
		return signalStrength;
	}

	/**
	 * @param signalStrength the signalStrength to set
	 */
	public void setSignalStrength(Integer signalStrength) {
		this.signalStrength = signalStrength;
	}

	
	/*
	 * Used by the jquery progressbar which goes from 0% to 100%
	 * @see com.disney.xband.xbrc.lib.config.EventConfig.SSA_MIN for signal strength's 100%
	 */
	public Integer getFormattedSignalStrength(){
		if (signalStrength == 0)
			return 0;
		
		Double result = new Double((signalStrength - eventConfig.getSsaMin())) * eventConfig.getSsaMultiplier();
		
		if (result < 0)
			result *= -1;
		
		if (result > 100)
			result = 100.0;
		
		return result.intValue();
	}

	public Date getTimestamp() {
		return timestamp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 59;
		int result = 1;
		result = prime * result
				+ ((channelId == null) ? 0 : channelId.hashCode());
		result = prime * result
				+ ((readerId == null) ? 0 : readerId.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
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
		ReaderChannel other = (ReaderChannel) obj;
		if (channelId == null) {
			if (other.channelId != null)
				return false;
		} else if (!channelId.equals(other.channelId))
			return false;
		if (readerId == null) {
			if (other.readerId != null)
				return false;
		} else if (!readerId.equals(other.readerId))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}
}
