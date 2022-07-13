package com.disney.xband.xbrc.gxpcheck;

public class CLOptions
{
	private boolean bDisplayUsage = false;
	
	private String sGxp = "http://192.168.0.62:8081/dap-controller/services/redemption";
	private String sXbrc = "http://192.168.0.50:8080";
	private String sIDMS = "http://192.168.0.65:8080/IDMS";
	private String sLocation = "Table1";
	private String sFacilityId = "80010114";
	private String sLocationId = "5";
	private String sLogFile = null;
	private int cThreadCount = 4;
	
	public CLOptions()
	{
	}
	
	public static void usage()
	{
		System.out.println("GxpCheck - checks Gxp entitlements for guests under a location");
		System.out.println("Usage:      GxpCheck --help");
		System.out.println("            GxpCheck OPTIONS");
		System.out.println("");
		System.out.println("OPTIONS:");
		System.out.println(" --gxp URL          Sets the GXP url to URL");
		System.out.println("     (default: http://192.168.0.62:8081/dap-controller/services/redemption)");
		System.out.println(" --xbrc URL         Sets the xBRC url to URL");
		System.out.println("     (default: http://192.168.0.50:8080)");
		System.out.println(" --idms URL         Sets the IDMS url to URL");
		System.out.println("     (default: http://192.168.0.65:8080/IDMS)");
		System.out.println(" --location LOC     Sets the xBRC location to be used to LOC (default: Table1)");
		System.out.println(" --facility ID      Sets the default entertainment id to ID (default: 80010114)");
		System.out.println(" --lid ID           Sets the default reader location id to ID (default: 5)");
		System.out.println(" --log LOGFILE      Sets the log file to LOGFILE (default: none)");
		System.out.println("");
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
			else if (sOpt.equals("--url") && i<(asArgs.length-1))
				clo.setXbrc(asArgs[++i]);
			else if (sOpt.equals("--url") && i<(asArgs.length-1))
				clo.setXbrc(asArgs[++i]);
			else if (sOpt.equals("--facility") && i<(asArgs.length-1))
				clo.setFacilityId(asArgs[++i]);
			else if (sOpt.equals("--location") && i<(asArgs.length-1))
				clo.setLocation(asArgs[++i]);
			else if (sOpt.equals("--lid") && i<(asArgs.length-1))
				clo.setLocationId(asArgs[++i]);
			else if (sOpt.equals("--log") && i<(asArgs.length-1))
				clo.setLogFile(asArgs[++i]);
			else if (sOpt.equals("--threads") && i<(asArgs.length-1))
				clo.setThreadCount(Integer.parseInt(asArgs[++i]));
			else
			{
				System.err.println("Invalid command line option: " + sOpt);
				return null;
			}
		}
		
		return clo;
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

	public String getXbrc()
	{
		return sXbrc;
	}

	public void setXbrc(String sXbrc)
	{
		this.sXbrc = sXbrc;
	}

	public String getLocation()
	{
		return sLocation;
	}

	public void setLocation(String sLocation)
	{
		this.sLocation = sLocation;
	}

	public String getIDMS()
	{
		return sIDMS;
	}

	public void setIDMS(String sIDMS)
	{
		this.sIDMS = sIDMS;
	}
	
	
}
