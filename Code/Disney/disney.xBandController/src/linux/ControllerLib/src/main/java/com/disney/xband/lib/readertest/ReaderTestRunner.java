package com.disney.xband.lib.readertest;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class ReaderTestRunner {
	private static Logger logger = Logger.getLogger(ReaderTestRunner.class);
	
	private ReaderInfo ri;
	private ReaderTest readerTest;
	private Thread thread = null;
	private Properties prop;
	private boolean run = true;
	private String logPrefix;
	private boolean acknowledged = false;
	private boolean allowAcknowledge = false;
	private Runnable onFailure;
	private Runnable onSuccess; 
	
	public ReaderTestRunner(ReaderTest readerTest, ReaderInfo ri, Properties prop,
							Runnable onSuccess, Runnable onFailure) {
		this.readerTest = readerTest;
		this.ri = ri;
		this.prop = prop;
		this.onSuccess = onSuccess;
		this.onFailure = onFailure;		
		logPrefix = "(readertest) " + ri.getName() + ": ";
	}
	
	public void start() {
		run = true;
				
		thread = new Thread() {
			public void run() {
				runTests();
				thread = null;
			}
		};
		thread.start();
	}
	
	public void stop() {
		run = false;

		wakeupThread();
	}
	
	public void waitForFinish(long milliseconds) throws InterruptedException {
		Thread tmpThread = thread;
		
		if (tmpThread == null)
			return;
		
		synchronized (tmpThread) {
			tmpThread.join(milliseconds);
		}
	}
	
	public void acknowledgeTest() {
		acknowledged = true;
		
		if (readerTest.getAcknowledgeAction() != null) {
			try {
				playAction(readerTest.getAcknowledgeAction().getAction());
			} catch(Exception e) {
				logger.error("Exception while running reader action " + readerTest.getAcknowledgeAction().getAction().getName()
						+ " of type " + readerTest.getAcknowledgeAction().getClass().toString(), e);
			}
		}
		
		wakeupThread();
	}
	
	public boolean isAllowAcknowledge() {
		return allowAcknowledge;
	}
	
	private void wakeupThread() {
		Thread tmpThread = thread;
		
		if (tmpThread == null)
			return;
		
		synchronized (tmpThread) {
			// wake up our send thread to let it exit
			tmpThread.notify();
		}
	}
	
	private void playAction(ReaderAction ra) throws Exception {
		ra.initialize(prop, ri);
		ra.performAction();
		
		if (ra.getWaitMs() != null && ra.getWaitMs() > 0){
			try {
				logger.info(logPrefix + "Pausing for " + ra.getWaitMs() + " milliseconds");
				synchronized(thread) {
					thread.wait(ra.getWaitMs());
				}
			} catch (InterruptedException e) {			
			}
		}
	}

	private void runTests() {
		
		for (ReaderUnitTest test : readerTest.getTests())
		{		
			if (!run)
				break;
			
			for (ReaderActionContainer ac : test.getActions()) {
				
				if (!run)
					break;
								
				ReaderAction ra = ac.getAction();
				logger.info(logPrefix + "Starting action: " + ra.getName());
				
				try {
					playAction(ra);
				} catch(Exception e) {
					logger.error("Exception while running reader action " + ra.getName()
							+ " of type " + ra.getClass().toString(), e);
				}
				
				logger.info(logPrefix + "Finished action: " + ra.getName());
			}			
		}
		
		if (!run)
			return;
		
		// Wait for the user to acknowledge the tests.
		if (readerTest.getWaitForAckMs() != null) {
			logger.info(logPrefix + "Waiting for " + readerTest.getWaitForAckMs() + " milliseconds for operator to acknowledge the test");
			
			try {
				synchronized(thread) {
					allowAcknowledge = true;
					thread.wait(readerTest.getWaitForAckMs());
				}
			} catch (InterruptedException e) {			
			}
		}
		
		ReaderAction finalAction = null;
		
		if (acknowledged) {
			logger.info(logPrefix + "The reader test was acknownledged by the operator.");			
			if (readerTest.getPassedAction() != null)
				finalAction = readerTest.getPassedAction().getAction();			
		}
		else {
			logger.warn(logPrefix + "The reader test was not acknowledged by the operator.");
			if (readerTest.getFailedAction() != null)
				finalAction = readerTest.getFailedAction().getAction();			
		}
		
		if (finalAction != null) {
			try {
				playAction(finalAction);
			} catch (Exception e) {
				logger.warn(logPrefix + "Failed to play action", e);
			}
		}
		
		if (acknowledged) {
			if (onSuccess != null)
				onSuccess.run();
		} else {
			if (onFailure != null)
				onFailure.run();			
		}
	}
}
