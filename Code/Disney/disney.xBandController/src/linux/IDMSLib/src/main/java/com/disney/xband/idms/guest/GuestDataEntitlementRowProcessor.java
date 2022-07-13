package com.disney.xband.idms.guest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.oneview.GuestDataEntitlement;

public class GuestDataEntitlementRowProcessor extends BasicRowProcessor
{
	public static GuestDataEntitlementRowProcessor INSTANCE = 
			new GuestDataEntitlementRowProcessor(); 
	
	private GuestDataEntitlementRowProcessor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GuestDataEntitlement> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<GuestDataEntitlement> entitlements = new ArrayList<GuestDataEntitlement>();

		while(rs.next())
		{
			GuestDataEntitlement entitlement = toBean(rs, GuestDataEntitlement.class);
			entitlements.add(entitlement);
		}

		return entitlements;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GuestDataEntitlement toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) 
			throws SQLException
	{
		GuestDataEntitlement entitlement = new GuestDataEntitlement();
		
		entitlement.setID(rs.getString("celebrationId"));
		entitlement.setType("celebration");
		
		Date startDate = rs.getDate("startDate");
		Date endDate = rs.getDate("endDate");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String formattedStartDate = sdf.format(startDate);
		String formattedEndDate = sdf.format(endDate);
		
		entitlement.setStartDateTime(formattedStartDate);
		entitlement.setEndDateTime(formattedEndDate);
		
		Link self = new Link();
		
		self.setHref("/celebration/id;celebration-id=" +  rs.getString("celebrationId"));
		entitlement.getLinks().setSelf(self);
		
		return entitlement;
	}

}
