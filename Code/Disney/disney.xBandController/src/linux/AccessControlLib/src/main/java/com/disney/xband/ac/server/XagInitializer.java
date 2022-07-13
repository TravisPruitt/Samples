package com.disney.xband.ac.server;

import com.disney.xband.ac.lib.AuthServiceFactory;
import com.disney.xband.ac.lib.IAuthService;
import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.ac.lib.XbUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/27/12
 * Time: 4:42 PM
 */
public class XagInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //try {Thread.sleep(5000);} catch(Exception e) {};
        Logger logger = Logger.getLogger(this.getClass());
        InputStream is = null;
        final ServletContext sc = servletContextEvent.getServletContext();
        final HashMap<String, String> props = new HashMap<String, String>(16);
        final Enumeration<String> en = sc.getInitParameterNames();

        while(en.hasMoreElements()) {
            final String pName = en.nextElement();
            props.put(pName, sc.getInitParameter(pName));
        }

        // Load properties
        XbUtils.loadProperties(props, servletContextEvent.getServletContext().getContextPath().substring(1), logger);
        sc.setAttribute(PkConstants.NAME_ATTR_AC_PROPS, props);

        if(logger.isTraceEnabled()) {
            logger.trace("xAG will be using these properties:\n" + XbUtils.dumpProperties(props));
        }

        String usersPath = props.get(PkConstants.NAME_PROP_AC_USERS_FILE);

        if(usersPath == null) {
            usersPath = PkConstants.DEFAULT_AC_USERS_FILE;
        }

        // Initialize authentication service
        props.put(PkConstants.NAME_PROP_AC_USERS_PATH, sc.getRealPath(usersPath));
        final IAuthService auth = AuthServiceFactory.getAuthService(props);
        sc.setAttribute(PkConstants.NAME_ATTR_AC_AUTH_SERVICE, auth);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
