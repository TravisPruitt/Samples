package com.disney.configuration;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationProperties
{
    private static Logger logger = Logger.getLogger(ConfigurationProperties.class.getName());

    public static final ConfigurationProperties INSTANCE = new ConfigurationProperties();

    private static Properties properties;

    private static String DATABASE_CONNECTION = "database.connection";
    private static String DATABASE_USER = "database.user";
    private static String DATABASE_PWD = "database.pwd";
    private static String GXP_SERVICE_URL = "gxp.url";
    private static String IDMS_SERVICE_URL = "idms.url";
    private static String IDMS_SERVICE_URL_XBANDS = "idms.url.xbands";
    private static String WAIT_TIME = "minutesToWaitBetweenRuns";
    private static String ONEVIEW_SERVICE_ROOT_URL = "oneview.url.root";
    private static String ONEVIEW_SERVICE_PROFILE_URL = "oneview.url.profile";
    private static String ONEVIEW_SERVICE_IDENTIFIERS_URL = "oneview.url.identifiers";
    private static String WORKER_THREADS = "workerThreads";
    private static String RETRY_ATTEMPTS = "retryAttempts";
    private static String MINUTES_TO_RETRY = "waitToTryInMinutes";
    private static String ALWAYS_WRITE_GXP_LINK_ID = "AlwaysWriteGxpLinkId";


    private static int worker_thread_default=4;
    private static int retryAttempts_default = 576 ;
    private static int waitTimeToTry_default = 10;
    private static boolean  AlwaysWriteGxpLinkId_default = false;


    public static String DBDriver = "net.sourceforge.jtds.jdbc.Driver";




    private ConfigurationProperties()
    {
        //BasicConfigurator.configure();
        logger.info("Configuration Properties Setup.");
        properties = new Properties();

        try
        {
            properties.load(new FileInputStream("xibackfill.properties"));
        }
        catch (Exception ex)
        {
            System.out.println(ex.getLocalizedMessage());
            logger.error(ex.getLocalizedMessage());
        }

    }


    public boolean getAlwaysWriteGxPLinkId()
    {
        String s = properties.getProperty(ALWAYS_WRITE_GXP_LINK_ID);
        boolean alwaysWriteGXPLinkId = AlwaysWriteGxpLinkId_default;

        if (s != null && !s.isEmpty())
        {
            try {
                alwaysWriteGXPLinkId = Boolean.parseBoolean(s);
            }
            catch (Exception ex)
            {
                alwaysWriteGXPLinkId = AlwaysWriteGxpLinkId_default;
            }

        }

        return  alwaysWriteGXPLinkId;

    }


    public String getIDMSServiceXBandsURL()
    {
        return properties.getProperty(IDMS_SERVICE_URL_XBANDS);
    }


    public int MinutesToRetry()
    {
        String s = properties.getProperty(MINUTES_TO_RETRY);
        int minutes = waitTimeToTry_default;

        if (s != null && !s.isEmpty())
        {
            try {

                minutes = Integer.valueOf(s);

            }
            catch (Exception ex) {
               minutes = waitTimeToTry_default;
            }
        }

        return minutes;
    }


    public int getRetryAttempts()
    {
        String r = properties.getProperty(RETRY_ATTEMPTS);
        int retryAttempts = retryAttempts_default;

        if (r != null && !r.isEmpty())
        {
            try {

                retryAttempts = Integer.valueOf(r);

            }
            catch (Exception ex)
            {
                retryAttempts = retryAttempts_default;
            }

        }

        return retryAttempts;
    }

    public int getWorkerThreads()
    {
        String s = properties.getProperty(WORKER_THREADS);
        int workers = worker_thread_default;

        if (s!= null && !s.isEmpty())
        {
            // try to cast it to an int.
            try
            {
                workers = Integer.valueOf(s);
            }
            catch (Exception ex)
            {
               // Whatever.
                workers = worker_thread_default;
            }
        }

        return workers;

    }

    public String  getOneViewGuestIdentifiers()
    {
        return properties.getProperty(ONEVIEW_SERVICE_IDENTIFIERS_URL);
    }

    public String  getOneViewGuestProfile()
    {
        return properties.getProperty(ONEVIEW_SERVICE_PROFILE_URL);
    }

    public String getOneViewRootURL()
    {
        return properties.getProperty(ONEVIEW_SERVICE_ROOT_URL);
    }


    public int MinutesToWait()
    {


        return Integer.valueOf(properties.getProperty(WAIT_TIME));
    }

    public  String getConnectionString()
    {
       return properties.getProperty(DATABASE_CONNECTION);
    }

    public  String getDatabaseUser()
    {
        return properties.getProperty(DATABASE_USER);
    }

    public  String getDatabasePwd()
    {
        return properties.getProperty(DATABASE_PWD);
    }

    public  String getGXPURL()
    {
        return properties.getProperty(GXP_SERVICE_URL);
    }

    public  String getIDMSURL()
    {
        return properties.getProperty(IDMS_SERVICE_URL);
    }
}
