package com.disney.xband.xbrc.Controller;

import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.audit.model.AuditEvent.Category;
import com.disney.xband.common.lib.audit.model.AuditEvent.Type;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.lib.readertest.ReaderAction;
import com.disney.xband.lib.readertest.ReaderTest;
import com.disney.xband.lib.readertest.ReaderTestRunner;
import com.disney.xband.lib.xbrapi.Sequence;
import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;
import java.sql.Connection;

/**
 * Coordinates running of multiple reader tests.
 */
public class ReaderTestCoordinator 
{
	private static Logger logger = Logger
			.getLogger(ReaderTestCoordinator.class);
	private Hashtable<String, ReaderTestRunner> readerTests = new Hashtable<String, ReaderTestRunner>();

	public static ReaderTestCoordinator INSTANCE = new ReaderTestCoordinator();

	public void onTap(ReaderInfo ri, Xband xb, Guest guest) throws Exception 
	{
		ReaderTestRunner rtr = readerTests.get(ri.getName());

		if (rtr != null) 
		{
			if (!rtr.isAllowAcknowledge())
				throw new Exception(
						"The reader test did not finish yet so it cannot be acknowledged.");
			
			rtr.acknowledgeTest();
			return;
		}

		// Turn off tap and idle sequence
		Controller.getInstance().enableTapSequence(ri, false);
		Controller.getInstance().setIdleSequence(ri, false);
		
		// Give the reader time to turn lights off
		try { Thread.sleep(500); } catch (Exception e){}
		
		rtr = getReaderTestRunner(ri, xb, guest);
		readerTests.put(ri.getName(), rtr);
		rtr.start();
	}

	public void onShutdown() 
	{
		for (ReaderTestRunner rtr : readerTests.values()) 
		{
			rtr.stop();
		}
	}

	private void onReaderTestFinished(ReaderInfo ri) 
	{
		// restore previous idle and TAP sequences
		Controller.getInstance().enableTapSequence(ri, Processor.INSTANCE.getModel().isTapSequenceEnabled(ri));
		Controller.getInstance().setIdleSequence(ri, Processor.INSTANCE.getModel().isIdleSequenceEnabled(ri));
		
		ReaderTestRunner rtr = readerTests.get(ri.getName());

		// This should not happen
		if (rtr == null) 
		{
			logger.warn("Received notification that reader test finished for reader "
					+ ri.getName()
					+ ", but could not find the test object for this reader.");
			return;
		}

		readerTests.remove(ri.getName());
	}

	private ReaderTestRunner getReaderTestRunner(ReaderInfo ri, Xband xb, Guest guest)
			throws Exception 
	{
		ReaderTest readerTest = ReaderTest
				.readFromXmlFile("/usr/share/xbrc/readertest.xml");

		Properties prop = new Properties();
		prop.put(ReaderAction.PROPKEY_READER, ri.getName());
		prop.put(ReaderAction.PROPKEY_READER_LOCATION, ri.getLocation()
				.getName());
		prop.put(ReaderAction.PROPKEY_VENUE, ConfigOptions.INSTANCE
				.getControllerInfo().getName());
		prop.put(
				ReaderAction.PROPKEY_LEDSCRIPTS_DIR,
				InputValidator.validateDirectoryName(ConfigOptions.INSTANCE
						.getControllerInfo().getWwwdir())
						+ "/media/expanded/ledscripts");
		prop.put(ReaderAction.PROPKEY_SUCCESS_SEQUENCE, XBRCController
				.getInstance().getReaderSequence(ri, Sequence.success));
		prop.put(ReaderAction.PROPKEY_ERROR_SEQUENCE, XBRCController
				.getInstance().getReaderSequence(ri, Sequence.error));
		prop.put(ReaderAction.PROPKEY_FAILURE_SEQUENCE, XBRCController
				.getInstance().getReaderSequence(ri, Sequence.failure));
		
		if (ConfigOptions.INSTANCE.getControllerInfo().getReaderTestVolume() > 0)
			prop.put(ReaderAction.PROPKEY_SPEECH_VOLUME, 
						Integer.toString(ConfigOptions.INSTANCE.getControllerInfo().getReaderTestVolume()));

		ReaderTestRunner runner = new ReaderTestRunner(readerTest, ri, prop,
				new ReaderTestPassed(ri, xb, guest), new ReaderTestFailed(ri, xb, guest));

		return runner;
	}
	
	private void updateReaderTestStatus(ReaderInfo ri)
	{
		Connection conn = null;
		try
		{
			conn = XBRCController.getInstance().getPooledConnection();
			ReaderService.updateLastReaderTest(conn, ri.getId(), ri.getLastReaderTestTime(), 
					ri.isLastReaderTestSuccess(), ri.getLastReaderTestUser());	
		}
		catch(Exception e)
		{
			logger.error("Failed to update last reader test result for reader " + ri.getName(), e);
		}
		finally
		{
			if (conn != null)
				XBRCController.getInstance().releasePooledConnection(conn);
		}
		
	}

	class ReaderTestFailed implements Runnable 
	{
		private ReaderInfo ri;
		private Xband xb;
		private Guest guest;

		public ReaderTestFailed(ReaderInfo ri, Xband xb, Guest guest) 
		{
			this.ri = ri;
			this.xb = xb;
			this.guest = guest;
		}

		@Override
		public void run() 
		{
			onReaderTestFinished(ri);

			final IAudit auditor = Auditor.getInstance().getAuditor();
			auditor.audit(auditor.create(Type.WARN, Category.STATUS,
					"Reader test failed for reader " + ri.getName(),
					"READER_TEST_FAILED", ri.getName()));
			logger.warn("Reader test for reader " + ri.getName()
					+ " was not acknowledged and was marked as failed.");
			
			ri.setLastReaderTestTime(new Date());
			ri.setLastReaderTestSuccess(false);
			if (guest != null || xb != null)
				ri.setLastReaderTestUser(guest != null ? (guest.getFirstName() + " " + guest.getLastName()) : xb.getBandFriendlyName());
			else
				ri.setLastReaderTestUser("unknown");
			
			updateReaderTestStatus(ri);						
		}
	}

	class ReaderTestPassed implements Runnable 
	{
		private ReaderInfo ri;
		private Xband xb;
		private Guest guest;

		public ReaderTestPassed(ReaderInfo ri, Xband xb, Guest guest) 
		{
			this.ri = ri;
			this.xb = xb;
			this.guest = guest;
		}

		@Override
		public void run() 
		{
			onReaderTestFinished(ri);

			final IAudit auditor = Auditor.getInstance().getAuditor();
			auditor.audit(auditor.create(Type.INFO, Category.STATUS,
					"Reader test passed for reader " + ri.getName(),
					"READER_TEST_PASSED", ri.getName()));
			logger.info("Reader test for reader " + ri.getName()
					+ " was acknowledged as successful.");
			
			ri.setLastReaderTestTime(new Date());
			ri.setLastReaderTestSuccess(true);
			
			if (guest != null || xb != null)
				ri.setLastReaderTestUser(guest != null ? (guest.getFirstName() + " " + guest.getLastName()) : xb.getBandFriendlyName());
			else
				ri.setLastReaderTestUser("unknown");
			
			updateReaderTestStatus(ri);
		}
	}
}
