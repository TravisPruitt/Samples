package com.disney.xband.idms.dao.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.disney.xband.idms.dao.PartyDAO;
import com.disney.xband.idms.dao.sql.SqlServerDAOFactory;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.lib.model.Party;
import com.disney.xband.idms.lib.model.PartyMember;
import com.disney.xband.idms.party.PartyMemberRowProcessor;
import com.disney.xband.idms.party.PartyRowProcessor;

public class SqlPartyDAO implements PartyDAO
{
	@Override
	public Party getParty(String partyName) throws DAOException 
	{
		Party party = null;
		Connection conn = null;
		CallableStatement cs = null;

		try
		{
			conn = SqlServerDAOFactory.createConnection();

			cs = conn.prepareCall("{call usp_party_retrieve_by_name(?)}");
			cs.setString("@partyName", partyName);

			cs.execute();
			
			party = ProcessResults(cs);

		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(cs);
			SqlServerDAOFactory.release(conn);
		}

		return party;
	}

	@Override
	public Party getParty(long partyId) throws DAOException
	{
		Party party = null;
		Connection conn = null;
		CallableStatement cs = null;

		try
		{
			conn = SqlServerDAOFactory.createConnection();

			cs = conn.prepareCall("{call usp_party_retrieve(?)}");
			cs.setLong("@partyId", partyId);

			cs.execute();
			
			party = ProcessResults(cs);

		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(cs);
			SqlServerDAOFactory.release(conn);
		}

		return party;
	}

	private Party ProcessResults(CallableStatement cs) throws SQLException
	{
		ResultSet rs = null;
		Party party = null;
		
		try
		{
			rs = cs.getResultSet();
	
			if (rs.next())
			{
				party = PartyRowProcessor.INSTANCE.toBean(rs, Party.class);
		
				// Now we need the members.
				if (cs.getMoreResults()) // Get party members
				{
					rs = cs.getResultSet();
					party.setMembers(
							PartyMemberRowProcessor.INSTANCE.toBeanList(
									rs, PartyMember.class));
				}
			}
		}
		finally
		{
			SqlServerDAOFactory.close(rs);
		}
		
		return party;
	}

	@Override
	public Party getParty(String identifierType, String identifierValue)
			throws DAOException 
	{
		Party party = null;
		Connection conn = null;
		CallableStatement cs = null;

		try
		{
			conn = SqlServerDAOFactory.createConnection();
			
			cs = conn.prepareCall("{call usp_party_retrieve(?,?)}");
			cs.setString("@identifierType", identifierType);
			cs.setString("@identifierValue", identifierValue);
			
			cs.execute();
			
			party = ProcessResults(cs);
		}
		catch (Exception e)
		{
			throw new DAOException(e);
		}
		finally
		{
			SqlServerDAOFactory.close(cs);
			SqlServerDAOFactory.release(conn);
		}

		return party;
	}

	@Override
	public long createParty(Party party) throws DAOException 
	{
		long partyId = 0;
		Connection conn = null;
		CallableStatement cs = null;
		
		try
		{
			conn = SqlServerDAOFactory.createConnection();
			
			cs = conn.prepareCall("{call usp_party_create(?,?,?)}");
			cs.setLong("@primaryGuestId",party.getPrimaryGuestId());
			cs.setString("@partyName",party.getPartyName());
			cs.registerOutParameter("@partyId", java.sql.Types.BIGINT);

			cs.execute();
			
			partyId = cs.getLong("@partyId");
		}
		catch (Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(cs);
			SqlServerDAOFactory.release(conn);
		}
		
		return partyId;
	}

	@Override
	public boolean addGuestToParty(long guestId, long partyId) 
			throws DAOException
	{
		boolean result = false;
		Connection conn = null;
		CallableStatement cs = null;
		
		try
		{
			conn = SqlServerDAOFactory.createConnection();
			
			cs = conn.prepareCall("{call usp_party_guest_create(?,?)}");
			cs.setLong("@partyId", partyId);
			cs.setLong("@guestId",guestId);

			cs.execute();

			result = true;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(cs);
			SqlServerDAOFactory.release(conn);
		}
		
		return result;
	}
}
