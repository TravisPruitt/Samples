package com.disney.xband.idms.celebration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.Celebration;
import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.LinkCollection;

public class CelebrationRowProcessor extends BasicRowProcessor 
{

	public static CelebrationRowProcessor INSTANCE = new CelebrationRowProcessor();

	private CelebrationRowProcessor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Celebration> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<Celebration> celebrations = 
				new ArrayList<Celebration>();

		while (rs.next())
		{
			Celebration c = (Celebration)this.toBean(rs, type);
			celebrations.add(c);
		}

		return celebrations;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Celebration toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		Celebration celebration = null;

		celebration = new Celebration();
		celebration.setActive(rs.getBoolean("active"));
		celebration.setCelebrationId(rs.getLong("celebrationId"));
		celebration.setCelebrationMessage(rs.getString("message"));
		celebration.setCelebrationName(rs.getString("name"));
		celebration.setStart(rs.getDate("dateStart"));
		celebration.setEnd(rs.getDate("dateEnd"));
		celebration.setIDMSTypeName(rs.getString("IDMSTypeName"));
		celebration.setIDMSTypeId(rs.getInt("IDMSTypeId"));
		celebration.setGuestId(rs.getLong("guestId"));

		LinkCollection lc = new LinkCollection();
		Link ownerProfile = new Link();

		Link self = new Link();

		self.setHref("/celebrations/id/" + rs.getLong("celebrationId"));
		self.setName("self");

		ownerProfile.setHref("/guest/" + rs.getLong("guestId"));
		ownerProfile.setName("owner");

		lc.setOwnerProfile(ownerProfile);
		lc.setSelf(self);

		celebration.setLinks(lc);

		return celebration;

	}
}
