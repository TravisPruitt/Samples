package com.disney.xband.idms.dao.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.disney.xband.idms.dao.GuestDAO;
import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.data.GuestDataIdentifier;
import com.disney.xband.idms.lib.model.oneview.GuestDataEntitlement;
import com.disney.xband.idms.lib.model.oneview.GuestDataGuest;
import com.disney.xband.idms.lib.model.oneview.GuestDataGuestIdentifier;
import com.disney.xband.idms.lib.model.oneview.GuestDataResult;
import com.disney.xband.idms.lib.model.oneview.GuestDataXband;
import com.disney.xband.idms.dao.sql.SqlServerDAOFactory;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.lib.model.Guest;
import com.disney.xband.idms.lib.model.GuestAcquisition;
import com.disney.xband.idms.lib.model.GuestEmailPut;
import com.disney.xband.idms.lib.model.GuestIdentifierPut;
import com.disney.xband.idms.lib.model.GuestLocatorCollection;
import com.disney.xband.idms.lib.model.ReservationGuest;
import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.ScheduledItem;
import com.disney.xband.idms.lib.model.ScheduledItemCollection;
import com.disney.xband.idms.lib.model.GuestSearchCollection;
import com.disney.xband.idms.lib.model.GuestSearchItem;
import com.disney.xband.idms.xband.GuestDataXbandRowProcessor;
import com.disney.xband.idms.guest.GuestAcquisitionRowProcessor;
import com.disney.xband.idms.guest.GuestDataEntitlementRowProcessor;
import com.disney.xband.idms.guest.GuestSearchItemRowProcessor;
import com.disney.xband.idms.guest.GuestDataGuestIdentifierRowProcessor;

public class SqlGuestDAO extends SqlDAOBase implements GuestDAO
{
	private static String GET_GUEST = "{call usp_guest_retrieve(?,?)}";
	private static String GET_GUEST_BY_NAME = "{call usp_guest_search(?)}";
	private static String GET_GUEST_BY_EMAIL = "{call usp_guest_retrieve_by_email(?)}";
	private static String GET_GUEST_SCHEDULED_ITEMS = "{call usp_scheduled_item_retrieve(?,?)}";
	private static String CREATE_GUEST = "{call usp_guest_create(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String UPDATE_GUEST = "{call usp_guest_update(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CREATE_GUEST_IDENTIFIER = "{call usp_source_system_link_create(?,?,?,?)}";
	private static String DELETE_GUEST_IDENTIFIER = "{call usp_source_system_link_delete(?,?,?,?)}";
	private static String GET_GUEST_DATA = "{call usp_guest_data_retrieve(?,?,?,?)}";
	private static String GET_GUEST_LOCATORS = "{call usp_guest_locators_retrieve()}";
	private static String CREATE_RESERVATION_GUEST = "{call usp_reservation_guest_create(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String GET_GUEST_ACQUISITION = "{call usp_guest_acquisition_retrieve(?,?)}";
	private static String GET_GUEST_ACQUISITION_BY_XBAND_REQUEST = "{call usp_guest_acquisition_retrieve_by_xband_request(?)}";
	private static String GET_GUEST_ACQUISITION_BY_ACQUISITION = "{call usp_guest_acquisition_retrieve_by_acquisition(?,?)}";
	
	static public ResultSetHandler<GuestData> resultSetHandler = new BeanHandler<GuestData>(GuestData.class, new BasicRowProcessor());
	static public ResultSetHandler<List<GuestData>> listResultSetHandler = new BeanListHandler<GuestData>(GuestData.class, new BasicRowProcessor());
	
	private GuestData GetGuestData(CallableStatement statement) throws DAOException 
	{
		ResultSet resultSet = null;
		ResultSet bandsResultSet = null;
		ResultSet identifiersResultSet = null;
		
		GuestData guestData = null;
	
		try
		{
			resultSet = statement.executeQuery();
			guestData = resultSetHandler.handle(resultSet);
			if (guestData != null)
			{
				if (statement.getMoreResults()) // Get bands
				{
					bandsResultSet = statement.getResultSet();
			
					List<BandData> bandList = SqlXBandDAO.listResultSetHandler.handle(bandsResultSet);
						
					guestData.setBandList(bandList);
					guestData.setBandListComplete(true);
			
					if (statement.getMoreResults()) // Get identifiers
					{
						identifiersResultSet = statement.getResultSet();
				
						List<GuestDataIdentifier> identifierList = new ArrayList<GuestDataIdentifier>();
				
						while (identifiersResultSet.next())
						{
							GuestDataIdentifier identifier = new GuestDataIdentifier(identifiersResultSet);
							identifierList.add(identifier);
						}
				
						guestData.setIdentifierList(identifierList);
						guestData.setIdentifierListComplete(true);
				        // TODO Add Celebrations
					}
				}
			}
		}
	
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(resultSet);
			SqlServerDAOFactory.close(bandsResultSet);
			SqlServerDAOFactory.close(identifiersResultSet);
		}
		return guestData;
	}

	@Override
	public GuestData GetGuest(String identifierType, String identifierValue)	throws DAOException {
		GuestData guestData = null;
		Connection connection = null;
		CallableStatement statement = null;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(GET_GUEST);
			statement.setString("@identifierType", identifierType);
			statement.setString("@identifierValue", identifierValue);
			
			guestData = GetGuestData(statement);			
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}

		return guestData;
	}

