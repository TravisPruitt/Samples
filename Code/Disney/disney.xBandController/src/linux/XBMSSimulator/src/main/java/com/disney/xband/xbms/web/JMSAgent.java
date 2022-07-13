package com.disney.xband.xbms.web;

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

public class JMSAgent
{
	// singleton pattern
	public static JMSAgent INSTANCE = new JMSAgent();

	private ConcurrentHashMap<String,Timer> messageTasks;
	
	public JMSAgent()
	{
		this.messageTasks = new ConcurrentHashMap<String,Timer>();
	}
	
	public void AddBatch(int messageBatchId)
	{
		Timer timer = new Timer();
		MessagePublisher task = new MessagePublisher(messageBatchId);
    	timer.schedule(task ,0, 
    			ConfigurationSettings.INSTANCE.getJmsMessageInterval());
		
		this.messageTasks.put(String.valueOf(messageBatchId), timer);
	}
	
	public void FinishBatch(int messageBatchId)
	{
		String key = String.valueOf(messageBatchId);
		Timer timer = this.messageTasks.get(key);
		if(timer != null)
		{
			timer.cancel();
			
			this.messageTasks.remove(key);
		}
	}
	
	public void Stop()
	{
		for(String key : this.messageTasks.keySet())
		{
			Timer timer = this.messageTasks.get(key);
			if(timer != null)
			{
				timer.cancel();
			}
		}
		
		this.messageTasks.clear();
	}
}
