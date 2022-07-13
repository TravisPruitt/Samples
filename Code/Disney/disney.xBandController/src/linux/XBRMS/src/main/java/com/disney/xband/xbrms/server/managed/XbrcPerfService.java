package com.disney.xband.xbrms.server.managed;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TimeZone;

import com.disney.xband.xbrms.common.SslUtils;
import com.disney.xband.xbrms.common.model.HealthItemDto;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.server.SSConnectionPool;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;
import com.disney.xband.xbrms.server.model.XbrmsStatusBo;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.common.lib.performance.PerfMetric;
import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import com.disney.xband.common.lib.performance.PerfMetricMetadataEnvelope;
import com.disney.xband.common.lib.performance.PerfMetricType;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.model.XbrcModel;
import com.disney.xband.xbrms.common.SslUtils;

public class XbrcPerfService
{
	private static Logger logger = Logger.getLogger(XbrcPerfService.class);
	private static Logger plogger = Logger.getLogger("performance." + XbrcPerfService.class.toString());

	public Collection<PerfMetricMetadata> getPerfMetricsMetadataFromHealthItem(String ip, int port)
	{
		long begin = System.currentTimeMillis();
		
		Collection<PerfMetricMetadata> metadata = new LinkedList<PerfMetricMetadata>();

		InputStream is = null;
		int readTimeout = 2000;
		try
		{
			HttpURLConnection conn = SslUtils.getConnection(ip, port, "perfmetricsmetadata");
			conn.setReadTimeout(readTimeout);
			
			logger.info("Executing a call to " + conn.getURL().toString());
			
			int responseCode = conn.getResponseCode();
			if (responseCode < 0 || responseCode >= 400)
			{	
				logger.error("Error: " + conn.getResponseCode() + " received from Controller (" + conn.getURL().toString() + ")");
				return null;
			}
			
			is = conn.getInputStream();
			if (is == null)
			{
				logger.warn("Call to /perfmetricsmetadata didn't return any data. It most likely means that reading the results of GET /perfmetricsmetadata takes longer than " + readTimeout + " milliseconds.");
				return metadata;
			}
			
			PerfMetricMetadataEnvelope envelope = XmlUtil.convertToPojo(is, PerfMetricMetadataEnvelope.class);
			
			if (envelope != null){
				for (PerfMetricMetadata meta : envelope.getMetadata()){
					if (meta == null)
						continue;
					
					metadata.add(meta);
				}
			}
			
		} 
		catch (MalformedURLException e)
		{
			logger.error("URL Error:" + e.getMessage());
		} 
		catch (IOException e)
		{
			logger.error("IO Error: " + e.getMessage());
		}
		catch (Exception e)
		{
			logger.error("Error: ", e);
		}
		finally
		{
			if (is != null)
			{
				try {
					is.close();
				} catch (IOException e) {
					logger.warn("Input stream failed to close. Possible memory leak.");
				}
			}
            
            if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Getting perf metrics metadata from xbrc.");
            }
		}
		
