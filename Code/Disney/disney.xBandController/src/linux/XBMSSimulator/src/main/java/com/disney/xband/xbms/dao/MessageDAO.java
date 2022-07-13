package com.disney.xband.xbms.dao;

import com.disney.xband.jms.lib.entity.xbms.Xband;
import com.disney.xband.jms.lib.entity.xbms.XbandRequest;
import com.disney.xband.xbms.web.MessageBatch;

public interface MessageDAO
{
	public int Start(int count, int puckCount, int castMemberCount) throws DAOException;
	
	public void Finish(int messageBatchId) throws DAOException;

	public MessageBatch Retreive(int messageBatchId) throws DAOException;
	
	public Xband GetNextXbandMessage(int messageBatchId) throws DAOException;

	public XbandRequest GetNextXbandRequestMessage(int messageBatchId) throws DAOException;

	public void XbandMessageSent(String xbandId) throws DAOException;

	public void XbandRequestMessageSent(String xbandRequestId) throws DAOException;
}
