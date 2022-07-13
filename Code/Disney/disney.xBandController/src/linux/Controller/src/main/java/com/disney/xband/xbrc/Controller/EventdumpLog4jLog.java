package com.disney.xband.xbrc.Controller;

import java.util.concurrent.atomic.AtomicReferenceArray;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.Controller.Log.EventdumpLog;

public class EventdumpLog4jLog implements EventdumpLog {
	
	private static Logger logger = Logger.getLogger("eventdump");
		
	private AtomicReferenceArray<String> buffer;
	private Object lock = new Object();
	private int position = -1;
	private long bufferCount = 0;

	public EventdumpLog4jLog(int bufferSize)	
	{		
		buffer = new AtomicReferenceArray<String>(bufferSize);
	}
	
	@Override
	public void logEKG(String s) 
	{
		logger.info(s);
	
		synchronized(lock)
		{
			position++;
			
			if (position >= buffer.length())
			{
				position = 0;
				bufferCount++;
				
				if ((bufferCount * buffer.length()) >= (Long.MAX_VALUE - buffer.length()))
					bufferCount = 0;
			}
			
			buffer.set(position, s);
		}
	}

	@Override
	public long getEKGPosition() 
	{
		synchronized(lock)
		{
			return bufferCount * buffer.length() + position;
		}
	}

	@Override
	public String readEKGFromPosition(long lPosition, int cMaxLines) 
	{
		StringBuilder sb = new StringBuilder();
			
		long currentSize;
		int currentPosition;
		long currentBufferCount;
		int count = 0;
		
		synchronized(lock) {
			currentBufferCount = bufferCount;
			currentSize = bufferCount * buffer.length() + position;
			currentPosition = position;
		}
		
		if (lPosition > currentSize)
		{			
			return Long.toString(lPosition) + "\n";
		}
		
		int readPosition = 0;
		if (lPosition < 0)
			lPosition = 0;
		
		if (currentSize - lPosition > buffer.length())
			lPosition =  currentSize - buffer.length() + 1;	
				
		readPosition = (int)(lPosition % buffer.length());
		
		int end = currentPosition < readPosition ? buffer.length() - 1 : currentPosition;
		
		// Read from readPosition to the end of the buffer or currentPosition 
		for (readPosition = readPosition; readPosition <= end && count < cMaxLines; readPosition++)
		{
			String s = buffer.get(readPosition);
			
			// We do this loop because buffer.set() is outside of the lock in logEkg() (to improve perfomance) which may 
			// cause the read of the last line before the line is actually set for this position. 
			while(s == null)
			{	
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {					
				}
				s = buffer.get(readPosition);
			}
			
			sb.append(s);
			sb.append("\n");
			count++;
		}
		
		// Read from 0 to currentPosition
		if (currentBufferCount > 0 && readPosition == buffer.length())
		{
			for (readPosition = 0; readPosition <= currentPosition && count < cMaxLines; readPosition++)
			{
				String s = buffer.get(readPosition);											
				sb.append(s);
				sb.append("\n");
				count++;
			}	
		}
		
		return Long.toString(lPosition + count) + "\n" + sb.toString();
	}
	
	public void close()
	{		
		position = -1;
		buffer = null;
		bufferCount = 0;
	}
}
