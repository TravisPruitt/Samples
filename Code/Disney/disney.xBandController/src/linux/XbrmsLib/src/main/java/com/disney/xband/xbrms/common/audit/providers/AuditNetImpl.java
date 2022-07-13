package com.disney.xband.xbrms.common.audit.providers;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 4/30/13
 * Time: 4:06 PM
 */

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.audit.AuditBase;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.xbrms.common.SslUtils;
import com.disney.xband.xbrms.common.XbrmsUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class pushes audit events to an event collector.
 */
public class AuditNetImpl extends AuditBase {
    private String ip;
    private int port;
    private String path;

    public AuditNetImpl(final AuditConfig conf) {
        super(conf);

        if (XbrmsUtils.isEmpty(conf.getCollectorUrl())) {
            this.logger.error("AuditNetImpl requires collectorUrl property to be set");
            throw new RuntimeException("Initialization of AuditNetImpl failed.");
        }

        try {
            final URI uri = new URI(conf.getCollectorUrl());
            this.ip = uri.getHost();
            this.port = uri.getPort();
            this.path = uri.getRawPath();
        }
        catch (Exception e) {
            this.logger.error("AuditConfig: collectorUrl property has invalid value");
            throw new RuntimeException("Initialization of AuditNetImpl failed.");
        }
    }

    @Override
    public boolean audit(final AuditEvent event) {
        if (event == null) {
            return false;
        }

        BufferedWriter bw = null;

        try {
            final AuditEventList elist = new AuditEventList();
            final List<AuditEvent> list = new ArrayList<AuditEvent>(1);
            list.add(event);
            elist.setEvents(list);
            String xml = XmlUtil.convertToXml(elist, AuditEventList.class);
            HttpURLConnection conn = SslUtils.getConnection(this.ip, this.port, this.path);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-type", "application/xml");
            conn.setRequestProperty("Content-length", Integer.toString(xml.length()));
            bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bw.write(xml);
            bw.flush();

            if (conn.getResponseCode() >= 400) {
                logger.error("Failed to send audit event: " + conn.getResponseCode() + " received (" + conn.getURL().toString() + ")");
                return false;
            }
        }
        catch (Exception e) {
            logger.error("Failed to send audit event: " + e.getMessage());
            return false;
        }
        finally {
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (IOException e) {
                }
            }
        }

        return true;
    }
    
    @Override
    public boolean audit(final Collection<AuditEvent> events)
    {
    	if (events == null || events.size() == 0)
    		return false;
    	
    	for (AuditEvent event: events)
    	{
    		audit(event);
    	}

        return true;
    }
}
