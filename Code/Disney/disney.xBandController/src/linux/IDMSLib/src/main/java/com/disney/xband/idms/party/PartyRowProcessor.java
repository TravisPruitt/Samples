package com.disney.xband.idms.party;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.Party;

public class PartyRowProcessor extends BasicRowProcessor
{

	public static PartyRowProcessor INSTANCE = 
			new PartyRowProcessor(); 
	
	private PartyRowProcessor()
	{
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Party toBean(ResultSet rs, @SuppressWarnings("rawtypes") Class type) 
			throws SQLException
	{
		Party party = new Party();

		party.setPartyName(rs.getString("partyName"));
		party.setPrimaryGuestId(rs.getLong("primaryGuestId"));
		party.setCount(rs.getInt("count"));
		party.setPartyId(rs.getLong("partyId"));

		return party;
	}
}
