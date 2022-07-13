package com.disney.xband.lib.readertest;

import java.util.Properties;

import com.disney.xband.xbrc.lib.model.ReaderInfo;

/**
 * Represents a single HTTP rest call to the reader.
 */
public interface ReaderAction {
	
	/**
	 * The following keys can be used in text actions in the following form ${KEY}.
	 * They are replaced by the xBRC with the corresponding value.
	 */
	
	/**
	 * Venue name. Example: Star Tours
	 */
	public final String PROPKEY_VENUE = "VENUE";
	
	/**
	 * Reader name. Example: Entry-Left
	 */
	public final String PROPKEY_READER = "READER";
	
	/**
	 * Location name. Example: Entry
	 */
	public final String PROPKEY_READER_LOCATION = "LOCATION";
	
	/**
	 * Success sequence.
	 */
	public final String PROPKEY_SUCCESS_SEQUENCE = "SUCCESS_SEQUENCE";
	
	/**
	 * Error sequence.
	 */
	public final String PROPKEY_ERROR_SEQUENCE = "ERROR_SEQUENCE";
	
	/**
	 * Success sequence.
	 */
	public final String PROPKEY_FAILURE_SEQUENCE = "FAILURE_SEQUENCE";
	
	
	/**
	 * The following keys are used by some actions. They are for system use only.
	 */
	public final String PROPKEY_SPEECH_VOLUME = "SPEECH_VOLUME";
	public final String PROPKEY_LEDSCRIPTS_DIR = "LEDSCRIPTS_DIR";
	
	/**
	 * Perform any required initialization in this function.
	 */
	public void initialize(Properties prop, ReaderInfo ri) throws Exception;	
	
	/**
	 * Peforms the actual action usually by calling a reader REST call.
	 * @throws Exception
	 */
	public void performAction() throws Exception; 

	
	/**
	 * @return The name of the action used for reporting. 
	 */
	public String getName();
	
	/**
	 * @return The HTTP method used to send the REST command to the reader 
	 */
	public HttpMethod getMethod();
	
	/**	 
	 * @return The HTTP path to call (without the server name) 
	 */
	public String getPath();
	
	/**
	 * @return The data to POST or PUT to the reader. Can be null.
	 */
	public byte[] getData();
	
	/**
	 * @return The content type header to set when calling the reader. Can be null.
	 */
	public String getContentType();
	
	/**
	 * 
	 * @return The number of milliseconds to pause after the command is sent to the reader.
	 */
	public Long getWaitMs();
}
