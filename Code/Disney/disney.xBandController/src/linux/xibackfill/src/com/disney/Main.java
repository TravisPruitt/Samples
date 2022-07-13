package com.disney;

import com.disney.configuration.ConfigurationProperties;
import com.disney.model.GuestLookup;
import com.disney.model.GuestIdentifier;
import com.disney.model.IdentifiersEntry;
import com.disney.model.Metrics;
import com.disney.model.idms.Xband;
import com.disney.model.idms.Xbands;
//import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

//import com.disney.xband.idms.lib.model.GuestIdentifier;


public class Main {


    public static final Queue<GuestLookup> guestListQueue = new LinkedList<GuestLookup>();
    public static final List<GuestLookup> cache = new ArrayList<GuestLookup>();

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static final Metrics metrics = new Metrics();

    static int countBadList = 0;

    private static ConfigurationProperties cfg = ConfigurationProperties.INSTANCE;

    public static int MinutesToRetry = cfg.MinutesToRetry();

    public static boolean AlwaysWriteGXPLinkId = cfg.getAlwaysWriteGxPLinkId();

    public static int RetryAttempts = cfg.getRetryAttempts();


    private static String BAD_FILE_NAME = "BadLookups.out.text";

    private static StringBuilder outFileSB;

    private static boolean running = false;

    protected static ConnectionPool connectionPool;

    private static int waitTime = cfg.MinutesToWait();

    private static int lastEventId = 0;

    private static int attemptsPerMinute;

    private static List<String> gxpInCache;


    public static void main(String[] args) {

        //BasicConfigurator.configure();
        gxpInCache = new ArrayList<String>();

        URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(" Log4J and Properties should be located in : " + location.getFile());

        PropertyConfigurator.configure("log4j.xml");

        running = true;
        connectionPool = new ConnectionPool(cfg.DBDriver, cfg.getConnectionString(), cfg.getDatabaseUser(), cfg.getDatabasePwd(), "10", "10", "10");

        logger.info("\nNemo - xi Public ID to GxP Link ID Backfill Application ver 1.0");
        logger.info("This app attempts to reconcile GxP bookings to Public IDs (bands) for the xi reporting services.");
        logger.info("Start Time: " + new Date().toString());
        logger.info("\n");

        Date startTime = new Date();

        // Charge our queue first?

        // Run this for testing.  Otherwise, get rid of it.
        //ResetGuestRecordsInDB();

        System.out.println("Starting working threads - count: " + cfg.getWorkerThreads());
        // so, we spin up our worker threads -and set them running. :

        List<Thread> runningThreads = new ArrayList<Thread>();
        for (int i = 1; i < cfg.getWorkerThreads(); i++) {
            Runnable task = new DataProcessor();
            Thread worker = new Thread(task);
            worker.setName(String.valueOf(i));
            worker.start();

            runningThreads.add(worker);
        }

        //System.out.println("Worker threads running.");

        FetchNewDataList();


    }

    private static int GetStaleCount()
    {
        int retVal = 0;

        synchronized (cache)
        {
            for(GuestLookup gl : cache)
            {
                if (gl.getStale())
                    retVal++;
            }
        }

        return retVal;
    }


    private static int GetRetryCount()
    {
        int retval = 0;

        synchronized (guestListQueue)
        {
            for(GuestLookup gl : guestListQueue)
            {
                if (gl.getRetryCount() > 0)
                {
                    retval++;
                }
            }
        }

        return retval;
    }


