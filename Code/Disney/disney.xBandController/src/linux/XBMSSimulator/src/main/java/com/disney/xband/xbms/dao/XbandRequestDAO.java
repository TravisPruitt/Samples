package com.disney.xband.xbms.dao;

import com.disney.xband.jms.lib.entity.xbms.XbandRequest;

public interface XbandRequestDAO
{
	public XbandRequest GetXbandRequest(String xbandRequestId) throws DAOException;
}
