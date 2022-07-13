package com.disney.xband.xbms.dao.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.disney.xband.jms.lib.entity.xbms.CustomizationSelection;
import com.disney.xband.jms.lib.entity.xbms.QualifyingId;
import com.disney.xband.jms.lib.entity.xbms.Xband;
import com.disney.xband.jms.lib.entity.xbms.XbandRequest;
import com.disney.xband.xbms.dao.DAOException;
import com.disney.xband.xbms.dao.MessageDAO;
import com.disney.xband.xbms.web.MessageBatch;
import com.disney.xband.xbms.web.Message;
import com.disney.xband.xbms.web.MessageState;

public class SqlMessageDAO implements MessageDAO
{
	private static String START_MESSAGE_BATCH = "{call usp_messageBatch_start(?,?,?,?)}";
	private static String FINISH_MESSAGE_BATCH = "{call usp_messageBatch_finish(?)}";
	private static String GET_MESSAGE_BATCH = "{call usp_messageBatch_retrieve(?)}";
	private static String GET_XBAND_NEXT = "{call usp_xband_next(?)}";
	private static String GET_XBANDREQUEST_NEXT = "{call usp_xbandRequest_next(?)}";
	private static String XBAND_MESSAGE_SENT = "{call usp_xband_sent(?)}";
	private static String XBANDREQUEST_MESSAGE_SENT = "{call usp_xbandRequest_sent(?)}";

	@Override
	public int Start(int count, int puckCount, int castMemberCount) throws DAOException
	{
		int messageBatchId = 0;
		Connection connection = null;
		CallableStatement statement = null;

		try
		{
			connection = SqlDAOFactory.createConnection();
			statement = connection.prepareCall(START_MESSAGE_BATCH);
			statement.setInt("@messageBatchId", messageBatchId);
			statement.setInt("@count", count);
			statement.setInt("@puckCount", puckCount);
			statement.setInt("@castMemberCount", castMemberCount);
			statement.registerOutParameter("@messageBatchId", java.sql.Types.INTEGER);
			
			statement.execute();

			messageBatchId = statement.getInt("@messageBatchId");
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}
		
		return messageBatchId;
	}

	@Override
	public void Finish(int messageBatchId) throws DAOException
	{
		Connection connection = null;
		CallableStatement statement = null;

		try
		{
			connection = SqlDAOFactory.createConnection();
			statement = connection.prepareCall(FINISH_MESSAGE_BATCH);
			statement.setInt("@messageBatchId", messageBatchId);
			
			statement.execute();
			
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}
	}

