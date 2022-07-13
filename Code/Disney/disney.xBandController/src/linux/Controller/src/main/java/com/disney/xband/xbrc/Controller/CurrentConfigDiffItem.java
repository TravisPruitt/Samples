package com.disney.xband.xbrc.Controller;

public class CurrentConfigDiffItem
{
	public enum DiffResult
	{
		ADDED,
		DELETED,
		MODIFIED
	}
	
	public CurrentConfigDiffItem(String key, String prevValue, String currValue, DiffResult result)
		throws IllegalArgumentException
	{
		if (key == null || key.trim().length() == 0)
			throw new IllegalArgumentException("Missing key");
		if (result == null)
			throw new IllegalArgumentException("Missing result: " 
					+ DiffResult.ADDED.name() + ", "
					+ DiffResult.DELETED.name() + ", or "
					+ DiffResult.MODIFIED.name());
		
		this.key = key;
		this.prevValue = prevValue;
		this.currValue = currValue;
		this.result = result;
	}
	
	public String getKey()
	{
		return key;
	}
	public String getPrevValue()
	{
		return prevValue;
	}
	public String getCurrValue()
	{
		return currValue;
	}
	public DiffResult getResult()
	{
		return result;
	}
	
	private String key;
	private String prevValue;
	private String currValue;
	private DiffResult result;
}
