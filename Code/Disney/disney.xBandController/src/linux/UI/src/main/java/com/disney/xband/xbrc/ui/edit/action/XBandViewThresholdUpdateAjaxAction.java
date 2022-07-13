package com.disney.xband.xbrc.ui.edit.action;

import java.sql.Connection;

import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.bean.LocationBean;
import com.disney.xband.xbrc.ui.bean.ReaderValue;
import com.disney.xband.xbrc.ui.db.BandViewService;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import com.disney.xband.xbrc.ui.httpclient.Controller;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class XBandViewThresholdUpdateAjaxAction extends ActionSupport implements Preparable {
	
	private BandViewService bandViewService;
	private String readerId;
	private Integer threshold;
	private ReaderValue reader;
	
	@Override
	public void prepare() throws Exception {
		bandViewService = ServiceLocator.getInstance().createService(BandViewService.class);
	}
	
	/** 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		if (readerId == null || threshold == null)
			return INPUT;
		
		bandViewService.updateThreshold(readerId, threshold);		
		
		Reader r = null;
		Connection conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			r = ReaderService.find(conn, readerId);
		}
		catch (Exception e)
		{
			this.addActionMessage("Failed to find reader id " + readerId);
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		if (r != null){
			reader = new ReaderValue(readerId, 
									r.getGain(), 
									r.getSignalStrengthThreshold(), 
									new LocationBean(r.getLocationId()));
		}
		
		Controller.getInstance().notifyXbrcOfReaderSignalChange(readerId);
		
		return SUCCESS;
	}

	/**
	 * @return the readerId
	 */
	public String getReaderId() {
		return readerId;
	}

	/**
	 * @param readerId the readerId to set
	 */
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}

	/**
	 * @return the threshold
	 */
	public Integer getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public ReaderValue getReader() {
		return reader;
	}
}
