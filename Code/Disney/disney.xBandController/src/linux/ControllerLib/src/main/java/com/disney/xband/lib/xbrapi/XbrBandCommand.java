package com.disney.xband.lib.xbrapi;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="xbrbandcommand")
public class XbrBandCommand
{
	/*
	 * XMIT_MODE item names are hard coded in the UI, so if you are changing these
	 * do a search and replace through the *.jsp files as well.
	 */
	public enum XMIT_MODE
	{
		REPLY,			//0
		BROADCAST		//1
	};
	
	public enum XMIT_COMMAND 
	{
		//code,frequencyMin,frequencyMax,frequencyProgrammable,timeoutMin,timeoutMax,timeoutProgrammable,title
		FAST_RX_ONLY ((byte)0x01, 1000, 5000, true, 3600000, 10800000, true, "Fast Receive"),	//0
		SLOW_PING ((byte)0x02, 1000, 1000, false, 1800000, 1800000, false, "Slow Transmit"),	//1
		FAST_PING ((byte)0x03, 100, 5000, true, 1000, 30000, true, "Fast Transmit"),			//2
		SET_TRANSMIT_POWER ((byte)0x04, null, null, false, null, null, false, null),			//3
		SET_MULTIPLE_MODE_PARAMETERS ((byte)0x05, null, null, false, null, null, false, null),	//4
		GET_BAND_HARDWARE_VERSION ((byte)0x06, null, null, false, null, null, false, null),		//5
		GET_BAND_FIRMWARE_VERSION ((byte)0x07, null, null, false, null, null, false, null),		//6
		TRANSMIT_CARRIER ((byte)0x08, null, null, false, null, null, false, null),				//7
		KILL_CARRIER ((byte)0x09, null, null, false, null, null, false, null),					//8
		PERFORM_FCC_TEST ((byte)0x0F, null, null, false, null, null, false, null);				//9
		
		/**
		 * Returns command with corresponding parameters in a form of 
		 * six bytes formatted as hex string:
		 * 
		 * byte 0: command code
		 * bytes 1-2: interval seconds
		 * byte 3: interval milliseconds in 10 millisecond units
		 * bytes 4-5: timeout seconds
		 * 
		 * Interval and timeout outside of legal range will be truncated by the reader to the legal values.
		 * 
		 * @param interval in milliseconds, if not provided the default will be used, or zero if default not specified.
		 * @param timeout in milliseconds, if not provided the default will be used, or Integer.MAX_VALUE if default not specified.
		 * @return
		 */
		public String formatInHex(Integer interval, Integer timeout)
		{
			if (!frequencyProgrammable || interval == null)
				interval = this.frequencyMin;
			if (interval == null)
				interval = new Integer(0);
			
			if (!timeoutProgrammable || timeout == null)
				timeout = this.timeoutMax;
			if (timeout == null)
				timeout = Integer.MAX_VALUE;
			
			StringBuffer commandInHex = new StringBuffer();
			
			// byte 0
			commandInHex.append(String.format("%02X", this.getCode()));
			
			// bytes 1 to 2
			short intervalSeconds = (short) (interval / 1000);
			commandInHex.append(String.format("%04X", intervalSeconds));
			
			// byte 3
			byte intervalMilliseconds = (byte)((interval % 1000) / 10);
			commandInHex.append(String.format("%02X", intervalMilliseconds));
			
			// bytes 4 to 5
			short timeoutSeconds = (short)(timeout / 1000);
			commandInHex.append(String.format("%04X", timeoutSeconds));
			
			return commandInHex.toString();
		}

		public byte getCode()
		{
			return code;
		}

		public void setCode(byte code)
		{
			this.code = code;
		}

		public Integer getFrequencyMin()
		{
			return frequencyMin;
		}

		public Integer getFrequencyMax()
		{
			return frequencyMax;
		}

		public Integer getTimeoutMin()
		{
			return timeoutMin;
		}

		public Integer getTimeoutMax()
		{
			return timeoutMax;
		}

		public String getTitle()
		{
			return title;
		}

		public void setFrequencyMin(Integer frequencyMin)
		{
			this.frequencyMin = frequencyMin;
		}

		public void setFrequencyMax(Integer frequencyMax)
		{
			this.frequencyMax = frequencyMax;
		}

		public void setTimeoutMin(Integer timeoutMin)
		{
			this.timeoutMin = timeoutMin;
		}

		public void setTimeoutMax(Integer timeoutMax)
		{
			this.timeoutMax = timeoutMax;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}
		
		public boolean isFrequencyProgrammable()
		{
			return frequencyProgrammable;
		}

		public void setFrequencyProgrammable(boolean frequencyProgrammable)
		{
			this.frequencyProgrammable = frequencyProgrammable;
		}
		
		public boolean isTimeoutProgrammable()
		{
			return timeoutProgrammable;
		}

		public void setTimeoutProgrammable(boolean timeoutProgrammable)
		{
			this.timeoutProgrammable = timeoutProgrammable;
		}

		private byte code;
		private Integer frequencyMin;			// in milliseconds
		private Integer frequencyMax;			// in milliseconds
		private boolean frequencyProgrammable;
		private Integer timeoutMin;				// in milliseconds
		private Integer timeoutMax;				// in milliseconds
		private boolean timeoutProgrammable;	
		private String title;
		
