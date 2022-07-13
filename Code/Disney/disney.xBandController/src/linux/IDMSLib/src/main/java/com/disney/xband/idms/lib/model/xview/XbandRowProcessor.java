package com.disney.xband.idms.lib.model.xview;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.BasicRowProcessor;

public class XbandRowProcessor extends BasicRowProcessor
{
	public static XbandRowProcessor INSTANCE =
			new XbandRowProcessor();
	
	private XbandRowProcessor()
	{
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Xband toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) 
			throws SQLException
	{
		Xband xband = new Xband();
		
		xband.setActive(rs.getBoolean("active"));
		xband.setBandFriendlyName(rs.getString("bandFriendlyName"));
		xband.setPrintedName(rs.getString("printedName"));
		xband.setBandId(rs.getString("bandId"));

		xband.setLRId(rs.getString("longRangeId"));
		xband.setTapId(rs.getString("tapId"));
		xband.setXBandId(String.valueOf(rs.getLong("xbandId")));
		xband.setBandType(rs.getString("BandType"));

		return xband;
	}
}
