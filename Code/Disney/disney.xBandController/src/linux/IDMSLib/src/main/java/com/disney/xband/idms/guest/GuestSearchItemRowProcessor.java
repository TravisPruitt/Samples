package com.disney.xband.idms.guest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.GuestAddress;
import com.disney.xband.idms.lib.model.GuestSearchItem;

public class GuestSearchItemRowProcessor extends BasicRowProcessor 
{
	public static GuestSearchItemRowProcessor INSTANCE = 
			new GuestSearchItemRowProcessor(); 
	
	private GuestSearchItemRowProcessor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GuestSearchItem> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<GuestSearchItem> guestSearchItems = new ArrayList<GuestSearchItem>();

		while(rs.next())
		{
			GuestSearchItem guestSearchItem = toBean(rs, GuestSearchItem.class);
			guestSearchItems.add(guestSearchItem);
		}

		return guestSearchItems;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GuestSearchItem toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) 
			throws SQLException
	{
		GuestSearchItem guestSearchItem = new GuestSearchItem();
		
		guestSearchItem.setEmailAddress(rs.getString("emailAddress"));	
		guestSearchItem.setLeadGuest(rs.getString("firstName") + " " + rs.getString("lastName"));
				
		return guestSearchItem;
	}
}
