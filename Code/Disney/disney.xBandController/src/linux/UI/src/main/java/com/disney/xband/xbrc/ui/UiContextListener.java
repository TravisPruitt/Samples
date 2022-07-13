package com.disney.xband.xbrc.ui;

import com.disney.xband.xbrc.ui.db.UIConnectionPool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/4/13
 * Time: 5:03 PM
 */
public class UiContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        UIProperties.getInstance();
        UIConnectionPool.getInstance();
        UIProperties.getInstance().initAudit();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
