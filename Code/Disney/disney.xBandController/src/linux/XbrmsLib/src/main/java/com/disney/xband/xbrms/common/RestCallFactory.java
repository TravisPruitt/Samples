package com.disney.xband.xbrms.common;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 12/27/12
 * Time: 10:26 PM
 */
public class RestCallFactory {
    public static IRestCall createRestCall(boolean isLocalCall) throws Exception {
        if(isLocalCall) {
            return (IRestCall) Class.forName("com.disney.xband.xbrms.server.LocalCall").newInstance();
        }
        else {
            return (IRestCall) Class.forName("com.disney.xband.xbrms.client.rest.RestCall").newInstance();
        }
    }
}
