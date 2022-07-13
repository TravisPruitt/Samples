package com.disney.xband.xbrc.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.disney.xband.xbrc.lib.model.IXBRCModel;


public class CurrentConfigDiff
{
	public final static char PATH_SEPARATOR = '/';
	public final static char PRETTY_PRINT_SEPARATOR = ';';
	public final static String[] columns = {"Path","Previous Value","Current Value","Action"};
	
	private static Logger logger = Logger.getLogger(CurrentConfigDiff.class);
	
	private static final Map<String, Set<String>> exclude;
	
	static 
	{
		exclude = new HashMap<String, Set<String>>();
		
		// from property
		Set<String> propertyExclude = new HashSet<String>();
		// include both class and property like this: propertyExclude.add("AttractionModelConfig/abandonmenttimeout");
		exclude.put("property", propertyExclude);
				
		// from readerlocation
		Set<String> readerLocationExclude = new HashSet<String>();
		readerLocationExclude.add("id");
		exclude.put("readerlocation", readerLocationExclude);
				
		// from reader
		Set<String> readerExclude = new HashSet<String>();
		readerExclude.add("timelasthello");
		readerExclude.add("id");
		exclude.put("reader", readerExclude);
		
		// from griditem
		Set<String> gridItemExclude = new HashSet<String>();
		gridItemExclude.add("id");
		exclude.put("griditem", gridItemExclude);
		
		// from image
		Set<String> imageExclude = new HashSet<String>();
		imageExclude.add("id");
		imageExclude.add("blob");
		exclude.put("image", imageExclude);
		
		// from castMember
		Set<String> modelExclude = new HashSet<String>();
		modelExclude.add("id");
		modelExclude.add("omniserverid");
		modelExclude.add("locationId");
		exclude.put("model", modelExclude);
	}
	
	public static void prettyPrint(LinkedList<CurrentConfigDiffItem> result, File file, boolean printColumnNames)
	{
		if (result == null && result.size() == 0)
			return;

		if (file == null)
			return;
		
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new FileWriter(file, true));

			if (printColumnNames)
			{
				out.append(columns[0]).append(PRETTY_PRINT_SEPARATOR)
					.append(columns[1]).append(PRETTY_PRINT_SEPARATOR)
					.append(columns[2]).append(PRETTY_PRINT_SEPARATOR)
					.append(columns[3]).append("\n");
			}
			
