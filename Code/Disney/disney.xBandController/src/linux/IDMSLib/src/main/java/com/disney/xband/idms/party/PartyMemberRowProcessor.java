package com.disney.xband.idms.party;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.LinkCollection;
import com.disney.xband.idms.lib.model.PartyMember;

public class PartyMemberRowProcessor extends BasicRowProcessor 
{
	public static PartyMemberRowProcessor INSTANCE = 
			new PartyMemberRowProcessor(); 
	
	private PartyMemberRowProcessor()
	{
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PartyMember> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<PartyMember> partyMembers = 
				new ArrayList<PartyMember>();

		while (rs.next())
		{
			PartyMember c = (PartyMember)this.toBean(rs, type);
			partyMembers.add(c);
		}

		return partyMembers;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PartyMember toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) 
			throws SQLException
	{
		PartyMember partyMember = new PartyMember();

		partyMember.setGuestId(rs.getLong("guestId"));
		partyMember.setPrimary(rs.getBoolean("isPrimary"));
		
		LinkCollection links = new LinkCollection();
		
		Link owner = new Link();
		owner.setHref("/guest/" + rs.getLong("guestId") + "/profile");

		links.setOwnerProfile(owner);
		
		partyMember.setLinks(links);

		return partyMember;
	}
}
