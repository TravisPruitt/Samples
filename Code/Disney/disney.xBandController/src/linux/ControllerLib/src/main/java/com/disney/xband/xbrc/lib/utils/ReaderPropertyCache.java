package com.disney.xband.xbrc.lib.utils;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

/*
   Stores properties of Readers and then updates the Readers properties
   in the database at a specified interval.
*/
public class ReaderPropertyCache {
    private Date lastWrite = new Date();
    private HashMap<ReaderInfo, Object> propertyCache = new HashMap<ReaderInfo, Object>();
    private final String propertyName;
    private final String propertyDbType;
    private final long interval;

    private static Logger logger = Logger.getLogger(ReaderPropertyCache.class);

    /**
     * Instantiates a class to cache reader changes.
     * @param propertyName The property to manage and change on the reader.
     * @param interval The interval to cache the reader changes before writing to the DB in milliseconds.
     */
    public ReaderPropertyCache(String propertyName, String propertyDbType, long interval) {

        this.propertyName = propertyName;
        this.propertyDbType = propertyDbType;
        this.interval = interval;
    }

    /**
     * Updates a value on the specified reader. The value to be
     * updated is dependent on the constructor.
     * @param ri The reader to modify.
     * @param newValue The new value of the property.
     */
    public void updateProperty(ReaderInfo ri, Object newValue) {
        // Always update the value in our cache.
        propertyCache.put(ri, newValue);

        // Determine if we need to flush the cache.
        if ( (new Date()).getTime() - lastWrite.getTime()  > interval ) {
            flushCache();
        }
    }

    private void flushCache() {
        if ( propertyCache.size() > 0 ) {
            // Write the current cache out.
            Connection dbConn = null;
            Statement pstmt = null;

            try
            {
                dbConn = XBRCController.getInstance().getPooledConnection();
                pstmt = dbConn.createStatement();
                pstmt.clearBatch();

                // Create a temporary table using the propertyname.
                String tableName = propertyName + "_tmp";

                // Ensure any existing tables are dropped.
                pstmt.addBatch("DROP TABLE IF EXISTS " + tableName);

                pstmt.addBatch("CREATE TABLE " + tableName + "( macAddress varchar(32), port int(11), " + "" +
                        "propertyValue " + propertyDbType + "  )");

                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append( "INSERT INTO " + tableName + " values " );

                for ( ReaderInfo ri : propertyCache.keySet()) {
                    sqlBuilder.append( "(" );
                    sqlBuilder.append( "'" );
                    sqlBuilder.append( ri.getMacAddress() );
                    sqlBuilder.append( "', " );
                    sqlBuilder.append( ri.getPort() );
                    sqlBuilder.append( ", " );
                    sqlBuilder.append( propertyCache.get(ri) );
                    sqlBuilder.append(  "), " );
                }

                String sSQL = sqlBuilder.toString();
                sSQL = sSQL.substring(0, sSQL.length() - 2);
                sSQL += ";";

                pstmt.addBatch(sSQL);

                // Do the join update.
                pstmt.addBatch("UPDATE Reader, " + tableName + " SET Reader." + propertyName + " = " +
                        tableName + ".propertyValue "+
                        "WHERE Reader.macAddress = " + tableName + ".macAddress AND " +
                        "Reader.port = " + tableName + ".port;");

                pstmt.addBatch("DROP TABLE " + tableName);

                pstmt.executeBatch();

                propertyCache.clear();
            }
            catch (Exception e)
            {
                logger.error(ExceptionFormatter.format(
                        "Error writing reader event id cache to DB. Error=", e));
            }
            finally
            {
                try
                {
                    if (pstmt != null)
                        pstmt.close();
                }
                catch (Exception e)
                {
                }
                if (dbConn != null)
                    XBRCController.getInstance().releasePooledConnection(dbConn);
            }

            // Set the last update time of the cache.
            lastWrite = new Date();
        }
    }
}