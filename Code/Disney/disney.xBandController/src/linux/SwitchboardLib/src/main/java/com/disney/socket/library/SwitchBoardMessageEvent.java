package com.disney.socket.library;

import java.util.EventObject;

public class SwitchBoardMessageEvent extends EventObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	
	public SwitchBoardMessageEvent(Object source, String message) {
		super(source);
		
		this.message = message;

	}
	
	public String getMessage()
	{
		return this.message;
	}

}