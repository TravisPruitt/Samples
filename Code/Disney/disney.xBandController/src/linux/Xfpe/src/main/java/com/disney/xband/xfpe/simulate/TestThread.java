package com.disney.xband.xfpe.simulate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.GuestInfo;
import com.disney.xband.xfpe.XfpeProperties;
import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.db.GuestPositionService;
import com.disney.xband.xfpe.model.XfpeReader;
import com.disney.xband.xfpe.model.XfpeReaderLight;

public class TestThread extends Thread {
	private static Logger logger = Logger.getLogger(TestThread.class);
	private PEGuestTest test;
	private Simulator simulator;
	boolean run = true;
	private String currentAction = "";
	private Object accessMutext = new Integer(0);
	private double x, y;
	private GuestPositionService gps = new GuestPositionService();
	// This makes the guest move.
	private double slope = 0;
	private double yintercept = 0;
	private SimulatorEvents lastEvent = SimulatorEvents.TapLightOff;
	private XfpeReader reader;
	private LinkedList<SimulatorEvents> eventNotifications = new LinkedList<SimulatorEvents>();

	public TestThread(double x, double y, XfpeReader reader, Simulator simulator) {
		this.reader = reader;
		this.simulator = simulator;
		this.x = x;
		this.y = y;
		reader.setCurrentTest(this);
	}

	public void stopTest() {
		if (!this.isAlive())
			return;

		logger.debug(test.getDesc() + " => Stopping simulate thread.");
		run = false;
		try {
			// Make sure we release the thread if it is sitting in a wait()
			// call.
			synchronized (this) {
				this.notify();
			}
			// Wait for the thread to finish.
			this.join();
		} catch (InterruptedException e) {
			logger.error(test.getDesc() + "," + test.getReader().getReaderId() + " => Exception while trying to stop the simulation thread.", e);
		}
	}

