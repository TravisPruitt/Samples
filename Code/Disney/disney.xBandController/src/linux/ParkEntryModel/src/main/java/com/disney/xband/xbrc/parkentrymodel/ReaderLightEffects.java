package com.disney.xband.xbrc.parkentrymodel;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.LightMsg;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class ReaderLightEffects
{	
	private static Logger logger = Logger.getLogger(ReaderLightEffects.class);
	
	class ReaderLightEffect
	{
		ReaderLightEffect()
		{			
		}
		
		ReaderLightEffect(String sequence, LightMsg.ColorValue light, Long duration)
		{
			this.sequence = sequence;
			this.light = light;
			this.duration = duration;
		}
		
		String sequence = null;
		LightMsg.ColorValue light;
		Long duration;
	}
	
	private ReaderLightEffect castLoginOk;
	private ReaderLightEffect startScan;
	
	public ReaderLightEffects()
	{		
		castLoginOk = new ReaderLightEffect();
		startScan = new ReaderLightEffect();
	}
	
	public void initialize()
	{
		setReaderLightEffect("castLoginOk", castLoginOk, ConfigOptions.INSTANCE.getSettings().getCastLoginOkLight(),
				 ConfigOptions.INSTANCE.getSettings().getCastLoginOkDurationMs(),
				 new ReaderLightEffect("entry_login_ok",null,0l));
		
		setReaderLightEffect("startScan", startScan, ConfigOptions.INSTANCE.getSettings().getStartScanLight(),
				 ConfigOptions.INSTANCE.getSettings().getStartScanDurationMs(),
				 new ReaderLightEffect("entry_start_scan",null,0l));
	}
	
	public void castLoginOk(ReaderInfo reader)
	{
		setReaderEffect(reader, castLoginOk);
	}
	
	public void startScan(ReaderInfo reader)
	{
		setReaderEffect(reader, startScan);
	}
	
	private void setReaderEffect(ReaderInfo reader, ReaderLightEffect rle)
	{
        // If we do, we need to pass in the controller to this class.
		if (rle.sequence != null)
			ReaderApi.setReaderSequence(reader, rle.sequence, rle.duration);
		else
			ReaderApi.setReaderLight(reader, rle.light, rle.duration);
	}
	
	private void setReaderLightEffect(String name, ReaderLightEffect rle, String setting, Long duration, ReaderLightEffect def)
	{
		rle.sequence = null;
		rle.duration = null;
		rle.light = null;
		rle.duration = duration;
		
		try
		{
			rle.sequence = setting;
		}
		catch(Exception e)
		{
		}
		
		try
		{
			rle.light = LightMsg.ColorValue.valueOf(setting);
		}
		catch(Exception e)
		{			
		}
		
		if (rle.light == null && (rle.sequence == null || rle.sequence.isEmpty())) {
			logger.error("Invalid reader light setting " + setting + " for " + name + ". Using default light setting.");
			rle.sequence = def.sequence;
			rle.light = def.light;
		}
	}
}
