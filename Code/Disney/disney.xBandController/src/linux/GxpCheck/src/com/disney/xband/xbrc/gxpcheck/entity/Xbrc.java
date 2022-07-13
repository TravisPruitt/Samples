package com.disney.xband.xbrc.gxpcheck.entity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.disney.xband.xbrc.lib.utils.XmlUtil;

public class Xbrc
{
	private String sURL;
	
	public Xbrc(String sURL)
	{
		this.sURL = sURL;
	}
	
	public List<String> getGuestsAtLocation(String sLocation)
	{
		try
		{
			List<String> li = new ArrayList<String>();
			Venue v = sendGetGuestInfo("gueststatus");
			if (v!=null && v.getGuests()!=null)
			{
				for (Guest g : v.getGuests())
				{
					if (g.getLocation().getName().equals(sLocation))
					li.add(g.getId());
				}
			}
			return li;
				
		}
		catch(Exception e)
		{
			System.err.println("Error geting guest info: " + e);
		}
		return null;
		
	}
	
	public Venue sendGetGuestInfo(String sPath) throws JAXBException
	{
		InputStream ins = null;

		try
		{
			URL url = new URL(sURL + "/" + sPath);

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setConnectTimeout(2000);
			httpCon.setRequestMethod("GET");

			httpCon.setConnectTimeout(500);
			httpCon.setReadTimeout(500);

			httpCon.getResponseCode();
			ins = httpCon.getInputStream();
			return (Venue)XmlUtil.convertToPojo(ins, Venue.class);
		}
		catch (MalformedURLException e)
		{
			System.err.println("Bad URL when talking to reader: "
					+ e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			System.err.println("IO error when talking to reader: "
					+ e.getLocalizedMessage());
		}
		finally {
			if(ins != null) {
				try {
					ins.close();
				}
				catch(Exception ignore) {
				}
			}
		}
		
		return null;
	}
	

}