    private static void FetchNewDataList() {
        Date startTime = new Date();
        int lastIDMSperMinute = 0;
        int lastSFOperMinute = 0;
        int lastGXPCachePerMinute = 0;

        // This will just run in an infinite loop, really.
        while (running) {
            try {

                int count = 0;
                int newCount = 0;
                int retryCount = GetRetryCount();
                int staleCount = GetStaleCount();


                List<GuestLookup> fetchList = getGuestList();
                if (!(fetchList== null) && !fetchList.isEmpty()) {
                    ChargeList(fetchList);
                }



                count = cache.size() - staleCount;
                newCount = fetchList.size();


                logger.info("Report time: " + new Date().toString());
                logger.info("Thread count: " + Thread.activeCount());
                logger.info("Queue count: " + count + " records. " + newCount + " new records were added to the queue.");
                logger.info("Records in Retry State: " + GetRetryCount() + ".");
                synchronized (metrics)
                {
                    attemptsPerMinute = ((attemptsPerMinute + ((metrics.getIDMSLookupAttempts() - lastIDMSperMinute) + (metrics.getSFOneViewLookupAttempts() - lastSFOperMinute)) ) /2);

                    lastIDMSperMinute = metrics.getIDMSLookupAttempts();
                    lastSFOperMinute = metrics.getSFOneViewLookupAttempts();
                    //lastGXPCachePerMinute = (int) metrics.getGxpInCache();

                    logger.info("Stale records: " + metrics.getStaleRecords());
                    logger.info("Total Lookups completed: " + metrics.getLooksupsCompleted() + ".\n");
                    logger.info("GXP Average Lookup Time (ms): " + metrics.getAverageGXPLookupTime());
                    logger.info("Total successful IDMS Looksups: " + metrics.getIDMSLookups() + ". Avg Time (ms): " + metrics.getAverageIDMSLookupTime());
                    logger.info("IDMS Total Lookup Attemps: " + metrics.getIDMSLookupAttempts() );
                    logger.info("Total successful SFOV Lookups: " + metrics.getSFOneViewLooksups() + ". Avg Time (ms): " + metrics.getAverageSFOVLookupTime());
                    logger.info("SFOV Total Attempts: " + metrics.getSFOneViewLookupAttempts());
                    logger.info("GXP Cache Hits: " + metrics.getGxpInCache());
                    logger.info("Avg Attempts Per Minute: " + attemptsPerMinute );
                    logger.info("================================================\n");
                }

                startTime = new Date();

                // Sleep for the wait time.
                while (!((new Date().getTime() - startTime.getTime()) > (waitTime * (60 * 1000))))
                {
                    try {

                        Thread.sleep(1000);

                    } catch (Exception ex) {

                    }
                }
            } catch (Exception ex) {
                //System.out.println("Error in : FetchNewDataList " + ex.getLocalizedMessage());
                logger.error("Error in : FetchNewDataList",  ex);
                // And keep on going...
            }

            try {
                Thread.sleep(1000);
            }
            catch (Exception ex)
            {
                logger.error("Error in FetchNewDataList" , ex);
            }

        }
    }


    private static void ChargeList(List<GuestLookup> newGuestList) {
        // This is a very silly way to do this and I really should try to figure out how to use a predicate, but I don't know how to
        // use a predicate and I'm in a hurry, so ... screw it.
        boolean found = false;

        logger.info("I got a new GuestList: " + newGuestList.size());

        for (GuestLookup gl : newGuestList) {
            found = false;
            try {
                // this locks the guestListQueue object until the charge is complete.  The threads will halt during this time.
                // That *should* be okay?
                synchronized (guestListQueue) {
                    synchronized (cache)  {
                    for (GuestLookup inList : cache) {

                        //logger.info("I found this businessID: " + gl.getBusinessEventId());

                        if (gl.getBusinessEventId().equals(inList.getBusinessEventId()))
                        {
                            logger.info("I found this businessID: " + gl.getBusinessEventId());
                            found = true;

                            break;
                        }
                    }

                    if (!found) {
                        //logger.info("Not Found - Adding to Queue: " + gl.getBusinessEventId());
                        cache.add(gl);
                        guestListQueue.add(gl);
                    }
                    }
                }


                Thread.sleep(0);
            } catch (Exception ex) {
                logger.error("Error in ChargeList. ", ex);
            }

        }



    }


    public static void Terminate() {
        running = false;
    }


