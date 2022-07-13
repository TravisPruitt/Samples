package com.disney.xband.xbms.dao;

import com.disney.xband.xbms.dao.sql.SqlDAOFactory;
import com.disney.xband.xbms.web.ConfigurationSettings;

public abstract class DAOFactory
{
	public static final String SQL_SERVER_DRIVER = "net.sourceforge.jtds.jdbc.Driver";

	public static DAOFactory getDAOFactory() 
	{
		if(ConfigurationSettings.INSTANCE.getDatabaseDriver().compareToIgnoreCase(SQL_SERVER_DRIVER) == 0)
		{
			return new SqlDAOFactory();
		}

		return null;
	}
	
	public abstract ConnectionStatus getStatus();
	
	public abstract XbandDAO getXbandDAO();

	public abstract XbandRequestDAO getXbandRequestDAO();

	public abstract MessageDAO getMessageDAO();
}
