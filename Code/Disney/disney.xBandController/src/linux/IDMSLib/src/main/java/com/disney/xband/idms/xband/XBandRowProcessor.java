package com.disney.xband.idms.xband;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.LinkCollection;
import com.disney.xband.idms.lib.model.XBand;

public class XBandRowProcessor extends BasicRowProcessor {

	public static XBandRowProcessor INSTANCE = new XBandRowProcessor();
	
	private XBandRowProcessor()
	{
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<XBand> toBeanList(ResultSet rs, Class type) throws SQLException
	{
		List<XBand> xbands = new ArrayList<XBand>();

		while(rs.next())
		{
			XBand band= toBean(rs, XBand.class);
			xbands.add(band);
		}

		return xbands;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public XBand toBean(ResultSet rs, Class type) throws SQLException
	{
		XBand x = new XBand();

		x.setAssignmentDateTime(rs.getTimestamp("updatedDate"));
		x.setPrintedName(rs.getString("printedName"));
		x.setLongRangeTag(rs.getString("longRangeId"));
		x.setShortRangeTag(rs.getString("tapId"));
		x.setXbandId(rs.getLong("xbandId"));
		x.setProductId(rs.getString("bandId"));
		x.setXbandRequestId(rs.getString("bandId"));
		x.setBandId(rs.getString("bandId"));
		x.setBandType(rs.getString("bandType"));
		x.setPublicId(rs.getString("publicId"));


		if (rs.getBoolean("active"))
		{
			x.setState("Active");
			x.setSecondaryState("Active");

		}
		else
		{
			x.setState("Inactive");
			x.setSecondaryState("Inactive");
		}

		// Setup the Links.
		LinkCollection links = new LinkCollection();
		links.setHistory(null);
		links.setNextAction(null);
		Link ownerLink = new Link();
		ownerLink.setHref("/guest/" + x + "/profile");

		links.setOwnerProfile(ownerLink);
		links.setProductIdReference(null);

		Link self = new Link();
		self.setHref("/xband/" +  x.getXbandId());
		links.setSelf(self);

		x.setLinks(links);

		return x;
	}
}
