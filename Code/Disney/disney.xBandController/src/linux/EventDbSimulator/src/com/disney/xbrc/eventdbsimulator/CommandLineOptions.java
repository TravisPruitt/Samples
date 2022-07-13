package com.disney.xbrc.eventdbsimulator;


public class CommandLineOptions
{
	private boolean bDisplayUsage = false;
	
	private int threads = 1;
	private int duration = 60;

	public static void usage()
	{
		System.out.println("EventDbSimulator - generates xBRC event data to an Oracle database.");
		System.out.println("Usage:      EventDbSimulator --help");
		System.out.println("            EventDbSimulator OPTIONS");
		System.out.println("");
		System.out.println("OPTIONS:");
		System.out.println("   --threads THREADS   Sets the number of threads to use (default: 1)");
		System.out.println("   --duration DURATION Sets the duration (in seconds) (default: 60)");
		System.out.println("");
		System.out.println("Note that EventDbSimulator also reads the file properties.xml to obtain the databse connection information.");
	}
	
	public static CommandLineOptions parse(String[] args)
	{
		CommandLineOptions commandLineOptions = new CommandLineOptions();
		
		for (int i=0; i<args.length; i++)
		{
			String option = args[i];
			
			if (option.equals("--help") || option.equals("-?"))
				commandLineOptions.setDisplayUsage(true);
			else if (option.equals("--threads") && i<(args.length-1))
				commandLineOptions.setThreads(Integer.parseInt(args[++i]));
			else if (option.equals("--duration") && i<(args.length-1))
				commandLineOptions.setDuration(Integer.parseInt(args[++i]));
			else
			{
				System.err.println("Invalid command line option: " + option);
				return null;
			}
		}

		return commandLineOptions;
	}
	
	public boolean hasDisplayUsage()
	{
		return bDisplayUsage;
	}
	
	public void setDisplayUsage(boolean bDisplayUsage)
	{
		this.bDisplayUsage = bDisplayUsage;
	}

	public int getThreads()
	{
		return threads;
	}

	public void setThreads(int threads)
	{
		this.threads = threads;
	}
	
	public int getDuration()
	{
		return this.duration;
	}
	
	public void setDuration(int duration)
	{
		this.duration = duration;
	}
	
}
