package com.disney.xband.ac.lib;

import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/25/12
 * Time: 11:56 AM
 */
public interface IAuthService {
    void init(HashMap<String, String> props);
    List<String> getDirectories();
    XbSecureToken authenticate(IAuthRequestWrapper request);
    XbSecureToken authenticate(IAuthRequestWrapper request, XbSecureToken token, StringBuilder err);
    XbSecureToken authenticate(IAuthRequestWrapper request, String directory, String name, String password, StringBuilder err);
}
