package com.disney.xband.xbrc.parkentrymodel;

import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;

public class OmniResponse
{
	private AnswerCommand answer;
	// have we received all answers
	private boolean receivedAll = true;
	
	// signal reader state changes to the CEP
	private String readerName;
	private boolean readerStateChange = false;
	
	public OmniResponse(AnswerCommand answer, boolean receivedAll)
	{
		this.answer = answer;
		this.receivedAll = receivedAll;
	}
	
	public OmniResponse(String readerName)
	{
		this.readerName = readerName;
		this.readerStateChange = true;
	}

	public AnswerCommand getAnswer()
	{
		return answer;
	}

	public void setAnswer(AnswerCommand answer)
	{
		this.answer = answer;
	}

	public boolean isReceivedAll()
	{
		return receivedAll;
	}

	public void setReceivedAll(boolean receivedAll)
	{
		this.receivedAll = receivedAll;
	}

	public String getReaderName()
	{
		return readerName;
	}

	public void setReaderName(String readerName)
	{
		this.readerName = readerName;
	}

	public boolean isReaderStateChange()
	{
		return readerStateChange;
	}

	public void setReaderStateChange(boolean readerStateChange)
	{
		this.readerStateChange = readerStateChange;
	}
}
