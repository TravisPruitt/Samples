package com.disney.xband.idms.xband;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.oneview.GuestDataXband;

public class GuestDataXbandRowProcessor extends BasicRowProcessor {

	public static GuestDataXbandRowProcessor INSTANCE = new GuestDataXbandRowProcessor();
	
	private GuestDataXbandRowProcessor()
	{
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<GuestDataXband> toBeanList(ResultSet rs, Class type) throws SQLException
	{
		List<GuestDataXband> xbands = new ArrayList<GuestDataXband>();

		while(rs.next())
		{
			GuestDataXband band= toBean(rs, GuestDataXband.class);
			xbands.add(band);
		}

		return xbands;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public GuestDataXband toBean(ResultSet rs, Class type) throws SQLException
	{
		GuestDataXband x = new GuestDataXband();

		x.setShortRangePublicId(rs.getString("publicId"));
		x.setShortRangePublicId(rs.getString("publicId"));
		x.setXBandRequestId(rs.getString("xbandId"));
		x.setExternalNumber(rs.getString("xbandId"));
		x.setExternalNumber(rs.getString("xbandId"));
		x.setSecureId(rs.getString("secureId"));
		x.setLongRangeTag(rs.getString("longRangeId"));
		x.setShortRangeTag(rs.getString("tapId"));
		x.setPublicXBandId(rs.getString("xbmsId")); 

		if (rs.getBoolean("active"))
		{
			x.setPrimaryStatus("Active");
			x.setSecondaryStatus("Active");
		}
		else
		{
			x.setPrimaryStatus("Inactive");
			x.setSecondaryStatus("Inactive");
		}

		Link self = new Link();
		self.setHref("/xband/" +  rs.getLong("xbandId"));
		x.getLinks().setSelf(self);

		return x;
	}
}

