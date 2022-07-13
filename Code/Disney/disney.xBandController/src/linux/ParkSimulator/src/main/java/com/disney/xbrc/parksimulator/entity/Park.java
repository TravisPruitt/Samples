package com.disney.xbrc.parksimulator.entity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Park
{
	// singleton
	public static Park INSTANCE = new Park();
	
	private static final int nBaseTime = 10;
	private static final int nDataPoints = 15;
	private Map<String,Attraction> mapNameAttraction = new Hashtable<String,Attraction>();
	
	private Park()
	{
	}
	
	public List<String> getAttractions()
	{
		List<String> liAttractions = new ArrayList<String>();
		liAttractions.addAll(mapNameAttraction.keySet());
		return liAttractions;
	}
	
	public Attraction getAttraction(String sAttraction)
	{
		return getAttraction(sAttraction, false);
	}
	
	private Attraction getAttraction(String sAttraction, boolean bCreate)
	{
		Attraction attr = null;
		if (mapNameAttraction.containsKey(sAttraction))
			attr = mapNameAttraction.get(sAttraction);
		else
		{
			if (bCreate)
			{
				attr = new Attraction(sAttraction, nBaseTime, nDataPoints);
				mapNameAttraction.put(sAttraction, attr);
			}
		}
		return attr;
	}


    public void readParkdata(String sFilename) throws Exception {
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(sFilename);
            br = new BufferedReader(fr);

            while (true) {
                String sLine = br.readLine();
                if (sLine == null) {
                    break;
                }

                // skip blank lines
                if (sLine.trim().length() == 0) {
                    continue;
                }

                // if not blank, then it's a section name
                String[] asParts = getParts(sLine, 2);

                String sSection = asParts[0].replaceAll("\"", "");
                int cLines = Integer.parseInt(asParts[1]);
                if (cLines < 2) {
                    throw new Exception("Invalid section line count in park info data file:\n" + sLine);
                }

                // skip the first line
                br.readLine();

                // read and process the other ones
                for (int i = 1; i < cLines; i++) {
                    sLine = br.readLine();
                    if (sLine == null) {
                        throw new Exception("Premature end to park info data file");
                    }

                    asParts = getParts(sLine, nDataPoints + 1);

                    // parse out the attraction name
                    String sAttraction = asParts[0].replaceAll("\"", "");

                    // create the attraction if we don't already have it
                    Attraction attr = getAttraction(sAttraction, true);

                    // handle the appropriate section
                    if (sSection.compareToIgnoreCase("StandbyGC") == 0) {
                        attr.processStandbyGC(asParts);
                    }
                    else if (sSection.compareToIgnoreCase("xPassGC") == 0) {
                        attr.processxPassGC(asParts);
                    }
                    else if (sSection.compareToIgnoreCase("StandbyWait") == 0) {
                        attr.processStandbyWait(asParts);
                    }
                    else if (sSection.compareToIgnoreCase("xPassWait") == 0) {
                        attr.processxPassWait(asParts);
                    }
                    else {
                        throw new Exception("Unknown section in park info data file: " + sSection);
                    }
                }
            }
        }
        finally {
            if (fr != null) {
                try {
                    fr.close();
                }
                catch (Exception ignore) {
                }
            }

            if (br != null) {
                try {
                    br.close();
                }
                catch (Exception ignore) {
                }
            }
        }
    }

    private String[] getParts(String sLine, int cExpected) throws Exception
	{
		// split the line along the commas
		String[] asParts = sLine.split(",");
		
		// validate the length
		if (asParts.length != cExpected)
			throw new Exception("Line in park information file has incorrect field count:\n" + sLine);
		
		return asParts;
	}


}
