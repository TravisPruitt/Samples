package com.disney.xband.xbrc.ui.db;

import java.util.Collection;

import com.disney.xband.xbrc.ui.bean.LocationBean;

public interface BandViewService {

	public Collection<LocationBean> getByBandId(String id, long deltaTime) throws Exception;
	
	public void updateGain(String readerId, Double gain) throws Exception;
	
	public void updateThreshold(String readerId, Integer threshold) throws Exception;
}