	@Override
	public GuestData GetGuestByEmail(String emailAddress) throws DAOException {
		GuestData guestData = null;
		Connection connection = null;
		CallableStatement statement = null;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(GET_GUEST_BY_EMAIL);
			statement.setString("@emailAddress", emailAddress);

			guestData = GetGuestData(statement);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}

		return guestData;
	}

	@Override
	public boolean DeleteGuestById(long guestId) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean SaveGuestIdentifier(String identifierType, String identifierValue,
			GuestIdentifierPut identifier) throws DAOException 
	{
		boolean result = false;
		Connection connection = null;
		CallableStatement statement = null;
		
		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(CREATE_GUEST_IDENTIFIER);
			statement.setString("@identifierType", identifierType);
			statement.setString("@identifierValue", identifierValue);
			statement.setString("@sourceSystemIdType", identifier.getType());
			statement.setString("@sourceSystemIdValue", identifier.getValue());

			statement.execute();
			
			result = true;
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}
		
		if(!result)
		{
			throw new DAOException("Save Guest Identifier failed.");
		}

		return result;
	}

	@Override
	public boolean DeleteGuestIdentifier(String identifierType, String identifierValue,
			GuestIdentifierPut identifier) throws DAOException 
	{
		boolean result = false;
		Connection connection = null;
		CallableStatement statement = null;
		
		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(DELETE_GUEST_IDENTIFIER);
			statement.setString("@identifierType", identifierType);
			statement.setString("@identifierValue", identifierValue);
			statement.setString("@sourceSystemIdType", identifier.getType());
			statement.setString("@sourceSystemIdValue", identifier.getValue());

			statement.execute();
			
			result = true;
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}
		
		if(!result)
		{
			throw new DAOException("Save Guest Identifier failed.");
		}

		return result;
	}

	@Override
	public long SaveOneViewGuest(Guest guest) throws DAOException 
	{
		long result = 0;
		
		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@guestId", guest.getGuestId());
			parameters.put("@swid", guest.getSwid());
			parameters.put("@guestType", guest.getIDMSTypeName());
			parameters.put("@lastname", guest.getName().getLastName());
			parameters.put("@firstname", guest.getName().getFirstName());
			parameters.put("@DOB", guest.getDateOfBirth());
			parameters.put("@middlename", guest.getName().getMiddleName());
			parameters.put("@title", guest.getName().getTitle());
			parameters.put("@suffix", guest.getName().getSuffix());
			parameters.put("@emailAddress", guest.getEmailAddress());
			parameters.put("@parentEmail", guest.getParentEmail());
			parameters.put("@countryCode", guest.getCountryCode());
			parameters.put("@languageCode", guest.getLanguageCode());
			parameters.put("@gender", guest.getGender());
			parameters.put("@userName", guest.getUserName());
			parameters.put("@visitCount", guest.getVisitCount());
			parameters.put("@avatarName", guest.getAvatar());

			result = super.ExecuteCreateProcedure(CREATE_GUEST, parameters, "@guestId");
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		if(result == 0)
		{
			throw new DAOException("Save Guest failed.");
		}
		
		return result;
	}

	@Override
	public boolean UpdateGuestEmail(GuestEmailPut guestEmail)
			throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

    /*
	@visitCount int = NULL,
	@avatarName nvarchar(50) = NULL,
	@active bit = NULL)
	*/
	@Override
	public boolean UpdateGuest(Guest guest) throws DAOException {
		boolean result = false;
		
		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			boolean active = false;

			parameters.put("@guestId", guest.getGuestId());
			parameters.put("@guestType", guest.getIDMSTypeName());
		    parameters.put("@lastname", guest.getName().getLastName());
    	    parameters.put("@firstname", guest.getName().getFirstName());
		    parameters.put("@DOB", guest.getDateOfBirth());
		    parameters.put("@middlename", guest.getName().getMiddleName());
		    parameters.put("@title", guest.getName().getTitle());
		    parameters.put("@suffix", guest.getName().getSuffix());
			parameters.put("@emailAddress", guest.getEmailAddress());
		    parameters.put("@parentEmail", guest.getParentEmail());
		    parameters.put("@countryCode", guest.getCountryCode());
		    parameters.put("@languageCode", guest.getLanguageCode());
		    parameters.put("@gender", guest.getGender());
		    parameters.put("@userName", guest.getUserName());
		    parameters.put("@avatarName", guest.getAvatar());
            parameters.put("@visitCount", guest.getVisitCount());

			if (guest.getStatus().equals("Active"))
			{
				active = true;
			}

            parameters.put("@active", active);

			result = super.ExecuteUpdateProcedure(UPDATE_GUEST, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}

		return result;
	}

	@Override
	public GuestSearchCollection SearchGuestByName(String searchName)
			throws DAOException 
	{
		GuestSearchCollection guestSearchCollection = null;
		
		try
		{
			ResultSetHandler<List<GuestSearchItem>> handler = 
					new BeanListHandler<GuestSearchItem>(GuestSearchItem.class, GuestSearchItemRowProcessor.INSTANCE);

			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@searchString", searchName);

			List<GuestSearchItem> guestSearchItems = super.ExecuteProcedure(
					GET_GUEST_BY_NAME, handler, parameters);
			
			guestSearchCollection = new GuestSearchCollection();
			
			guestSearchCollection.setResults(guestSearchItems);
			
			guestSearchCollection.setSearchString(searchName);
						
			if (guestSearchCollection.getResults().size() == 0)
			{
				guestSearchCollection = null;
			}
			else
			{
				guestSearchCollection.setCount(guestSearchCollection.getResults().size());
				
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}

		return guestSearchCollection;
	}

	@Override
	public ScheduledItemCollection GetScheduledItems(String identifierType,	String identifierValue) throws DAOException
	{
		ScheduledItemCollection scheduledItems = null;

		try
		{
			ResultSetHandler<List<ScheduledItem>> handler = new BeanListHandler<ScheduledItem>(ScheduledItem.class);
			
			List<ScheduledItem> scheduledItemList = super.ExecuteProcedure(GET_GUEST_SCHEDULED_ITEMS, handler, identifierType, identifierValue);
			
			if(scheduledItemList != null)
			{
				if(scheduledItemList.size() > 0)
				{
					scheduledItems = new ScheduledItemCollection();
					
					scheduledItems.setItemCount(scheduledItemList.size());
					scheduledItems.setItemLimit(scheduledItemList.size());
					scheduledItems.setStartItem(0);
					scheduledItems.setEntries(scheduledItemList);
				}
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return scheduledItems;
	}

	@Override
	public GuestDataResult GetGuestData(String identifierType,
			String identifierValue, String startDate, String endDate)
			throws DAOException
	{
		GuestDataResult guestDataResult = null;
		
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;
		ResultSet xbands = null;
		ResultSet entitlements = null;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(GET_GUEST_DATA);
			statement.setString("@identifierType", identifierType);
			statement.setString("@identifierValue", identifierValue);
			statement.setString("@startDate", startDate);
			statement.setString("@endDate", endDate);
			
			if(statement.execute())
			{
				resultSet = statement.getResultSet();
				
				List<GuestDataGuestIdentifier> identifierList = 
						GuestDataGuestIdentifierRowProcessor.INSTANCE.toBeanList(resultSet, 
								GuestDataGuestIdentifier.class);
				
				//Guest should always have at least one identifier, xid.
				if(identifierList.size() > 0)
				{

					com.disney.xband.idms.lib.model.oneview.GuestData guestData = new com.disney.xband.idms.lib.model.oneview.GuestData();
					
					GuestDataGuest guest = new GuestDataGuest();
					
					Link self = new Link();
					
					self.setHref(
							"/guest-data?guest-id-type=" + identifierType + 
							"&guest-id-value=" + identifierValue + 
							"&start-date-time=" + startDate + 
							"&end-date-time=" + endDate);
					
					guestData.getLinks().setSelf(self);
					
					guest.setGuestIdentifiers(identifierList);
								
					guestData.setGuest(guest);
					
					if (statement.getMoreResults()) 
					{
						xbands = statement.getResultSet();
						
						List<GuestDataXband> xbandList = 
								GuestDataXbandRowProcessor.INSTANCE.toBeanList(xbands, 
										GuestDataXband.class);
						
						//guestData.setXBands(xbandList);
						guestData.xBands = xbandList;
						
						if (statement.getMoreResults())
						{
							entitlements = statement.getResultSet();
		
							List<GuestDataEntitlement> entitlementList = 
									GuestDataEntitlementRowProcessor.INSTANCE.toBeanList(entitlements, 
											GuestDataEntitlement.class);
							
							guestData.setEntitlements(entitlementList);	
						}	
					}
					
					guestDataResult = new GuestDataResult();
					guestDataResult.setGuestData(guestData);
				}
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(resultSet);
			SqlServerDAOFactory.close(xbands);
			SqlServerDAOFactory.close(entitlements);
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}

		return guestDataResult;
	}

	@Override
	public GuestLocatorCollection GetGuestLocators()
			throws DAOException
	{
		GuestLocatorCollection result = null;
		List<String> guestLocators = new ArrayList<String>();
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;		

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(GET_GUEST_LOCATORS);
			
			if(statement.execute())
			{
				resultSet = statement.getResultSet();

				while (resultSet.next())
				{
					guestLocators.add(resultSet.getString("guestLocator"));
				}
			}
			
			if(guestLocators.size()>0)
			{
				result = new GuestLocatorCollection();
				result.setGuestLocators(guestLocators);
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(resultSet);
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}

		return result;
	}

	@Override
	public GuestData CreateReservationGuest(ReservationGuest reservationGuest)
			throws DAOException
	{
		long result = 0;
		
		try
		{			
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@guestId", 0);
			parameters.put("@guestType", reservationGuest.getGuestType());
			parameters.put("@lastname", reservationGuest.getLastName());
			parameters.put("@firstname", reservationGuest.getFirstName());
			parameters.put("@guestIdType", reservationGuest.getGuestIdType());
			parameters.put("@guestIdValue", reservationGuest.getGuestIdValue());
			parameters.put("@xbandRequestId", reservationGuest.getXbandRequestId());
			parameters.put("@acquisitionType", reservationGuest.getAcquisitionType());
			parameters.put("@acquisitionId", reservationGuest.getAcquisitionId());

            if(reservationGuest.getAcquisitionStartDate() == null) {
                parameters.put("@acquisitionStartDate", reservationGuest.getAcquisitionUpdateDate());
            }
            else {
                parameters.put("@acquisitionStartDate", reservationGuest.getAcquisitionStartDate());
            }

			parameters.put("@acquisitionUpdateDate", reservationGuest.getAcquisitionUpdateDate());
			parameters.put("@xbandOwnerId", reservationGuest.getXbandOwnerId());
			parameters.put("@isPrimaryGuest", reservationGuest.getIsPrimaryGuest());

			result = super.ExecuteCreateProcedure(CREATE_RESERVATION_GUEST, parameters, "@guestId");
			
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		if(result == 0)
		{
			throw new DAOException("Save Reservation Guest failed.");
		}
		
		return GetGuest("guestId", String.valueOf(result));
	}

	@Override
	public List<GuestAcquisition> GetGuestAcquisitionByGuest(
			String identifierType, String identifierValue) throws DAOException
	{
		List<GuestAcquisition> result = new ArrayList<GuestAcquisition>();

		try
		{
			ResultSetHandler<List<GuestAcquisition>> handler = 
					new BeanListHandler<GuestAcquisition>(GuestAcquisition.class, GuestAcquisitionRowProcessor.INSTANCE);

			result = super.ExecuteProcedure(GET_GUEST_ACQUISITION, 
					handler, identifierType, identifierValue);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}

		return result;
	}

	@Override
	public List<GuestAcquisition> GetGuestAcquisitionByXbandRequest(
			String xbandRequestId) throws DAOException
	{
		List<GuestAcquisition> result = new ArrayList<GuestAcquisition>();

		try
		{
			ResultSetHandler<List<GuestAcquisition>> handler = 
					new BeanListHandler<GuestAcquisition>(GuestAcquisition.class, GuestAcquisitionRowProcessor.INSTANCE);

			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@xbandRequestId", xbandRequestId);

			result = super.ExecuteProcedure(GET_GUEST_ACQUISITION_BY_XBAND_REQUEST, 
					handler, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}

		return result;
	}

	@Override
	public List<GuestAcquisition> GetGuestAcquisitionByAcquisition(
			String acquisitionId, String acquisitionIdType) throws DAOException
	{
		List<GuestAcquisition> result = new ArrayList<GuestAcquisition>();

		try
		{
			ResultSetHandler<List<GuestAcquisition>> handler = 
					new BeanListHandler<GuestAcquisition>(GuestAcquisition.class, GuestAcquisitionRowProcessor.INSTANCE);

			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@acquisitionId", acquisitionId);
			parameters.put("@acquisitionIdType", acquisitionIdType);

			result = super.ExecuteProcedure(GET_GUEST_ACQUISITION_BY_ACQUISITION, 
					handler, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}

		return result;
	}
}