			for (CurrentConfigDiffItem item : result)
			{
				out.append(item.getKey()).append(PRETTY_PRINT_SEPARATOR)
					.append(item.getPrevValue()).append(PRETTY_PRINT_SEPARATOR)
					.append(item.getCurrValue()).append(PRETTY_PRINT_SEPARATOR)
					.append(item.getResult().name()).append("\n");
			}
		}
		catch(IOException e)
		{
		       System.out.println("Failed to pretty print results of /currentconfiguration diff.");
		}
		finally
		{
			if (out != null)
				out.close();
		}
	}
	
	public static String prettyPrint(LinkedList<CurrentConfigDiffItem> result, boolean printColumnNames)
	{
		if (result == null && result.size() == 0)
			return "";
		
		StringBuffer output = new StringBuffer();
		
		if (printColumnNames)
		{
			output.append(columns[0]).append(PRETTY_PRINT_SEPARATOR)
					.append(columns[1]).append(PRETTY_PRINT_SEPARATOR)
					.append(columns[2]).append(PRETTY_PRINT_SEPARATOR)
					.append(columns[3]).append("\n");
		}
		
		for (CurrentConfigDiffItem item : result)
		{
			output.append(item.getKey()).append(PRETTY_PRINT_SEPARATOR)
					.append(item.getPrevValue()).append(PRETTY_PRINT_SEPARATOR)
					.append(item.getCurrValue()).append(PRETTY_PRINT_SEPARATOR)
					.append(item.getResult().name()).append("\n");
		}
		
		return output.toString();
	}
	
	public static LinkedList<CurrentConfigDiffItem> diff(InputStream previous, InputStream current) 
	{
		Map<String, String> flatPrevious = new HashMap<String, String>();
		Map<String, String> flatCurrent = new HashMap<String, String>();

		/*
		 *  flatten previous /currentconfig
		 */
		
		if (logger.isInfoEnabled())
			logger.info("Processing previous /currentconfig xml.");
		
		SAXBuilder builder = new SAXBuilder();
		
		try {
			if (previous != null)
			{
				Document document = (Document) builder.build(previous);
				Element venue = document.getRootElement();
				
				if (venue == null)
				{
					logger.warn("Abborting /currentconfig diff. Element 'venue' not found in the xml provided.");
					return null;
				}
	
				flatten(venue, flatPrevious);
			}
		} catch (IOException io) {
			logger.error("Invalid current input stream.");
		} catch (JDOMException jdomex) {
			logger.error("Failed to parse xml provided as current input stream");
		}
		
		/*
		 *  flatten current /currentconfig
		 */
		
		if (logger.isInfoEnabled())
			logger.info("Processing current /currentconfig xml.");
		
		builder = new SAXBuilder();
		
		try {
			if (current != null)
			{
				Document document = (Document) builder.build(current);
				Element venue = document.getRootElement();
				
				if (venue == null)
				{
					logger.warn("Abborting /currentconfig diff. Element 'venue' not found in the xml provided.");
					return null;
				}
	
				flatten(venue, flatCurrent);
			}
		} catch (IOException io) {
			logger.error("Invalid current input stream.");
		} catch (JDOMException jdomex) {
			logger.error("Failed to parse xml provided as current input stream");
		}
		
		/*
		 *  diff the previous and current /currentconfig
		 */
		LinkedList<CurrentConfigDiffItem> result = new LinkedList<CurrentConfigDiffItem>();
		
		if (flatPrevious.size() == 0 && flatCurrent.size() == 0)
		{
			logger.warn("Both previous and current /currentconfig found empty.");
		}
		else if (flatPrevious.size() == 0)
		{
			// all added
			for (String key : flatCurrent.keySet())
			{
				CurrentConfigDiffItem item = new CurrentConfigDiffItem(
						key,
						null,
						flatCurrent.get(key),
						CurrentConfigDiffItem.DiffResult.ADDED);
			
				result.add(item);
			}
		}
		else if (flatCurrent.size() == 0)
		{
			// all removed
			for (String key : flatPrevious.keySet())
			{
				CurrentConfigDiffItem item = new CurrentConfigDiffItem(
						key,
						flatPrevious.get(key),
						null,
						CurrentConfigDiffItem.DiffResult.DELETED);

				result.add(item);
			}
		}
		else
		{
			// added, removed, and modified		        
			final TreeSet<String> allKeys = new TreeSet<String>();     
		    allKeys.addAll(flatPrevious.keySet());
		    allKeys.addAll(flatCurrent.keySet());
		    
		    for (String key: allKeys)
		    {	
		    	CurrentConfigDiffItem item = null;
		    	
		    	if (!flatPrevious.containsKey(key))
		    	{
		    		item = new CurrentConfigDiffItem(
							key,
							null,
							flatCurrent.get(key),
							CurrentConfigDiffItem.DiffResult.ADDED);
		    	}
		    	else if (!flatCurrent.containsKey(key))
		    	{
		    		item = new CurrentConfigDiffItem(
							key,
							flatPrevious.get(key),
							null,
							CurrentConfigDiffItem.DiffResult.DELETED);
		    	}
		    	else
		    	{
		    		if (!flatPrevious.get(key).equals(flatCurrent.get(key)))
		    		{
		    			item = new CurrentConfigDiffItem(
								key,
								flatPrevious.get(key),
								flatCurrent.get(key),
								CurrentConfigDiffItem.DiffResult.MODIFIED);
		    		}
		    	}
		    	
		    	if (item != null)
					result.add(item);
		    }
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param node the <CODE>configuration</CODE> xml element.
	 */
	private static void flatten(Element venue, Map<String, String> results)
	{
		// flatten venue name
		results.put(venue.getName() + PATH_SEPARATOR + "name", venue.getAttributeValue("name"));
		
		// flatten configuration
		Element configuration = venue.getChild("configuration");
		if (configuration == null)
		{
			logger.warn("Element 'configuration' not found in the xml provided.");
			return;
		}
			
		final String path = configuration.getName();
		
		// flatten description
		results.put(path + PATH_SEPARATOR + configuration.getChild("description").getName(), configuration.getChild("description").getText());
		
		// flatten descendants
		flattenProperties(configuration.getChild("properties"), results);
		flattenReaderLocationInfo(configuration.getChild("readerlocationinfo"), results);
		flattenGridItems(configuration.getChild("griditems"), results);
		flattenImages(configuration.getChild("images"), results);
		flattenSchedulerSettings(configuration.getChild("schedulerSettings"), results);
		
		// delegate flattening of the model specific section to the model itself
		Processor.INSTANCE.getModel().flattenModel(configuration.getChild("model"), results, exclude.get("model"), PATH_SEPARATOR);
	}
	
	/**
	 * Flattens the properties section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <venue name="xBRC" time="2013-05-01T22:22:16.298">
	 * 		<configuration name="current" type="full">
	 * 			<properties>
	 * 				<property class="AttractionModelConfig" name="abandonmenttimeout">1111</property>
	 * 			</properties>
	 * 		<properties>
	 * </venue>
	 * 
	 * @param properties the <CODE>properties</CODE> xml element.
	 */
	private static void flattenProperties(Element properties, Map<String, String> results)
	{
		if (properties == null)
		{
			logger.warn("Element 'properties' not found in the xml provided.");
			return;
		}
		
		final List list = properties.getChildren("property");
		final String path = properties.getParentElement().getName() + PATH_SEPARATOR + properties.getName();

		Element property = null;
		StringBuffer key = null;
		for (int i = 0; i < list.size(); i++) 
		{
			property = (Element) list.get(i);
			
			if (exclude.containsKey(property.getName()) 
					&& exclude.get(property.getName()).contains(property.getAttributeValue("class") + "/" + property.getAttributeValue("name")))
				continue;
			
			key = new StringBuffer(path);
			key.append(PATH_SEPARATOR).append(property.getAttributeValue("class"));
			key.append(PATH_SEPARATOR).append(property.getAttributeValue("name"));
			
			results.put(key.toString(), property.getText());
		}
	}
	
	/**
	 * Flattens the readerlocationinfo section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <venue name="iwona" time="2013-05-01T22:22:16.298">
	 * 		<configuration name="current" type="full">
	 * 			<readerlocationinfo>
	 * 				<readerlocation>
	 * 					<locationid/>
	 * 					<name>UNKNOWN</name>
	 * 					<id>0</id>
	 * 					...
	 * 					<readers>
	 * 						...
	 * 					</readers>
	 * 			</readerlocation>
	 * 		</configuration>
	 * </venue>
	 * 
	 * @param rootNode the <CODE>properties</CODE> xml element.
	 */
	private static void flattenReaderLocationInfo(Element readerlocationinfo, Map<String, String> results)
	{
		if (readerlocationinfo == null)
		{
			logger.warn("Element 'readerlocationinfo' not found in the xml provided.");
			return;
		}
		
		final List list = readerlocationinfo.getChildren("readerlocation");
		final String path = readerlocationinfo.getParentElement().getName() + PATH_SEPARATOR + readerlocationinfo.getName();

		for (int i = 0; i < list.size(); i++) 
		{
			flattenReaderLocation((Element) list.get(i), results, path);
		}
	}
	
	/**
	 * Flattens the griditems section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <venue name="iwona" time="2013-05-01T22:22:16.298">
	 * 		<configuration name="current" type="full">
	 * 			<griditems>
	 * 				<griditem id="1" type="Gate" xgrid="1" ygrid="5">
	 * 					<state>HASENTERED</state>
	 * 					<label>Entry</label>
	 * 					<description>Entry for non-XPass guests</description>
	 * 					<image/>
	 * 					<sequence>0</sequence>
	 * 					<gueststoshow>2</gueststoshow>
	 * 					<locationid>0</locationid>
	 * 				</griditem>
	 * 			<griditems>
	 * 		</configuration>
	 * </venue>
	 * 
	 * @param rootNode the <CODE>properties</CODE> xml element.
	 */
	private static void flattenGridItems(Element griditems, Map<String, String> results)
	{
		if (griditems == null)
		{
			logger.warn("Element 'griditems' not found in the xml provided.");
			return;
		}
		
		final List list = griditems.getChildren("griditem");
		final String path = griditems.getParentElement().getName() + PATH_SEPARATOR + griditems.getName() + PATH_SEPARATOR;

		Element gridItem = null;
		List gridItemElements = null;
		List gridItemAttributes = null;
		String gridItemId = null;
		StringBuffer gridItemPath = null;;
		Attribute gridItemAttribute = null;
		Element gridItemElement = null;
		
		for (int i = 0; i < list.size(); i++) 
		{
			gridItemPath = new StringBuffer(path);
			
			gridItem = (Element)list.get(i);
			gridItemId = gridItem.getAttributeValue("id");
			gridItemElements = gridItem.getChildren();
			gridItemAttributes = gridItem.getAttributes();
			
			gridItemPath.append(gridItem.getName()).append(PATH_SEPARATOR)
					.append("id").append(PATH_SEPARATOR)
					.append(gridItemId).append(PATH_SEPARATOR);
			
			for (int j = 0; j < gridItemAttributes.size(); j++)
			{
				gridItemAttribute = (Attribute)gridItemAttributes.get(j);
				
				if (exclude.containsKey(gridItem.getName()) && exclude.get(gridItem.getName()).contains(gridItemAttribute.getName()))
					continue;
				
				results.put(gridItemPath.toString() + gridItemAttribute.getName(), gridItemAttribute.getValue());
			}
			
			for (int j = 0; j < gridItemElements.size(); j++)
			{
				gridItemElement = (Element)gridItemElements.get(j);
				
				if (exclude.containsKey(gridItem.getName()) && exclude.get(gridItem.getName()).contains(gridItemElement.getName()))
					continue;
				
				results.put(gridItemPath.toString() + gridItemElement.getName(), gridItemElement.getText());
			}
		}
	}
	
	private static void flattenReaderLocation(Element readerlocation, Map<String, String> results, String path)
	{
		if (readerlocation == null){
			logger.info("Element 'readerlocation' not found in the xml provided.");
			return;
		}
		
		final List list = readerlocation.getChildren();
		
		// get id of this readerlocation element to make its key unique
		Element idElement = readerlocation.getChild("id");
		if (idElement == null)
			return;
		String id = idElement.getText();
		
		// flatten all the children
		StringBuffer key = null;
		Element child = null;
		for (int i = 0; i < list.size(); i++)
		{
			child = (Element)list.get(i);
			
			if (exclude.containsKey(readerlocation.getName()) && exclude.get(readerlocation.getName()).contains(child.getName()))
				continue;
			
			key = new StringBuffer(path);
			key.append(PATH_SEPARATOR).append(readerlocation.getName()).append(PATH_SEPARATOR).append(idElement.getName())
				.append(PATH_SEPARATOR).append(id).append(PATH_SEPARATOR).append(child.getName());
			
			if (child.getChildren().size() == 0)
			{
				results.put(key.toString(), child.getText());
			}
			else
			{
				if (child.getName().equals("transmitCommands"))
				{
					flattenBandTransmitCommands(child, results, key.toString());
				} 
				else if (child.getName().equals("readers"))
				{
					List readers = child.getChildren();
					for (int j = 0; j < readers.size(); j++)
					{
						Element reader = (Element) readers.get(j);
								
						flattenReader((Element) readers.get(j), results, key.toString() + PATH_SEPARATOR + reader.getName());
					}
				}
			}
		}
	}
	
	private static void flattenReader(Element reader, Map<String, String> results, String path)
	{
		if (reader == null){
			logger.info("Element 'reader' not found in the xml provided.");
			return;
		}
		
		final List list = reader.getChildren();
		
		// get id of this reader element to make its key unique
		Element idElement = reader.getChild("id");
		if (idElement == null)
			return;
		String id = idElement.getText();
		
		// flatten all the children
		StringBuffer key = null;
		Element child = null;
		for (int i = 0; i < list.size(); i++)
		{
			key = new StringBuffer(path);
			
			child = (Element)list.get(i);
			
			if (child.getChildren().size() == 0)
			{
				if (exclude.containsKey(reader.getName()) && exclude.get(reader.getName()).contains(child.getName()))
					continue;
				
				key.append(PATH_SEPARATOR).append(idElement.getName())
					.append(PATH_SEPARATOR).append(id)
					.append(PATH_SEPARATOR).append(child.getName());
				
				results.put(key.toString(), child.getText());
			}
			else
			{
				key.append(PATH_SEPARATOR).append(idElement.getName())
					.append(PATH_SEPARATOR).append(id);
				
				if (child.getName().equals("antennas"))
					flattenAntennas(child, results, key.toString());
				else
					logger.warn("No audit handler present for the /currentconfiguration xml element: " + child.getName());
			}
		}
	}
	
	/**
	 * Flattens the transmitCommands section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <reader>
	 * 		<transmitCommands>
	 * 			<transmitCommand>
	 * 				<command>FAST_PING</command>
	 * 				<enableThreshold>false</enableThreshold>
	 * 				<interval>1000</interval>
	 * 				<mode>REPLY</mode>
	 * 				<recipients>
	 * 					<recipientLocationId>1</recipientLocationId>
	 * 					<recipientLocationId>3</recipientLocationId>
	 * 				</recipients>
	 * 				<threshold>-90</threshold>
	 * 				<timeout>30000</timeout>
	 * 				<timeout>locationId</timeout>
	 * 			</transmitCommand>
	 * 		</transmitCommands>
	 * </reader>
	 * 
	 * @param xbandCommands the <CODE>transmitCommands</CODE> xml element.
	 * @param results a map of resulting key/value pairs
	 * @param path a chain of xml elements separated by '/' leading up to 
	 * 			and including the <CODE>reader</CODE> element followed by its id
	 */
	private static void flattenBandTransmitCommands(Element xbandCommands, Map<String, String> results, String path)
	{
		if (xbandCommands == null){
			logger.info("Element 'xbandCommands' not found in the xml provided.");
			return;
		}
		
		final List list = xbandCommands.getChildren();
		final String childPath = path + PATH_SEPARATOR;
		
		// flatten all the children
		Element child = null;
		Element recipients = null;
		StringBuffer key = null;
		List locations = null;
		Element location = null;
		StringBuffer value = null;
		boolean sstEnabled = false;
		for (int i = 0; i < list.size(); i++)
		{
			child = (Element)list.get(i);
			
			/*
			 * Readers don't assign unique identifiers to xBand commands. Therefore,
			 * all information about a command is needed to create a unique key.
			 * 
			 * When PUT /currentconfiguration updates xBand commands, it simply
			 * deletes all the old commands and persists them all anew.
			 */
			
			// command parameters form the key
			key = new StringBuffer(childPath);
			key.append(child.getName()).append(PATH_SEPARATOR)
				.append("locationId").append(PATH_SEPARATOR).append(child.getChild("locationId").getText()).append(PATH_SEPARATOR)
				.append(child.getChild("mode").getText()).append(PATH_SEPARATOR)
				.append(child.getChild("command").getText()).append(PATH_SEPARATOR)
				.append("timeout").append(PATH_SEPARATOR).append(child.getChildText("timeout")).append(PATH_SEPARATOR)
				.append("interval").append(PATH_SEPARATOR).append(child.getChildText("interval"));
			
			sstEnabled = Boolean.parseBoolean(child.getChildText("enableThreshold"));
			if (sstEnabled)
				key.append(PATH_SEPARATOR).append("threshold").append(PATH_SEPARATOR).append(child.getChildText("threshold"));
			
			// recipients (either signal strength threshold or a list of location IDs) form the value
			recipients = (Element)child.getChild("recipients");
			if (recipients != null)
			{
				locations = recipients.getChildren();
				value = new StringBuffer();
				for (int j = 0; j < locations.size(); j++)
				{
					location = (Element)locations.get(j);
					if (value.length() == 0)
						value.append(PATH_SEPARATOR).append("locationIDs").append(PATH_SEPARATOR).append(location.getText());
					else
						value.append(",").append(location.getText());
				}
				key.append(value);
			}
					
			results.put(key.toString(), "");
			
			key = value = null;
		}
	}
	
	/**
	 * Flattens the antennas section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <reader>
	 * 		<antennas type="array">
	 * 			<value>true</value>
	 * 			<value>true</value>
	 * 			<value>true</value>
	 * 			<value>true</value>
	 * 			<value>true</value>
	 * 			<value>true</value>
	 * 			<value>true</value>
	 * 			<value>true</value>
	 * 		</antennas>
	 * </reader>
	 * 
	 * @param antennas the <CODE>antennas</CODE> xml element.
	 * @param results a map of resulting key/value pairs
	 * @param path a chain of xml elements separated by '/' leading up to 
	 * 			and including the <CODE>reader</CODE> element followed by its id
	 */
	private static void flattenAntennas(Element antennas, Map<String, String> results, String path)
	{
		if (antennas == null){
			logger.info("Element 'antennas' not found in the xml provided.");
			return;
		}
		
		final List list = antennas.getChildren();
		final String childPath = path + PATH_SEPARATOR + antennas.getName() + PATH_SEPARATOR;
		
		// flatten all the children
		Element child = null;
		for (int i = 0; i < list.size(); i++)
		{
			child = (Element)list.get(i);
			
			/*
			 *  <antennas> have multiple identical <value>true</value> elements. 
			 *  There is no choice but to assume that they always come in the same 
			 *  order since they are not labeled with unique IDs. 
			 *  Using index as a unique identifier.
			 */
			results.put(childPath + child.getName() + PATH_SEPARATOR + i, child.getText());
		}
	}
	
	/**
	 * Flattens the images section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <venue name="xBRC" time="2013-05-01T22:22:16.298">
	 * 		<configuration name="current" type="full">
	 * 			<images>
	 * 				<image>
	 * 					<blob></blob>
	 * 					<description>Facility view background image</description>
	 * 					<filename>IMG3346742625711763044.png</filename>
	 * 					<height>0</height>
	 * 					<id>1</id>
	 * 					<title>Background image</title>
	 * 					<width>0</width>
	 * 				</image>
	 * 			</images>
	 * 		</configuration>
	 * </venue>
	 * 
	 * @param rootNode the <CODE>images</CODE> xml element.
	 * @param results
	 */
	private static void flattenImages(Element images, Map<String, String> results)
	{
		if (images == null)
		{
			logger.warn("Element 'images' not found in the xml provided.");
			return;
		}
		
		final List list = images.getChildren("image");
		final String path = images.getParentElement().getName() + PATH_SEPARATOR + images.getName() + PATH_SEPARATOR;

		Element image = null;
		List imageElements = null;
		String imageId = null;
		StringBuffer imagePath = null;;
		Element imageElement = null;
		
		for (int i = 0; i < list.size(); i++) 
		{
			imagePath = new StringBuffer(path);
			
			image = (Element)list.get(i);
			imageId = image.getChildText("filename");
			imageElements = image.getChildren();
			
			imagePath.append(image.getName()).append(PATH_SEPARATOR).
				append(imageId).append(PATH_SEPARATOR);
			
			for (int j = 0; j < imageElements.size(); j++)
			{
				imageElement = (Element)imageElements.get(j);
				
				if (exclude.containsKey(image.getName()) && exclude.get(image.getName()).contains(imageElement.getName()))
					continue;
				
				results.put(imagePath.toString() + imageElement.getName(), imageElement.getText());
			}
		}
	}
	
	/**
	 * Flattens the schedulerSettings section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <schedulerSettings>
	 * 		<schedulerItems>
	 * 			<schedulerItem>
	 * 				<parameters>
	 * 					<parameter>
	 * 					</parameter>
	 * 				</parameters>
	 * 			</schedulerItem>
	 * 		</schedulerItems>
	 * </schedulerSettings>
	 * 
	 * @param schedulerSettings the <CODE>schedulerSettings</CODE> xml element.
	 */
	private static void flattenSchedulerSettings(Element schedulerSettings, Map<String, String> results)
	{
		if (schedulerSettings == null)
		{
			logger.warn("Element 'schedulerSettings' not found in the xml provided.");
			return;
		}
		
		final Element schedulerItems = schedulerSettings.getChild("schedulerItems");
		if (schedulerItems == null)
		{
			logger.warn("Element 'schedulerItems' not found in the xml provided.");
			return;
		}
		
		final List list = schedulerItems.getChildren("schedulerItem");
		final String path = schedulerSettings.getParentElement().getName() + PATH_SEPARATOR + schedulerSettings.getName() + 
					PATH_SEPARATOR + schedulerItems.getName();

		for (int i = 0; i < list.size(); i++) 
		{
			flattenSchedulerItem((Element) list.get(i), results, path);
		}
	}
	
	private static void flattenSchedulerItem(Element schedulerItem, Map<String, String> results, String path)
	{
		if (schedulerItem == null){
			logger.info("Element 'schedulerItem' not found in the xml provided.");
			return;
		}
		
		final List list = schedulerItem.getChildren();
		path += PATH_SEPARATOR + schedulerItem.getName();
		
		// get id of this reader element to make its key unique
		Element idElement = schedulerItem.getChild("itemKey");
		if (idElement == null)
			return;
		String id = idElement.getText();
		
		// flatten all the children
		StringBuffer key = null;
		Element child = null;
		for (int i = 0; i < list.size(); i++)
		{
			key = new StringBuffer(path);
			
			child = (Element)list.get(i);
			
			if (child.getChildren().size() == 0)
			{
				if (exclude.containsKey(schedulerItem.getName()) && exclude.get(schedulerItem.getName()).contains(child.getName()))
					continue;
				
				key.append(PATH_SEPARATOR).append(idElement.getName())
					.append(PATH_SEPARATOR).append(id)
					.append(PATH_SEPARATOR).append(child.getName());
				
				results.put(key.toString(), child.getText());
			}
			else
			{
				key.append(PATH_SEPARATOR).append(idElement.getName())
					.append(PATH_SEPARATOR).append(id);
				
				if (child.getName().equals("parameters"))
					flattenSchedulerParameters(child, results, key.toString());
				else
					logger.warn("No audit handler present for the /currentconfiguration xml element: " + child.getName());
			}
		}
	}
	
	private static void flattenSchedulerParameters(Element parameters, Map<String, String> results, String path)
	{
		if (parameters == null){
			logger.info("Element 'parameters' not found in the xml provided.");
			return;
		}
		
		final List list = parameters.getChildren();
		final String childPath = path + PATH_SEPARATOR + parameters.getName() + PATH_SEPARATOR;
		
		// flatten all the children
		Element child = null;
		for (int i = 0; i < list.size(); i++)
		{
			child = (Element)list.get(i);
			
			flattenSchedulerParameter(child, results, childPath + child.getName());
		}
	}
	
	private static void flattenSchedulerParameter(Element parameter, Map<String, String> results, String path)
	{
		if (parameter == null){
			logger.info("Element 'parameter' not found in the xml provided.");
			return;
		}
		
		final List list = parameter.getChildren();
		
		// get id of this reader element to make its key unique
		Element idElement = parameter.getChild("name");
		if (idElement == null)
			return;
		String id = idElement.getText();
		
		// flatten all the children
		StringBuffer key = null;
		Element child = null;
		for (int i = 0; i < list.size(); i++)
		{
			key = new StringBuffer(path);
			
			child = (Element)list.get(i);
			
			if (child.getChildren().size() == 0)
			{
				if (exclude.containsKey(parameter.getName()) && exclude.get(parameter.getName()).contains(child.getName()))
					continue;
				
				key.append(PATH_SEPARATOR).append(idElement.getName())
					.append(PATH_SEPARATOR).append(id)
					.append(PATH_SEPARATOR).append(child.getName());
				
				results.put(key.toString(), child.getText());
			}
			else
			{
				key.append(PATH_SEPARATOR).append(idElement.getName())
					.append(PATH_SEPARATOR).append(id);
				
				logger.warn("No audit handler present for the schedulerSettings/schedulerItem/parameters/parameter xml element: " + child.getName());
			}
		}
	}
	
	public static void main(String[] args) {
        if(args.length < 5) {
            System.out.println("This diffing tool takes four parameters:");
            System.out.println("1. Full path to the first file containing the output of /currentconfiguration REST endpoint.");
            System.out.println("2. Full path to the second file containing the output of /currentconfiguration REST endpoint.");
            System.out.println("3. Full path to the csv file that will contain the output of the diff.");
            System.out.println("4. Model you'd run the /currentconfiguration on. Choose from: ");
            System.out.println("\tcom.disney.xband.xbrc.attractionmodel.CEP");
            System.out.println("\tcom.disney.xband.xbrc.parkentrymodel.CEP");
            System.out.println("\tcom.disney.xband.xbrc.spacemodel.CEP");
            System.out.println("5. True or false value indicating whether the output should include a header row.");
            System.out.println();
            System.out.println("The first two xml files will be compared and the differences betwen their content will be written to the third file.");
            System.out.println();
            System.out.println("NOTE: If the output file already exists it will be overwritten.");
            System.out.println();
            
            System.exit(1);
        }
        
        System.out.println("\nStarting....");
        
        InputStream previous = null;
        InputStream current = null;
        try
        {
        	File first = new File(args[0]);

        	try
        	{
        		System.out.println("Reading in file " + args[0] + "...");
        		previous = new FileInputStream(args[0]);
        	}
        	catch (FileNotFoundException e)
        	{
        		System.out.println("ERROR: File not found: " + first);
        		System.exit(1);
        	}

        	File second = new File(args[1]);

        	try
        	{
        		System.out.println("Reading in file " + args[1] + "...");
        		current = new FileInputStream(args[1]);
        	}
        	catch (FileNotFoundException e)
        	{
        		System.out.println("ERROR: File not found: " + second);
        		System.exit(1);
        	}
        	
        	File output = new File(args[2]);
            
            boolean printColumnNames = Boolean.parseBoolean(args[4]);
            
            System.out.println("Initializing xBRC model " + args[3] + "...");
            // Since we are running this from outside of xBRC, we need to trick it a bit.
            // WARNING! This is a hack intended for testing only. DON'T DUPLICATE!!!
    		IXBRCModel model = (IXBRCModel) Class.forName(args[3]).newInstance();
    		model.readConfiguration();
    		model.initialize();
    		Field privateModel = Processor.class.getDeclaredField("model");
    		privateModel.setAccessible(true);
    		privateModel.set(Processor.INSTANCE, model);
    		
    		System.out.println("Running the diffing tool...");
            // diff the files
            LinkedList<CurrentConfigDiffItem> result = diff(previous, current);
            
            // construct results and write them to the output file provided
            if (output.exists())
            {
            	System.out.println("Deleting old output file " + args[2]);
            	output.delete();
            }
    		
    		try
    		{
    			System.out.println("Creating output file " + args[2]);
    			output.createNewFile();
    		}
    		catch (IOException e)
    		{
    			System.out.println("ERROR: Failed to create the " + output);
    			System.exit(1);
    		}
    		
    		System.out.println("Writting the results to the output file...");
            prettyPrint(result, output, printColumnNames);
            
            System.out.println("Finished. You'll find the results in " + args[2]);
            System.exit(0);
        }
		catch (Exception e)
		{
			System.out.println("ERROR: Failed to instantiate a model object for " + args[4]);
			System.exit(1);
		}
        finally
        {
        	if (previous != null)
        	{
        		try 
        		{
        			previous.close();
        		}
        		catch (Exception e)
        		{
        			System.out.println("ERROR: Failed to close input stream for " + args[0]);
        		}
        	}
        	
        	if (current != null)
        	{
        		try 
        		{
        			current.close();
        		}
        		catch (Exception e)
        		{
        			System.out.println("ERROR: Failed to close input stream for " + args[0]);
        		}
        	}
        }
    }
}
