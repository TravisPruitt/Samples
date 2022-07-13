package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DbTransactionManager {

	public static class DbTransaction 
	{
		private int transactionIsolation;
		private boolean autoCommit;
	}
	
	public static DbTransaction startTransaction(Connection conn) throws SQLException
	{
		DbTransaction tr = new DbTransaction();
		tr.transactionIsolation = conn.getTransactionIsolation();
		conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		tr.autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		return tr;
	}
	
	public static void commitTransaction(DbTransaction tr, Connection conn) throws SQLException
	{
		try
		{
			conn.commit();			
		}
		finally 
		{
			conn.setAutoCommit(tr.autoCommit);
			conn.setTransactionIsolation(tr.transactionIsolation);
		}
	}
	
	public static void rollbackTransaction(DbTransaction tr, Connection conn) throws SQLException
	{	
		try
		{
			if (!conn.getAutoCommit())
				conn.rollback();
		}
    	finally 
    	{
    		conn.setAutoCommit(tr.autoCommit);
        	conn.setTransactionIsolation(tr.transactionIsolation);
    	}    	
	}
}
