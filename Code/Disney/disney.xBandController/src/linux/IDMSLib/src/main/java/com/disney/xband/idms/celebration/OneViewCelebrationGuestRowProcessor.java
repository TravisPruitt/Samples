package com.disney.xband.idms.celebration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.oneview.CelebrationGuest;

public class OneViewCelebrationGuestRowProcessor extends BasicRowProcessor 
{
	public static OneViewCelebrationGuestRowProcessor INSTANCE = new OneViewCelebrationGuestRowProcessor();

	private OneViewCelebrationGuestRowProcessor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CelebrationGuest> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<CelebrationGuest> celebrationGuests = 
				new ArrayList<CelebrationGuest>();

		while (rs.next())
		{
			CelebrationGuest c = (CelebrationGuest)this.toBean(rs, type);
			celebrationGuests.add(c);
		}

		return celebrationGuests;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CelebrationGuest toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		CelebrationGuest celebrationGuest = new CelebrationGuest();
		celebrationGuest.setRole(rs.getString("role"));
		celebrationGuest.setRelationship(rs.getString("relationship"));
		celebrationGuest.setFirstname(rs.getString("firstname"));
		celebrationGuest.setLastname(rs.getString("lastname"));
		
		Link self = new Link();

		self.setHref("/guest/" + rs.getString("xid") + "/profile");
		self.setName("self");

		celebrationGuest.getLinks().setSelf(self);

		return celebrationGuest;

	}
}
