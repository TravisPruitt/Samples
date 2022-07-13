package com.disney.xband.xbrms.client.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;

public class PerfMetricsAction extends BaseAction {

    private static Logger logger = Logger.getLogger(PerfMetricsAction.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(PkConstants.DATE_FORMAT, Locale.ENGLISH);

    private String id;
    private String metrics;
    private String healthItemId;
    private String healthItemIp;
    private String healthItemPort;
    private String perfMetricName;
    private String perfMetricVersion;
    private String selectedEndDate;
    private long defaultTimeMillis = 86400000;    //one day

    public String execute() throws Exception {
    	try {
            if (healthItemId == null || healthItemId.isEmpty()) {
                this.errMsg = "issing health item id.";
                return INPUT;
            }

            if (healthItemIp == null || healthItemIp.isEmpty()) {
                this.errMsg = "missing health item IP address.";
                return INPUT;
            }

            if (healthItemPort == null || healthItemPort.isEmpty()) {
                this.errMsg = "missing health item port number.";
                return INPUT;
            }

            Date endDate = null;

            if (selectedEndDate == null || selectedEndDate.isEmpty())
            // current data
            {
                endDate = new Date(System.currentTimeMillis());
            }
            else
            // historical data
            {
                endDate = dateFormat.parse(selectedEndDate);
            }

            // show maximum defaultTimeMillis worth of data
            final Date startDate = new Date(endDate.getTime() - defaultTimeMillis);


            if (perfMetricName != null && !perfMetricName.isEmpty() &&
            		perfMetricVersion != null && !perfMetricVersion.isEmpty()) 
            {
                metrics = XbrmsUtils.getRestCaller().getMetrics(
                        healthItemId,
                        perfMetricName,
                        perfMetricVersion,
                        startDate.getTime() + "",
                        endDate.getTime() + "");
            }
            else 
            {
                // find all metrics for current facility
                metrics = XbrmsUtils.getRestCaller().getMetrics(
                        healthItemId,
                        null,
                        null,
                        startDate.getTime() + "",
                        endDate.getTime() + "");
            }

            if (metrics == null) {
                throw new Exception("Failed to retrieve performance metrics for health item ID " + healthItemId);
            }
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch (HTTPException e)
		{
			this.errMsg = e.getCause() != null ? "HTTP status code: " + e.getStatusCode() : "unknown";
			String errMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : "unknown";
			
			if (logger.isDebugEnabled())
				logger.error("Failed to retrieve performance metric data for health item ID " + healthItemId
						+ ". Cause: " + errMessage, e);
			else
				logger.error("Failed to retrieve performance metric data for health item ID " + healthItemId
						+ ". Cause: " + errMessage);
			
			return ERROR;
		}

        return SUCCESS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getPerfMetricName() {
        return perfMetricName;
    }

    public void setPerfMetricName(String perfMetricName) {
        this.perfMetricName = perfMetricName;
    }

    public String getPerfMetricVersion() {
        return perfMetricVersion;
    }

    public void setPerfMetricVersion(String perfMetricVersion) {
        this.perfMetricVersion = perfMetricVersion;
    }

    public String getFacilityIp() {
        return healthItemIp;
    }

    public void setHealthItemIp(String healthItemIp) {
        this.healthItemIp = healthItemIp;
    }

    public String getHealthItemPort() {
        return healthItemPort;
    }

    public void setHealthItemPort(String healthItemPort) {
        this.healthItemPort = healthItemPort;
    }

    public String getHealthItemId() {
        return healthItemId;
    }

    public void setHealthItemId(String healthItemId) {
        this.healthItemId = healthItemId;
    }

    public String getSelectedEndDate() {
        return selectedEndDate;
    }

    public void setSelectedEndDate(String selectedEndDate) {
        this.selectedEndDate = selectedEndDate;
    }
}
