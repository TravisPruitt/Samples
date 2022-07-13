package com.disney.xband.common.lib.security;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/1/12
 * Time: 3:40 PM
 */
public class InputValidator {
/*
# Validators used by ESAPI
Validator.AccountName=^[a-zA-Z0-9]{3,20}$
Validator.SystemCommand=^[a-zA-Z\\-\\/]{1,64}$
Validator.RoleName=^[a-z]{1,20}$

#the word TEST below should be changed to your application
#name - only relative URL's are supported
Validator.Redirect=^\\/NGE.*$

# Global HTTP Validation Rules
# Values with Base64 encoded data (e.g. encrypted state) will need at least [a-zA-Z0-9\/+=]
Validator.HTTPScheme=^(http|https)$
Validator.HTTPServerName=^[a-zA-Z0-9_.\\-]*$
Validator.HTTPParameterName=^[a-zA-Z0-9_:._\\-",!&']*$
Validator.HTTPParameterValue=^[a-zA-Z0-9.\\-\\/+=",@_ &!*#$\\(\\)(�)(ô)':;\\{\\}]*$
Validator.JSONParameterValues=^[a-zA-Z0-9.\\-\\/+=",\\{\\}\\[\\]@_ &!*#$\\(\\)(�)(ô)':;]*$
Validator.HTTPCookieName=^[a-zA-Z0-9\\-_]{1,32}$
Validator.HTTPCookieValue=^[a-zA-Z0-9\\-\\/+=_ ]*$
Validator.HTTPHeaderName=^[a-zA-Z0-9\\-_]{1,32}$
Validator.HTTPHeaderValue=^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&"_ #]*$
Validator.HTTPContextPath=^\\/?[a-zA-Z0-9.\\-\\/_]*$
Validator.HTTPServletPath=^[a-zA-Z0-9.\\-\\/_]*$
Validator.HTTPPath=^[a-zA-Z0-9.\\-_]*$
Validator.HTTPQueryString=^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&_ %]*$
Validator.HTTPURI=^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&_ ]*$
Validator.HTTPURL=^.*$
Validator.HTTPJSESSIONID=^[A-Z0-9]{10,30}$

# Validation of file related input
Validator.FileName=^[a-zA-Z0-9!@#$%^&{}\\[\\]()_+\\-=,.~'`]{1,255}$
Validator.DirectoryName=^[a-zA-Z0-9:/\\\\!@#$%^&{}\\[\\]()_+\\-=,.~'` ]{1,255}$

# Validation of dates. Controls whether or not 'lenient' dates are accepted.
# See DataFormat.setLenient(boolean flag) for further details.
Validator.AcceptLenientDates=false
*/
    private static final String FILE_NAME_PATTERN = "^[a-zA-Z0-9!@#$%^&{}\\[\\]()_+\\-=,.~'`]{1,255}$";
    private static final String DIR_NAME_PATTERN = "^[a-zA-Z0-9:/\\\\!@#$%^&{}\\[\\]()_+\\-=,.~'` ]{1,255}$";

    private static final String HTTP_URL_PATTERN = "^.*$";
    private static final String HTTP_URI_PATTERN = "^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&_ ]*$";
    private static final String HTTP_QUERY_STRING_PATTERN = "^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&_ %]*$";
    private static final String HTTP_HEADER_VAL_PATTERN = "^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&\"_ #%]*$";
    private static final String HTTP_PARAM_VAL_PATTERN = "^[a-zA-Z0-9.\\-\\/+=\",@_ &!*#$\\(\\)(�)(ô)':;\\{\\}%]*$";
    private static final String HTTP_COOKIE_VAL_PATTERN = "^[a-zA-Z0-9\\\\-\\\\/+=_ ]*$";
    private static final String USER_NAME = "^[a-zA-Z0-9]{3,20}$";
    private static final String USER_PW = "^[a-zA-Z0-9\\?\\<\\>\\.\\,\\?\\/\\!\\@\\#\\$\\%\\^\\&\\*\\_\\+\\=\\-\\~\\(\\)\\{\\}\\[\\]\\:\\;]{1,48}$";

    private static final Pattern sFilePattern;
    private static final Pattern sDirPattern;

    private static final Pattern sUrlPattern;
    private static final Pattern sUriPattern;
    private static final Pattern sQueryStringPattern;
    private static final Pattern sHeaderValPattern;
    private static final Pattern sParamValPattern;
    private static final Pattern sCookieValPattern;

    private static final Pattern sUserNamePattern;
    private static final Pattern sUserPwPattern;

    static {
        sFilePattern = Pattern.compile(InputValidator.FILE_NAME_PATTERN);
        sDirPattern = Pattern.compile(InputValidator.DIR_NAME_PATTERN);
        sUrlPattern = Pattern.compile(InputValidator.HTTP_URL_PATTERN);
        sUriPattern = Pattern.compile(InputValidator.HTTP_URI_PATTERN);
        sQueryStringPattern = Pattern.compile(InputValidator.HTTP_QUERY_STRING_PATTERN);
        sHeaderValPattern = Pattern.compile(InputValidator.HTTP_HEADER_VAL_PATTERN);
        sParamValPattern = Pattern.compile(InputValidator.HTTP_PARAM_VAL_PATTERN);
        sCookieValPattern = Pattern.compile(InputValidator.HTTP_COOKIE_VAL_PATTERN);
        sUserNamePattern = Pattern.compile(InputValidator.USER_NAME);
        sUserPwPattern = Pattern.compile(InputValidator.USER_PW);
    }