	private void runTest()
	{
		logger.info(test.getDesc() + " => Starting test");
		
		synchronized(this) {
			lastEvent = SimulatorEvents.TapLightOff;
			eventNotifications.clear();
		}
		
		// clear reader in case it was in BLUELANE state
		try
		{
			if (!XfpeProperties.getInstance().getXfpeSimConfig().getCastMemberRfid().isEmpty() && DB.isParkentryModel())
			{
				if (test.getReader().getCurrentLight() == XfpeReaderLight.blue) {
					logger.info(test.getDesc() + " => Reader in blue light state. Tapping cast member card to reset..");
					XfpeController.getInstance().handleGuestTap(test.getReader().getReaderId(),						
															XfpeProperties.getInstance().getXfpeSimConfig().getCastMemberRfid());
					// wait for the light to be reset, or timeout
					synchronized(this) {
						this.wait(4000);
					}
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to send a cast member tap event to clear the reader", e);
		}
		
		// set the starting position for the guest
		x = XfpeProperties.getInstance().getXfpeSimConfig().getGuestQueueStartX();
		y = XfpeProperties.getInstance().getXfpeSimConfig().getGuestQueueStartY();
		
		// Calculate the direction where the guest is going.
		// Use the line formula to make the guest walk to the reader.
		slope = (test.getReader().getReader().getY() - y) /
				(test.getReader().getReader().getX() - x);
		yintercept = y - slope*x;
		
		// First make the guest walk to the reader;
		if (XfpeProperties.getInstance().getXfpeSimConfig().isSimulateWalking())
		{
			while(run && x < test.getReader().getReader().getX()) {
				try {
					sleep(300);
				} catch (InterruptedException e) {
				}
				x += 4;
				if (x > test.getReader().getReader().getX()) {
					x = test.getReader().getReader().getX();
				}
				y = slope*x + yintercept;
				saveGuestPosition();
				setCurrentAction("Guest is " + (new Double((test.getReader().getReader().getX() - x) / 3)).intValue() + " feet from the reader." );
			}
		} 
		else
		{
			x = test.getReader().getReader().getX();
			y = test.getReader().getReader().getY();
		}
		
		for (PEGuestAction action : test.getActions()) {
			if (!run) {
				logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Test aborted by user request. ");
				break;
			}
			
			saveGuestPosition();
			
			logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Starting action: " + action.getDesc());
			setCurrentAction(action.getDesc());
			
			// If this action requires external event the we must first wait for that event.
			
			if (action.getFireAfterEvent()) {
				try {
					synchronized(this) {
						logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Waiting for event.");
						// If there is a pending event the don't wait for it..
						if (eventNotifications.peek() != null)
							eventNotifications.pop();
						else
						{
							this.wait();
							// there was an event, so just remove it..
							if (eventNotifications.peek() != null)
								eventNotifications.pop();
						}
						logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Finished waiting for event. ");
					}
				} catch (InterruptedException e) {
					logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => TestThread interrupted while waiting for notify.");
					break;
				}
			}
						
			if (run && action.getDelaySec() > 0) {
				try {
					synchronized(this) {
						logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Pausing for " + action.getDelaySec() + " seconds.");
						this.wait(action.getDelaySec() * 1000);
						logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Finished a pause.");
					}
				} catch (InterruptedException e) {
					logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Interrupted while pausing.");
					break;
				}
			}
			
			if (!run) {
				logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Test aborted by user request.");
				break;
			}
			
			if (lastEvent == SimulatorEvents.TapLightGreen || lastEvent == SimulatorEvents.TapLightBlue)
			{
				// Need to finish this test since we got our final result.
				logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Received " + lastEvent.toString() + " event. Finishing test.");				
				break;
			}
			
			logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Performing action: " + action.getDesc());
			
			switch(action.getType()) {
			case TAP:
				try {
					XfpeController.getInstance().handleGuestTap(test.getReader().getReaderId(), test.getBandId());
				} catch (Exception e) {
					logger.error(test.getDesc() + "," + test.getReader().getReaderId() + " => Failed to process TAP action.",e);
				}
				break;
			case SCAN:
				try {
					XfpeController.getInstance().handleGuestScan(test.getReader().getReaderId(), action.getData());
				} catch (Exception e) {
					logger.error(test.getDesc() + "," + test.getReader().getReaderId() + " => Failed to process SCAN action.",e);
				}
				break;
			}
		}		
		
		if (XfpeProperties.getInstance().getXfpeSimConfig().isSimulateWalking())
		{
			// Walk away from the reader or stay if the light is blue.
			if (run && (lastEvent == SimulatorEvents.TapLightGreen || !DB.isParkentryModel())) {
				while(run && x < 130) {
					try {
						sleep(300);
					} catch (InterruptedException e) {
					}
					x += 4;
					saveGuestPosition();
					setCurrentAction("Guest is leaving the reader. (" + new Double((x - test.getReader().getReader().getX()) / 10).intValue() + " feet away from the reader)" );
				}	
			}
		}
		else
		{
			x = 130;
		}
	
		boolean reportReaderTestResults = XfpeProperties.getInstance().getXfpeSimConfig().getReportReaderTestResults();
		
		//
		// Deal with the test results
		//
		
		TestResult tr = new TestResult();
		tr.setTest(test);

		// actual result
		String actualResult = "ABANDONED";		// This can also be considered an unknown state
		if (lastEvent == SimulatorEvents.TapLightGreen)
			actualResult = "ENTERED";
		else if (lastEvent == SimulatorEvents.TapLightBlue)
			actualResult = "BLUELANE";		
		tr.setActualResult(actualResult);
		
		simulator.getLastSuiteResults().add(tr);
		
		if (reportReaderTestResults) {
			saveTestResults(tr);
		}
		
		saveGuestPosition();
		
		logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Finished test");
	}
	
	public void run() {
	
		simulator.startedTest();
		
		int guestentryloop = XfpeProperties.getInstance().getXfpeSimConfig().getGuestEntryLoop(); 
		
		synchronized (this) {
			test = simulator.getNextTest();			
		}
	
		while (run && test != null)
		{
			test.setReader(reader);
			
			try
			{				
				for (int q=0; run && q < guestentryloop; q++) {
				
					if (guestentryloop > 0 ) {
						 try
						 {
						 	sleep(300);    
						 }
						 catch (Exception e)
						 {}
					}
					runTest();					
				}				
			}
			catch(Exception e)
			{
				logger.error("Caught exception while running test thread: ", e);
			}
			finally
			{
				test.setReader(null);				
			}
			
			synchronized (this) {
				test = simulator.getNextTest();
			}
		}
		
		simulator.finishedTest();
	}

	public void notifyOfEvent(SimulatorEvents event, String eventDesc) {
		synchronized (this) {
			// Ignore the EntryStartScan event as it is a duplicate of FpLightOn event
			if (event == SimulatorEvents.EntryStartScan)
				return;
			
			lastEvent = event;
			if (test == null)
				return;
			eventNotifications.push(event);
			this.notify();
			if (test.getReader() != null)
				logger.info(test.getDesc() + "," + test.getReader().getReaderId() + " => Notified of event: " + event.name() + ", " + eventDesc + ".");
			else
				logger.info(test.getDesc() + " => Notified of event: " + event.name() + ", " + eventDesc + ".");
		}
	}

	private void setCurrentAction(String currentAction) {
		synchronized (accessMutext) {
			this.currentAction = currentAction;
		}
	}

	public String getCurrentAction() {
		synchronized (accessMutext) {
			return this.currentAction;
		}
	}

	public String getDesc() {
		return test.getDesc();
	}

	public PEGuestTest getTest() {
		return test;
	}
	
	private void saveGuestPosition() {
		if (!XfpeProperties.getInstance().getXfpeSimConfig().isSimulateWalking())
			return;
		
		GuestInfo gi = new GuestInfo(test.getBandId(), 
									 this.x, this.y,
									 // set hasxPass to true for guests that will enter to show them in
									 // different color
									 test.getFinalResult() != PEGuestTestResult.ENTERED);
		try {
			gps.save(gi);
		} catch (SQLException e) {
			logger.error("Failed to save GuestPosition", e);
		}
	}
	
	private void saveTestResults(TestResult tr) {
		Connection conn = null;
		PreparedStatement stmt = null;	
		
		try
		{
			conn = XfpeProperties.getInstance().getConn();
			if (conn==null)
			{
				logger.warn(test.getDesc() + " => Failed to get database connnection. Aborting save of test results.");
				return;
			}
			
			stmt = conn.prepareStatement("insert into TestResults (suiteId, suiteStartTime, guestId, bandId, readerName, endTime, expectedResult, actualResult, testResult)" +
					   " values (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, test.getSuiteId().toString());
			stmt.setTimestamp(2,new java.sql.Timestamp(simulator.getSuiteStartTime().getTime()));			
			stmt.setString(3, test.getId().toString());
			stmt.setString(4, test.getBandId().toString());
			stmt.setString(5, test.getReader().getReaderId());
			
			Date endTime = new Date();
			stmt.setTimestamp(6,new java.sql.Timestamp(endTime.getTime()));
			
			String expectedResult = test.getFinalResult().toString();
			stmt.setString(7, expectedResult);
			
			stmt.setString(8, tr.getActualResult());
			
			// test result
			stmt.setBoolean(9, (expectedResult.compareTo(tr.getActualResult()) == 0) ? true : false);
			
			stmt.execute();
			stmt.close();
		} 
		catch (Exception e)
		{
			logger.error("SQL error updating testResults table: " + e.getLocalizedMessage(), e);
		}
		finally
		{
			try {
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e) {
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                }
            }
		}
	}
}

