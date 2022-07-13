package com.disney.xband.xbrc.ui.exception;

public class LocationCanNotBeDeletedException extends Exception {
	
	public LocationCanNotBeDeletedException(String message){
		super(message);
	}
	
	public LocationCanNotBeDeletedException(String message, Throwable exception){
		super(message, exception);
	}
	
	public LocationCanNotBeDeletedException(){}
}
