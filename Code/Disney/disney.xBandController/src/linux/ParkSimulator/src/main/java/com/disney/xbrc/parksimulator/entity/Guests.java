package com.disney.xbrc.parksimulator.entity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

public class Guests 
{
	// singleton
	public static Guests INSTANCE = new Guests();

	private LinkedList<Long> liAvailableGuest = new LinkedList<Long>();
	
	private long lGuestId;
	
	private Guests()
	{
		lGuestId = 0;
	}

	public void readGuests(String sFilename) throws Exception
	{
        FileReader fr = null;
        BufferedReader br = null;

        try {
		    fr = new FileReader(sFilename);
		    br = new BufferedReader(fr);

		    while (true)
		    {
			    String sLine = br.readLine();
			    if (sLine==null)
				    break;
			
			    // skip blank lines
			    if (sLine.trim().length()==0)
				    continue;
			
			    liAvailableGuest.add(Long.parseLong(sLine));
		    }
        }
        finally {
            if(fr != null) {
                try {
                    fr.close();
                }
                catch (Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
	
	public long getAvailableGuest() {
		// if we have one that's available, get that one
		if (liAvailableGuest.size() > 0)
			return liAvailableGuest.removeFirst();
		else
			return lGuestId++;
	}
	
	public void setAvailableGuest(long lGuestId) 
	{
		liAvailableGuest.add(lGuestId);
	}

}
