package com.disney.xband.jms.lib.connection;

import org.apache.log4j.Logger;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConnectionManager
{
    private static Logger logger = Logger.getLogger(ConnectionManager.class);

	public static Connection createConnection(
			String connectionFactoryJndiName, String brokerDomain,
			String brokerUrl, String user, String password, ExceptionListener el) throws JMSException, NamingException
	{
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sonicsw.jndi.mfcontext.MFContextFactory");
		env.put("com.sonicsw.jndi.mfcontext.domain", brokerDomain);
		env.put(Context.PROVIDER_URL, brokerUrl);
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, password);

		InitialContext context = new InitialContext(env);

		// create the connection factory
		javax.jms.ConnectionFactory factory = (javax.jms.ConnectionFactory) context
				.lookup(connectionFactoryJndiName);

		// now use the factory to create our connection
		Connection connection = factory.createConnection(user, password);
		
		if(el != null)
		{
			connection.setExceptionListener(el);
		}
		
		return connection;

	}
	
	public static Connection createConnection(String connectionFactoryJndiName, String brokerDomain,
			String brokerUrl, String user, String password) throws JMSException, NamingException
	{
		return createConnection(connectionFactoryJndiName, brokerDomain, brokerUrl, user, password, null);
	}

	public static void closeConnection(Connection connection)
	{
		if(connection != null)
		{
            try {
                connection.close();
            }
            catch (Exception e) {
                logger.error("Connection close failed. This should never happen ", e);
            }
		}
	}
}
