package com.disney.xband.xbrms.server.managed;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.*;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.SslUtils;
import com.disney.xband.xbrms.common.model.XbrcConfigListDto;
import com.disney.xband.xbrms.common.model.XbrcDto;

import com.disney.xband.xbrms.server.SystemHealthConsumer;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.model.XbrcModel;

public class XbrcService {
	
	private static Logger logger = Logger.getLogger(XbrcService.class);
    private static volatile HashMap<String, String> ioExceptionMap = new HashMap<String, String>();
    private static volatile long lastIoExceptionReset = System.currentTimeMillis();
    private static final long IO_RESET_SECS = 15 * 60 * 1000; // 15 minutes
	
	public Collection<XbrcDto> findByModel(XbrcModel model)
	{
		Collection<XbrcDto> all = SystemHealthConsumer.getInstance().getInventory(XbrcDto.class);
		Collection<XbrcDto> ret = new LinkedList<XbrcDto>();
		Iterator<XbrcDto> it = all.iterator();
		while (it.hasNext()) {
			XbrcDto xbrcDto = it.next();
			if (xbrcDto.getModel() != null && XbrcModel.getByModel(xbrcDto.getModel()) == model)
				ret.add(xbrcDto);
		}
		return ret;
	}
	
	public ReaderLocationInfo getReaderLocationInfo(XbrcDto xbrcDto) throws Exception {
		if (xbrcDto == null)
			return null;
		
		return getReaderLocationInfo(xbrcDto.getIp(), xbrcDto.getPort());
	}
	
