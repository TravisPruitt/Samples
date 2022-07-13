package com.disney.xbrc.parksimulator;

import java.io.ObjectInputStream.GetField;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.disney.xbrc.parksimulator.entity.Attraction;
import com.disney.xbrc.parksimulator.entity.Park;

public class CLOptions
{
	private boolean bDisplayUsage = false;
	
	private String sJMSBroker = null;
	private String sJMSTopic = "com.synapse.parksimulator";
	private String sJMSUser = "Administrator";
	private String sJMSPassword = "Administrator";
	
	private boolean bFast = false;
	private boolean bUseIdms = false;
	private boolean bPrefill = false;
	private Calendar calBase; 
	private int nStartHour;
	private int nStartMin;
	private int nEndHour = 23;
	private int nEndMin = 59;
	
	private String sLogFile = null;
	
	private List<String> liAttractions = null;
	
	public CLOptions()
	{
		// calculate the current NOW hour time in eastern time
		Calendar calMCO = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));

		nStartHour = calMCO.get(Calendar.HOUR_OF_DAY);
		nStartMin = calMCO.get(Calendar.MINUTE);

		// set the base time
		calBase = Calendar.getInstance();
		calBase.set(Calendar.HOUR_OF_DAY, 0);
		calBase.set(Calendar.MINUTE, 0);
		calBase.set(Calendar.SECOND, 0);
		calBase.set(Calendar.MILLISECOND, 0);
		
	}

	public static void usage()
	{
		System.out.println("ParkSimulator - generates xBRC JMS messages based on historical park metrics");
		System.out.println("Usage:      ParkSimulator --help");
		System.out.println("            ParkSimulator OPTIONS");
		System.out.println("");
		System.out.println("OPTIONS:");
		System.out.println("   --broker BROKER      Sets the JMS broker:port to BROKER (default: don't send)");
		System.out.println("   --topic TOPIC        Sets the JMS TOPIC (default: com.synapse.parksimulator)");
		System.out.println("   --user USER          Sets the JMS USER (default: Administrator)");
		System.out.println("   --password PASSWORD  Sets the JMS PASSWORD (default: Administrator)");
		System.out.println("   --from HOUR[:MIN]    Starts simulation at the given park time (default NOW)");
		System.out.println("   --to HOUR[:MIN]      Ends the simulation at the given park time (default 23:59)");
		System.out.println("   --base DATETIME      Sets the base for from/to times. (default: today, 00:00 UTC");
		System.out.println("   --prefill            Send data (in fast mode) for the hours previous to HOUR");
		System.out.println("   --fast               Runs simulation as fast as possible (not real time)");
		System.out.println("   --attractions ATTRS  Only simulate attractions in ATTRS list (comma separated)");
		System.out.println("   --log FILE           Stores all JMS messages in FILE (default: no log)");
		System.out.println("   --idms               Uses IDMS test guest IDs, if available, from guests.dat file.");
		System.out.println("");
		System.out.println("Note that ParkSimulator also reads the file parkdata.dat to obtain its metrics");
		System.out.println("Note, too, that --prefill makes most sense w/o --fast.");
	}
	
	public static CLOptions parse(String[] asArgs)
	{
		CLOptions clo = new CLOptions();
		
		for (int i=0; i<asArgs.length; i++)
		{
			String sOpt = asArgs[i];
			
			if (sOpt.equals("--help") || sOpt.equals("-?"))
				clo.setDisplayUsage(true);
			else if (sOpt.equals("--broker") && i<(asArgs.length-1))
				clo.setJMSBroker(asArgs[++i]);
			else if (sOpt.equals("--topic") && i<(asArgs.length-1))
				clo.setJMSTopic(asArgs[++i]);
			else if (sOpt.equals("--user") && i<(asArgs.length-1))
				clo.setJMSUser(asArgs[++i]);
			else if (sOpt.equals("--password") && i<(asArgs.length-1))
				clo.setJMSPassword(asArgs[++i]);
			else if (sOpt.equals("--fast"))
				clo.setFast();
			else if (sOpt.equals("--prefill"))
				clo.setPrefill(true);
			else if (sOpt.equals("--idms"))
				clo.setUseIdms();
			else if (sOpt.equals("--from") && i<(asArgs.length-1))
				parseStartTime(clo, asArgs[++i]);
			else if (sOpt.equals("--to") && i<(asArgs.length-1))
				parseEndTime(clo, asArgs[++i]);
			else if (sOpt.equals("--base") && i<(asArgs.length-1))
				clo.setCalBase(parseCalBase(asArgs[++i]));
			else if (sOpt.equals("--log") && i<(asArgs.length-1))
				clo.setLogFile(asArgs[++i]);
			else if (sOpt.equals("--attractions") && i<(asArgs.length-1))
				clo.processAttractions(asArgs[++i]);
			else
			{
				System.err.println("Invalid command line option: " + sOpt);
				return null;
			}
		}
		
		return clo;
	}
	
	private static void parseStartTime(CLOptions clo, String sTime)
	{
		// break out hour:minute
		String[] asParts = sTime.split(":");
		if(asParts.length==1)
		{
			clo.setStartHour(Integer.parseInt(asParts[0]));
			clo.setStartMin(0);
		}
		else if (asParts.length==2)
		{
			clo.setStartHour(Integer.parseInt(asParts[0]));
			clo.setStartMin(Integer.parseInt(asParts[1]));
		}
		else
		{
			System.err.println("Invalid start time: " + sTime);
		}
	}

	private static void parseEndTime(CLOptions clo, String sTime)
	{
		// break out hour:minute
		String[] asParts = sTime.split(":");
		if(asParts.length==1)
		{
			clo.setEndHour(Integer.parseInt(asParts[0]));
			clo.setEndMin(0);
		}
		else if (asParts.length==2)
		{
			clo.setEndHour(Integer.parseInt(asParts[0]));
			clo.setEndMin(Integer.parseInt(asParts[1]));
		}
		else
		{
			System.err.println("Invalid end time: " + sTime);
		}
	}

	private void processAttractions(String string)
	{
		// create the attraction list
		liAttractions = new ArrayList<String>();
		
		// split the entries by comma
		String[] asAttrs = string.split("\\,");
		for( String sAttr : asAttrs)
		{
			// cleanup
			sAttr = sAttr.trim();
			
			// lookup the attraction
			Attraction attr = Park.INSTANCE.getAttraction(sAttr);
			if (attr==null)
				System.err.println("Unknown attraction: " + sAttr);
			else
				liAttractions.add(sAttr);
		}
	}

	private static int parseInt(String sInt)
	{
		int nHour = Integer.parseInt(sInt);
		return nHour;
	}
	
	private static Calendar parseCalBase(String sCal)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Calendar cal = Calendar.getInstance();
		try
		{
			Date dt = sdf.parse(sCal);
			cal.setTime(dt);
		}
		catch (ParseException e)
		{
			System.err.println("Invalid date: " + sCal + ": " + e.getLocalizedMessage());
		}
		return cal;
	}
	
	public boolean hasDisplayUsage()
	{
		return bDisplayUsage;
	}
	
	public void setDisplayUsage(boolean bDisplayUsage)
	{
		this.bDisplayUsage = bDisplayUsage;
	}

	public String getJMSBroker()
	{
		return sJMSBroker;
	}

	public void setJMSBroker(String sJMSBroker)
	{
		this.sJMSBroker = sJMSBroker;
	}
	
	public String getJMSTopic()
	{
		return sJMSTopic;
	}
	
	public void setJMSTopic(String sJMSTopic)
	{
		this.sJMSTopic = sJMSTopic;
	}

	public String getJMSUser()
	{
		return sJMSUser;
	}

	public void setJMSUser(String sJMSUser)
	{
		this.sJMSUser = sJMSUser;
	}

	public String getJMSPassword()
	{
		return sJMSPassword;
	}

	public void setJMSPassword(String sJMSPassword)
	{
		this.sJMSPassword = sJMSPassword;
	}

	public boolean isFast()
	{
		return bFast;
	}
	
	public void setFast()
	{
		bFast = true;
	}

	public boolean useIdms()
	{
		return bUseIdms;
	}

	public void setUseIdms()
	{
		bUseIdms = true;
	}

	public int getStartHour()
	{
		return nStartHour;
	}

	public void setStartHour(int nStart)
	{
		this.nStartHour = nStart;
	}

	public int getEndHour()
	{
		return nEndHour;
	}

	public void setEndHour(int nEnd)
	{
		this.nEndHour = nEnd;
	}

	public int getStartMin()
	{
		return nStartMin;
	}

	public void setStartMin(int nStartMin)
	{
		this.nStartMin = nStartMin;
	}

	public int getEndMin()
	{
		return nEndMin;
	}

	public void setEndMin(int nEndMin)
	{
		this.nEndMin = nEndMin;
	}

	public Calendar getCalBase()
	{
		return calBase;
	}

	public void setCalBase(Calendar calBase)
	{
		this.calBase = calBase;
	}

	public String getLogFile()
	{
		return sLogFile;
	}

	public void setLogFile(String sLogFile)
	{
		this.sLogFile = sLogFile;
	}
	
	public List<String> getAttractions()
	{
		return liAttractions;
	}

	public boolean isPrefill()
	{
		return bPrefill;
	}

	public void setPrefill(boolean bPrefill)
	{
		this.bPrefill = bPrefill;
	}

}
