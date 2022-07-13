package com.disney.xband.xbrms.common.model;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/7/13
 * Time: 6:58 PM
 */
public enum HealthItemType {
    XBRC("XbrcDto", 8080),
    IDMS("IdmsDto", 8080),
    JMSLISTENER("JmsListenerDto", 8081);

    private final String className;
    private final int port;


    HealthItemType(String className, int port) {
        this.className = className;
        this.port = port;
    }
    
    public static HealthItemType getByClassName(String className)
    {
    	if (className == null)
    		return null;
    	
    	if (className.indexOf(XBRC.className()) >= 0)
    		return XBRC;
    	else if (className.indexOf(IDMS.className()) >= 0)
    		return IDMS;
    	else if (className.indexOf(JMSLISTENER.className()) >= 0)
    		return JMSLISTENER;
    	
    	return null;
    }

    public String className() {
        return className;
    }

    public int port() {
        return port;
    }
}
