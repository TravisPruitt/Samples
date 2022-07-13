package com.disney.xband.xbrc.Controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ZipUtils;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.xbrc.Controller.Log.EventdumpLog;

public class EventdumpFileLog implements EventdumpLog {

	private static Logger logger = Logger.getLogger(EventdumpFileLog.class);
	
	private RandomAccessFile raf = null;
	private RandomAccessFile rafRead = null;

	public void close()
	{
		if (raf != null)
		{
			try
			{
				raf.close();
			}
			catch (IOException e)
			{
				logger.warn("Failed to close event log output stream.", e);
			}
		}
		if (rafRead != null)
		{
			try
			{
				rafRead.close();
			}
			catch (IOException e)
			{
				logger.warn("Failed to close event log input stream.", e);
			}
		}
		
		raf = null;
		rafRead = null;
	}

	private void openEKGFile()
	{
		if (raf != null)
			return;

		String szFilename = ConfigOptions.INSTANCE.getControllerInfo().getEventDumpFile();

		if (szFilename.isEmpty() || szFilename.startsWith("#"))
			return;

        szFilename = "/var/log/xbrc/" + InputValidator.validateFileName(InputValidator.getFileName(szFilename));

		// create the dump file
		try
		{
			raf = new RandomAccessFile(szFilename, "rw");
			
			// move to the end
			raf.seek(raf.length());

			// now open the second handle for reading via HTTP
			rafRead = new RandomAccessFile(szFilename, "r");
		}
		catch (IOException e)
		{
			logger.error("Failed to open the event log file " + szFilename, e);
		}
	}

	public void logEKG(String s)
	{
		// dump the events to the event dump file
		if (raf == null)
			openEKGFile();
		
		// if no raf, then no loggy
		if (raf==null)
			return;		

		synchronized (raf)
		{
			// write w/ autoflush
			try
			{
				if (raf.getFilePointer() > (((long)ConfigOptions.INSTANCE.getControllerInfo().getEventdumpmaxsizemb()) <<  20))
				{
					// we will be closing both read and write pointers so synchronize on both 
					synchronized (rafRead)
					{
						rollover();
					}
					
					if (raf == null)
						return;
				}
				
				raf.writeBytes(s + "\n");
			}			
			catch (IOException e)
			{
				logger.error("Failed to write to the event log file.", e);
			}
		}
	}
	
	class CompressThread implements Runnable
	{
		private String filePath;
		
		public CompressThread(String filePath)
		{
			this.filePath = filePath;
		}
		
		@Override
		public void run()
		{
			try
			{
				ZipUtils.gzip(filePath);
				new File(filePath).delete();
			}
			catch (IOException e)
			{
				logger.error("Failed to gzip file " + filePath, e);
			}
		}
	}
	
	private void compressFile(String filePath)
	{
		Runnable compressThread = new CompressThread(filePath);
		new Thread(compressThread).start();
	}
	
	private void rollover()
	{
		try
		{
			close();
			
			String szFilename = ConfigOptions.INSTANCE.getControllerInfo()
					.getEventDumpFile();
			
			// Delete the oldest file if necessary
			File file = new File(szFilename + "." + ConfigOptions.INSTANCE.getControllerInfo().getEventdumpmaxfiles());
			if (file.exists())
			{
				if (!file.delete())
					logger.error("Failed to delete file " + file.getAbsolutePath());
			}
			else
			{
				File zipped = new File(file.getAbsolutePath() + ".gz");
				if (zipped.exists())
				{
					if (!zipped.delete())
						logger.error("Failed to delete file " + zipped.getAbsolutePath());
				}
			}
			
			// Rename the rest of the archived files
			for (int i = ConfigOptions.INSTANCE.getControllerInfo().getEventdumpmaxfiles() - 1; i >= 1; i--)
			{
				file = new File(szFilename + "." + i);
				
				if (file.exists())
				{
					if (!file.renameTo(new File(szFilename + "." + (i + 1))))
						logger.error("Failed to rename file " + file.getAbsolutePath());
				}
				else
				{
					File zipped = new File(file.getAbsolutePath() + ".gz");
					if (zipped.exists())
					{
						if (!zipped.renameTo(new File(szFilename + "." + (i + 1) + ".gz")))
							logger.error("Failed to delete file " + zipped.getAbsolutePath());
					}
				}
			}
			
			String newFileName = szFilename + ".1";
			if (new File(szFilename).renameTo(new File(newFileName)))
				compressFile(newFileName);
			else
				logger.error("Failed to reaname " + szFilename);
			
			openEKGFile();
		}
		catch(Exception e)
		{
			logger.error("Failed to rollover the eventdump file.", e);
		}
	}

	public long getEKGPosition()
	{
		if (raf != null)
		{
			synchronized (raf)
			{
				try
				{
					return raf.getFilePointer();
				}
				catch (IOException e)
				{
					logger.error("Exception in getEKGPosition", e);
				}
			}
		}
		return 0;
	}

	public String readEKGFromPosition(long lPosition, int cMaxLines)
	{
		StringBuilder sb = new StringBuilder();

		if (rafRead == null)
			return "0"; // the file position

		synchronized (rafRead)
		{
			try
			{
				// move to the mark
				if (lPosition >= 0)
					rafRead.seek(lPosition);

				// read
				for (int cLine = 0; cLine < cMaxLines; cLine++)
				{
					String sLine = rafRead.readLine();
					if (sLine == null)
						break;

					sb.append(sLine);
					sb.append("\n");
				}
			}
			catch (IOException e)
			{
				logger.error("Failed to read the event log file.", e);
			}

			// update position
			try
			{
				lPosition = rafRead.getFilePointer();
			}
			catch (IOException e)
			{
				logger.error("Failed to read the event log file.", e);
			}

			// prepend the new position
			return Long.toString(lPosition) + "\n" + sb.toString();
		}

	}
}