		return metadata;
	}
	
	public Map<String, PerfMetricMetadata> getPerfMetricsMetadataFromDB(String ip, int port) throws Exception
	{
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        Map<String, PerfMetricMetadata> metadata = new LinkedHashMap<String, PerfMetricMetadata>();
        
        String query = "SELECT * FROM PerformanceMetricDesc WHERE PerformanceMetricDescID in (SELECT DISTINCT PerformanceMetricDescID FROM HealthItem AS hi JOIN PerformanceMetric AS pm (nolock) ON hi.id = pm.HealthItemID WHERE hi.ip = ? AND hi.port = ?)";

        long begin = System.currentTimeMillis();
        
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();
			stmt.setString(1, ip);
			stmt.setInt(2, port);
			stmt.execute();

            rs = stmt.getResultSet();
            while (rs.next())
            {
            	PerfMetricMetadata pmm = new PerfMetricMetadata();
            	pmm.setDescription(rs.getString("PerformanceMetricDescription"));
            	pmm.setDisplayName(rs.getString("PerformanceMetricDisplayName"));
            	pmm.setName(rs.getString("PerformanceMetricName"));
            	pmm.setType(PerfMetricType.valueOf(rs.getString("PerformanceMetricUnits")));
            	pmm.setVersion(rs.getString("PerformanceMetricVersion"));

            	metadata.put(pmm.getName(), pmm);
            }

            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
            
			return metadata;
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Looking up perf metrics metadata for health item: " + ip + ":" + port);
		}
        catch(Exception e) 
        {
            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.RetrieveMetricsData, "Looking up perf metrics metadata for health item: " + ip + ":" + port, e);
        }
        finally {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;

        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
		
		return null;
	}
	
	/**
	 * Returns performance metric data formatted as JSON string:
	 * 
	 * {"Performance metric name":["Timestamp up to seconds",Maximum,Minimum,Mean],[...],[...]}
	 * 
	 * @param healthItemId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public String find(int healthItemId, String healthItemIp, long startTime, long endTime, String metricName, String metricVersion) throws Exception {
		
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String r = "";

        String query = "SELECT pmd.PerformancemetricName, pm.Maximum, pm.Minimum, pm.Mean, pm.Timestamp FROM PerformanceMetric AS pm (nolock) LEFT JOIN PerformanceMetricDesc AS pmd ON pm.PerformanceMetricDescID = pmd.PerformanceMetricDescID WHERE pm.HealthItemID = ? AND pm.Timestamp > ? AND pm.Timestamp <= ? AND pmd.PerformanceMetricName = ? AND pmd.PerformanceMetricVersion = ? ORDER BY pmd.PerformanceMetricName, pm.Timestamp";
        long begin = System.currentTimeMillis();
        
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+0"));

			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
				
			stmt.clearParameters();
			stmt.setInt(1, healthItemId);
			stmt.setTimestamp(2, new Timestamp(startTime), cal);
			stmt.setTimestamp(3, new Timestamp(endTime), cal);
			stmt.setString(4, metricName);
			stmt.setString(5, metricVersion);
			stmt.execute();

            rs = stmt.getResultSet();
            
            r = formatPerfMetricAsJsonString(rs);

            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
            
			return r;
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Looking up perf metrics for health item: " + (healthItemIp != null ? healthItemIp : healthItemId));
			
			return null;
		}
        catch(Exception e) {

            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.RetrieveMetricsData, "Find health item call failed for id = " + healthItemId, e);

            throw e;
        }
        finally 
        {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;

        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
    }
	
	/**
	 * Returns performance metric data formatted as JSON string:
	 * 
	 * {"Performance metric name":["Timestamp up to seconds",Maximum,Minimum,Mean],[...],[...]}
	 * 
	 * @param healthItemId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public String findAll(int healthItemId, String healthItemIp, long startTime, long endTime) throws Exception {
		
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String r = "";

        String query = "SELECT pmd.PerformancemetricName, pm.Maximum, pm.Minimum, pm.Mean, pm.Timestamp FROM PerformanceMetric AS pm (nolock) LEFT JOIN PerformanceMetricDesc AS pmd ON pm.PerformanceMetricDescID = pmd.PerformanceMetricDescID WHERE pm.HealthItemID = ? AND pm.Timestamp > ? AND pm.Timestamp <= ? ORDER BY pmd.PerformanceMetricName, pm.Timestamp";
        long begin = System.currentTimeMillis();
        
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+0"));

			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
				
			stmt.clearParameters();
			stmt.setInt(1, healthItemId);
			stmt.setTimestamp(2, new Timestamp(startTime), cal);
			stmt.setTimestamp(3, new Timestamp(endTime), cal);
			stmt.execute();

            rs = stmt.getResultSet();
            
            r = formatPerfMetricAsJsonString(rs);

            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
            
			return r;
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Looking up perf metrics for health item: " + (healthItemIp != null ? healthItemIp : healthItemId));
			
			return null;
		}
        catch(Exception e) {

            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.RetrieveMetricsData, "Find health item call failed for id = " + healthItemId, e);

            throw e;
        }
        finally 
        {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;

        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
	}

	private String formatPerfMetricAsJsonString(ResultSet rs) throws Exception {
		StringBuffer result = null;
		
		if (rs == null)
			return "";
		
		String current = "";
		String name = null;
		String timestamp = null;
		
		boolean newMetric = false;
		boolean previousPresent = false;
		
		while (rs.next())
		{
			if (result == null){
				result = new StringBuffer("{");
			}
			
			name = rs.getString("PerformanceMetricName");
			
			newMetric = !current.equals(name);
			previousPresent = result.charAt(result.length() -1) == ']';
			
			if (newMetric)
			{
				current = name.substring(0);
				
				// close the previous, if present
				if (previousPresent)
					result.append("],");
				
				// start the new
				result.append('"').append(name).append("\":[");
			}
			
			if (!newMetric && previousPresent)
				result.append(',');
			
			// metric data
			timestamp = rs.getString("Timestamp");
			
			result.append("[\"");
			result.append(timestamp.substring(0, timestamp.indexOf('.'))).append("\",");
			result.append(rs.getString("Maximum")).append(',');
			result.append(rs.getString("Minimum")).append(',');
			result.append(rs.getString("Mean")).append(']');
		}
		
		if (result != null) {
			result.append("]}");
		    return result.toString();
        }

        return "";
	}

	private void prepareInsertMetric(CallableStatement stmt, PerfMetric metric, XbrcStatus status, HealthItemDto healthItem) throws SQLException, IllegalArgumentException {

        XbrcModel model =  XbrcModel.getByModel(status.getModel());
        if (model == null) {
        	StringBuffer errorMessage = new StringBuffer("Model not defined in XbrcModel detected: ");
        	errorMessage.append(status.getModel()).append(" while processing perf metric insert for facility ").append(status.getFacilityId());
        	
            throw new IllegalArgumentException(errorMessage.toString());
        }

        try 
        {
        	stmt.setLong("@HealthItemId", healthItem.getId());
	        stmt.setString("@Vanue", status.getFacilityId());
	        stmt.setString("@Source", healthItem.getClass().getName());
	        stmt.setString("@Time", status.getTime());
	        stmt.setString("@Metric", metric.getName());
	        stmt.setString("@Version", metric.getVersion());
	        stmt.setString("@Maximum", metric.getMax().toString());
	        stmt.setString("@Minimum", metric.getMin().toString());
	        stmt.setString("@Mean", metric.getMean().toString());
	        
	        stmt.addBatch();
        } 
        catch (NullPointerException e) 
        {
        	StringBuffer errorMessage = new StringBuffer("Failed to insert perf metric ");
        	errorMessage.append(metric.getName()).append(" v.").append(metric.getVersion());
        	errorMessage.append(" for health item ").append(healthItem.getIp())
        		.append(":").append(healthItem.getPort())
        		.append(":").append(healthItem.getName())
        		.append(":").append(healthItem.getClass().getName());
        	
        	if (logger.isDebugEnabled())
        		logger.error(errorMessage, e);
        	else
        		logger.error(errorMessage);
        }
    }

	public void insert(XbrcStatus status, HealthItemDto healthItem) throws Exception {
		Connection conn = null;
		PerfMetric metric = null;
		CallableStatement stmt = null;
		
		String query = "{call usp_PerformanceMetric_create(?,?,?,?,?,?,?,?,?)}";
		long begin = System.currentTimeMillis();
		
		try
		{
            if (status == null || healthItem == null || healthItem.getId() == null)
                return;
            
            conn = SSConnectionPool.getInstance().getConnection();
			
			stmt = SSConnectionPool.getInstance().getCollableStatement(conn, query);
			stmt.clearBatch();
			
			for (Method m: status.getClass().getDeclaredMethods())
			{
				//don't process compiler generated methods
				if (m.isSynthetic())
					continue;
				
				if (m.getReturnType() != PerfMetric.class)
					continue;
				
				metric = (PerfMetric) m.invoke(status, new Object[]{});
				
				if (metric == null)
					continue;
				
				prepareInsertMetric(
						stmt,
						metric,
						status,
						healthItem);
			}
			
			stmt.executeBatch();
			
			XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Inserting perf metrics for health item: " + healthItem.getIp());
			
			ProblemsReportBo.getInstance().setLastError(
                    ProblemAreaType.InsertMetricsData,
                    "Failed to insert performance metrics data for " + healthItem.getIp() + ":" + healthItem.getPort(), e);
		}
        catch(Exception e) 
        {
            ProblemsReportBo.getInstance().setLastError(
            		ProblemAreaType.InsertMetricsData,
            		"Failed to insert performance metrics data for " + healthItem.getIp() + ":" + healthItem.getPort(), e);

            throw e;
        }
		finally
		{
			SSConnectionPool.getInstance().releaseResources(conn, stmt);
            conn = null;
            stmt = null;

			if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Stored procedure: " + query);
            }
		}
	}
	
	public void insert(Collection<PerfMetricMetadata> metadataCollection, HealthItemDto item) throws Exception {
		Connection conn = null;
		CallableStatement stmt = null;
		
		String query = "{call usp_PerformanceMetricDesc_create(?,?,?,?,?,?)}";
		long begin = System.currentTimeMillis();
		
		try
		{
            if ((metadataCollection == null) || (metadataCollection.size() == 0) || item == null || item.getType() == null || item.getIp() == null)
                return;
            
            conn = SSConnectionPool.getInstance().getConnection();

			stmt = SSConnectionPool.getInstance().getCollableStatement(conn, query);
	        stmt.clearBatch();
	        
	        for (PerfMetricMetadata metadata : metadataCollection)
	        {
	        	stmt.setString("@Name", metadata.getName());
		        stmt.setString("@DisplayName", metadata.getDisplayName());
		        stmt.setString("@Description", metadata.getDescription());
		        stmt.setString("@Units", metadata.getType().name());
		        stmt.setString("@Version", metadata.getVersion());
		        stmt.setString("@Source", item.getClass().getName());
		        
		        stmt.addBatch();
	        }
	        stmt.executeBatch();
	       
	        XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Inserting performance metric description for health item ip " + item.getIp());
		}
		finally 
		{
            SSConnectionPool.getInstance().releaseResources(conn, stmt);
            conn = null;
            stmt = null;

            if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Stored procedure: " + query);
            }
        }
	}
	
	public void deleteOlderThan(java.util.Date olderThan, Calendar cal)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "DELETE FROM PerformanceMetric WHERE Timestamp < ?");
			ps.clearParameters();
			ps.setTimestamp(1, new Timestamp(olderThan.getTime()), cal);
			ps.execute();
			
		}
		catch (SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "deleting old entries from the PerformanceMetric table.");
		}
		finally
		{
			SSConnectionPool.getInstance().releaseResources(conn, ps);
            conn = null;
            ps = null;
		}
	}
}
