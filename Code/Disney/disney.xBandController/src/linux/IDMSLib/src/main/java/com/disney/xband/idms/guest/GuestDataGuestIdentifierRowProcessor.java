package com.disney.xband.idms.guest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.oneview.GuestDataGuestIdentifier;

public class GuestDataGuestIdentifierRowProcessor extends BasicRowProcessor 
{
	public static GuestDataGuestIdentifierRowProcessor INSTANCE = 
			new GuestDataGuestIdentifierRowProcessor(); 
	
	private GuestDataGuestIdentifierRowProcessor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GuestDataGuestIdentifier> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<GuestDataGuestIdentifier> guestIdentifiers = new ArrayList<GuestDataGuestIdentifier>();

		while(rs.next())
		{
			GuestDataGuestIdentifier guestIdentifier = toBean(rs, GuestDataGuestIdentifier.class);
			guestIdentifiers.add(guestIdentifier);
		}

		return guestIdentifiers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GuestDataGuestIdentifier toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) 
			throws SQLException
	{
		GuestDataGuestIdentifier guestIdentifier = new GuestDataGuestIdentifier();
		
		guestIdentifier.setType(rs.getString("type"));
		guestIdentifier.setValue(rs.getString("value"));
				
		return guestIdentifier;
	}

}
