package com.disney.xbrc.parksimulator.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SimulationAction
{
	public enum Action
	{
		enter,
		merge,
		load,
		exit
	}
	
	private Attraction attr;
	private Action action;
	private long lWhen;
	private long lGuestId;
	private boolean bxPass;
	
	public SimulationAction(Attraction attr, long lWhen, Action action, long lGuestId, boolean bxPass)
	{
		this.attr = attr;
		this.lWhen = lWhen;
		this.action = action;
		this.lGuestId = lGuestId;
		this.bxPass = bxPass;
	}

	public Attraction getAttr()
	{
		return attr;
	}

	public Action getAction()
	{
		return action;
	}

	public long getWhen()
	{
		return lWhen;
	}

	public long getGuestId()
	{
		return lGuestId;
	}
	
	public boolean isXPass()
	{
		return bxPass;
	}
	
	public boolean isDue(long lSimulation)
	{
		return lSimulation > lWhen;
	}
	
	public Message createMessage(Calendar cal)
	{
		StringBuilder sb = new StringBuilder();
		
		// set the msec seconds as msec as per this action
		cal.set(Calendar.MILLISECOND, (int)(lWhen % 1000));
		
		// map action type to messaage type
		String sType = null;
		switch(getAction())
		{
			case enter:
			{
				sType = "ENTRY";
				break;
			}
			
			case merge:
			{
				sType = "MERGE";
				break;
			}
			
			case load:
			{
				sType = "LOAD";
				break;
			}
			
			case exit:
			{
				sType = "EXIT";
				break;
			}

            default:
            {
                sType = "UNKNOWN";
                break;
            }
		}
		
		sb.append("<venue name=\""
				+ getAttr().getName() 
				+ "\" " + "time=\"" + formatTime(cal.getTime())
				+ "\">\n");
		sb.append("    <message type=\"" + sType + "\" time=\""
				+ formatTime(cal.getTime()) + "\">\n");

		// type specific stuff
		if (sType.equals("ENTRY"))
		{
			sb.append("        <guestid>" + getGuestId() + "</guestid>\n");
			sb.append("        <xpass>" + (isXPass() ? "true" : "false") + "</xpass>\n");
			sb.append("        <locationid>SomeSection</locationid>\n");
			sb.append("        <readerlocation>Entry</readerlocation>\n");
			sb.append("        <bandtype>Guest</bandtype>\n");
		}
		else if (sType.equals("MERGE"))
		{
			sb.append("        <guestid>" + getGuestId() + "</guestid>\n");
			sb.append("        <xpass>" + (isXPass() ? "true" : "false") + "</xpass>\n");
			sb.append("        <locationid>SomeSection</locationid>\n");
			if (isXPass())
				sb.append("        <readerlocation>Merge</readerlocation>\n");
			else
				sb.append("        <readerlocation>MergeLRR</readerlocation>\n");
			sb.append("        <bandtype>Guest</bandtype>\n");
		}
		else if (sType.compareTo("LOAD") == 0)
		{
			sb.append("        <guestid>" + getGuestId() + "</guestid>\n");
			sb.append("        <xpass>" + (isXPass() ? "true" : "false") + "</xpass>\n");
			sb.append("        <locationid>SomeSection</locationid>\n");
			sb.append("        <readerlocation>Load</readerlocation>\n");
			sb.append("        <carid>SomeCarId</carid>\n");
			sb.append("        <bandtype>Guest</bandtype>\n");
		}
		else if (sType.compareTo("EXIT") == 0)
		{
			sb.append("        <guestid>" + getGuestId() + "</guestid>\n");
			sb.append("        <xpass>" + (isXPass() ? "true" : "false") + "</xpass>\n");
			sb.append("        <locationid>SomeSection</locationid>\n");
			sb.append("        <readerlocation>Exit</readerlocation>\n");
			sb.append("        <carid>SomeCarId</carid>\n");
			sb.append("        <statistics>\n");
			sb.append("            <waittime>0</waittime>\n");
			sb.append("            <mergetime>0</mergetime>\n");
			sb.append("            <totaltime>0</totaltime>\n");
			sb.append("        </statistics>\n");
			sb.append("        <bandtype>Guest</bandtype>\n");
		}

		sb.append("    </message>\n");
		sb.append("</venue>\n");

		Message msg = Message.create(attr.getName(), sType, sb.toString());
		return msg;
	}
	
	private static String formatTime(Date ts)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		return sdf.format(ts);
	}
	
	
}
