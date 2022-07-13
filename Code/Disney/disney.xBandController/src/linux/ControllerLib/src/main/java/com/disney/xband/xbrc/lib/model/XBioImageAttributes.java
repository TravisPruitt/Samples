package com.disney.xband.xbrc.lib.model;

public class XBioImageAttributes {
	
	private int imagerSaturation;
	private int meanPixelValue;
	private int gain;
	private int exposure;
	
	public XBioImageAttributes()
	{
		
	}
	
	public XBioImageAttributes(int imagerSaturation, int meanPixelValue, int gain, int exposure)
	{
		this.setImagerSaturation(imagerSaturation);
		this.setMeanPixelValue(meanPixelValue);
		this.setGain(gain);
		this.setExposure(exposure);
	}

	/**
	 * @return the imagerSaturation
	 */
	@SuppressWarnings("unused")
	private int getImagerSaturation() {
		return imagerSaturation;
	}

	/**
	 * @param imagerSaturation the imagerSaturation to set
	 */
	private void setImagerSaturation(int imagerSaturation) {
		this.imagerSaturation = imagerSaturation;
	}

	/**
	 * @return the meanPixelValue
	 */
	@SuppressWarnings("unused")
	private int getMeanPixelValue() {
		return meanPixelValue;
	}

	/**
	 * @param meanPixelValue the meanPixelValue to set
	 */
	private void setMeanPixelValue(int meanPixelValue) {
		this.meanPixelValue = meanPixelValue;
	}

	/**
	 * @return the gain
	 */
	@SuppressWarnings("unused")
	private int getGain() {
		return gain;
	}

	/**
	 * @param gain the gain to set
	 */
	private void setGain(int gain) {
		this.gain = gain;
	}

	/**
	 * @return the exposure
	 */
	@SuppressWarnings("unused")
	private int getExposure() {
		return exposure;
	}

	/**
	 * @param exposure the exposure to set
	 */
	private void setExposure(int exposure) {
		this.exposure = exposure;
	}
	
	
	

}
