package com.disney.xband.xbms.dao.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.disney.xband.jms.lib.entity.xbms.CustomizationSelection;
import com.disney.xband.jms.lib.entity.xbms.QualifyingId;
import com.disney.xband.jms.lib.entity.xbms.XbandRequest;
import com.disney.xband.xbms.dao.DAOException;
import com.disney.xband.xbms.dao.XbandRequestDAO;

public class SqlXbandRequestDAO implements XbandRequestDAO
{
	private static String GET_XBAND_REQUEST = "{call usp_xbandRequest_retrieve(?)}";
	
	@Override
	public XbandRequest GetXbandRequest(String xbandRequestId)
			throws DAOException
	{
		XbandRequest xbandRequest = null;
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;
		ResultSet guests = null;
		
		List<String> entitlements = new ArrayList<String>();
		entitlements.add("STANDARD");
		
		QualifyingId q = new QualifyingId();
		q.setQualifyingId("577038094");
		q.setQualifyingIdType("travel-component-id");
		
		List<QualifyingId> qualifyingIds = new ArrayList<QualifyingId>();
		qualifyingIds.add(q);

		try
		{
			connection = SqlDAOFactory.createConnection();
			statement = connection.prepareCall(GET_XBAND_REQUEST);
			statement.setString("@xbandRequestId", xbandRequestId);
			
			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				xbandRequest = new XbandRequest();
				
				xbandRequest.setOptions("/reorder-options/" + resultSet.getString("xbandRequestId"));
				xbandRequest.setOrder("/orders/"  + resultSet.getString("xbandRequestId"));
				xbandRequest.setState("FULFILLMENT");
				xbandRequest.setPrimaryGuestOwnerId(resultSet.getString("primaryGuestOwnerId"));
				xbandRequest.setXbandRequestId(resultSet.getString("xbandRequestId"));
				xbandRequest.setAcquisitionId(resultSet.getString("acquisitionId"));
				xbandRequest.setAcquisitionIdType(resultSet.getString("acquisitionIdType"));
				xbandRequest.setAcquisitionStartDate(resultSet.getString("acquisitionStartDate"));
				xbandRequest.setAcquisitionUpdateDate(resultSet.getString("acquisitionUpdateDate"));
				xbandRequest.setCreateDate(resultSet.getString("createDate"));
				xbandRequest.setUpdateDate(resultSet.getString("updateDate"));
				xbandRequest.setReorder("/xband-requests/" + resultSet.getString("xbandRequestId") + "/reorder");
				xbandRequest.setCustomizationEndDate(resultSet.getString("createDate"));
				xbandRequest.setSelf("/xband-requests/" + resultSet.getString("xbandRequestId"));

				List<CustomizationSelection> customizationSelections = new ArrayList<CustomizationSelection>();
				
				if (statement.getMoreResults()) // Get celebration guests
				{
					guests = statement.getResultSet();
					
					while(guests.next())
					{
						
						CustomizationSelection cs = new CustomizationSelection();
						
						cs.setXbandOwnerId(guests.getString("xbandOwnerId"));
						cs.setXbandRequestId(guests.getString("xbandRequestId"));
						cs.setGuestId(Long.valueOf(guests.getString("guestId")));
						cs.setGuestIdType(guests.getString("guestIdType"));
						cs.setEntitlements(entitlements);
						cs.setCustomizationSelectionId(guests.getString("customizationSelectionId"));
						cs.setQualifyingIds(qualifyingIds);
						cs.setBandProductCode("B11313");
						cs.setPrintedName(guests.getString("firstName"));
						cs.setConfirmedCustomization(false);
						cs.setLastName(guests.getString("lastName"));
						cs.setFirstName(guests.getString("firstName"));
						cs.setPrimaryGuest(guests.getBoolean("primaryGuest"));
						cs.setCreateDate(guests.getString("createDate"));
						cs.setUpdateDate(guests.getString("updateDate"));
						cs.setSelf("/customization-selections/" + guests.getString("customizationSelectionId"));
						
						customizationSelections.add(cs);
						
					}
				}
				
				if(customizationSelections.size() != 0)
				{
					xbandRequest.setCustomizationSelections(customizationSelections);
				}
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlDAOFactory.close(resultSet);
			SqlDAOFactory.close(guests);
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}
		
		return xbandRequest;
	}

}
