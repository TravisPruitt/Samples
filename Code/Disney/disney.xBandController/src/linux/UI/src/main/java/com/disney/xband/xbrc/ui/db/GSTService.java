package com.disney.xband.xbrc.ui.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import com.disney.xband.xbrc.lib.entity.GST;

public interface GSTService {
	
	public GST find(String id) throws Exception;
	
	public Collection<GST> findAll() throws Exception;
	
	public void save(GST gst) throws Exception;
	
	public void delete(GST gst) throws Exception;
	
	public GST instantiateGST(ResultSet rs) throws SQLException;
}
