package com.disney.xband.idms.dao;

import com.disney.xband.idms.dao.sql.SqlServerDAOFactory;
import com.disney.xband.idms.dao.sql.cache.CacheSqlServerDAOFactory;
import com.disney.xband.idms.lib.model.ConfigurationSettings;

import java.sql.Connection;

//Abstract class DAO Factory
public abstract class DAOFactory 
{
	public static final String SQL_SERVER_DRIVER = "net.sourceforge.jtds.jdbc.Driver";

	public static DAOFactory getDAOFactory() 
	{
		if(ConfigurationSettings.INSTANCE.getDatabaseDriver().compareToIgnoreCase(SQL_SERVER_DRIVER) == 0)
		{
			if (ConfigurationSettings.INSTANCE.isCacheEnabled())
				return new CacheSqlServerDAOFactory();
			else
				return new SqlServerDAOFactory();
		}

		return null;
	}
	
	public abstract ConnectionStatus getStatus();
	
	public abstract CelebrationDAO getCelebrationDAO();

	public abstract GuestDAO getGuestDAO();

	public abstract PartyDAO getPartyDao();

	public abstract XBandDAO getXBandDAO();

    public boolean testConnectivity() {
        Connection conn = null;

        try {
            conn = SqlServerDAOFactory.createConnection();
            return true;
        }
        catch (Exception e) {
            return false;
        }
        finally {
            if(conn != null) {
                SqlServerDAOFactory.release(conn);
            }
        }
    }
}