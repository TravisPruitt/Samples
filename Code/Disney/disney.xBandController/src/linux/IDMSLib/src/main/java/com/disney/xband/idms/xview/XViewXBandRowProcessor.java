package com.disney.xband.idms.xview;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.xview.Xband;

public class XViewXBandRowProcessor extends BasicRowProcessor
{
	public static XViewXBandRowProcessor INSTANCE = new XViewXBandRowProcessor();
	
	private XViewXBandRowProcessor()
	{
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Xband> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<Xband> xbands = new ArrayList<Xband>();
			
		while(rs.next())
		{
			Xband band = (Xband)this.toBean(rs, type);
			xbands.add(band);
		}
		
		return xbands;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Xband toBean(ResultSet rs,
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		Xband xband = new Xband();
		
		xband.setActive(rs.getBoolean("active"));
		xband.setBandFriendlyName(rs.getString("bandFriendlyName"));
		xband.setBandId(rs.getString("bandId"));
		xband.setLRId(rs.getString("longRangeId"));
		xband.setTapId(rs.getString("tapId"));
		xband.setPrintedName(rs.getString("printedName"));
		xband.setXBandId(String.valueOf(rs.getLong("xbandId")));
		xband.setBandType(rs.getString("BandType"));
		xband.setPublicId(rs.getString("publicId"));
		
		return xband;
	}

}