	public ReaderLocationInfo getReaderLocationInfo(String ip, int port) throws Exception {
		if (ip == null || ip.trim().isEmpty())
			return null;
		
		InputStream is = null;
		
		try {
			// Retrieve reader & location info from the controller
			is = makeRestfullGETRequest(
					ip, 
					port, 
					"readerlocationinfo",
					XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec(),
					XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec()
			);
			
			if (is == null){
				logger.warn("Request " + ip + ":" + port + "/readerlocationinfo" + " produced no response");
				return null;
			}
			
			ReaderLocationInfo info = XmlUtil.convertToPojo(is, ReaderLocationInfo.class);
			
			return info;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e){
				logger.warn("Input stream failed to close. Possible memory leak.");
			}
		}
	}

    public AuditEventList getEvents(XbrcDto xbrcDto, long afterEventId) throws Exception {
        if (xbrcDto == null) {
            return new AuditEventList();
        }

        InputStream is = null;

        try {
            is = makeRestfullGETRequest(
            		xbrcDto.getIp(), 
            		xbrcDto.getPort(), 
            		"getauditevents/" + afterEventId,
            		XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec(),
            		XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec()
            );

            if (is == null) {
                logger.warn("Request " + xbrcDto.getIp() + ":" + xbrcDto.getPort() + "/getauditevents/" + afterEventId + " produced no response");
                return null;
            }

            AuditEventList events = XmlUtil.convertToPojo(is, AuditEventList.class);

            return events;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException e) {
                logger.warn("Input stream failed to close. Possible memory leak.");
            }
        }
    }

    public void deleteEvents(XbrcDto xbrcDto, long upToEventId) throws Exception {
        if (xbrcDto == null) {
            return;
        }

        try {
            int code = makeRestfullDeleteRequest(xbrcDto.getIp(), xbrcDto.getPort(), "deleteauditevents/" + upToEventId);

            if((code < 200) || (code >= 400)) {
                throw new RuntimeException();
            }
        }
        catch(Exception e) {
            logger.warn("Request " + xbrcDto.getIp() + ":" + xbrcDto.getPort() + "/deleteauditevents/" + upToEventId + " failed");
        }
    }

    public int replaceReader(XbrcDto xbrc, String oldReaderName, String newReader) throws Exception {
        final String ip;
        Integer port;

        if(XbrmsUtils.isEmpty(xbrc.getVip()) || (xbrc.getVip() != null && xbrc.getVip().startsWith("#"))) {
            ip = xbrc.getIp();
            port = xbrc.getPort();
        }
        else {
            ip = xbrc.getVip();
            port = xbrc.getVport();

            if((port == null) || (port == 0)) {
                port = xbrc.getPort();
            }
        }

        if((port == null) || (port == 0)) {
            port = new Integer(8080);
        }

        // make the call, wait for the results as long as it takes
        return makeRestfullPutRequest(ip, port, "replacereader/" + oldReaderName, newReader, 0, 0);
    }
	
	public Map<String, XbrcConfigListDto> getProperties(Collection<XbrcDto> xbrcDtos){
		
		Map<String, Collection<XbrcConfig>> configMap = new HashMap<String, Collection<XbrcConfig>>();
		XbrcModel model = null;
		InputStream is = null;

		Collection<XbrcConfig> xbrcConfig = null;
		for (XbrcDto xbrcDto : xbrcDtos)
		{
            if("true".equalsIgnoreCase(xbrcDto.getHaEnabled()) && (HAStatusEnum.slave.name().equalsIgnoreCase(xbrcDto.getHaStatus()))) {
                continue;
            }

			if (xbrcDto.getIp() == null || xbrcDto.getPort() == null){
				logger.error("Need ip and port to make a GET status request to attraction: " + xbrcDto.getName());
				continue;
			}
			
			if (xbrcDto.getModel() == null || xbrcDto.getModel().isEmpty()){
				logger.error("Model not specified on ControllerInfo for attraction: " + xbrcDto.getName());
				continue;
			}
			
			model = XbrcModel.getByModel(xbrcDto.getModel());
			if (model == null){
				logger.error("Unrecognized model: " + xbrcDto.getModel());
				continue;
			}
			
			try {
				// get status info from the xbrcDto
				is = makeRestfullGETRequest(
						xbrcDto.getIp(), 
						xbrcDto.getPort(), 
						"properties",
						XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec(),
						XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec()
				);
				
				if (is == null){
					logger.warn("Request " + xbrcDto.getIp() + ":" + xbrcDto.getPort() + "/properties" + " produced no response");
					continue;
				}
				
				XbrcConfig config = XmlUtil.convertToPojo(is, XbrcConfig.class);
				if (config != null){

					xbrcConfig = configMap.get(model.name());
					if (config.getIp() != null && !config.getIp().equals(xbrcDto.getIp()))
						config.setIp(xbrcDto.getIp());
					
					if (xbrcConfig == null){
						xbrcConfig = new LinkedList<XbrcConfig>();
						configMap.put(model.name(), xbrcConfig);
					}

					xbrcConfig.add(config);
				}
				
			} catch (JAXBException e){
				logger.error("Failed to unmarshal result.", e);
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e){
					logger.warn("Input stream failed to close. Possible memory leak.");
				}
			}
		}

        if(configMap != null) {
            final Map<String, XbrcConfigListDto> map = new HashMap<String, XbrcConfigListDto>();

            for(String key : configMap.keySet()) {
                final XbrcConfigListDto item = new XbrcConfigListDto();
                item.setXbrcConfiguration(new ArrayList<XbrcConfig>(configMap.get(key)));
                map.put(key, item);
            }

            return map;
        }
		else {
		    return new HashMap<String, XbrcConfigListDto>();
        }
	}
	
	public XbrcConfig getProperties(XbrcDto xbrcDto){
		
		if (xbrcDto.getIp() == null || xbrcDto.getPort() == null){
			logger.error("Need ip and port to make a GET status request to attraction: " + xbrcDto.getName());
			return null;
		}
			
		if (xbrcDto.getModel() == null || xbrcDto.getModel().isEmpty()){
			logger.error("Model not specified on ControllerInfo for attraction: " + xbrcDto.getName());
			return null;
		}
			
		XbrcModel model = XbrcModel.getByModel(xbrcDto.getModel());
		if (model == null){
			logger.error("Unrecognized model: " + xbrcDto.getModel());
			return null;
		}
			
		InputStream is = null;
		try {
			// get status info from the xbrcDto
			is = makeRestfullGETRequest(
					xbrcDto.getIp(), 
					xbrcDto.getPort(), 
					"properties",
					XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec(),
					XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec()
			);
			
			if (is == null){
				logger.warn("Request " + xbrcDto.getIp() + ":" + xbrcDto.getPort() + "/properties" + " produced no response");
				return null;
			}

			XbrcConfig config = XmlUtil.convertToPojo(is, XbrcConfig.class);

			return config;

		} catch (JAXBException e){
			logger.error("Failed to unmarshal result.", e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e){
				logger.warn("Input stream failed to close. Possible memory leak.");
			}
		}
		
		return null;
	}
	
	public boolean updateConfiguration(XbrcConfig xbrcConfig, String ip, Integer port) throws Exception {
		if (xbrcConfig == null || ip == null || ip.isEmpty() || port == null)
			return false; 
		
		BufferedWriter bw = null;
		try
		{
			
			String xml = XmlUtil.convertToXml(xbrcConfig, XbrcConfig.class);
			
			// establish PUT request
			HttpURLConnection conn = SslUtils.getConnection(ip, port, "properties");
            conn.setConnectTimeout(XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec());

			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-type", "application/xml");
			conn.setRequestProperty("Content-length", Integer.toString(xml.length()));
			
			// send
			bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			bw.write(xml);
			bw.flush();
			
			if (conn.getResponseCode()>=400)
			{
				logger.error("Error: " + conn.getResponseCode() + " received (" + conn.getURL().toString() + ")");
				return false;
			}
			
			return true;
		} 
		catch (MalformedURLException e)
		{
			logger.error("URL Error: " + e.getLocalizedMessage(), e);
		} 
		catch (IOException e)
		{
			logger.error("IO Error: " + e.getLocalizedMessage(), e);
		}
		catch(Exception e)
		{
			logger.error("Error: " + e.getLocalizedMessage(), e);
		}
		finally
		{
			if (bw != null)
			{
				try {
					bw.close();
				} catch (IOException e){
					logger.warn("Buffered writer failed to close. Possible memory leak.");
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @param resourcePath
	 * @param connTimeout millisecond value to be set on the http connection. If larger than zero, it will limit
	 * 				how long we wait to connect before giving up.
	 * @param readTimeout millisecond value to be set on the http connection. If larger than zero, it will limit
	 * 				how long we wait for the results before giving up and closing the connection.
	 * @return
	 * @throws IOException
	 */
	public InputStream makeRestfullGETRequest(String ip, int port, String resourcePath, int connTimeout, int readTimeout)
	{
		try
		{
			HttpURLConnection conn = SslUtils.getConnection(ip, port, resourcePath);
			
			if (connTimeout > 0)
				conn.setConnectTimeout(connTimeout);

			if (readTimeout > 0)
				conn.setReadTimeout(readTimeout);

			logger.info("Executing a call to " + conn.getURL().toString());
			
			int responseCode = conn.getResponseCode();
			if (responseCode < 0 || responseCode >= 400)
			{	
				logger.error("Error: " + conn.getResponseCode() + " received from Controller (" + conn.getURL().toString() + ")");
				return null;
			}
			return conn.getInputStream();
		} 
		catch (MalformedURLException e)
		{
			logger.error("URL Error:", e);
		} 
		catch (IOException e)
		{
            if((System.currentTimeMillis() - lastIoExceptionReset) > IO_RESET_SECS) {
                ioExceptionMap = new HashMap<String, String>();
                lastIoExceptionReset = System.currentTimeMillis();
            }

            final String key = ip + ":" + port + "/" + resourcePath;

            if(ioExceptionMap.get(key) == null) {
			    logger.error("IO Error: Failed to open HTTP connection to xbrc at " + key);
                ioExceptionMap.put(key, "");
            }
		}
		catch (Exception e)
		{
			logger.error("Error: ", e);
		} 
		
		return null;
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @param resourcePath
	 * @param payload
	 * @param connTimeout millisecond value to be set on the http connection. If larger than zero, it will limit
	 * 				how long we wait to connect before giving up.
	 * @param readTimeout millisecond value to be set on the http connection. If larger than zero, it will limit
	 * 				how long we wait for the results before giving up and closing the connection.
	 * @return
	 * @throws IOException
	 */
	public int makeRestfullPutRequest(String ip, int port, String resourcePath, String payload, int connTimeout, int readTimeout) throws IOException
	{
		OutputStreamWriter wr = null;
		OutputStream os = null;
		try
		{
			HttpURLConnection conn = SslUtils.getConnection(ip, port, resourcePath);
			
			logger.debug("Executing a call to " + conn.getURL().toString());
			
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");

			if (connTimeout > 0)
				conn.setConnectTimeout(connTimeout);

			if (readTimeout > 0)
				conn.setReadTimeout(readTimeout);

			os = conn.getOutputStream();
			
			wr = new OutputStreamWriter(os);
			wr.write(payload);
			wr.flush();
		
			int responseCode = conn.getResponseCode();
			
			if (responseCode < 0 || responseCode >= 400)
			{	
				String msg = "Error: " + conn.getResponseCode() + " received from Controller (" + conn.getURL().toString() + ")";
				logger.error(msg);
			}
			
			return responseCode;
		}
		finally
		{
			if (os != null)
			{
				try {
					os.close();
				} catch (IOException e) {
					logger.warn("Output stream failed to close. Possible memory leak.");
				}
			}
			
			if (wr != null)
			{
				try {
					wr.close();
				} catch (IOException e) {
					logger.warn("Output writer failed to close. Possible memory leak.");
				}
			}
		}
	}
	
    public int makeRestfullDeleteRequest(String ip, int port, String resourcePath) throws IOException
    {
        OutputStreamWriter wr = null;
        OutputStream os = null;
        try
        {
            HttpURLConnection conn = SslUtils.getConnection(ip, port, resourcePath);

            logger.debug("Executing a call to " + conn.getURL().toString());

            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec());
            conn.setReadTimeout(XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec());

            int responseCode = conn.getResponseCode();

            if (responseCode < 0 || responseCode >= 400)
            {
                String msg = "Error: " + conn.getResponseCode() + " received from Controller (" + conn.getURL().toString() + ")";
                logger.error(msg);
            }

            return responseCode;
        }
        finally
        {
            if (os != null)
            {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.warn("Output stream failed to close. Possible memory leak.");
                }
            }

            if (wr != null)
            {
                try {
                    wr.close();
                } catch (IOException e) {
                    logger.warn("Output writer failed to close. Possible memory leak.");
                }
            }
        }
    }

    public XbrcStatus getStatus(XbrcDto xbrc)
    {
        InputStream is = null;
        try
        {
            is = makeRestfullGETRequest(
            		xbrc.getIp(), 
            		xbrc.getPort(), 
            		"status", 
            		XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec(),
            		XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec()
            );
            
            if (is == null){
				logger.warn("Request " + xbrc.getIp() + ":" + xbrc.getPort() + "/status" + " produced no response");
                return null;
            }

            XbrcStatus status = XmlUtil.convertToPojo(is, XbrcStatus.class);
            return status;
        }
        catch (Exception e)
        {
            logger.error("Failed to get Xbrc status: " + e.getMessage());
        }
        finally
        {
            if (is != null)
            {
                try {
                    is.close();
                }
                catch (IOException e) {
                }
            }
        }

        return null;
    }

	public XbrcConfiguration getConfiguration(XbrcDto xbrcDto, String name) throws Exception
	{
		InputStream is = null;
		try
		{
			String url = "currentconfiguration";
			if (!name.equals("current"))
				url = "configuration/name/" + name;
			
            final int port = xbrcDto.getVport() <= 0 ? xbrcDto.getPort() : xbrcDto.getVport();
            final String ip = (xbrcDto.getVip() == null) || (xbrcDto.getVip().length() == 0)  || xbrcDto.getVip().startsWith("#") ? xbrcDto.getIp() : xbrcDto.getVip();

			is = makeRestfullGETRequest(
					ip,
					port, 
					url,
					XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec(),
					XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec()
			);
			
			if (is == null){
				logger.warn("Request " + ip + ":" + port + "/" + url + " produced no response");
				return null;
			}
			
			return parseConfiguration(XbrmsUtils.inputStreamToString(is));
		}
		finally
		{
			if (is != null)
			{
				try {
					is.close();
				} catch (IOException e) {
					logger.warn("Input strem failed to close. Possible memory leak.");
				}
			}
		}
	}
	
	public XbrcConfiguration parseConfiguration(String xml) throws Exception
	{
		XbrcConfiguration conf = new XbrcConfiguration();
				
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(xml)));

		// walk through the tree
		Element el = doc.getDocumentElement();

		if (el.getNodeName().compareTo("venue") != 0)
			throw new Exception(
					"invalid stored configuration: missing venue element");
		
		Node venueNameNode = el.getAttributes().getNamedItem("name");
		if (venueNameNode == null || venueNameNode.getNodeType() != Node.ATTRIBUTE_NODE)
			throw new Exception(
					"invalid stored configuration: missing venue name attribute");
		conf.setVenueCode(venueNameNode.getTextContent());

		NodeList nl = el.getElementsByTagName("configuration");
		if (nl == null || nl.getLength() != 1)
			throw new Exception(
					"invalid stored configuration: must have exactly one configuration element");
		Node nodeConf = nl.item(0);

		Node nodeAttr = nodeConf.getAttributes().getNamedItem("name");
		if (nodeAttr == null || nodeAttr.getNodeType() != Node.ATTRIBUTE_NODE)
			throw new Exception(
					"invalid stored configuration: improper configuration element");
		conf.setName(nodeAttr.getTextContent());
		
		/*
		if (sName!=null && sName.compareTo(nodeAttr.getTextContent()) != 0)
			throw new Exception(
					"invalid stored configuration: configuration name attribute does not match database key");
		nodeAttr = nodeConf.getAttributes().getNamedItem("type");
		if (nodeAttr == null || nodeAttr.getNodeType() != Node.ATTRIBUTE_NODE)
			throw new Exception(
					"invalid stored configuration: improper configuration element");
		*/

		// zap the current configuration if the type is "full"
		boolean bFull = false;
		if (nodeAttr.getTextContent().compareTo("full") == 0)
			bFull = true;
		
		// process child nodes
		nl = nodeConf.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n2 = nl.item(i);

			// process elements
			if (n2.getNodeType() == Node.ELEMENT_NODE)
			{
				// process the config table
				if (n2.getNodeName().compareTo("properties") == 0)
				{
					processProperties(conf, n2);
					break;
				}
			}
		}
		
		conf.setXml(xml);
		conf.setCreateTime(new Date());
		
		return conf;
	}
	
	private String processProperties(XbrcConfiguration conf, Node node) throws Exception
	{	
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n2 = nl.item(i);

			// process elements
			if (n2.getNodeType() == Node.ELEMENT_NODE)
			{
				// process the config table
				if (n2.getNodeName().compareTo("property") == 0)
				{
					Node na = n2.getAttributes().getNamedItem("name");
					if (na == null
							|| na.getNodeType() != Node.ATTRIBUTE_NODE)
						throw new Exception(
								"malformed property element in stored configuration");

					String sName = na.getTextContent();

					na = n2.getAttributes().getNamedItem("class");
					if (na == null
							|| na.getNodeType() != Node.ATTRIBUTE_NODE)
						throw new Exception(
								"malformed property element in stored configuration");

					String sClass = na.getTextContent();
					String sValue = n2.getTextContent();
					
					if (sClass.equals("ControllerInfo") && sName.equals("model"))
						conf.setModel(sValue);
					
					if (sClass.equals("ControllerInfo") && sName.equals("name"))
						conf.setVenueName(sValue);
				}
				else
					throw new Exception(
							"Unexpected node type in stored configuration: "
									+ n2.getNodeName());
			}
		}
		return null;
	}

	public void putConfiguration(XbrcConfiguration conf, XbrcDto xbrcDto) throws Exception
	{
        final URIBuilder builder = new URIBuilder();
        builder.setPath("configuration");
        builder.setParameter("name", conf.getName());
        builder.setParameter("description", conf.getDescription());
        String url = builder.build().toString();
		
        // make the request, wait for the response for as long as it takes
        if (makeRestfullPutRequest(xbrcDto.getIp(), xbrcDto.getPort(), url, conf.getXml(), 0, 0) >= 400) {
            throw new RuntimeException("REST call failed");
        }
	}
	
	public void putScheduleOverride(XbrcDto xbrc, Long hours) throws Exception {
        final String ip;
        Integer port;

        if(XbrmsUtils.isEmpty(xbrc.getVip()) || xbrc.getVip().startsWith("#")) {
            ip = xbrc.getIp();
            port = xbrc.getPort();
        }
        else {
            ip = xbrc.getVip();
            port = xbrc.getVport();

            if((port == null) || (port == 0)) {
                port = xbrc.getPort();
            }
        }

        if(port == null) {
            port = new Integer(8080);
        }

        if (hours > 0)
        {
        	Long minutes = hours * 60;
        	// make the request, wait for the response for as long as it takes
        	makeRestfullPutRequest(ip, port, "scheduleoverrideon?minutes=" + minutes, "", 0, 0);
        }
        else
        {
        	// make the request, wait for the response for as long as it takes
        	makeRestfullGETRequest(ip, port, "scheduleoverrideclear", 0, 0);
        }
    }
}
