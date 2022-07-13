package com.disney.xband.jmstalker;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.disney.xband.jmstalker.entity.Message;


public class JmsTalker
{
	// next unallocated guest id
	// private LinkedList<Integer> liAvailableGuest = new LinkedList<Integer>();

	public static void main(String[] args)
	{
		CLOptions clo = CLOptions.parse(args);
		if (clo == null)
			return;

		if (clo.hasDisplayUsage() || clo.getInputFile() == null)
		{
			CLOptions.usage();
			return;
		}

		System.out.println("Simulating from: " + clo.getStartHour() + ":" + clo.getStartMin() +" to " + clo.getEndHour() +":" + clo.getEndMin());
		System.out.println("Base time:       " + formatTime(clo.getCalBase().getTime()) );
		if (clo.isFast())
			System.out.format("Time factor:     Fast%n");
		else
			System.out.format("Time factor:     Real-time%n");
		if (clo.getLogFile() != null)
			System.out.println("Logging to:      " + clo.getLogFile());
		if (clo.getJMSBroker() != null)
		{
			System.out.println("JMS broker:      " + clo.getJMSBroker());
			System.out.println("JMS topic:       " + clo.getJMSTopic());
			System.out.println("JMS user:        " + clo.getJMSUser());
		}
		JmsTalker talker = new JmsTalker();
		talker.Simulate(clo);
		System.out.println("Done.");
	}

	private void Simulate(CLOptions clo)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();			
			DocumentBuilder db = dbf.newDocumentBuilder();
			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(clo.getInputFile());
			Element el = dom.getDocumentElement();

					StringBuilder sb = new StringBuilder();

					String location = getTagValue("location", el);
					String eventType = getTagValue("eventType", el);
					String subType = getTagValue("subType", el);
					String referenceId = getTagValue("referenceId", el);
					String guestIdentifier = getTagValue("guestIdentifier", el);
					String timeStamp = getTagValue("timeStamp", el);
					String payLoad = getTagCDATA("payLoad", el);
					String correlationId = getTagValue("correlationId", el);

					// set up the JMS Agent
					JMSAgent jms = null;
					if (clo.getJMSBroker() != null)
					{
						jms = new JMSAgent(clo.getJMSBroker(), location /* "com.synapse.xbrc.qa.scott"*/ , clo.getJMSUser(), clo.getJMSPassword());
						jms.initialize();
					}					
					
					sb.append("<businessEvent>\n");
					sb.append("<location>"+ location + "</location>\n");
					sb.append("<eventType>"+ eventType + "</eventType>\n");
					sb.append("<subType>"+ subType + "</subType>\n");
					sb.append("<referenceId>"+ referenceId + "</referenceId>\n");
					sb.append("<guestIdentifier>"+ guestIdentifier + "</guestIdentifier>\n");
					sb.append("<timeStamp>"+ timeStamp + "</timeStamp>\n");
					sb.append("<payLoad><![CDATA["+ payLoad + "]]></payLoad>\n");
					sb.append("<correlationId>"+ correlationId + "</correlationId>\n");
					sb.append("</businessEvent>");

					Message msg = Message.create("businessEvent", location, sb.toString());
					//add it to list
					jms.publishMessage(msg);
	

			if (jms != null)
			{
				jms.terminate();
			}

		}
		catch (Exception ex)
		{
			System.err.println(ex);
			ex.printStackTrace();
		}
	}



	private static String formatTime(Date ts)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		//sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(ts);
	}

	private static String getTagValue(String sTag, Element eElement) {
		
		try
		{
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	 
	        Node nValue = (Node) nlList.item(0);
	 
	        return nValue.getNodeValue();
		}
		catch (Exception ex)
		{
			return "";
		}
	  }
	private static String getTagCDATA(String sTag, Element eElement) {
		
		try
		{
			CharacterData nlCdata = (CharacterData) eElement.getElementsByTagName(sTag).item(0).getFirstChild();
	 
	        return nlCdata.getData();
		}
		catch (Exception ex)
		{
			return "";
		}
	  }
}
