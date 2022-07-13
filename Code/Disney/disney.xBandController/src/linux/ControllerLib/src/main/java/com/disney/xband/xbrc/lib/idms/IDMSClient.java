package com.disney.xband.xbrc.lib.idms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.idms.lib.model.oneview.Celebration;
import com.disney.xband.idms.lib.model.oneview.GuestDataEntitlement;
import com.disney.xband.idms.lib.model.oneview.GuestDataResult;
import com.disney.xband.xbrc.lib.utils.FileUtils;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

public class IDMSClient
{
	// logger
	private static Logger logger = Logger.getLogger(IDMSResolver.class);

	private String sIDMSUrl;
	private int msecIDMSTimeout;
	private long msecIDMSRetryPeriod;
	
	// flag indicating that IDMS is down and when it died
	private boolean bIDMSDown = false;
	private Date dtDown;
	
	public IDMSClient()
	{
	}
	
	public void setIDMSUrl(String sIDMSUrl)
	{
		this.sIDMSUrl = sIDMSUrl;
	}

	public String getIDMSUrl()
	{
		return this.sIDMSUrl;
	}

	public void setIDMSTimeout(int msecIDMSTimeout)
	{
		this.msecIDMSTimeout = msecIDMSTimeout;
	}
	
	public void setIDMSRetryPeriod(int msecIDMSRetryPeriod)
	{
		this.msecIDMSRetryPeriod = msecIDMSRetryPeriod;
	}

