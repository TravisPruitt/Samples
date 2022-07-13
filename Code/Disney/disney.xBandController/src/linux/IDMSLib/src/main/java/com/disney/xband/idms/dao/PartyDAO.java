package com.disney.xband.idms.dao;

import com.disney.xband.idms.lib.model.Party;

public interface PartyDAO 
{
	public Party getParty(String partyName) throws DAOException;

	public Party getParty(long partyId) throws DAOException;

	public Party getParty(String identifierType, 
			String identifierValue) throws DAOException;
	
	public long createParty(Party party) throws DAOException;
	
	public boolean addGuestToParty(long guestId, long partyId) 
			throws DAOException;
}
