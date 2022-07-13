package com.disney.xband.lib.xbrapi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.JsonUtil;
import com.disney.xband.common.lib.ResponseFormatter;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class XbrJsonMapper
{
	static Logger logger = Logger.getLogger(XbrJsonMapper.class);

	static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	// initialize the date formatter here since this is very slow
	static
	{
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public static XbrEvents mapEvents(String str) throws Exception
	{

		JSONObject jo = JSONObject.fromObject(str);

		XbrEvents res = new XbrEvents();
		res.setReaderName(jo.getString("reader name"));
		Object oja = jo.get("events");
		if (oja == null)
			return res;
		JSONArray ja = (JSONArray)oja;

		// logger.trace("Received " + ja.size() + " events");

		for (int ie = 0; ie < ja.size(); ie++)
		{
			JSONObject jee = (JSONObject) ja.get(ie);

			XbrEventType type = XbrEventType.fromType(getString(jee, "type"));
			
			// We did not get the type so try to guess the event type.
			// TODO: This should be a temporary solution.
			if (type == XbrEventType.Unknown)
			{
				// what kind of ID do we have
				if (getString(jee, "XLRID") != null)
					type = XbrEventType.LRR;
				else if (getString(jo, "uid") != null)
					type = XbrEventType.RFID;
			}

			try
			{
				XbrEvent e = mapEvent(jee, type);
				e.setReaderName(res.getReaderName());
				res.getEvents().add(e);
			}
			catch(Exception e) {
				logger.error("Failed to map event number " + ie + " " + e.getLocalizedMessage());
				logger.error(str);
			}
		}

		return res;
	}

	private static XbrEvent mapEvent(JSONObject jo, XbrEventType type)
			throws Exception
	{
		XbrEvent e;

		switch (type)
		{
			case LRR:
				e = new LrrEvent();
				mapLrrEvent(jo, (LrrEvent) e);
				break;
			case RFID:
				e = new TapEvent();
				mapTapEvent(jo, (TapEvent) e);
				break;
			case BioEnroll:
				e = new BioEvent(XbrEventType.BioEnroll);
				mapBioEvent(jo, (BioEvent) e);
				break;
			case BioMatch:
				e = new BioEvent(XbrEventType.BioMatch);
				mapBioEvent(jo, (BioEvent) e);
				break;
			case XfpDiag:
				e = new XfpDiagEvent();
				mapDiagEvent(jo, (XfpDiagEvent) e);
				break;
			case XbioDiag:
				e = new XbioDiagEvent();
				mapBioDiagEvent(jo, (XbioDiagEvent) e);
				break;
			case BioImage:
				e = new BioImageEvent();
				mapBioImageEvent(jo, (BioImageEvent)e);
				break;
			case BioScanError:
				e = new BioScanErrorEvent();
				mapBioScanErrorEvent(jo, (BioScanErrorEvent)e);
				break;
			case XbioFWUpgrade:
				e = new XbioFWUpgradeEvent();
				mapXbioFWEvent(jo, (XbioFWUpgradeEvent) e);
				break;
			case XtpGpio:
				e = new XtpGpioEvent();
				mapXtpGpioEvent(jo, (XtpGpioEvent)e);
				break;
			case Shutdown:
				e = new ShutdownEvent();
				mapShutdownEvent(jo, (ShutdownEvent)e);
				break;
            case XbrDiag:
				e = new XbrDiagEvent();
				mapXbrDiagEvent(jo, (XbrDiagEvent)e);
				break;
			default:
				// We must create some sort of event because the xBRC keeps track of the last eno received.
				e = new UnknownEvent();

                String readerName = "Unidentified Reader";
                if ( e != null && e.getReaderName() != null )
                {
                    readerName = e.getReaderName();
                }

				logger.warn("Unrecognized event type received from reader: Type=" + type +
                    ", Name=" + readerName);
		}

		e.setType(type);
		
		// get the standard event parts
		e.setEno(jo.getLong("eno"));

		String sDate = jo.getString("time");
		e.setTime(parseDate(sDate));
	
		return e;
	}
	
	private static Date parseDate(String datetime)
    {
	    try
		{
			Date dt;
			synchronized(dateFormatter)
			{
				dt = dateFormatter.parse(datetime);
			}
			return dt;
		}
		catch (Exception ex)
		{
			logger.error("Invalid time: " + datetime);
			return null;
		}
    }

	private static LrrEvent mapLrrEvent(JSONObject jo, LrrEvent e)
	{
		e.setXlrid(jo.getString("XLRID"));
		e.setPno(jo.getLong("pno"));
		e.setSs(jo.getLong("ss"));
		e.setChan(jo.getLong("chan"));
		e.setFreq(jo.getLong("freq"));
		return e;
	}

	private static TapEvent mapTapEvent(JSONObject jo, TapEvent e)
			throws Exception
	{
		String uid = getString(jo, "uid"); 
		String pid = getString(jo, "pid");
		String sid = getString(jo, "sid");
		String iin = getString(jo, "iin");
		
		// For legacy readers...
		if (uid == null)
			uid = getString(jo, "RFID");		
		if (uid == null)
			uid = getString(jo, "XRFID");
		
		if (uid == null)
			throw new Exception(
					"No RFID or XRFID or uid fields found in the Tap event data.");
				
		e.setUid(uid);
		e.setPid(pid);
		e.setSid(sid);
		e.setIin(iin);
			
		return e;
	}
	
	private static XtpGpioEvent mapXtpGpioEvent(JSONObject jo, XtpGpioEvent e)
			throws Exception
	{		
		String channel = getString(jo, "channel"); 
		e.setChannel(channel);
		return e;
	}
	
	public static String reverseId(String id)
	{
		StringBuffer sb = new StringBuffer(id.length());
		for (int i = id.length() - 2; i >= 0; i -= 2)
		{			
			sb.append(id.substring(i, i+2));
		}
		return sb.toString();
	}
	
	public static String publicIdToLongRangeId(long publicId) {
		return String.format("%010x", publicId);
	}
		
	private static BioEvent mapBioEvent(JSONObject jo, BioEvent e) {
		e.setXbioTemplate(getString(jo, "xbio-template"));
		e.setXbioImages(getString(jo,"xbio-images"));
		return e;
	}

	private static XfpDiagEvent mapDiagEvent(JSONObject jo, XfpDiagEvent e)
	{
		e.setAmb(Double.valueOf(getString(jo,"amb")));
		e.setTemp(Double.valueOf(getString(jo,"temp")));
		e.setMaxTemp(Double.valueOf(getString(jo,"max temp")));
		e.setStatus(StatusType.valueOf(getString(jo,"status")));
		e.setStatusMsg(getString(jo,"status msg"));
		e.setRfidStatus(getString(jo,"RFID status"));
		e.setRfidStatusMsg(getString(jo,"RFID msg"));
		e.setXbioStatus(getString(jo,"xbio status"));
		e.setXbioStatusMsg(getString(jo,"xbio msg"));
		return e;
	}
	
	private static XbioDiagEvent mapBioDiagEvent(JSONObject jo, XbioDiagEvent e)
	{
		e.setXbioData(getString(jo,"xbio-data"));
		return e;
	}
	
	private static BioImageEvent mapBioImageEvent(JSONObject jo, BioImageEvent e) {
		e.setUid(getString(jo, "uid"));
		e.setXbioImages(getString(jo,"xbio-images"));
		e.setTemplateId(getLong(jo,"templateId"));
		e.setTransactionId(getLong(jo,"transactionId"));
		return e;
	}
	
	private static BioScanErrorEvent mapBioScanErrorEvent(JSONObject jo, BioScanErrorEvent e) {
		e.setUid(getString(jo, "uid"));
		// legacy reader support
		if (e.getUid() == null)
			e.setUid(getString(jo, "RFID"));
		e.setReason(getString(jo,"reason"));
		return e;
	}
	
	private static XbioFWUpgradeEvent mapXbioFWEvent(JSONObject jo, XbioFWUpgradeEvent e) {
		e.setStatus(getString(jo, "status"));
		e.setVersion(getString(jo, "version"));
		return e;
	}
	
	private static ShutdownEvent mapShutdownEvent(JSONObject jo, ShutdownEvent e) {
		e.setTimeout(getString(jo, "timeout"));
		return e;
	}

	private static XbrDiagEvent mapXbrDiagEvent(JSONObject jo, XbrDiagEvent e) {
		e.setStatus(StatusType.valueOf(getString(jo, "status")));
		e.setStatusMsg(getString(jo, "status msg"));
		
		String batteryLevel = getString(jo, "bat level");
		if (batteryLevel != null)
		{
			Double level = new Double(batteryLevel);
			e.setBatteryLevel(new Integer(level.intValue()));
		}
		String batteryTime = getString(jo, "bat time");
		if (batteryTime != null)
		{
			e.setBatteryTime(new Integer(batteryTime));
		}
		String temperature = getString(jo, "temp");
		if (temperature != null)
		{
			e.setTemperature(new Double(temperature));
		}
		return e;
	}

	public static HelloMsg mapHello(String json) throws Exception
	{		
		return mapHello(JSONObject.fromObject(json));	
	}
	
	public static HelloMsg mapHello(JSONObject jo)
	{
		HelloMsg hm = new HelloMsg();
		
		hm.setMac(jo.getString("mac"));
		hm.setPort(jo.getInt("port"));
		hm.setNextEno(jo.getLong("next eno"));
		hm.setReaderName(jo.getString("reader name"));
		hm.setReaderType(jo.getString("reader type"));		
		hm.setReaderVersion("0.0.0.0");
		if (jo.containsKey("reader version"))
			hm.setReaderVersion(jo.getString("reader version"));
		else
			hm.setReaderVersion("");
		
		// Assume that all TAP readers support multiple URLs.
		hm.setSupportsMultipleStreams(ReaderType.supportsMultipleStreams(hm.getType()));
		
		Object oja = jo.get("update stream");
		if (oja != null)
		{			
			hm.setUpdateStream(new LinkedList<UpdateStream>());
			
			if (oja instanceof JSONArray)
			{
				hm.setSupportsMultipleStreams(true);
				
				JSONArray ja = (JSONArray)oja;				

				for (int i = 0; i < ja.size(); i++)
				{
					JSONObject jus = (JSONObject) ja.get(i);
					UpdateStream us = new UpdateStream(checkNull(jus.getString("url")));
					if (us.getUrl() != null && !us.getUrl().isEmpty())
						hm.getUpdateStream().add(us);
				}
			}
			else
			{
				UpdateStream us = new UpdateStream(checkNull(jo.getString("update stream")));
				if (us.getUrl() != null && !us.getUrl().isEmpty())
					hm.getUpdateStream().add(us);
			}
		}
		
		if (jo.containsKey("time"))
		{
			hm.setTime(parseDate(jo.getString("time")));
		}
		
		// The reader may include other information following the four digit
		// version number.
		// This information we should ignore.
		int idxSpace = hm.getReaderVersion().indexOf(' ');
		if (idxSpace > 0)
			hm.setReaderVersion(hm.getReaderVersion().substring(0, idxSpace));
		
		// truncate if too long
		if (hm.getReaderVersion().length()>32)
			hm.setReaderVersion(hm.getReaderVersion().substring(0, 32));

		hm.setMinXbrcVersion("1.0.0.0");
		if (jo.containsKey("min xbrc version"))
			hm.setMinXbrcVersion(jo.getString("min xbrc version"));
		hm.setLinuxVersion("0.0.0.0");
		if (jo.containsKey("linux version"))
			hm.setLinuxVersion(jo.getString("linux version"));
		hm.setHardwareType("xTP1");
		if (jo.containsKey("HW type"))
			hm.setHardwareType(jo.getString("HW type"));
			
		hm.setSimulated(hm.getLinuxVersion().indexOf("XfpeEmulator") >= 0);
		hm.setListens(hm.getType() != ReaderType.mobileGxp && hm.getPort() !=0);		// TODO: is the latter clause sufficient?
	
		hm.setLocationId(-1);
		if (jo.containsKey("location id"))
			hm.setLocationId(jo.getInt("location id"));

        if (jo.containsKey("media hash"))
        	hm.setMediaHash(jo.getString("media hash"));
		
		return hm;
	}
	
	public static XfpTempMsg mapTempMsg(String str){
		XfpTempMsg tempMsg = new XfpTempMsg();

		JSONObject jo = JSONObject.fromObject(str);
		tempMsg.setNow(getDouble(jo,"now"));
		tempMsg.setMax(getDouble(jo,"max"));

		return tempMsg;
	}
	
	public static XfpAmbLightMsg mapAmbLightMsg(String str){
		XfpAmbLightMsg alm = new XfpAmbLightMsg();
		
		JSONObject jo = JSONObject.fromObject(str);
		alm.setAmbLight(getDouble(jo, "amb"));
		
		return alm;
	}
	
	public static XfpTempMsg mapTempMsg(JSONObject jo){
		XfpTempMsg tempMsg = new XfpTempMsg();

		tempMsg.setNow(getDouble(jo,"now"));
		tempMsg.setMax(getDouble(jo,"max"));

		return tempMsg;
	}
	
	public static XfpAmbLightMsg mapAmbLightMsg(JSONObject jo){
		XfpAmbLightMsg alm = new XfpAmbLightMsg();

		alm.setAmbLight(getDouble(jo, "amb"));
		
		return alm;
	}
	
	public static BioOptionsMsg bioOptionsMsg(JSONObject jo){
		BioOptionsMsg bioOpMsg = new BioOptionsMsg();
		
		String imgCapOn = getString(jo,"image_capture");
		bioOpMsg.setImageCapture(imgCapOn.equals("on") ? true : false);
		
		return bioOpMsg;
	}

	/*
	 * Used for testing only to simulate xBR.
	 */
	public static String serializeEvents(XbrEvents events) throws Exception
	{
		JSONObject jo = new JSONObject();

		jo.put("reader name", events.getReaderName());

		JSONArray ja = new JSONArray();
		for (XbrEvent e : events.getEvents())
		{
			JSONObject jee = new JSONObject();
			jee.put("eno", e.getEno());
			jee.put("type", e.getType().toString());

/*
			SimpleDateFormat df = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			jee.put("time", df.format(e.getTime()));
			
*/
			String sDate;
			synchronized(dateFormatter)
			{
				sDate = dateFormatter.format(e.getTime());
			}
			jee.put("time", sDate);

			switch (e.getType())
			{
				case LRR:
					serializeEvent(jee, (LrrEvent) e);
					break;
				case RFID:
					serializeEvent(jee, (TapEvent) e);
					break;
				case BioEnroll:
				case BioMatch:
					serializeEvent(jee, (BioEvent) e);
					break;
				case BioImage:
					serializeEvent(jee, (BioImageEvent) e);
					break;
				default:
					throw new Exception(
							"Don't know how to serialize event type: "
									+ e.getType());
			}

			ja.add(jee);
		}

		jo.put("events", ja);

		return jo.toString();
	}
	
	public static String serializeHello(HelloMsg hm) throws JsonGenerationException, JsonMappingException, IOException
	{
		return JsonUtil.convertToJson(hm);
	}

	private static void serializeEvent(JSONObject jo, LrrEvent e)
	{
		jo.put("XLRID", e.getXlrid());
		jo.put("pno", e.getPno());
		jo.put("ss", e.getSs());
		jo.put("chan", e.getChan());
		jo.put("freq", e.getFreq());
	}

	private static void serializeEvent(JSONObject jo, TapEvent e)
	{
		jo.put("uid", e.getUid());
		if (e.getPid() != null)
			jo.put("pid", e.getPid());
		if (e.getSid() != null)
			jo.put("sid", e.getSid());
		if (e.getIin() != null)
			jo.put("sid", e.getIin());
	}

	private static void serializeEvent(JSONObject jo, BioEvent e) {
		jo.put("xbio-template", e.getXbioTemplate());
		jo.put("xbio-images", e.getXbioImages());
	}
	
	private static void serializeEvent(JSONObject jo, BioImageEvent msg)
	{
		jo.put("uid", msg.getUid());
		jo.put("xbio-images", msg.getXbioImages());
		jo.put("templateId", msg.getTemplateId());
		jo.put("transactionId", msg.getTransactionId());
	}

	private static String getString(JSONObject jo, String key)
	{
		try
		{
			return jo.getString(key);
		}
		catch (Exception e)
		{
		} // expected exception

		return null;
	}

	private static Long getLong(JSONObject jo, String key)
	{
		try
		{
			return jo.getLong(key);
		}
		catch (Exception e)
		{
		} // expected exception

		return null;
	}
	
	private static Integer getInt(JSONObject jo, String key){
		try {
			return jo.getInt(key);
		} catch (Exception e){}
		
		return null;
	}
	
	private static Double getDouble(JSONObject jo, String key){
		try {
			return jo.getDouble(key);
		} catch (Exception e){}
		
		return null;
	}
	
	private static Boolean getBoolean(JSONObject jo, String key){
		try {
			return jo.getBoolean(key);
		} catch (Exception e){}
		
		return null;
	}

	public static String serializeBioMatch(BioMatchMsg msg)
	{
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		for (String template : msg.getTemplates())
		{
			JSONObject to = new JSONObject();
			to.put("template", template);
			ja.add(to);
		}
		jo.put("templates", ja);
		return jo.toString();
	}

	public static BioMatchMsg mapBioMatch(String str)
	{
		JSONObject jo = JSONObject.fromObject(str);

		BioMatchMsg msg = new BioMatchMsg();
		msg.setTemplates(new LinkedList<String>());

		JSONArray ja = (JSONArray) jo.get("templates");

		for (int ie = 0; ie < ja.size(); ie++)
		{
			JSONObject to = (JSONObject) ja.get(ie);
			String template = getString(to, "template");
			msg.getTemplates().add(template);
		}
		return msg;
	}
	
	private static String checkNull(String str)
	{
		if (str != null && str.equals("null"))
			return null;
		return str;
	}
}