	public Xband getBandForSecureIdFromIDMS(String secureId)
	{
		if (!tryIDMS())
			return null;
		
		URL url;
        InputStream ins = null;
        BufferedReader br = null;
        InputStreamReader isr = null;

		try
		{
			String sUrl;
			sUrl = sIDMSUrl + "/xbands/secureid/" + secureId;
			
			url = new URL(sUrl);
			
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setReadTimeout(msecIDMSTimeout);
			if (conn.getResponseCode() >= 400)
			{
				logger.error("!! Error: " + conn.getResponseCode() + " received from xView(" + 
						sIDMSUrl + "/xbands/secureid/" + FileUtils.hideLeadingChars(secureId,4) + ")");
				return null;
			}

			ins = conn.getInputStream();
            isr = new InputStreamReader(ins);
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			while(br.ready())
			{
				sb.append(br.readLine());
			}
			
			ObjectMapper om = new ObjectMapper();
			Xband xb = (Xband) om.readValue(sb.toString(), Xband.class);
			
			if (xb == null)
			{
				logger.error("!! Error: received unexpected object type from xView (" + sb.toString() + ")");
				return null;
			}
			
			String sBandID = xb.getBandId();
			
			if (xb.getGuests() != null && xb.getGuests().size()>0)
			{
				// display error if more than one guest!
				if (xb.getGuests().size() > 1)
				{
					logger.error("!! Error: bandid (" + sBandID + ") assigned to multiple guests!");
				}
			}
			
			return xb;
		}
		catch (MalformedURLException e)
		{
			logger.error("!! URL Error: " + e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			logger.error("!! IO Error: " + e.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
		catch(Exception ex)
		{
			logger.error("!! Error: " + ex.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;

	}
	
	/*
	public Guest
	getGuestFromSecureIdFromIDMS(String secureId)
	{
		if (!tryIDMS())
			return null;
		
		URL url;
		try
		{
			String sUrl;
			sUrl = xviewURL + "/xbands/secureId/" + secureId;
			
			url = new URL(sUrl);
			
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setReadTimeout(msecXviewTimeout);
			if (conn.getResponseCode() >= 400)
			{
				logger.error("!! Error: " + conn.getResponseCode() + " received from xView(" + sUrl + ")");
				return null;
			}
			InputStream ins = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			StringBuilder sb = new StringBuilder();
			while(br.ready())
			{
				sb.append(br.readLine());
			}
			
			ObjectMapper om = new ObjectMapper();
			Xband xb = (Xband) om.readValue(sb.toString(), Xband.class);
			
			if (xb == null)
			{
				logger.error("!! Error: received unexpected object type from xView (" + sb.toString() + ")");
				return null;
			}
			
			String sBandID = xb.getBandId();
			
			if  (xb.getActive())
				 this.liEnabledBands.add(sBandID);
			
			Guest gFirst = null;
			
			if (xb.getGuests() != null && xb.getGuests().size()>0)
			{
				// display error if more than one guest!
				if (xb.getGuests().size() > 1)
				{
					logger.error("!! Error: bandid (" + sBandID + ") assigned to multiple guests!");
				}
			}
			
			return gFirst;
		}
		catch (MalformedURLException e)
		{
			logger.error("!! URL Error: " + e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			logger.error("!! IO Error: " + e.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
		catch(Exception ex)
		{
			logger.error("!! Error: " + ex.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}

		return null;

	}
	*/

	public Xband getBandForRFIDFromIDMS(String sID)
	{
		if (!tryIDMS())
			return null;
		
		URL url;
        InputStream ins = null;
        BufferedReader br = null;
        InputStreamReader isr = null;

		try
		{
			String sUrl; 
			sUrl = sIDMSUrl + "/xbands/tapid/" + sID;
			url = new URL(sUrl);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(msecIDMSTimeout);
			if (conn.getResponseCode()>=400)
			{
				logger.error("!! Error: " + conn.getResponseCode() + " received from xView (" + sUrl + ")");
				return null;
			}

			ins = conn.getInputStream();
            isr = new InputStreamReader(ins);
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			while (br.ready())
				sb.append(br.readLine());

			ObjectMapper om = new ObjectMapper();
			Xband xb = (Xband) om.readValue(sb.toString(), Xband.class);
			if (xb == null)
			{
				logger.error("!! Error: received unexpected object type from xView (" + sb.toString() + ")");
				return null;
			}
			
			String sBandID = xb.getBandId();
			
			if (xb.getGuests() != null && xb.getGuests().size()>0)
			{
				// display error if more than one guest!
				if (xb.getGuests().size() > 1)
				{
					logger.error("!! Error: bandid (" + sBandID + ") assigned to multiple guests!");
				}
			}
			
			return xb;
		} 
		catch (MalformedURLException e)
		{
			logger.error("!! URL Error: " + e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			logger.error("!! IO Error: " + e.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
		catch(Exception ex)
		{
			logger.error("!! Error: " + ex.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;
	}


	public Xband getBandForLRIDFromIDMS(String sID)
	{
		if (!tryIDMS())
			return null;
		
		URL url;
        InputStream ins = null;
        BufferedReader br = null;
        InputStreamReader isr = null;

		try
		{
			String sUrl; 
			sUrl = sIDMSUrl + "/xbands/lrid/" + sID;
			url = new URL(sUrl);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(msecIDMSTimeout);
			if (conn.getResponseCode()>=400)
			{
				logger.error("!! Error: " + conn.getResponseCode() + " received from xView (" + sUrl + ")");
				return null;
			}

			ins = conn.getInputStream();
            isr = new InputStreamReader(ins);
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			while (br.ready())
				sb.append(br.readLine());

			ObjectMapper om = new ObjectMapper();
			Xband xb = (Xband) om.readValue(sb.toString(), Xband.class);
			if (xb == null)
			{
				logger.error("!! Error: received unexpected object type from xView (" + sb.toString() + ")");
				return null;
			}
			
			String sBandID = xb.getBandId();
			
			if (xb.getGuests() != null && xb.getGuests().size()>0)
			{
				// display error if more than one guest!
				if (xb.getGuests().size() > 1)
				{
					logger.error("!! Error: bandid (" + sBandID + ") assigned to multiple guests!");
				}
			}
			
			return xb;
		} 
		catch (MalformedURLException e)
		{
			logger.error("!! URL Error: " + e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			logger.error("!! IO Error: " + e.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
		catch(Exception ex)
		{
			logger.error("!! Error: " + ex.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;
	}

	public Guest getGuestFromBandIDFromIDMS(String sBandID)
	{
		if (!tryIDMS())
			return null;
		
		URL url;
        InputStream ins = null;
        BufferedReader br = null;
        InputStreamReader isr = null;

		try
		{
			String sUrl = sIDMSUrl + "/xbands/bandid/" + sBandID;
			url = new URL(sUrl);
				
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(msecIDMSTimeout);
			if (conn.getResponseCode()>=400)
			{
				logger.error("!! Error: " + conn.getResponseCode() + " received from xView(" + sUrl + ")");
				return null;
			}

			ins = conn.getInputStream();
            isr = new InputStreamReader(ins);
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();

			while (br.ready())
				sb.append(br.readLine());

			ObjectMapper om = new ObjectMapper();
			Xband xb = (Xband) om.readValue(sb.toString(), Xband.class);
			if (xb == null)
			{
				logger.error("!! Error: received unexpected object type from xView (" + sb.toString() + ")");
				return null;
			}
			
			if (xb.getGuests() != null && xb.getGuests().size()>0)
			{
				// display error if more than one guest!
				if (xb.getGuests().size() > 1)
				{
					logger.error("!! Error: bandid (" + sBandID + ") assigned to multiple guests!");
				}
				
				// just handle one guest
				Guest g = xb.getGuests().get(0);
				g = getGuestFromGuestIDFromIDMS(g.getGuestId());
				
				// return the guest
				return g;
			}
			
			return null;
			
		} 
		catch (MalformedURLException e)
		{
			logger.error("!! URL Error: " + e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			logger.error("!! IO Error: " + e.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
		catch(Exception ex)
		{
			logger.error("!! Error: " + ex.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;
	}

	public Guest getGuestFromGuestIDFromIDMS(String sGuestID)
	{
		if (!tryIDMS())
			return null;
		
		URL url;
        InputStream ins = null;
        BufferedReader br = null;
        InputStreamReader isr = null;

		try
		{
			String sUrl = sIDMSUrl + "/guests/id/" + sGuestID;
			url = new URL(sUrl);
				
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(msecIDMSTimeout);
			if (conn.getResponseCode()>=400)
			{
				logger.error("!! Error: " + conn.getResponseCode() + " received from xView(" + sUrl + ")");
				return null;
			}

			ins = conn.getInputStream();
            isr = new InputStreamReader(ins);
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			while (br.ready())
				sb.append(br.readLine());

			ObjectMapper om = new ObjectMapper();
			Guest g = (Guest) om.readValue(sb.toString(), Guest.class);
			if (g == null)
				logger.error("!! Error: received unexpected object type from xView (" + sb.toString() + ")");
			
			return g;
			
		} 
		catch (MalformedURLException e)
		{
			logger.error("!! URL Error: " + e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			logger.error("!! IO Error: " + e.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
		catch(Exception ex)
		{
			logger.error("!! Error: " + ex.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;
	}
	
	private <T> T getObjectFromIDMS(String sUrl, Class<T> type)
	{
		if (!tryIDMS())
			return null;
		
		URL url;
        InputStream ins = null;
        BufferedReader br = null;
        InputStreamReader isr = null;

		try
		{
			url = new URL(sUrl);
				
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(msecIDMSTimeout);
			if (conn.getResponseCode()>=400)
			{
				logger.error("!! Error: " + conn.getResponseCode() + " received from xView(" + sUrl + ")");
				return null;
			}

			ins = conn.getInputStream();
            isr = new InputStreamReader(ins);
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			while (br.ready())
				sb.append(br.readLine());

			ObjectMapper om = new ObjectMapper();
			T obj = (T) om.readValue(sb.toString(), type);
			if (obj == null)
				logger.error("!! Error: received unexpected object type from xView (" + sb.toString() + ")");
			
			return obj;
			
		} 
		catch (MalformedURLException e)
		{
			logger.error("!! URL Error: " + e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			logger.error("!! IO Error: " + e.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
		catch(Exception ex)
		{
			logger.error("!! Error: " + ex.getLocalizedMessage());
			bIDMSDown = true;
			dtDown = new Date();
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;
	}
	
	
	public List<Celebration> getCelebrationListFromGuestIDFromIDMS(String sGuestID)
	{
		String sUrl = sIDMSUrl + "/guest-data?guest-id-type=guestid&guest-id-value=" + sGuestID;
		GuestDataResult gdr = getObjectFromIDMS(sUrl, GuestDataResult.class);
		if (gdr == null)
			return null;
		if (gdr.getGuestData() == null)
			return null;
		if (gdr.getGuestData().getEntitlements() == null)
			return null;
		
		List<Celebration> cl = new LinkedList<Celebration>();	
		for (GuestDataEntitlement gde : gdr.getGuestData().getEntitlements())
		{
			if (gde.getType().equals("celebration") && gde.getLinks() != null && gde.getLinks().getSelf() != null)
			{
				sUrl = sIDMSUrl + gde.getLinks().getSelf().getHref();
				Celebration c = getObjectFromIDMS(sUrl, Celebration.class);
				if (c != null)
					cl.add(c);
			}
		}
		
		return cl.isEmpty() ? null : cl;
	}
	
	/*
	 * returns true if IDMS is running or if it's time to retry after a failure
	 */
	private boolean tryIDMS()
	{
		if (!bIDMSDown)
			return true;
		
		long msecDiff = new Date().getTime() - dtDown.getTime();
		if (msecDiff > msecIDMSRetryPeriod)
		{
			// reset flag
			bIDMSDown = false;
			return true;
		}
		else
			return false;
	}
}
