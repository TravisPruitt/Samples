package com.disney.xband.lib.xbrapi;

import com.disney.xband.common.lib.health.StatusType;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public final class XbrJsonMapperFast
{
    static private enum Token {
        TkReaderName,
        TkEno,
        TkTime,
        TkType,
        TkXlrid,
        TkPno,
        TkFreq,
        TkChan,
        TkSs,
        TkUid,
        TkSid,
        TkPid,
        TkIin,
        TkChannel,
        TkXbioTemplate,
        TkXbioImages,
        TkAmb,
        TkTemp,
        TkMaxTemp,
        TkStatus,
        TkStatusMsg,
        TkRfidStatus,
        TkRfidMsg,
        TkXbioStatus,
        TkXbioMsg,
        TkXbioData,
        TkTemplateId,
        TkTransactionId,
        TkRfid,
        TkReason,
        TkVersion,
        TkMac,
        TkPort,
        TkReaderType,
        TkReaderVersion,
        TkLinuxVersion,
        TkNextEno,
        TkNumEvents,
        TkQueuedEvents,
        TkOldestEvent,
        TkPushUrl,
        TkXbioFwVersion,
        TkXbioHwVersion,
        TkXbioSerialNumber,
        TkRfidFwVersion,
        TkRfidDescription,
        TkHwType,
        TkNow,
        TkMax,
        TkImageCapture,
        TkOn,
        TkXrfid,
        TkTimeout,
        TkBatteryLevel,
        TkBatteryTime,
        TkUnknownToken
    };

    static private final Logger logger;
    static private final SimpleDateFormat dateFormatter;
    static private final JsonFactory jsonFactory;
    static private final HashMap<String, Token> tokens;
    static private final boolean USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY = true;

	// initialize the date formatter here since this is very slow
	static
	{
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        jsonFactory = new JsonFactory();
        logger = Logger.getLogger(XbrJsonMapperFast.class);
        tokens = new HashMap<String, Token>(64);

        tokens.put("reader name", Token.TkReaderName);
        tokens.put("eno", Token.TkEno);
        tokens.put("type", Token.TkType);
        tokens.put("XLRID", Token.TkXlrid);
        tokens.put("time", Token.TkTime);
        tokens.put("pno", Token.TkPno);
        tokens.put("freq", Token.TkFreq);
        tokens.put("chan", Token.TkChan);
        tokens.put("ss", Token.TkSs);
        tokens.put("uid", Token.TkUid);
        tokens.put("pid", Token.TkPid);
        tokens.put("sid", Token.TkSid);
        tokens.put("iin", Token.TkIin);
        tokens.put("channel", Token.TkChannel);
        tokens.put("xbio-template", Token.TkXbioTemplate);
        tokens.put("xbio-images", Token.TkXbioImages);
        tokens.put("amb", Token.TkAmb);
        tokens.put("temp", Token.TkTemp);
        tokens.put("max temp", Token.TkMaxTemp);
        tokens.put("status", Token.TkStatus);
        tokens.put("status msg", Token.TkStatusMsg);
        tokens.put("RFID status", Token.TkRfidStatus);
        tokens.put("RFID msg", Token.TkRfidMsg);
        tokens.put("xbio status", Token.TkXbioStatus);
        tokens.put("xbio msg", Token.TkXbioMsg);
        tokens.put("xbio-data", Token.TkXbioData);
        tokens.put("templateId", Token.TkTemplateId);
        tokens.put("transactionId", Token.TkTransactionId);
        tokens.put("RFID", Token.TkRfid);
        tokens.put("reason", Token.TkReason);
        tokens.put("status", Token.TkStatus);
        tokens.put("version", Token.TkVersion);
        tokens.put("mac", Token.TkMac);
        tokens.put("port", Token.TkPort);
        tokens.put("reader type", Token.TkReaderType);
        tokens.put("reader version", Token.TkReaderVersion);
        tokens.put("linux version", Token.TkLinuxVersion);
        tokens.put("next eno", Token.TkNextEno);
        tokens.put("queued events", Token.TkQueuedEvents);
        tokens.put("num events", Token.TkNumEvents);
        tokens.put("oldest event", Token.TkOldestEvent);
        tokens.put("push url", Token.TkPushUrl);
        tokens.put("xbio fw version", Token.TkXbioFwVersion);
        tokens.put("xbio hw version", Token.TkXbioHwVersion);
        tokens.put("xbio serial number", Token.TkXbioSerialNumber);
        tokens.put("RFID fw version", Token.TkRfidFwVersion);
        tokens.put("RFID description", Token.TkRfidDescription);
        tokens.put("HW type", Token.TkHwType);
        tokens.put("now", Token.TkNow);
        tokens.put("max", Token.TkMax);
        tokens.put("image_capture", Token.TkImageCapture);
        tokens.put("on", Token.TkOn);
        tokens.put("XRFID", Token.TkXrfid);
        tokens.put("timeout", Token.TkTimeout);
        tokens.put("bat level", Token.TkBatteryLevel);
        tokens.put("bat time", Token.TkBatteryTime);
	}

    public static XbrEvents mapEvents(final String str) throws Exception
    {
        final JsonParser jParser = jsonFactory.createJsonParser(str);
        final XbrEvents res = new XbrEvents();
        final Map<Token, Object> map = new HashMap<Token, Object>(32);

        try {
            jParser.nextToken();

            while (jParser.nextToken() != JsonToken.END_OBJECT) {

                if (!"events".equals(jParser.getCurrentName())) {
                    jParser.nextToken();

                    if("reader name".equals(jParser.getCurrentName())) {
                        res.setReaderName(jParser.getText());
                        jParser.nextToken();
                    }
                    else {
                        throw new Exception("reader name is not set");
                    }
                }

                if ("events".equals(jParser.getCurrentName())) {
                    jParser.nextToken();

                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        if(map.size() > 0) {
                            map.clear();
                        }

                        while (jParser.nextToken() != JsonToken.END_OBJECT) {
                            final Token name = tokens.get(jParser.getCurrentName());

                            if(name != null) {
                                if(jParser.nextToken() == JsonToken.VALUE_STRING) {
                                    map.put(name, jParser.getText());
                                }
                                else {
                                    map.put(name, jParser.getNumberValue());
                                }
                            }
                        }

                        final XbrEvent e = mapEvent(map);
                        e.setReaderName(res.getReaderName());
                        res.getEvents().add(e);
                    }
                }
                else {
                    for(XbrEvent e : res.getEvents()) {
                        e.setReaderName(res.getReaderName());
                    }

                    return res;
                }
            }
        }
        catch(TrySlowerParser e) {
            return XbrJsonMapper.mapEvents(str);
        }
        catch (Exception e) {
            logger.error("Failed to process reader message: " + e.getMessage());
        }
        finally {
            if(jParser != null) {
                jParser.close();
            }
        }

        return res;
    }

    private static
    XbrEvent mapEvent(final Map<Token, Object> map)
    throws Exception {
        final XbrEvent e;
        final XbrEventType type;
        final String stype = (String) map.get(Token.TkType);

        if(stype == null) {
            if (map.get(Token.TkXlrid) != null) {
                type = XbrEventType.LRR;
            }
            else if (map.get(Token.TkUid) != null){
                type = XbrEventType.RFID;
            }
            else
            	type = XbrEventType.Unknown;
        }
        else {
             type = XbrEventType.fromType(stype);
        }

        switch (type)
        {
            case LRR:
                e = new LrrEvent();
                mapLrrEvent(map, (LrrEvent) e);
                break;

            case RFID:
                e = new TapEvent();
                mapTapEvent(map, (TapEvent) e);
                break;

            case BioEnroll:
                if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }

                e = new BioEvent(XbrEventType.BioEnroll);
                mapBioEvent(map, (BioEvent) e);
                break;

            case BioMatch:
                if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }

                e = new BioEvent(XbrEventType.BioMatch);
                mapBioEvent(map, (BioEvent) e);
                break;

            case XfpDiag:
                if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }

                e = new XfpDiagEvent();
                mapDiagEvent(map, (XfpDiagEvent) e);
                break;

            case XbioDiag:
                if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }

                e = new XbioDiagEvent();
                mapBioDiagEvent(map, (XbioDiagEvent) e);
                break;

            case BioImage:
                if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }

                e = new BioImageEvent();
                mapBioImageEvent(map, (BioImageEvent) e);
                break;

            case BioScanError:
                if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }

                e = new BioScanErrorEvent();
                mapBioScanErrorEvent(map, (BioScanErrorEvent) e);
                break;

            case XbioFWUpgrade:
                if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }

                e = new XbioFWUpgradeEvent();
                mapXbioFWEvent(map, (XbioFWUpgradeEvent) e);
                break;

            case XtpGpio:
                if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }

                e = new XtpGpioEvent();
                mapXtpGpioEvent(map, (XtpGpioEvent) e);
                break;

            case Shutdown:
            	if (USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY) {
            		throw new TrySlowerParser();
            	}
            	
            	e = new ShutdownEvent();
            	mapShutdownEvent(map, (ShutdownEvent) e);
            	break;
            	
            case XbrDiag:
            	if (USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY) {
            		throw new TrySlowerParser();
            	}
            	e = new XbrDiagEvent();
            	mapXbrDiagEvent(map, (XbrDiagEvent) e);
            	break;
            case Unknown:				
            default:
            	if(USE_FAST_PARSER_FOR_LRR_AND_TAP_EVENTS_ONLY ) {
                    throw new TrySlowerParser();
                }
        }

        e.setType(type);
        e.setEno(new Long((Integer) map.get(Token.TkEno)));
        setTime(map, e);

        return e;
    }

    private static void
    setTime(final Map<Token, Object> map, final XbrEvent e)
    throws Exception {
        final String sDate = (String) map.get(Token.TkTime);

        try
        {
            final Date dt;

            synchronized(dateFormatter)
            {
                dt = dateFormatter.parse(sDate);
            }

            e.setTime(dt);
        }
        catch (Exception ex)
        {
            throw new Exception("Invalid time: " + sDate + " in event.", ex);
        }
    }

	private static LrrEvent
    mapLrrEvent(final Map<Token, Object> map, final LrrEvent e)
    throws Exception {
	    e.setXlrid((String) map.get(Token.TkXlrid));
	    e.setPno(new Long((Integer) map.get(Token.TkPno)));
	    e.setFreq(new Long((Integer) map.get(Token.TkFreq)));
        e.setSs(new Long((Integer) map.get(Token.TkSs)));
        e.setChan(new Long((Integer) map.get(Token.TkChan)));

		return e;
	}

	private static TapEvent
    mapTapEvent(final Map<Token, Object> map, final TapEvent e)
    throws Exception
	{
        if(map.get(Token.TkUid) != null) {
            e.setUid((String) map.get(Token.TkUid));
        }
        else {
            if(map.get(Token.TkRfid) != null) {
                e.setUid((String) map.get(Token.TkRfid));
            }
            else {
                e.setUid((String) map.get(Token.TkXrfid));
            }
        }

        e.setPid((String) map.get(Token.TkPid));
        e.setSid((String) map.get(Token.TkSid));
        e.setIin((String) map.get(Token.TkIin));

		return e;
	}
	
	private static XtpGpioEvent
    mapXtpGpioEvent(final Map<Token, Object> map, final XtpGpioEvent e)
	throws Exception {
		e.setChannel((String) map.get(Token.TkChannel));

		return e;
	}
	
	private static ShutdownEvent
    mapShutdownEvent(final Map<Token, Object> map, final ShutdownEvent e)
	throws Exception {
		e.setTimeout((String) map.get(Token.TkTimeout));

		return e;
	}
	
	private static XbrDiagEvent
	mapXbrDiagEvent(final Map<Token, Object> map, final XbrDiagEvent e)
	throws Exception {
		e.setStatus(StatusType.valueOf((String) map.get(Token.TkStatus)));
		e.setStatusMsg((String) map.get(Token.TkStatus));
		
		String batteryLevel = (String) map.get(Token.TkBatteryLevel);
		if (batteryLevel != null)
		{
			Double level = new Double(batteryLevel);
			e.setBatteryLevel(new Integer(level.intValue()));
		}
		String batteryTime = (String) map.get(Token.TkBatteryTime);
		if (batteryTime != null)
		{
			e.setBatteryTime(new Integer(batteryTime));
		}
		String temperature = (String) map.get(Token.TkTemp);
		if (temperature != null)
		{
			e.setTemperature(new Double(temperature));
		}
		return e;
	}
	
	private static BioEvent
    mapBioEvent(final Map<Token, Object> map, final BioEvent e)
    throws Exception {
		e.setXbioTemplate((String) map.get(Token.TkXbioTemplate));
		e.setXbioImages((String) map.get(Token.TkXbioImages));

		return e;
	}

	private static XfpDiagEvent
    mapDiagEvent(final Map<Token, Object> map, final XfpDiagEvent e)
    throws Exception {
	    e.setAmb(Double.valueOf((String) map.get(Token.TkAmb)));
        e.setTemp(Double.valueOf((String) map.get(Token.TkTemp)));
        e.setMaxTemp(Double.valueOf((String) map.get(Token.TkMaxTemp)));
		e.setStatus(StatusType.valueOf((String) map.get(Token.TkStatus)));
		e.setStatusMsg((String) map.get(Token.TkStatusMsg));
		e.setRfidStatus((String) map.get(Token.TkRfidStatus));
		e.setRfidStatusMsg((String) map.get(Token.TkRfidMsg));
		e.setXbioStatus((String) map.get(Token.TkXbioStatus));
		e.setXbioStatusMsg((String) map.get(Token.TkXbioMsg));

		return e;
	}
	
	private static XbioDiagEvent
    mapBioDiagEvent(final Map<Token, Object> map, final XbioDiagEvent e)
    throws Exception {
		e.setXbioData((String) map.get(Token.TkXbioData));

		return e;
	}
	
	private static BioImageEvent
    mapBioImageEvent(final Map<Token, Object> map, final BioImageEvent e)
    throws Exception {
		e.setUid((String) map.get(Token.TkUid));
		e.setXbioImages((String) map.get(Token.TkXbioImages));
        e.setTemplateId(new Long((Integer) map.get(Token.TkTemplateId)));
        e.setTransactionId(new Long((Integer) map.get(Token.TkTransactionId)));

		return e;
	}
	
	private static BioScanErrorEvent
    mapBioScanErrorEvent(final Map<Token, Object> map, final BioScanErrorEvent e)
    throws Exception {
		e.setUid((String) map.get(Token.TkUid));

		// legacy reader support
		if (e.getUid() == null) {
			e.setUid((String) map.get(Token.TkRfid));
		    e.setReason((String) map.get(Token.TkReason));
        }

		return e;
	}
	
	private static XbioFWUpgradeEvent
    mapXbioFWEvent(final Map<Token, Object> map, final XbioFWUpgradeEvent e)
    throws Exception {
		e.setStatus((String) map.get(Token.TkStatus));
		e.setVersion((String) map.get(Token.TkVersion));

		return e;
	}

	public static HelloMsg
    mapHello(final Map<Token, Object> map)
    throws Exception {
		final HelloMsg hm = new HelloMsg();
		/*
		hm.setMac((String) map.get(Token.TkMac));
        hm.setPort((Integer) map.get(Token.TkPort));
		hm.setReaderName((String) map.get(Token.TkReaderName));
		hm.setReaderType((String) map.get(Token.TkReaderType));
		hm.setReaderVersion((String) map.get(Token.TkReaderVersion));
		hm.setLinuxVersion((String) map.get(Token.TkLinuxVersion));
        hm.setNextEno(new Long((Integer) map.get(Token.TkNextEno)));
        hm.setNumEvents((Integer) map.get(Token.TkNumEvents));
        hm.setQueuedEvents((Integer) map.get(Token.TkQueuedEvents));
		hm.setOldestEvent((String) map.get(Token.TkOldestEvent));
		hm.setPushUrl((String) map.get(Token.TkPushUrl));
		hm.setxBioFWVersion((String) map.get(Token.TkXbioFwVersion));
		hm.setxBioHWVersion((String) map.get(Token.TkXbioHwVersion));
        hm.setxBioSn((Integer) map.get(Token.TkXbioSerialNumber));
		hm.setRfidFWVersion((String) map.get(Token.TkRfidFwVersion));
		hm.setRfidDescription((String) map.get(Token.TkRfidDescription));
		hm.setHardwareType((String) map.get(Token.TkHwType));
		*/

		return hm;
	}

	public static XfpTempMsg mapTempMsg(final Map<Token, Object> map) throws Exception {
		final XfpTempMsg tempMsg = new XfpTempMsg();
        tempMsg.setNow(Double.valueOf((Float) map.get(Token.TkNow)));
        tempMsg.setMax(Double.valueOf((Float) map.get(Token.TkMax)));

		return tempMsg;
	}
	
	public static XfpAmbLightMsg mapAmbLightMsg(final Map<Token, Object> map) throws Exception {
		final XfpAmbLightMsg alm = new XfpAmbLightMsg();
        alm.setAmbLight(Double.valueOf((Float) map.get(Token.TkAmb)));

		return alm;
	}

	public static BioOptionsMsg bioOptionsMsg(final Map<Token, Object> map) throws Exception {
		final BioOptionsMsg bioOpMsg = new BioOptionsMsg();
		final String imgCapOn = (String) map.get(Token.TkImageCapture);
		bioOpMsg.setImageCapture(imgCapOn.equals("on") ? true : false);
		
		return bioOpMsg;
	}

    final private static class TrySlowerParser extends RuntimeException {
    }

    /*
    public static void main(String[] args) throws Exception {
        String s = "{" +
                "   \"events\" : [" +
                "      {" +
                "         \"eno\" : 34," +
                "         \"time\" : \"2012-10-06T01:36:49.47\"," +
                "         \"type\" : \"xbio-diagnostics\"," +
                "         \"xbio-data\" : \"fQBwPyAAHwAdAA==\"" +
                "      }" +
                "   ]," +
                "   \"reader name\" : \"joef-buzz-hw\"" +
                "}";

        String s2 = "{" +
                "   \"reader name\" : \"joef-buzz-hw\"," +
                "   \"events\" : [" +
                "      {" +
                "         \"eno\" : 34," +
                "         \"time\" : \"2012-10-06T01:36:49.47\"," +
                "         \"type\" : \"xbio-diagnostics\"," +
                "         \"xbio-data\" : \"fQBwPyAAHwAdAA==\"" +
                "      }" +
                "   ]" +
                "}";
        XbrEvents e = XbrJsonMapperFast.mapEvents(s2);
        System.out.println(e.toString());
        XbrEvents e2 = XbrJsonMapperFast.mapEvents(s);
        System.out.println(e2.toString());
    }
    */
}
