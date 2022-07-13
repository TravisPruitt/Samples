package com.disney.xband.xbrms.server.managed;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.xbrms.common.SslUtils;
import com.disney.xband.xbrms.common.model.*;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import org.apache.log4j.Logger;
import org.htmlparser.filters.StringFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/30/13
 * Time: 5:00 PM
 */
public class XbrmsService {
    private static Logger logger = Logger.getLogger(XbrmsService.class);

    /*
    // This code is for future
    public XbrmsStatusDto getStatus(XbrmsHiDto xbrms, XbrmsDto dto)
    {
        InputStream is = null;

        try {
            final URI  url = new URI(dto.getUrl());

            if (url == null) {
                return null;
            }

            is = makeRestfullGETRequest(url.getHost(), url.getPort(), "XBRMS/rest/status/info");
            final DtoWrapper<XbrmsStatusDto> statusWrap = XmlUtil.convertToPojo(is, DtoWrapper.class);
            final XbrmsStatusDto status = statusWrap.getContent();

            return status;
        }
        catch (Exception e) {
            logger.warn("Request " + dto.getUrl() + "/status/info produced no response: " + e.getMessage());
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException e) {
            }
        }

        return null;
    }
    */

    public AuditEventList getEvents(URI url, long afterEventId) throws Exception {
        if (url == null) {
            return new AuditEventList();
        }

        InputStream is = null;

        try {
            is = makeRestfullGETRequest(url.getHost(), url.getPort(), "XBRMS/rest/audit/pull/" + afterEventId);
            final DtoWrapper<AuditEventList> eventsWrap = XmlUtil.convertToPojo(is, DtoWrapper.class);
            final AuditEventList events = eventsWrap.getContent();
            return events;
        }
        catch (Exception e) {
            logger.warn("Request " + url.getHost() + ":" + url.getPort() + "/XBRMS/rest/audit/pull/" + afterEventId + " produced no response");
            throw e;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException e) {
            }
        }
    }

    public void deleteEvents(URI url, long upToEventId) throws Exception {
        if (url == null) {
            return;
        }

        try {
            int code = makeRestfullDeleteRequest(url.getHost(), url.getPort(), "XBRMS/rest/audit/delete/" + upToEventId);

            if ((code < 200) || (code >= 400)) {
                throw new RuntimeException();
            }
        }
        catch (Exception e) {
            logger.warn("Request " + url.getHost() + ":" + url.getPort() + "/XBRMS/rest/audit/delete/" + upToEventId + " failed");
            throw e;
        }
    }

    public InputStream makeRestfullGETRequest(String ip, int port, String resourcePath) throws IOException {
        HttpURLConnection conn = SslUtils.getConnection(ip, port, resourcePath);
        conn.setConnectTimeout(XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec());
        conn.setReadTimeout(XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec());
        conn.setRequestProperty("Accept", "application/xml");

        logger.info("Executing a call to " + conn.getURL().toString());

        final int responseCode = conn.getResponseCode();

        if (responseCode < 0 || responseCode >= 400) {
            logger.warn("Error: " + conn.getResponseCode() + " received from (" + conn.getURL().toString() + ")");
            throw new RuntimeException();
        }

        return conn.getInputStream();
    }

    public int makeRestfullDeleteRequest(String ip, int port, String resourcePath) throws IOException {
        OutputStreamWriter wr = null;
        OutputStream os = null;

        try {
            HttpURLConnection conn = SslUtils.getConnection(ip, port, resourcePath);

            logger.debug("Executing a call to " + conn.getURL().toString());

            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec());
            conn.setReadTimeout(XbrmsConfigBo.getInstance().getDto().getHttpConnectionTimeout_msec());

            int responseCode = conn.getResponseCode();

            if (responseCode < 0 || responseCode >= 400) {
                logger.warn("Error: " + conn.getResponseCode() + " received from Controller (" + conn.getURL().toString() + ")");
                throw new RuntimeException();
            }

            return responseCode;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException e) {
                }
            }

            if (wr != null) {
                try {
                    wr.close();
                }
                catch (IOException e) {
                }
            }
        }
    }
}
