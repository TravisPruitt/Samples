package com.disney.xband.xbrms.server.model;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.lib.net.NetInterface;
import com.disney.xband.xbrms.common.model.ProblemDto;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.IDataTransferObject;
import com.disney.xband.xbrms.common.model.XbrmsStatusDto;
import com.disney.xband.xbrms.server.SSConnectionPool;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;

import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class XbrmsStatusBo implements IBusinessObject 
{
	
    XbrmsStatusDto dto;

    private Timer timer = new Timer();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
    private final Lock reader = rwl.readLock();
    private final Lock writter = rwl.writeLock();
    private Logger logger = Logger.getLogger(XbrmsStatusBo.class);

    public static XbrmsStatusBo getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void initialize(Logger logger, ServletContext context, boolean force) throws Exception {
        if (!force && this.dto.getTime() != null) {
            return; // already initialized
        }

        try {
            writter.lock();

            if (logger.isInfoEnabled()) {
                logger.info("Initializing xbrms status...");
            }

            if (logger == null || context == null) {
                throw new IllegalArgumentException("Can not initialize " + this.getClass().getName() + " without the ServletContext.");
            }

            this.dto.setVersion(XbrmsUtils.getXbrmsVersion(logger, context));

            this.dto.setName(XbrmsConfigBo.getInstance().getDto().getName());
            this.dto.setId(XbrmsConfigBo.getInstance().getDto().getId());

            testSchemaVersion();
            
            resolveNetworkAddress();

            calculateStatus();
            
            this.dto.setStartTime(Calendar.getInstance().getTime());
            this.dto.setTime(Calendar.getInstance().getTime());
        }
        finally {
            try {
                writter.unlock();
            }
            catch (IllegalMonitorStateException e) {
                // This will only happen if a thread which doesn't have right to release this
                // lock attempts to do so.
            }
        }
    }

    public void refreshStatusInfo() {
        try {
            writter.lock();

            this.dto.setName(XbrmsConfigBo.getInstance().getDto().getName());
            this.dto.setId(XbrmsConfigBo.getInstance().getDto().getId());
            
            resolveNetworkAddress();
        }
        finally {
            try {
                writter.unlock();
            }
            catch (IllegalMonitorStateException e) {
                // This will only happen if a thread which doesn't have right to release this
                // lock attempts to do so.
            }
        }
    }

    private void testSchemaVersion() throws Exception {
        try {
            String persistedSchemaVersion = MetaDao.getSchemaVersion();

            if (persistedSchemaVersion.startsWith(this.dto.getRequiredDatabaseVersion())) {
                this.dto.setStatus(StatusType.Green);
                this.dto.setStatusMessage("");
            }
            else {
                StringBuffer sm = new StringBuffer("Incorrect schema version. Required: ");
                sm.append(this.dto.getRequiredDatabaseVersion());
                sm.append(". Found: ");
                sm.append(persistedSchemaVersion);
                
                ProblemsReportBo.getInstance().setLastError(ProblemAreaType.ReadXbrmsConfig, "Problem detected while trying to read xBRMS configuration from the database.");
                ProblemsReportBo.getInstance().setLastError(ProblemAreaType.IncorrectSchemaVersion, sm.toString());
            }
        }
        catch (Exception e) {
            this.dto.setStatus(StatusType.Red);
            this.dto.setStatusMessage("Failed to test schema version due to database connectivity problems.");
        }
    }

    public StatusType getStatus() {
        try {
            writter.lock();

            calculateStatus();
            
            return this.dto.getStatus();
        }
        finally {
            try {
                writter.unlock();
            }
            catch (IllegalMonitorStateException e) {
                // This will only happen if a thread which doesn't have the right
                // anyway to release this lock attempts to do so.
            }
        }
    }

    public void setDbReadStatus(StatusType dbStatus, String dbStatusMessage) {
        try {
            writter.lock();

            this.dto.setDbStatus(dbStatus);
            this.dto.setDbStatusMessage(dbStatusMessage != null ? dbStatusMessage : "");

            if (this.dto.getDbStatus() == StatusType.Green) {
                ProblemsReportBo.getInstance().clearLastError(ProblemAreaType.ConnectToXbrmsDb);
                ProblemsReportBo.getInstance().clearLastError(ProblemAreaType.RetrieveMetricsData);

                calculateStatus();
            }
            else {
                this.dto.setStatus(StatusType.Red);
                this.dto.setStatusMessage("Database issues.");
            }
        }
        finally {
            try {
                writter.unlock();
            }
            catch (IllegalMonitorStateException e) {
                // This will only happen if a thread which doesn't have the right
                // anyway to release this lock attempts to do so.
            }
        }
    }
    
    public void setDbWriteStatus(StatusType dbStatus, String dbStatusMessage) {
        try {
            writter.lock();

            this.dto.setDbStatus(dbStatus);
            this.dto.setDbStatusMessage(dbStatusMessage != null ? dbStatusMessage : "");

            if (this.dto.getDbStatus() == StatusType.Green) {
                ProblemsReportBo.getInstance().clearLastError(ProblemAreaType.DatabaseFull);
                ProblemsReportBo.getInstance().clearLastError(ProblemAreaType.InsertMetricsData);

                calculateStatus();
            }
            else {
                this.dto.setStatus(StatusType.Red);
                this.dto.setStatusMessage("Database issues.");
            }
        }
        finally {
            try {
                writter.unlock();
            }
            catch (IllegalMonitorStateException e) {
                // This will only happen if a thread which doesn't have the right
                // anyway to release this lock attempts to do so.
            }
        }
    }

    public boolean hasProblemsInArea(ProblemAreaType area) {
        final Iterator<ProblemAreaType> it = ProblemsReportBo.getInstance().getErrorAreas().iterator();

        while (it.hasNext()) {
            if (it.next() == area) {
                return true;
            }
        }

        return false;
    }

    public void stopTimer() {
        timer.cancel();
    }

    private static class SingletonHolder {
        public static final XbrmsStatusBo INSTANCE = new XbrmsStatusBo();
    }

    private XbrmsStatusBo() {
        this.dto = new XbrmsStatusDto();
        this.dto.setStatus(StatusType.Green);
        this.dto.setStatusMessage("OK");

        this.dto.setDbStatus(StatusType.Green);
        this.dto.setDbStatusMessage("OK");

        // check database connection status every 5 minutes, delay the first check by 5 minutes
        timer.schedule(new TimerTask() {
            public void run() {
                if (!Thread.currentThread().isInterrupted()) {
                    try {
                        // writter.lock(); doing a lock here creates a deadlock between this thread
                    	// and threads calling other setters on this object. The deadlock is a combination
                    	// of the Connection pool and this lock.

                    	Connection conn = null;
                        PreparedStatement stmt = null;

                        try {
                            conn = SSConnectionPool.getInstance().getConnection();

                            stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "SELECT count(*) FROM schema_version");
                            stmt.execute();

                            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
                        }
                        catch (SQLException e) {
                        	SSConnectionPool.handleSqlException(e, "Checking health status of database connection");
                        }
                        finally 
                        {
                            SSConnectionPool.getInstance().releaseResources(conn, stmt);
                            conn = null;
                            stmt = null;
                        }
                    }
                    catch (Exception ignore) {
                    	logger.error("Caught exception: ", ignore);
                    }
                }
                else {
                    timer.cancel();
                }
            }
        }, 60 * 5000, 60 * 5000);

        this.dto.setRecentProblems(ProblemsReportBo.getInstance().getDto());
    }

    private void calculateStatus() 
    {
        final List<ProblemDto> errors = ProblemsReportBo.getInstance().getErrors();

        if (errors == null || errors.size() == 0) {
            this.dto.setStatus(StatusType.Green);
            this.dto.setStatusMessage("OK");

            this.dto.setDbStatus(StatusType.Green);
            this.dto.setDbStatusMessage("OK");
        }

        final Iterator<ProblemAreaType> it = ProblemsReportBo.getInstance().getErrorAreas().iterator();
        
        ProblemDto problem = null;

        while (it.hasNext()) {
            switch (it.next()) {
                case ReadXbrmsConfig:
                    this.dto.setStatus(StatusType.Red);

                    problem = ProblemsReportBo.getInstance().getError(ProblemAreaType.ReadXbrmsConfig);
                    if (problem == null || (problem != null && problem.getMessage() == null))
                    	this.dto.setStatusMessage("Xbrms configuration issues.");
                    else
                    	this.dto.setStatusMessage(problem.getMessage());
                    

                case ConnectToXbrmsDb:
                    this.dto.setStatus(StatusType.Red);

                    if (this.dto.getStatusMessage() == null) {
                        this.dto.setStatusMessage("Database connection issues.");
                    }
                    
                    this.dto.setDbStatus(StatusType.Red);
                    
                    problem = ProblemsReportBo.getInstance().getError(ProblemAreaType.ConnectToXbrmsDb);
                    if (problem == null || (problem != null && problem.getMessage() == null))
                    	this.dto.setDbStatusMessage("Database connection issues.");
                    else
                    	this.dto.setDbStatusMessage(problem.getMessage());
               
                case IncorrectSchemaVersion:
                	this.dto.setStatus(StatusType.Red);

                    if (this.dto.getStatusMessage() == null) {
                        this.dto.setStatusMessage("Xbrms configuration issues.");
                    }
                    
                    this.dto.setDbStatus(StatusType.Red);
                    
                    problem = ProblemsReportBo.getInstance().getError(ProblemAreaType.IncorrectSchemaVersion);
                    if (problem == null || (problem != null && problem.getMessage() == null))
                    	this.dto.setDbStatusMessage("Incorrect schema version. Required version: " + this.dto.getRequiredDatabaseVersion());
                    else
                    	this.dto.setDbStatusMessage(problem.getMessage());
            }
        }
    }
    
    private void resolveNetworkAddress()
	{
		String ip = null;
		final String ipPrefix = XbrmsConfigBo.getInstance().getDto().getOwnIpPrefix();
		Collection<String> lIPs = null;
		try
		{
			lIPs = NetInterface.getOwnIpAddress(ipPrefix);
		}
		catch (SocketException e)
		{
			logger.error(ExceptionFormatter.format("Unable to get our own ip address", e));
		}
		
		if (lIPs!=null && lIPs.size()>0)
			ip = lIPs.iterator().next();
		
		if (ip == null)
		{
			String errorMessage = "Failed to retrieve my own hostname and/or ip using net prefix " + ipPrefix + ". This means that this xBRMS server will never become HA master. Go to xBRMS configuration page to configure the prefix.";
			ProblemsReportBo.getInstance().setLastError(ProblemAreaType.Networking, errorMessage);
			logger.info(errorMessage);
			
			return;
		}
		
		byte[] ownIp = new byte[4];
		String[] ipSegments = ip.split("\\.");
		try
		{
			for (int i = 0; i < ipSegments.length; i++)
			{
				ownIp[i] = (new Integer(ipSegments[i])).byteValue();
			}
		}
		catch (NumberFormatException e)
		{
			return;
		}
		
		final String[] nameIp = new String[] {null, null};
		try {
            java.net.InetAddress inetAddr = java.net.InetAddress.getByAddress(ownIp);
            nameIp[0] = inetAddr.getHostName();
            nameIp[1] = inetAddr.getHostAddress();
        }
        catch (Exception e) {
        	ProblemsReportBo.getInstance().setLastError(ProblemAreaType.Networking, "Failed to resolve my own hostname and/or ip address!");
        	logger.info("Failed to resolve my own hostname and/or ip address!", e);
        }
		
		dto.setHostname(nameIp[0]);
		dto.setIp(nameIp[1]);
	}

    ///// IBusinessObject interface begin ////

    @Override
    public XbrmsStatusDto getDto() {
        return this.dto;
    }

    @Override
    public void setDto(IDataTransferObject dto) {
        this.dto = (XbrmsStatusDto) dto;
    }

    ///// IBusinessObject interface end ////
}
