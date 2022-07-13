package com.disney.xband.xbrc.gxploadtest;

import java.util.Calendar;
import java.util.Date;

public class CLOptions
{
	private boolean bDisplayUsage = false;
	
	private String sGxp = null;
	private String sDataFile = "GxpLoadTest.dat";
	private int nRequestsPerSecond = 44;
	private Date dtTime = new Date();
	private String sFacilityId = "80010114";
	private String sLocationId = "5";
	private String sReaderType = "Entrance";
	private String sSide = "Right";
	private String sLogFile = null;
	private int cThreadCount = 4;
	private boolean bNumericBandId = true;
	
	public CLOptions()
	{
	}
	
	public static void usage()
	{
		System.out.println("GxpLoadTest - generates Gxp entitlement requests to test Gxp performance");
		System.out.println("Usage:      GxpLoadTest --help");
		System.out.println("            GxpLoadTest OPTIONS");
		System.out.println("");
		System.out.println("OPTIONS:");
		System.out.println(" --gxp URL          Sets the GXP url to URL (default: none)");
		System.out.println(" --numeric          Set to true if using recent Gxp interface (default: true)");
		System.out.println(" --data FILENAME    Sets the data file to FILENAME (default: GxpLoadTest.dat)");
		System.out.println(" --rate RATE        Sets the request rate to RATE per second (default:44)");
		System.out.println(" --time TIME        Sets the request 'base' time to TIME (default: current)");
		System.out.println(" --facility ID      Sets the default entertainment id to ID (default: 80010114)");
		System.out.println(" --location ID      Sets the default reader location id to ID (default: 5)");
		System.out.println(" --readertype TYPE  Sets the default reader type to TYPE (default: Entrance)");
		System.out.println(" --side SIDE        Sets the default reader 'side' to SIDE (default: Right)");
		System.out.println(" --threads COUNT    Sets the per-attraction thread count to COUNT (default:4)");
		System.out.println(" --log LOGFILE      Sets the log file to LOGFILE (default: none)");
		System.out.println("");
		System.out.println("Note that GxpLoadTest requires a data file to operate. The data file contains");
		System.out.println("comma separated values for BANDID,ENTERTAINMENTID,LOCATIONID,READERTYPE,SIDE.");
		System.out.println("Other than BANDID, all items may be omitted and will be replaced with defaults");
	}
	
	public static CLOptions parse(String[] asArgs)
	{
		CLOptions clo = new CLOptions();
		
		for (int i=0; i<asArgs.length; i++)
		{
			String sOpt = asArgs[i];
			
			if (sOpt.equals("--help") || sOpt.equals("-?"))
				clo.setDisplayUsage(true);
			else if (sOpt.equals("--gxp") && i<(asArgs.length-1))
				clo.setGxp(asArgs[++i]);
			else if (sOpt.equals("--data") && i<(asArgs.length-1))
				clo.setDataFile(asArgs[++i]);
			else if (sOpt.equals("--rate") && i<(asArgs.length-1))
				clo.setRequestsPerSecond(Integer.parseInt(asArgs[++i]));
			else if (sOpt.equals("--time") && i<(asArgs.length-1))
				clo.setTime(parseDate(asArgs[++i]));
			else if (sOpt.equals("--facility") && i<(asArgs.length-1))
				clo.setFacilityId(asArgs[++i]);
			else if (sOpt.equals("--location") && i<(asArgs.length-1))
				clo.setLocationId(asArgs[++i]);
			else if (sOpt.equals("--readertype") && i<(asArgs.length-1))
				clo.setReaderType(asArgs[++i]);
			else if (sOpt.equals("--side") && i<(asArgs.length-1))
				clo.setSide(asArgs[++i]);
			else if (sOpt.equals("--log") && i<(asArgs.length-1))
				clo.setLogFile(asArgs[++i]);
			else if (sOpt.equals("--threads") && i<(asArgs.length-1))
				clo.setThreadCount(Integer.parseInt(asArgs[++i]));
			else if (sOpt.equals("--numeric") && i<(asArgs.length-1))
				clo.setNumericBandId(Boolean.parseBoolean(asArgs[++i]));
			else
			{
				System.err.println("Invalid command line option: " + sOpt);
				return null;
			}
		}
		
		return clo;
	}
	

	private static Date parseDate(String sTime)
	{
		int nHour = Integer.parseInt(sTime);
		
		// The parse returns a 1/1/1970 date so munge
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, nHour);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public boolean hasDisplayUsage()
	{
		return bDisplayUsage;
	}
	
	public void setDisplayUsage(boolean bDisplayUsage)
	{
		this.bDisplayUsage = bDisplayUsage;
	}

	public String getGxp()
	{
		return sGxp;
	}

	public void setGxp(String sGxp)
	{
		this.sGxp = sGxp;
	}

	public String getDataFile()
	{
		return sDataFile;
	}

	public void setDataFile(String sDataFile)
	{
		this.sDataFile = sDataFile;
	}

	public int getRequestsPerSecond()
	{
		return nRequestsPerSecond;
	}

	public void setRequestsPerSecond(int nRequestsPerSecond)
	{
		this.nRequestsPerSecond = nRequestsPerSecond;
	}

	public Date getTime()
	{
		return dtTime;
	}

	public void setTime(Date dtTime)
	{
		this.dtTime = dtTime;
	}

	public String getFacilityId()
	{
		return sFacilityId;
	}

	public void setFacilityId(String sFacilityId)
	{
		this.sFacilityId = sFacilityId;
	}

	public String getLocationId()
	{
		return sLocationId;
	}

	public void setLocationId(String sLocationId)
	{
		this.sLocationId = sLocationId;
	}

	public String getReaderType()
	{
		return sReaderType;
	}

	public void setReaderType(String sReaderType)
	{
		this.sReaderType = sReaderType;
	}

	public String getSide()
	{
		return sSide;
	}

	public void setSide(String sSide)
	{
		this.sSide = sSide;
	}

	public String getInputFile()
	{
		return this.sDataFile;
	}

	public void setInputFile(String sDataFile)
	{
		this.sDataFile = sDataFile;
	}
	
	public String getLogFile()
	{
		return this.sLogFile;
	}
	
	public void setLogFile(String sLogFile)
	{
		this.sLogFile = sLogFile;
	}

	public int getThreadCount()
	{
		return cThreadCount;
	}
	
	public void setThreadCount(int cThreadCount)
	{
		this.cThreadCount = cThreadCount;
	}
	
	public boolean isNumericBandId()
	{
		return bNumericBandId;
	}
	
	public void setNumericBandId(boolean bNumericBandId)
	{
		this.bNumericBandId = bNumericBandId;
	}
}
