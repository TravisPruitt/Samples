package com.disney.xband.idms.web;

import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.disney.xband.common.lib.audit.AuditFactory;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.NGEPropertiesDecoder;

public class IDMSServletContextListener implements ServletContextListener 
{
	private static Logger logger = Logger.getLogger(IDMSServletContextListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent context) 
	{
	}

	@Override
	public void contextInitialized(ServletContextEvent context) 
	{
		// first, read the properties file to get the database parameters
		NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
				
		// get property settings
		// is there is a system property to identify where the environemtn.properites is
		String sPropFile = System.getProperty("environment.properties");
		if (sPropFile != null)
		{
			logger.info("The environment.properties java argument is set. Using the " + sPropFile + " properties file.");
			decoder.setPropertiesPath(sPropFile);
		}
		
		String sJasyptPropFile = System.getProperty("jasypt.properties");
		if (sJasyptPropFile != null)
		{
			logger.info("The jasypt.properites java argument is set. Using the " + sJasyptPropFile + " properties file.");
			decoder.setJasyptPropertiesPath(sJasyptPropFile);
		}

		try
		{
			decoder.initialize();
			Properties prop = decoder.read();
			
			String keystore = prop.getProperty("nge.xconnect.idms.ssl.keystore.client");
			String pass = prop.getProperty("nge.xconnect.idms.ssl.keystorePassword.client");
			
			if (keystore == null || keystore.trim().isEmpty() || pass == null || pass.trim().isEmpty())
			{
				/*
				 * The keystore and its password are a tandem, either both are provided, or they should be ignored.
				 */
				logger.error("Communication over https not working! Missing properties in environment.properties file" + 
						": ssl.keyStore.client and ssl.keyStorePassword.client");
				return;
			}
			
			System.setProperty("javax.net.ssl.keyStore", keystore);
			System.setProperty("javax.net.ssl.keyStorePassword", pass);

            initializeAudit(prop);
		}
		catch (Exception ex)
		{
			logger.fatal("!! Could not read the properties file [" + 
					decoder.getPropertiesPath() + "]: "
						 + ex.getLocalizedMessage());
		}
	}

    private static void initializeAudit(final Properties props) {
        try {
            final AuditConfig config = new AuditConfig();
            final String implClass = "com.disney.xband.xbrms.common.audit.providers.AuditLog4JImpl";
            config.setEnabled(false);
            config.setImplStack(implClass);
            config.setAppClass(AuditEvent.AppClass.IDMS.toString());
            config.setAppId("IDMS");

            if(props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_ENABLED) != null) {
                config.setEnabled(Boolean.parseBoolean((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_ENABLED)));
            }

            if(props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_LEVEL) != null) {
                config.setLevel(AuditEvent.Type.valueOf((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_LEVEL)));
            }

            config.setInterceptors(AuditConfig.createAuditInterceptorConfigsFromProps(props, false));
            config.setDescTemplate(AuditConfig.createDescriptionTemplatesFromProps(props));

            final AuditFactory auditFactory = new AuditFactory(config);

            Auditor.getInstance().setAuditor(auditFactory.getAudit());
            Auditor.getInstance().setEventsProvider(auditFactory.getAuditControl().getEventsProvider(implClass));
            Auditor.getInstance().setConfig(config);
            Auditor.getInstance().setAuditFactory(auditFactory);
        }
        catch (Exception e) {
            logger.error("Audit subsystem configuration failed: " + e.getMessage());
        }
    }
}
