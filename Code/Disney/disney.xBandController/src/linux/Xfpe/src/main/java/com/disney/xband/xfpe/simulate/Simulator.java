package com.disney.xband.xfpe.simulate;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.disney.xband.xfpe.XfpeProperties;
import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.model.XfpeReader;
import com.disney.xband.xfpe.omni.OmniServer;

/*
 * Park Entry guest simulator
 */
public class Simulator {
	private LinkedBlockingDeque<PEGuestTest> testQueue;
	private LinkedHashMap<String,PEGuestTest> allTestBands;
	private LinkedHashMap<String,TestThread> testThreads;
	private Date suiteStartTime;
	private PEGuestTestSuite lastSuite = null;
	private List<TestResult> lastSuiteResults = null;
	private Thread omniThread = null;
	private OmniServer omniServer = null;
	private static Logger logger = Logger.getLogger(Simulator.class);
	private AtomicInteger runningTests = new AtomicInteger(0);
	
	private static class SingletonHolder { 
		public static final Simulator instance = new Simulator();
	}
	
	public static Simulator getInstance() {
		return SingletonHolder.instance;
	}
	
	private Simulator() {
		testQueue = new LinkedBlockingDeque<PEGuestTest>();
		testThreads = new LinkedHashMap<String,TestThread>();
		allTestBands = new LinkedHashMap<String,PEGuestTest>();
		
		reload();
	}
	
	public void startedTest()
	{
		runningTests.incrementAndGet();
	}
	
	public void finishedTest()
	{
		runningTests.decrementAndGet();
	}
	
	public void reload()
	{	
		LinkedHashMap<String,PEGuestTest> tests = new LinkedHashMap<String,PEGuestTest>();
		
		// Get all tests
		for (PEGuestTestSuite suite : DB.findAllTestSuites()) {
			for (PEGuestTest t : DB.findAllTests(suite.getId())) {
				tests.put(t.getBandId(), t);
			}
		}	
		
		synchronized(this) {
			allTestBands = tests;
		}
	}
	
	/*
	 * If we want to do orderly shutdown of all threads then call this function.
	 */
	public void shutdown() {
		synchronized (this) {
			// Stop the omniThread
			if (omniServer != null) {
				logger.debug("Stopping omni thread...");
				omniServer.stop();
				if (omniThread != null) {
					try {
						// Wait for the thread to finish.
						omniThread.join();
					} catch (InterruptedException e) {
						logger.error("Exception while trying to stop the omni thread.", e);
					}
					omniThread = null;
				}
			}
		}
		
		stopTests();
	}
	
	/*
	 * Stop all tests.
	 */
	public void stopTests() {
		synchronized(this) {
			logger.debug("Stopping all tests...");
			for (TestThread t : testThreads.values()) {
				t.stopTest();
			}
			logger.debug("Finished stopping all tests.");
		}
	}
	
	/*
	 * Start all tests.
	 */
	public void startTests(Long suiteId, int maxReaders) {
		
		stopTests();
		
		reload();
		
		synchronized(this) {
			
			logger.debug("Starting all tests");
						
			try
			{
				lastSuite = DB.getPEGuestTestSuite(suiteId);
			}
			catch (Exception e)
			{
				logger.error(e);
			}
			lastSuiteResults = new LinkedList<TestResult>();
			
			suiteStartTime = new Date();
			
			double x = XfpeProperties.getInstance().getXfpeSimConfig().getGuestQueueStartX();
			double y = XfpeProperties.getInstance().getXfpeSimConfig().getGuestQueueStartY();
			
			testThreads.clear();
			int nReaders = 0;
			
			// Get all readers and create a test thread for each reader
			for (XfpeReader r : XfpeController.getInstance().getReaders().values())
			{
				if (nReaders++ >= maxReaders)
					break;
				testThreads.put(r.getReaderId(),new TestThread(x,y,r,this));
			}
			
			logger.info("Starting tests using " + nReaders + " readers.");
			
			testQueue.clear();
			for (PEGuestTest t : DB.findAllTests(suiteId)) {
				testQueue.add(t);
			}
			
			runningTests.set(0);
			
			// Now start all the tests.
			for (TestThread t : testThreads.values()) {
				t.start();
			}
			
			logger.debug("Finished starting all tests.");
		}
	}
	
	public void startOmniSimulator() {
		synchronized(this) {
			// The omni thread keeps on running between tests.
			if (omniThread == null) {
				omniThread = new Thread(){public void run(){runOmniThread();}};
				omniThread.start();
			}
		}
	}
	
	
	/*
	 * This function can be called when some external event happens that the Guest may be interested in.
	 */
	public void notifyOfEvent(String bandId, SimulatorEvents event, String eventDesc) {
		synchronized(this) {
			// Find a test with a matching bandId and notify it.
			for (TestThread t : testThreads.values()) {
				if (t.getTest() != null) {
					if (t.getTest().getBandId().equals(bandId)) {
						t.notifyOfEvent(event,eventDesc);
					}
				}
			}
		}
	}
	
	/*
	 * This function can be called when some external event happens that the Guest may be interested in.
	 */
	public void notifyOfReaderEvent(String readerId, SimulatorEvents event, String eventDesc) {
		synchronized(this) {
			synchronized(this) {
				TestThread test = testThreads.get(readerId);
				if (test != null)
					test.notifyOfEvent(event, eventDesc);
			}
		}
	}
	
	protected void runOmniThread() {
		omniServer = new OmniServer();
		try {
			omniServer.start(XfpeProperties.getInstance().getXfpeSimConfig().getOmniThreadPort());
		} catch (IOException e) {
			logger.fatal("Failed to start the OmniServer thread.",e);
		}
	}
	
	/*
	 * Return the reader to the pool.
	 */
	public PEGuestTest getNextTest() {
		return testQueue.poll();
	}
	
	public boolean isTestRunning() {
		/*
		synchronized(this) {
			for (TestThread t: testThreads.values()) {
				if (t.isAlive())
					return true;
			}
		}
		return false;
		*/
		return runningTests.get() > 0;
	}
	
	public void logoutReader(int omniDeviceId) {
		//if (omniServer != null)
		//	omniServer.logoutReader(omniDeviceId);
	}
	
	public PEGuestTest getGuestTest(String bandId) {
		synchronized(this) {
			return allTestBands.get(bandId);
		}
	}

	public Date getSuiteStartTime()
	{
		return suiteStartTime;
	}

	public PEGuestTestSuite getLastSuite()
	{
		return lastSuite;
	}

	public List<TestResult> getLastSuiteResults()
	{
		return lastSuiteResults;
	}
}
