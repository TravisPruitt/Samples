package com.disney.xband.idms.guest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.GuestAcquisition;

public class GuestAcquisitionRowProcessor extends BasicRowProcessor
{

	public static GuestAcquisitionRowProcessor INSTANCE = 
			new GuestAcquisitionRowProcessor(); 
	
	private GuestAcquisitionRowProcessor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GuestAcquisition> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<GuestAcquisition> guestAcquisitions = new ArrayList<GuestAcquisition>();

		while(rs.next())
		{
			GuestAcquisition entitlement = toBean(rs, GuestAcquisition.class);
			guestAcquisitions.add(entitlement);
		}

		return guestAcquisitions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GuestAcquisition toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) 
			throws SQLException
	{
		GuestAcquisition guestAcquisition = new GuestAcquisition();
		
		guestAcquisition.setFirstName(rs.getString("firstName"));
		guestAcquisition.setLastName(rs.getString("lastName"));
		guestAcquisition.setXbandRequestId(rs.getString("xbandRequestId"));
		guestAcquisition.setAcquisitionId(rs.getString("acquisitionId"));
		guestAcquisition.setAcquisitionIdType(rs.getString("acquisitionIdType"));
		guestAcquisition.setAcquisitionStartDate(rs.getString("acquisitionStartDate"));
		guestAcquisition.setAcquisitionUpdateDate(rs.getString("acquisitionUpdateDate"));
		guestAcquisition.setPrimaryGuest(rs.getBoolean("isPrimaryGuest"));
		guestAcquisition.setGuestId(rs.getString("guestIdValue"));
		guestAcquisition.setGuestIdType(rs.getString("guestIdType"));
		
		return guestAcquisition;
	}

}
