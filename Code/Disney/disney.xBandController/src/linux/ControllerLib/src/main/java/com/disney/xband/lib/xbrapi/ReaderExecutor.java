package com.disney.xband.lib.xbrapi;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.xbrc.lib.db.XbioImageService;
import com.disney.xband.xbrc.lib.entity.XbioImage;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;

public class ReaderExecutor
{
	private static final Logger logger = Logger.getLogger(ReaderExecutor.class);
	
	ThreadPoolExecutor executor;
	
	private static class SingletonHolder { 
		public static final ReaderExecutor instance = new ReaderExecutor();
	}
	
	public static ReaderExecutor getInstance() {
		return SingletonHolder.instance;
	}
	
	private ReaderExecutor() {
		
	}

	public void initialize(Collection<ReaderInfo> readers)
	{
		if (executor != null)
			executor.shutdown(); 
		
		// When creating the thread pool we have to consider all readers for which we need to
		// post HTTP requests from the processor thread. This will include all readers with
		// lights (TAP/TAP+XBIO) as well as xBR readers that are transmitters.
		
		int corePoolSize = 1;
		int maximumPoolSize = 1;
		int threadRetentionTime = 20;
		
		int totalReaders = 0;
		for (ReaderInfo r : readers)
		{
			if (r.getType().hasLight() || r.isTransmitter())
				totalReaders++;
		}
		
		if (totalReaders > 0)
		{
			corePoolSize = Math.max(1, totalReaders/3);
			maximumPoolSize = totalReaders;
		}
		
		logger.info("Creating " + corePoolSize + " core reader threads allowing to grow up to " + maximumPoolSize + " threads for asychronous HTTP calls to the readers."
				+ " Idle thread retention time is " + threadRetentionTime + " seconds.");
		
		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, threadRetentionTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100));
	}
	
	public void shutdown()
	{
		if (executor != null)
			executor.shutdown(); 		
	}
	
	// Blocks until all threads have finished working or timeout occurs.
	// The shutdown() method must be called before calling this function.
	// Throws InterruptedException if interrupted while waiting.
	public boolean awaitTermination(Long waitMs) throws InterruptedException
	{
		if (executor == null)
			return true;
		
		return executor.awaitTermination(waitMs, TimeUnit.MILLISECONDS);
	}

	//
	// resetBioTapReader
	// 
	
	class ResetBioTapReader implements Runnable
	{	
		ReaderInfo reader;
		public ResetBioTapReader(ReaderInfo reader)
		{
			this.reader = reader;
		}

		@Override
		public void run()
		{
			ReaderApi.resetBioTapReader(reader);
		}
	}
	
	public void resetBioTapReader(ReaderInfo reader)
	{
		executor.execute(new ResetBioTapReader(reader));
	}
	
	//
	// biometricEnroll
	// 
	
	class BiometricEnroll implements Runnable
	{	
		ReaderInfo reader;
		public BiometricEnroll(ReaderInfo reader)
		{
			this.reader = reader;
		}

		@Override
		public void run()
		{
			int retCode = ReaderApi.biometricEnroll(reader);
			
			// try again 2 more times if it fails
			for (int i = 2; i <= 3 && retCode != 200; i++)
			{
				logger.warn("Attempt number " + i + " trying to talk to reader " + reader.getName());
				retCode = ReaderApi.biometricEnroll(reader);
			}
		}
	}
	
	public void biometricEnroll(ReaderInfo reader)
	{
		executor.execute(new BiometricEnroll(reader));
	}
	
	//
	// askForBioImageEvent
	// 
	
	class AskForBioImageEvent implements Runnable
	{	
		ReaderInfo reader;
		String uid;
		Long transactionId;
		Long templateId;
		
		public AskForBioImageEvent(ReaderInfo reader, String uid, Long transactionId, Long templateId)
		{
			this.reader = reader;
			this.uid = uid;
			this.transactionId = transactionId;
			this.templateId = templateId;
		}

		@Override
		public void run()
		{
			ReaderApi.askForBioImageEvent(reader, uid, transactionId, templateId);
		}
	}
	
	public void askForBioImageEvent(ReaderInfo reader, String uid, Long transactionId, Long templateId)
	{
		executor.execute(new AskForBioImageEvent(reader, uid, transactionId, templateId));
	}
	
	//
	// setReaderSequence
	// 
	
	class SetReaderSequence implements Runnable
	{	
		ReaderInfo reader;
		Sequence sequence;
		
		public SetReaderSequence(ReaderInfo reader, Sequence sequence)
		{
			this.reader = reader;
			this.sequence = sequence;
		}

		@Override
		public void run()
		{
			int retCode = XBRCController.getInstance().setReaderSequence(reader, sequence);
			
			// try again 2 more times if it fails
			for (int i = 2; i <= 3 && retCode != 200; i++)
			{
				logger.warn("Attempt number " + i + " trying to talk to reader " + reader.getName());
				retCode = XBRCController.getInstance().setReaderSequence(reader, sequence);
			}
		}
	}
	
	public void setReaderSequence(ReaderInfo reader, Sequence sequence)
	{
		executor.execute(new SetReaderSequence(reader, sequence));
	}
	
	//
	// setReaderSequence
	// 
	
	class SetIdleSequence implements Runnable
	{	
		ReaderInfo reader;
		boolean on;
		
		public SetIdleSequence(ReaderInfo reader, boolean on)
		{
			this.reader = reader;
			this.on = on;
		}

		@Override
		public void run()
		{
			XBRCController.getInstance().setIdleSequence(reader, on);
			XBRCController.getInstance().enableTapSequence(reader, on);
		}
	}
	
	public void setIdleSequence(ReaderInfo reader, boolean on)
	{
		executor.execute(new SetIdleSequence(reader, on));
	}
	
	//
	// saveXbioImage
	// 
	
	class SaveXbioImage implements Runnable
	{	
		private XbioImage xbioImage;
		
		public SaveXbioImage(XbioImage xbioImage)
		{
			this.xbioImage = xbioImage;
		}

		@Override
		public void run()
		{
			Connection conn = null;
			try {
				if (logger.isTraceEnabled())
					logger.trace("Inserting XbioImage with images " + xbioImage.getImages().length() + " characters long");
				conn = XBRCController.getInstance().getPooledConnection();
				XbioImageService.save(conn, xbioImage);
				logger.trace("After XbioImage insert");
			} catch (Exception e) {
				logger.error("Failed to update XbioImage with id " + xbioImage.getId(), e);
			}
			finally {
				XBRCController.getInstance().releasePooledConnection(conn);
			}
		}
	}
	
	public void saveXbioImage(XbioImage xbioImage)
	{
		executor.execute(new SaveXbioImage(xbioImage));
	}
	
	//
	// setReaderSequence
	// 
	
	class SetReaderColor implements Runnable
	{	
		ReaderInfo reader;
		String color;
		Long timeout;
		
		public SetReaderColor(ReaderInfo reader, String color, Long timeout)
		{
			this.reader = reader;
			this.color = color;
			this.timeout = timeout;
		}

		@Override
		public void run()
		{
			ReaderApi.setReaderColor(reader, color, timeout);
		}
	}
	
	public void setReaderColor(ReaderInfo reader, String color, Long timeout)
	{
		executor.execute(new SetReaderColor(reader, color, timeout));
	}
	
	//
	// sendBandCommands
	// 
	
	class SendBandCommands implements Runnable
	{	
		ReaderInfo reader;
		List<XbrBandCommand> commands;
		XMIT_MODE mode;
		String group;
		
		public SendBandCommands(List<XbrBandCommand> commands, ReaderInfo reader,
				XMIT_MODE mode, String group)
		{
			this.reader = reader;
			this.commands = commands;
			this.mode = mode;
			this.group = group;
		}

		@Override
		public void run()
		{
			ReaderApi.sendBandCommands(commands, reader, mode, group);
		}
	}
	
	public void sendBandCommands(List<XbrBandCommand> commands, ReaderInfo reader,
			XMIT_MODE mode, String group)
	{
		executor.execute(new SendBandCommands(commands, reader, mode, group));
	}
	
	//
	// sendDeleteBandCommands
	// 
	
	class SendDeleteBandCommands implements Runnable
	{	
		ReaderInfo reader;
		
		public SendDeleteBandCommands(ReaderInfo reader)
		{
			this.reader = reader;
		}

		@Override
		public void run()
		{
			try {
				ReaderApi.sendDeleteBandCommands(reader);
			} catch (Exception e) {
				logger.error("Failed to send DeleteBandCommands to reader " + reader.getName(), e);
			}
		}
	}
	
	public void sendDeleteBandCommands(ReaderInfo reader)
	{
		executor.execute(new SendDeleteBandCommands(reader));
	}
}