    // This is the call to SFOneView - it should provide a list of public IDs that are party of a guests identifiery records.
    // Note that's slightly different from IDMS, where you must get the xbands assigned to the guest.
    public static List<String> GetListOfPublicIdsForGuestSFOV(String gxpLinkId) {
        List<String> publicIds = null;

        long startTime = System.currentTimeMillis();

        try {

            String urlstr = cfg.getOneViewRootURL() + cfg.getOneViewGuestIdentifiers();
            urlstr = urlstr.replace("{linkId}", gxpLinkId);


            //logger.info("URL to IDMS: " + urlstr);
            //System.out.println(" URL to Public IDs " + urlstr);

            java.net.URL url = new URL(urlstr);

            String jsonGuestObject = retrieveGuestFromIdentifier(url);

            long endTime = System.currentTimeMillis();

            synchronized (metrics)
            {
                long avg = metrics.getAverageSFOVLookupTime();
                avg = (avg + (endTime-startTime))/2;
                metrics.setAverageSFOVLookupTime(avg);
            }


            //System.out.println(jsonGuestObject);

            if (jsonGuestObject != null)
            {

                // Now we should have a string that we can parse into a GuestIdentifier object.
                ObjectMapper objectMapper = new ObjectMapper();

                GuestIdentifier guestIds = objectMapper.readValue(jsonGuestObject, GuestIdentifier.class);

                if (guestIds != null)
                {
                    publicIds = new ArrayList<String>();

                    for(IdentifiersEntry ie : guestIds.identifiers)
                    {
                        if (ie.type.contains("public-id") & !publicIds.contains(ie.value)){
                        // If there's an xband, there has to be a public id.  What if there's not?  Oh well.
                            publicIds.add(ie.value);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            //System.out.println("Problem finding xBands: " + ex.getLocalizedMessage());
            logger.error(ex);
        }


        return publicIds;
    }


    public static List<String> GetListOfPublicIdsForGuestIDMS(String gxpLinkId) {
        List<String> publicIds = null;

        long startTime = System.currentTimeMillis();

        try {

            String urlstr = cfg.getIDMSURL()+ cfg.getIDMSServiceXBandsURL();
            urlstr = urlstr.replace("{linkId}", gxpLinkId);

            //logger.info("IDMS URL: " + urlstr );
            //System.out.println(" URL to Public IDs " + urlstr);

            java.net.URL url = new URL(urlstr);

            String jsonGuestObject = retrieveGuestFromIdentifier(url);

            long endTime = System.currentTimeMillis();

            synchronized (metrics)
            {
                long avg = metrics.getAverageIDMSLookupTime();
                avg = (avg + (endTime - startTime))/2;
                metrics.setAverageIDMSLookupTime(avg);
            }

            //System.out.println(jsonGuestObject);

            if (jsonGuestObject != null)
            {

            // Now we should have a string that we can parse into a GuestIdentifier object.
                ObjectMapper objectMapper = new ObjectMapper();

                Xbands guestIds = objectMapper.readValue(jsonGuestObject, Xbands.class);

                if (guestIds != null)
                {
                    publicIds = new ArrayList<String>();

                    for(Xband xband : guestIds.getXbands())
                    {
                        // If there's an xband, there has to be a public id.  What if there's not?  Oh well.
                        publicIds.add(xband.getPublicId());
                    }
                }
            }

        } catch (Exception ex) {
            //System.out.println("Problem finding xBands: " + ex.getLocalizedMessage());
            logger.error(ex);
        }


        return publicIds;
    }

    /// Get the list of messed up messed up guests from the database.
    private static List<GuestLookup> getGuestList() {

        Date now = new Date();

        String sql = "select * from gxp.BusinessEventPending where GuestID=0 AND BusinessEventID > " + lastEventId;

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        List<GuestLookup> guestList = null; //new ArrayList<GuestLookup>();

        try {
            connection = connectionPool.createConnection();

            statement = connection.createStatement();

            if (statement == null) {
                System.out.println("Statement Creation failed.");
            }

            rs = statement.executeQuery(sql);

            if (rs == null) {
                System.out.println("Recordset Creation failed.");
            }

            guestList = new ArrayList<GuestLookup>();

            while (rs.next()) {
                GuestLookup gl = new GuestLookup();
                gl.setBusinessEventId(Integer.toString(rs.getInt(1)));
                gl.setReferenceId(Integer.toString(rs.getInt(5)));
                gl.setGuestId(Integer.toString(rs.getInt(6)));
                gl.setEventStartDate(rs.getDate(9));
                //gl.setGxpId(Long.toString(retrieveGuestGXPId(gl.getReferenceId())));
                // Add to the queue.
                guestList.add(gl);
            }

            if (!guestList.isEmpty() && guestList.size() > 0)
                lastEventId = Integer.valueOf( guestList.get(guestList.size()-1).getBusinessEventId());

        } catch (Exception ex) {
            logger.error(ex);

        } finally {
            if (rs != null)
                try {
                    connectionPool.close(rs);

                } catch (Exception ex) {

                }

            if (statement != null)
                try {
                    statement.close();
                } catch (Exception ex) {

                }
            if (connection != null)
                try {
                    connectionPool.release(connection);
                } catch (Exception ex) {

                }

        }
        return guestList;
    }


    /// Reset all of the records in the database (basically, start over).
    private static void ResetGuestRecordsInDB() {
        String sql = "UPDATE gxp.BusinessEventPending set guestId=0";

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        List<GuestLookup> guestList = null; //new ArrayList<GuestLookup>();

        try {
            connection = connectionPool.createConnection();

            statement = connection.createStatement();

            System.out.println("Resetting guest records...\n");
            statement.executeUpdate(sql);

        } catch (Exception ex) {
            logger.error(ex);
            System.out.println("I got an error : " + ex.getStackTrace());
            System.out.println(ex + "\n");
            System.out.println(ex.getLocalizedMessage());
        } finally {


            if (statement != null)
                try {
                    statement.close();
                } catch (Exception ex) {

                }
            if (connection != null)
                try {
                    connectionPool.release(connection);
                } catch (Exception ex) {

                }

        }
    }

    // Check if the gxpId already exists in the cache.  If it does, then return true, else false.
    public static boolean GxPInCache(String gxpId)
    {

        // First, ask our cache if it already has it?  // This will save us banging on the database.
        if (gxpInCache.contains(gxpId))
            return true;

        String sql = "Select * from PublicID_GxpLinkID_xRef where GXPLinkId=" + gxpId + ";";

        boolean retVal = false;


        Connection conn = null;


        Statement statement = null;

        ResultSet result = null;

        try
        {
            conn = connectionPool.createConnection();
            statement = conn.createStatement();

            result = statement.executeQuery(sql);

            if (result.next())
            {
                // We got a record back, so mark it as true.

                retVal = true;
                gxpInCache.add(gxpId);
            }
            else
            {
                retVal = false;
            }

            // And we're done.
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        finally
        {
            if (result != null)
            {
                try {
                    connectionPool.close(result);

                }
                catch (Exception ignore)
                {

                }
            }

            if (statement != null)
            {
                try {
                    statement.close();

                }
                catch (Exception ignore)
                {

                }
            }

            if (conn != null)
            {
                try {

                    conn.close();
                }
                catch (Exception ignore)
                {

                }

            }

        }


        return retVal;
    }


    // Write a bad guest lookup to a string builder to be written out later.
    public static void SavePublicIDToGXPLinkId(String gxpId, String publicId) {
        String sql = "{call sp_insert_PublicID_GxpLinkID_xRef(?,?)}";
        Connection conn = null;
        CallableStatement statement = null;

        try {
            conn = connectionPool.createConnection();
            //conn.setAutoCommit(false);
            statement = conn.prepareCall(sql);
            statement.setLong(1, Long.parseLong(publicId));
            statement.setLong(2, Long.parseLong(gxpId));

            statement.execute();


            //conn.commit();

        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            logger.error(ex);

        } finally {
            if (statement != null) {
                try {
                    connectionPool.close(statement);
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }

            if (conn != null) {
                try {
                    connectionPool.release(conn);
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }
        }

    }

    public static void SaveToDatabase(GuestLookup guest) {
        String sql = "Update gxp.BusinessEventPending SET guestId=" + guest.getGxpId() + " where BusinessEventID=" + guest.getBusinessEventId();
        //logger.info(sql);

        Connection conn = null;
        Statement statement = null;


        try {
            conn = connectionPool.createConnection();
            conn.setAutoCommit(false);
            statement = conn.createStatement();
            statement.executeUpdate(sql);
            conn.commit();
        } catch (Exception ex) {
            logger.error(ex);
            //System.out.println(ex.getLocalizedMessage());

            try {
                conn.rollback();
            } catch (Exception exx) {
                //System.out.println(exx.getLocalizedMessage());
                logger.error(exx);
            }

        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ex) {
                    logger.error(ex);
                    //System.out.println(ex.getLocalizedMessage());
                }
            }

            if (conn != null) {
                try {
                    connectionPool.release(conn);
                } catch (Exception ex) {
                    logger.error(ex);
                    //System.out.println(ex.getLocalizedMessage());
                }
            }


        }


        //System.out.println(sql);
    }


    public static long retrieveGuestGXPId(String referenceId) {
        String gxpURL = cfg.getGXPURL();

        gxpURL = gxpURL.replace("{relId}", referenceId);

        long gxpId = 0;

        try {

            java.net.URL url = new URL(gxpURL);

            //System.out.println("GXP URL: " + url);

            gxpId = retrieveGuestGXPId(url);


        } catch (Exception ex) {
            //System.out.println(ex.getLocalizedMessage());
            logger.error(ex);

        }

        return gxpId;
    }


    // Web Service Call.
    protected static long retrieveGuestGXPId(URL url) {
        long gxpID = 0;

        InputStreamReader reader = null;
        long startTime = System.currentTimeMillis();

        try {
            //Call IDMS and get the guest
            URLConnection connection = url.openConnection();

            //Open the reader
            reader = new InputStreamReader(connection.getInputStream());

            // Get the response
            BufferedReader rd = new BufferedReader(reader);

            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();
            long endTime = System.currentTimeMillis();

            synchronized (metrics)
            {
                long avg = metrics.getAverageGXPLookupTime();
                avg = (avg + (endTime - startTime ))/2;
                metrics.setAverageGXPLookupTime(avg);
            }

            // The Stringbuffer has the GxpObject in it.
            // Simple parse of the gxpLinkId.  Not casting to object.

            int position = sb.toString().indexOf("gxpLinkId");
            int colonPosition = sb.toString().indexOf(":", position);
            int firstComma = sb.toString().indexOf(",", position);

            String sub = sb.toString().substring(colonPosition + 1, firstComma);

            gxpID = Integer.parseInt(sub);

        } catch (IOException io) {
            logger.error(
                    "Problem communicating with GxP", io);
        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        }


        return gxpID;

    }


    protected static String retrieveGuestFromIdentifier(URL url) {
        long guestId = 0;
        InputStreamReader reader = null;
        long startTime = System.currentTimeMillis();


        String retVal = null;

        try {
            //Call Oneview and get the guest
            URLConnection connection = url.openConnection();

            //Open the reader
            reader = new InputStreamReader(connection.getInputStream());

            // Get the response
            BufferedReader rd = new BufferedReader(reader);

            StringBuffer sb = new StringBuffer();
            String line;

            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }


            rd.close();

            long endTime = System.currentTimeMillis();

            retVal = sb.toString();

        }
        catch (FileNotFoundException e) {
            // Do I want to log this?  Seems like I don't.
            //logger.info("No IDMS record: " +  e.getLocalizedMessage());
        }


        catch (IOException io) {
            //logger.error(
            //        "Problem communicating with IDMS", io);
            logger.error("Problem communicating: ", io);
        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    //System.out.println(ex.getLocalizedMessage());
                }
            }
        }

        return retVal;
    }


}
