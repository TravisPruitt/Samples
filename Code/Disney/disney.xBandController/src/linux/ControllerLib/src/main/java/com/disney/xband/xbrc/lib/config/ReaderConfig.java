package com.disney.xband.xbrc.lib.config;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;

@PersistName("ReaderConfig")
public class ReaderConfig extends Configuration
{
	
	@PersistName("minimumGain")
	@MetaData(name = "minimumGain", description = "Minimum signal strength gain setting", defaultValue = "-25")
	public static double minimumGain;
	@PersistName("maximumGain")
	@MetaData(name = "maximumGain", description = "Maximum signal strength gain setting", defaultValue = "25")
	public static double GAIN_MAX;
	@PersistName("gainSliderIncrement")
	@MetaData(name = "gainSliderIncrement", description = "Signal strength slider increment", defaultValue = "1", updatable = false)
	public static double GAIN_SLIDER_INCREMENT;
	@PersistName("minimumThreshold")
	@MetaData(name = "minimumThreshold", description = "Minimum signal strength threshold", defaultValue = "-90")
	public static int THRESHOLD_MIN;
	@PersistName("maximumThreshold")
	@MetaData(name = "maximumThreshold", description = "Maximum signal strength threshold", defaultValue = "-35")
	public static int THRESHOLD_MAX;
	@PersistName("thresholdSliderIncrement")
	@MetaData(name = "thresholdSliderIncrement", description = "Signal strength threshold slider increment", defaultValue = "1")
	public static int THRESHOLD_SLIDER_INCREMENT;
	@PersistName("minNormalTemperature")
	@MetaData(name = "minNormalTemperature", description = "Minimum normal reader temperature threshold", defaultValue = "19")
	public static double TEMP_NORMAL_MIN;
	@PersistName("mmaxNormalTemperature")
	@MetaData(name = "mmaxNormalTemperature", description = "Maximum normal reader temperature threshold", defaultValue = "22")
	public static double TEMP_NORMAL_MAX;
	@PersistName("temperatureTooHigh")
	@MetaData(name = "temperatureTooHigh", description = "Abnormal reader temperature threshold", defaultValue = "25")
	public static double TEMP_CRITICAL;
	
	private transient long xbrcConfigModSeq;

	public transient static double THRESHOLD_MULTIPLIER;
	private transient static double SIGNAL_STRENGTH_SLIDER_LENGTH = 63;
	
	@SuppressWarnings("unused")
	private transient static Logger logger = Logger.getLogger(ReaderConfig.class);
	
	/**
	 * Returns a unique instance of this class with its class state initialized
	 * to default values.
	 * The first call to getInstance() should be followed by a call to initialize()
	 */
	public static synchronized ReaderConfig getInstance() {
		return ReaderConfigHolder.instance;
	}
	
	@Override
	protected void initHook(Connection conn)
	{
		double range = Math.abs(THRESHOLD_MAX - THRESHOLD_MIN);
		if (range == 0.0)
			THRESHOLD_MULTIPLIER = 0.0;
		else
			THRESHOLD_MULTIPLIER = SIGNAL_STRENGTH_SLIDER_LENGTH / range;
	}

	public long getXbrcConfigModSeq() {
		return xbrcConfigModSeq;
	}

	public void setXbrcConfigModSeq(long xbrcConfigModSeq) {
		this.xbrcConfigModSeq = xbrcConfigModSeq;
	}
	
	public void incrementXbrcConfigModSeq(){
		xbrcConfigModSeq = xbrcConfigModSeq + 1L;
	}
	
	public double getMinimumGain() {
		return minimumGain;
	}

	public double getMaximumGain() {
		return GAIN_MAX;
	}

	public double getGainSliderIncrement() {
		return GAIN_SLIDER_INCREMENT;
	}

	public int getMinimumThreshold() {
		return THRESHOLD_MIN;
	}

	public int getMaximumThreshold() {
		return THRESHOLD_MAX;
	}
	
	public double getThresholdMultiplier(){
		return THRESHOLD_MULTIPLIER;
	}
	
	public int getThresholdSliderIncrement(){
		return THRESHOLD_SLIDER_INCREMENT;
	}
	
	/**
	 * @return the tEMP_NORMAL_MIN
	 */
	public static double getTEMP_NORMAL_MIN() {
		return TEMP_NORMAL_MIN;
	}

	/**
	 * @return the tEMP_NORMAL_MAX
	 */
	public static double getTEMP_NORMAL_MAX() {
		return TEMP_NORMAL_MAX;
	}

	/**
	 * @return the tEMP_TOO_HIGH
	 */
	public static double getTEMP_CRITICAL() {
		return TEMP_CRITICAL;
	}

	/**
	 * @param tEMP_NORMAL_MIN the tEMP_NORMAL_MIN to set
	 */
	public static void setTEMP_NORMAL_MIN(double tEMP_NORMAL_MIN) {
		TEMP_NORMAL_MIN = tEMP_NORMAL_MIN;
	}

	/**
	 * @param tEMP_NORMAL_MAX the tEMP_NORMAL_MAX to set
	 */
	public static void setTEMP_NORMAL_MAX(double tEMP_NORMAL_MAX) {
		TEMP_NORMAL_MAX = tEMP_NORMAL_MAX;
	}

	/**
	 * @param tEMP_TOO_HIGH the tEMP_TOO_HIGH to set
	 */
	public static void setTEMP_CRITICAL(double tEMP_CRITICAL) {
		TEMP_CRITICAL = tEMP_CRITICAL;
	}
	
	private ReaderConfig() {
		xbrcConfigModSeq = new Long(0L);
	}
	
	private static class ReaderConfigHolder {
		public static final ReaderConfig instance = new ReaderConfig();
	}
}
