package com.disney.xband.xbrc.parkentrymodel;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.LightMsg;
import com.disney.xband.lib.xbrapi.ReaderExecutor;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;

public class ReaderColorManager
{
	private static Logger logger = Logger.getLogger(ReaderColorManager.class);
			
	public class Color {
		private Date expire;
		private String colorForReader;
		private String colorForGreeter;
		
		public Color(String colorForReader, String colorForGreeter, Date expire)
		{
			this.expire = expire;
			this.colorForReader = colorForReader;
			this.colorForGreeter = colorForGreeter;
		}
		
		public Date getExpire()
		{
			return expire;
		}
		public void setExpire(Date expire)
		{
			this.expire = expire;
		}

		public String getColorForReader()
		{
			return colorForReader;
		}

		public void setColorForReader(String colorForReader)
		{
			this.colorForReader = colorForReader;
		}

		public String getColorForGreeter()
		{
			return colorForGreeter;
		}

		public void setColorForGreeter(String colorForGreeter)
		{
			this.colorForGreeter = colorForGreeter;
		}
	}
	
	private LinkedList<Color> available;
	private LinkedList<Color> used;
	
	public ReaderColorManager(Collection<String> colors)
	{
		available = new LinkedList<Color>();
		used = new LinkedList<Color>();
		
		for (String strColor : colors)
		{
			Color color = createColor(strColor);
			if (color == null)
				logger.error("Ignoring flash color " + strColor + 
						" because the color format is not correct. Required format: #RRGGBB");
			else
				available.add(color);
		}
	}

	public Color createColor(String color)
	{
		// We expect colors of the form #RRGGBB
		if (color == null || color.length() != 7 || !color.startsWith("#"))
			return null;
		
		String red = color.substring(1, 3);
		String green = color.substring(3,5);
		String blue = color.substring(5,7);
		
		int nRed = Integer.parseInt(red, 16);
		int nGreen = Integer.parseInt(green, 16);
		int nBlue = Integer.parseInt(blue, 16);
		
		String forReader = LightMsg.formatColor(nRed, nGreen, nBlue);
		String forGreeter = "#" + red + green + blue;
		
		return new Color(forReader, forGreeter, null);
	}
	
	public String flashNextAvailable(String locationName)
	{
		synchronized(available)
		{
			if (available.isEmpty())
				return null;
			
			Color color = available.remove();
			color.setExpire(new Date(System.currentTimeMillis() + 
					ConfigOptions.INSTANCE.getSettings().getReaderFlashTimeMs()));
			used.add(color);
			
			for (ReaderInfo reader : XBRCController.getInstance().getReaders(locationName))
			{	
				if (!ReaderType.isTapReader(reader.getType()))
					continue;
				
				ReaderExecutor.getInstance().setReaderColor(reader, color.getColorForReader(), 
						ConfigOptions.INSTANCE.getSettings().getReaderFlashTimeMs());
			}
			
			return color.getColorForGreeter();
		}
	}
	
	public void timeoutFlash()
	{
		if (used.isEmpty())
			return;
		
		synchronized(available)
		{
			Date now = new Date();
			for (Iterator<Color> it = used.iterator(); it.hasNext();)
			{
				Color color = it.next();
				if (now.getTime() >= color.getExpire().getTime())
				{
					it.remove();
					available.add(color);
				}
			}
		}
	}
}