		private XMIT_COMMAND(byte code, Integer frequencyMin, Integer frequencyMax, boolean frequencyProgrammable,
				Integer timeoutMin, Integer timeoutMax, boolean timeoutProgrammable, String title)
		{
			this.setCode(code);
			this.frequencyMin = frequencyMin;
			this.frequencyMax = frequencyMax;
			this.frequencyProgrammable = frequencyProgrammable;
			this.timeoutMin = timeoutMin;
			this.timeoutMax = timeoutMax;
			this.timeoutProgrammable = timeoutProgrammable;
			this.title = title;
		}
	}

	private Long id;
	private XMIT_MODE mode;
	private Integer threshold;
	private XMIT_COMMAND command;
	private Integer interval;			// in milliseconds, may be null
	private Integer timeout;			// in milliseconds, may be null
	private Boolean enableThreshold; 
	private Long locationId;
	
	/**
	 * When specified, this command will be applied to the 
	 * list of bands visible to the locations identified as recipients.
	 */
	private Set<Long> recipients;	
	/**
	 * Id of the transmitting reader responsible for executing this command.
	 */
	private Long reader;
	
	private List<String> lisLRIDs;
	
	public boolean isValid()
	{
		boolean valid = true;
		
		if (mode == null || command == null)
			valid = false;
		else if (command == XMIT_COMMAND.FAST_RX_ONLY && mode == XMIT_MODE.REPLY)
			valid = false;
		
		return valid;
	}

	@XmlTransient
	public List<String> getListLRIDs()
	{
		return lisLRIDs;
	}
	
	public void setListLRIDs(List<String> lisLRIDs)
	{
		this.lisLRIDs = lisLRIDs;
	}
	
	@XmlElement
	public XMIT_MODE getMode()
	{
		return mode;
	}
	
	public void setMode(XMIT_MODE mode)
	{
		this.mode = mode;
	}

	public Boolean getEnableThreshold() {
		return enableThreshold;
	}

	public void setEnableThreshold(Boolean enableThreshold) {
		this.enableThreshold = enableThreshold;
	}

	@XmlElement
	public Integer getThreshold()
	{
		return threshold;
	}
	
	public void setThreshold(Integer threshold)
	{
		this.threshold = threshold;
	}
	
	@XmlElement
	public XMIT_COMMAND getCommand()
	{
		return command;
	}
	@XmlElement
	public Integer getInterval()
	{
		return interval;
	}
	@XmlElement
	public Integer getTimeout()
	{
		return timeout;
	}
	@XmlElementWrapper(name="recipients")
	@XmlElement(name="recipientLocationId")
	public Set<Long> getRecipients()
	{
		return recipients;
	}

	@XmlTransient
	public Integer getTimeoutSec()
	{
		return timeout / 1000; 
	}
	
	public void setCommand(XMIT_COMMAND command)
	{
		this.command = command;
	}

	public void setInterval(Integer interval)
	{
		this.interval = interval;
	}

	public void setTimeout(Integer timeout)
	{
		this.timeout = timeout;
	}

	public void setRecipients(Set<Long> recipients)
	{
		this.recipients = recipients;
	}
	@XmlElement
	public Long getReader()
	{
		return reader;
	}

	public void setReader(Long reader)
	{
		this.reader = reader;
	}
	@XmlTransient
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
	
	@XmlElement
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result
				+ ((interval == null) ? 0 : interval.hashCode());
		result = prime * result
				+ ((lisLRIDs == null) ? 0 : lisLRIDs.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((reader == null) ? 0 : reader.hashCode());
		result = prime * result
				+ ((recipients == null) ? 0 : recipients.hashCode());
		result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
		result = prime * result + (threshold == null ? 0 : threshold.hashCode());
		result = prime * result + (enableThreshold == null ? 0 : enableThreshold.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof XbrBandCommand))
			return false;
		XbrBandCommand other = (XbrBandCommand) obj;
		if (command != other.command)
			return false;
		
		if (locationId == null)
		{
			if (other.locationId != null)
				return false;
		}
		else if (!locationId.equals(other.locationId))
			return false;
		
		if (interval == null)
		{
			if (other.interval != null)
				return false;
		}
		else if (!interval.equals(other.interval))
			return false;
		if (lisLRIDs == null)
		{
			if (other.lisLRIDs != null)
				return false;
		}
		else if (!lisLRIDs.equals(other.lisLRIDs))
			return false;
		if (mode != other.mode)
			return false;
		if (reader == null)
		{
			if (other.reader != null)
				return false;
		}
		else if (!reader.equals(other.reader))
			return false;
		if (recipients == null)
		{
			if (other.recipients != null)
				return false;
		}
		else if (!recipients.equals(other.recipients))
			return false;
		if (threshold == null)
		{
			if (other.threshold != null)
				return false;
		}
		else if (!threshold.equals(other.threshold))
			return false;
		if (timeout == null)
		{
			if (other.timeout != null)
				return false;
		}
		else if (!timeout.equals(other.timeout))
			return false;		
		if (enableThreshold == null)
		{
			if (other.enableThreshold != null)
				return false;
		}
		else if (!enableThreshold.equals(other.enableThreshold))
			return false;
		
		return true;
	}

	@Override
	public String toString()
	{
		return "XbrBandCommand ["
				+ "id=" + (id != null? id : "null")
				+ ", locationId = " + locationId
				+ ", mode=" + (mode != null? mode : "null") 
				+ ", threshold=" + threshold
				+ ", enableThreshold=" + enableThreshold
				+ ", command=" + (command != null? command : "null") 
				+ ", interval=" + interval
				+ ", timeout=" + timeout 
				+ ", recipients=" + (recipients != null ? recipients : "null")
				+ ", reader=" + reader 
				+ ", lisLRIDs=" + (lisLRIDs != null? lisLRIDs : "null") + "]";
	}
}