	@Override
	public MessageBatch Retreive(int messageBatchId) throws DAOException
	{
		MessageBatch result = null;
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;
		ResultSet messagesResultSet = null;

		try
		{
			connection = SqlDAOFactory.createConnection();
			statement = connection.prepareCall(GET_MESSAGE_BATCH);
			statement.setInt("@messageBatchId", messageBatchId);
			
			statement.execute();

			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				result = new MessageBatch();
				
				result.setMessageBatchId(resultSet.getInt("messageBatchId"));
				result.setMessageBatchDescription(resultSet.getString("messageBatchDescription"));
				result.setStartDateTime(resultSet.getString("startDateTime"));
				result.setFinishDateTime(resultSet.getString("finishDateTime"));
				
				if (statement.getMoreResults()) // Get celebration guests
				{
					messagesResultSet = statement.getResultSet();
					List<Message> messages = new ArrayList<Message>();
					
					while(messagesResultSet.next())
					{
						Message message = new Message();
						
						message.setFirstName(messagesResultSet.getString("firstName"));
						message.setLastName(messagesResultSet.getString("lastName"));
						message.setXbandOwnerId(messagesResultSet.getString("xbandOwnerId"));
						message.setXbandId(messagesResultSet.getString("xbandId"));
						message.setXbandRequestId(messagesResultSet.getString("xbandRequestId"));
						message.setXbandRequestMessageState(MessageState.values()[messagesResultSet.getInt("xbandRequestMessageState")]);
						message.setXbandMessageState(MessageState.values()[messagesResultSet.getInt("xbandMessageState")]);
						
						messages.add(message);
					
					}
					
					result.setMessages(messages);				
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
			SqlDAOFactory.close(messagesResultSet);
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}
		
		return result;
	}

	@Override
	public Xband GetNextXbandMessage(int messageBatchId) throws DAOException
	{
		Xband result = null;
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;

		try
		{
			connection = SqlDAOFactory.createConnection();
			statement = connection.prepareCall(GET_XBAND_NEXT);
			statement.setInt("@messageBatchId", messageBatchId);
			
			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				result = new Xband();
				
				result.setAssignmentDateTime(resultSet.getString("assignmentDateTime"));
				result.setBandRole("Guest");
				result.setExternalNumber(resultSet.getString("externalNumber"));
				result.setGuestId(resultSet.getString("guestId"));
				result.setGuestIdType(resultSet.getString("guestIdType"));
				result.setProductId(resultSet.getString("productId"));
				result.setPublicId(resultSet.getLong("publicId"));
				result.setSecondaryState(resultSet.getString("secondaryState"));
				result.setSecureId(resultSet.getLong("secureId"));
				result.setShortRangeTag(resultSet.getString("shortRangeTag"));
				result.setState(resultSet.getString("state"));
				result.setXbandOwnerId(resultSet.getString("xbandOwnerId"));
				result.setXbandId(resultSet.getString("xbandId"));
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlDAOFactory.close(resultSet);
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}
		
		return result;
	}

	@Override
	public XbandRequest GetNextXbandRequestMessage(int messageBatchId)
			throws DAOException
	{
		XbandRequest result = null;
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
			statement = connection.prepareCall(GET_XBANDREQUEST_NEXT);
			statement.setInt("@messageBatchId", messageBatchId);
			
			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				result = new XbandRequest();
				
				result.setOptions("/reorder-options/" + resultSet.getString("xbandRequestId"));
				result.setOrder("/orders/"  + resultSet.getString("xbandRequestId"));
				result.setState("FULFILLMENT");
				result.setPrimaryGuestOwnerId(resultSet.getString("primaryGuestOwnerId"));
				result.setXbandRequestId(resultSet.getString("xbandRequestId"));
				result.setAcquisitionId(resultSet.getString("acquisitionId"));
				result.setAcquisitionIdType(resultSet.getString("acquisitionIdType"));
				result.setAcquisitionStartDate(resultSet.getString("acquisitionStartDate"));
				result.setAcquisitionUpdateDate(resultSet.getString("acquisitionUpdateDate"));
				result.setCreateDate(resultSet.getString("createDate"));
				result.setUpdateDate(resultSet.getString("updateDate"));
				result.setReorder("/xband-requests/" + resultSet.getString("xbandRequestId") + "/reorder");
				result.setCustomizationEndDate(resultSet.getString("createDate"));

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
					result.setCustomizationSelections(customizationSelections);
				}
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlDAOFactory.close(guests);
			SqlDAOFactory.close(resultSet);
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}
		
		return result;
	}

	@Override
	public void XbandMessageSent(String xbandId) throws DAOException
	{
		Connection connection = null;
		CallableStatement statement = null;

		try
		{
			connection = SqlDAOFactory.createConnection();
			statement = connection.prepareCall(XBAND_MESSAGE_SENT);
			statement.setString("@xbandId",xbandId);
			
			statement.execute();
			
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}		
	}

	@Override
	public void XbandRequestMessageSent(String xbandRequestId)
			throws DAOException
	{
		Connection connection = null;
		CallableStatement statement = null;

		try
		{
			connection = SqlDAOFactory.createConnection();
			statement = connection.prepareCall(XBANDREQUEST_MESSAGE_SENT);
			statement.setString("@xbandRequestId",xbandRequestId);
			
			statement.execute();
			
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}		
		
	}

}
