package com.disney.xband.ac.lib;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/8/12
 * Time: 4:22 PM
 */
public class AuthRequestWrapperFactory {
    public static IAuthRequestWrapper createAuthRequestWrapper(Object request) {
        final String name = request.getClass().getName();
        if(name.startsWith("org.apache.") || name.startsWith("com.disney.")) {
            return new ServletAuthRequestWrapper(request);
        }
        else {
            throw new UnsupportedOperationException("Request class is not supported.");
            // return new SfAuthRequestWrapper(request);
        }
    }
}
