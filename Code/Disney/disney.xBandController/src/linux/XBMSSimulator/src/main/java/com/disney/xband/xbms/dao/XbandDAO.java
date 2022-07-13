package com.disney.xband.xbms.dao;

import com.disney.xband.jms.lib.entity.xbms.Xband;

public interface XbandDAO
{
	public Xband GetXband(String xbandId) throws DAOException;

}
