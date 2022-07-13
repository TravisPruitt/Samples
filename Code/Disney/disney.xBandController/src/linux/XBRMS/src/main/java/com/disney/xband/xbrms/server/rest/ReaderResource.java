package com.disney.xband.xbrms.server.rest;

import java.io.InputStream;
import java.util.Date;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import com.disney.xband.xbrms.server.SystemHealthConsumer;
import net.sf.json.JSONObject;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 9/6/12
 * Time: 3:44 PM
 */
@Path("/hello")
public class ReaderResource extends ResourceBase {
    /*
    @GET
    public String handleReaderPutHello(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        try {
            if(true) {
                ReaderInfo myr = new ReaderInfo(1, "TestReader", ReaderType.getByType("Long Range"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:01", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                LocationInfo locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(2, "TestReader2", ReaderType.getByType("Long Range"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:02", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(3, "TestReader3", ReaderType.getByType("Long Range"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:03", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(8, "TestReader8", ReaderType.getByType("Tap"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:08", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(11, "TestReader11", ReaderType.getByType("Tap"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:11", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(12, "TestReader12", ReaderType.getByType("Tap"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:12", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(13, "TestReader13", ReaderType.getByType("Tap"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:13", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(14, "TestReader14", ReaderType.getByType("Tap"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:14", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(15, "TestReader15", ReaderType.getByType("Tap"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:15", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                myr = new ReaderInfo(16, "TestReader16", ReaderType.getByType("Tap"), "", "", 0, -90,
                        1.1, "00:00:00:00:00:16", "127.0.0.1", -1, new Date().getTime(),
                        true, 0, 0, 0, 0, "0.0.0.0", "0.0.0.0",
                        false, "", false, "Unknown");

                locInfo = new LocationInfo();
                locInfo.setId(-1);
                myr.setLocation(locInfo);

                XbrcDiscoveryConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(myr);

                return "";
            }
        }
        catch (Exception e) {

        }

        return "";
    }
    */

    //@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
    @PUT
    public void handleReaderPutHello(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        this.setAuditParams(request);

        try {
            final InputStream is = request.getInputStream();
            final String sPutData = new Scanner(is).useDelimiter("\\A").next();
            final String ipAddress = this.getThisHostIp(request);

            if(logger.isDebugEnabled()) {
                logger.debug("Got HELLO from reader at " + ipAddress);
            }

            if(logger.isTraceEnabled()) {
                logger.trace(sPutData);
            }

            JSONObject jo = null;

            try {
                jo = JSONObject.fromObject(sPutData);
            }
            catch (Exception ex) {
                logger.error(ExceptionFormatter.format("Error deserializing Hello message: " + sPutData, ex));
                response.sendError(500, ExceptionFormatter.format("Error deserializing Hello message.", ex));
                return;
            }

            final String sMac = jo.getString("mac");
            final int nPort = jo.getInt("port");
            final long lNextEno = jo.getLong("next eno");
            String sReaderName = jo.getString("reader name");
            final String sReaderType = jo.getString("reader type");
            final ReaderType rtype = ReaderType.getByType(sReaderType);
            String sReaderVersion = "0.0.0.0";

            if (jo.containsKey("reader version")) {
                sReaderVersion = jo.getString("reader version");
            }

            // The reader may include other information following the four digit
            // version number.
            // This information we should ignore.
            final int idxSpace = sReaderVersion.indexOf(' ');
            if (idxSpace > 0) {
                sReaderVersion = sReaderVersion.substring(0, idxSpace);
            }

            // truncate if too long
            if (sReaderVersion.length() > 32) {
                sReaderVersion = sReaderVersion.substring(0, 32);
            }

            String sMinXbrcVersion = "1.0.0.0";
            if (jo.containsKey("min xbrc version")) {
                sMinXbrcVersion = jo.getString("min xbrc version");
            }

            String sLinuxVersion = "0.0.0.0";
            if (jo.containsKey("linux version")) {
                sLinuxVersion = jo.getString("linux version");
            }

            String sHardwareType = "xTP1";
            if (jo.containsKey("HW type")) {
                sHardwareType = jo.getString("HW type");
            }

            boolean simulated = sLinuxVersion.indexOf("XfpeEmulator") >= 0;

            // Attempt to get the media package hash.
            String sMediaPackageHash = null;
            if (jo.containsKey("media hash")) {
                sMediaPackageHash = jo.getString("media hash");
            }

            // We must have a unique reader name because we use it as a key to a hash map.
            if (sReaderName.isEmpty()) {
                sReaderName = sMac;
            }

            // add entry to reader table
            final ReaderInfo r = new ReaderInfo(-1, sReaderName, rtype, "", "", nPort, -90,
                    1.1, sMac, ipAddress, -1, new Date().getTime(),
                    simulated, 0, 0, 0, 0, sReaderVersion, sMinXbrcVersion,
                    false, "", false, sHardwareType, "", true, 3, 1, null, true, null);

            // fetch the Location id, in case it's provided
            int iLocationId = -1;

            if (jo.containsKey("location id")) {
                iLocationId = jo.getInt("location id");
                final LocationInfo locInfo = new LocationInfo();
                locInfo.setId(iLocationId);
                r.setLocation(locInfo);
            }

            SystemHealthConsumer.getInstance().getReaderLocationInfoCache().addUnassignedReader(r);
            this.auditSuccess(request);
        }
        catch (Exception e) {
            this.auditFailure(request);
            logger.error(ExceptionFormatter.format("Error handling reader PUT hello", e));

            try {
                response.sendError(500, ExceptionFormatter.format("Error handling reader PUT hello.", e));
            }
            catch (Exception ignore) {
            }
        }
    }

    private String getThisHostIp(final HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");

        if(ip != null) {
            return ip;
        }

        ip = req.getHeader("x-forwarded-for");

        if(ip != null) {
            return ip;
        }

        ip = req.getHeader("X-FORWARDED-FOR");

        if(ip != null) {
            return ip;
        }

        if(logger.isInfoEnabled()) {
            logger.info("Hello request from a reader does not have X-Forwarded-For HTTP header set.");
        }

        return req.getRemoteAddr();
    }
}