    /**
     * Validate URL.
     *
     * @param val input to validate.
     * @return validate input or throws a runtime exception.
     */
    public static String validateUrl(final String val) {
        if(val != null) {
            final Matcher m = InputValidator.sUrlPattern.matcher(val);

            if (!m.matches()) {
                throw new InputValidationException("Invalid URL: " + val);
            }
        }

        return val;
    }

    /**
     * Validate URI.
     *
     * @param val input to validate.
     * @return validate input or throws a runtime exception.
     */
    public static String validateUri(final String val) {
        if(val != null) {
            final Matcher m = InputValidator.sUriPattern.matcher(val);

            if (!m.matches()) {
                throw new InputValidationException("Invalid URI: " + val);
            }
        }

        return val;
    }

    /**
     * Validate HTTP Query String.
     *
     * @param val input to validate.
     * @return validate input or throws a runtime exception.
     */
    public static String validateQueryString(final String val) {
        if(val != null) {
            final Matcher m = InputValidator.sQueryStringPattern.matcher(val);

            if (!m.matches()) {
                throw new InputValidationException("Invalid HTTP query string: " + val);
            }
        }

        return val;
    }

    /**
     * Validate HTTP header value.
     *
     * @param val input to validate.
     * @return validate input or throws a runtime exception.
     */
    public static String validateHttpHeaderVal(final String val) {
        if(val != null) {
            final Matcher m = InputValidator.sHeaderValPattern.matcher(val);

            if (!m.matches()) {
                throw new InputValidationException("Invalid HTTP header value: " + val);
            }
        }

        return val;
    }

    /**
     * Validate HTTP parameter value.
     *
     * @param val input to validate.
     * @return validate input or throws a runtime exception.
     */
    public static String validateHttpParamVal(final String val) {
        if(val != null) {
            final Matcher m = InputValidator.sParamValPattern.matcher(val);

            if (!m.matches()) {
                throw new InputValidationException("Invalid HTTP parameter value: " + val);
            }
        }

        return val;
    }

    /**
     * Validate HTTP cookie value.
     *
     * @param val input to validate.
     * @return validate input or throws a runtime exception.
     */
    public static String validateHttpCookieVal(final String val) {
        if(val != null) {
            final Matcher m = InputValidator.sCookieValPattern.matcher(val);

            if (!m.matches()) {
                throw new InputValidationException("Invalid HTTP cookie value: " + val);
            }
        }

        return val;
    }

    /**
     * Validate file name.
     *
     * @param fileName to validate.
     * @return file name or throws a runtime exception.
     */
    public static String validateFileName(final String fileName) {
        if(fileName != null) {
            final Matcher m = InputValidator.sFilePattern.matcher(fileName);

            if (!m.matches()) {
                throw new InputValidationException("Invalid file name: " + fileName);
            }
        }

        return fileName;
    }

    /**
     * Validate directory name.
     *
     * @param fileName to validate.
     * @return directory name or throws a runtime exception.
     */
    public static String validateDirectoryName(final String fileName) {
        if(fileName != null) {
            final Matcher m = InputValidator.sDirPattern.matcher(fileName);

            if (!m.matches()) {
                throw new InputValidationException("Invalid directory name: " + fileName);
            }
        }

        return fileName;
    }

    /**
     * Get file name component from a path.
     *
     * @param path File path
     * @return file name component
     */
    public static String getFileName(final String path) {
        if(path == null) {
            return path;
        }

        final int ind = path.lastIndexOf(File.separator);

        if(ind >= 0) {
            return path.substring(ind + 1);
        }

        return path;
    }

    /**
     * Get directory name component from a path.
     *
     * @param path File path
     * @return directory name component
     */
    public static String getDirName(final String path) {
        if(path == null) {
            return path;
        }

        final int ind = path.lastIndexOf(File.separator);

        if(ind >= 0) {
            return path.substring(0, ind);
        }

        return path;
    }

    /**
     * Validate file name.
     *
     * @param path to validate.
     * @return Validate file path or throws a runtime exception.
     */
    public static String validateFilePath(final String path) {
        InputValidator.validateFileName(InputValidator.getFileName(path));
        InputValidator.validateDirectoryName(InputValidator.getDirName(path));

        return path;
    }

    /**
     * Validate user name.
     *
     * @param name to validate.
     * @return validated user name or throws a runtime exception.
     */
    public static String validateUserName(final String name) {
        if(name != null) {
            final Matcher m = InputValidator.sUserNamePattern.matcher(name);

            if (!m.matches()) {
                throw new InputValidationException("Invalid user name: " + name);
            }
        }

        return name;
    }

    /**
     * Validate user password.
     *
     * @param pw password to validate.
     * @return validated user password or throws a runtime exception.
     */
    public static String validateUserPw(final String pw) {
        if(pw != null) {
            final Matcher m = InputValidator.sUserPwPattern.matcher(pw);

            if (!m.matches()) {
                throw new InputValidationException("Invalid user password: " + pw);
            }
        }

        return pw;
    }

    /*
    public static void main(String[] args) {
        // User name

        String s = "EMUser123";

        System.out.println("Good user name: " + validateUserName(s));

        s = "EmUser'`";

        try {
            validateUserName(s);
        }
        catch (InputValidationException e) {
            System.out.println("Bad user name: " + s);
        }

        // Password

        s = "EMUser123~!@#$%^&*()_[]{},.:;=+?/><";

        System.out.println("Good user password: " + validateUserPw(s));

        s = "EmUser'";

        try {
            validateUserPw(s);
        }
        catch (InputValidationException e) {
            System.out.println("Bad user password: " + s);
        }

        s = "EmUser\"";

        try {
            validateUserPw(s);
        }
        catch (InputValidationException e) {
            System.out.println("Bad user password: " + s);
        }
    }
    */
}
