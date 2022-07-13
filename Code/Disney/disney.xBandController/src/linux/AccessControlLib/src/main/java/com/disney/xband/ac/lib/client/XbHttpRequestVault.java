package com.disney.xband.ac.lib.client;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/18/12
 * Time: 12:34 PM
 */
public class XbHttpRequestVault extends HttpServletRequestWrapper implements Serializable {
    public enum RequestState {NOT_AUTH, REDIRECTED, AUTH_OK, AUTH_FAILED}

    private RequestState state;
    private Hashtable<String, Enumeration<String>> headers;
    private HashMap<String, String[]> params;
    private Vector<String> paramNames;
    private String body;
    private String contextPath;
    private String method;
    private String pathInfo;
    private String pathTranslated;
    private String queryString;
    private String requestURI;
    private StringBuffer requestURL;
    private String servletPath;
    private XbHttpRequestVault savedReq;

    public XbHttpRequestVault(HttpServletRequest request) {
        super(request);

        this.headers = new Hashtable<String, Enumeration<String>>();
        this.state = RequestState.NOT_AUTH;
        this.copyData(request);
    }

    public XbHttpRequestVault(HttpServletRequest request, XbHttpRequestVault savedReq) {
        super(request);

        this.savedReq = savedReq;
    }

    private void copyData(HttpServletRequest request) {
        // Saving headers
        final Enumeration<String> aNames = request.getHeaderNames();

        while(aNames.hasMoreElements()) {
            final String aName = aNames.nextElement();
            this.headers.put(aName, request.getHeaders(aName));
        }

        // Saving params
        this.params = new HashMap<String, String[]>(32);
        this.paramNames = new Vector<String>(32);
        final Enumeration<String> pNames = request.getParameterNames();

        while(pNames.hasMoreElements()) {
            final String pName = pNames.nextElement();
            final String[] pVals = request.getParameterValues(pName);
            this.params.put(pName, pVals);
            this.paramNames.add(pName);
        }

        // Saving context path
        this.contextPath = request.getContextPath();

        // Saving method
        this.method = request.getMethod();

        // Saving path info
        this.pathInfo = request.getPathInfo();
        this.pathTranslated = request.getPathTranslated();

        // Saving query string
        this.queryString = request.getQueryString();

        // Saving request URL and URI
        this.requestURI = request.getRequestURI();
        this.requestURL = request.getRequestURL();

        // Saving servlet path
        this.servletPath = request.getServletPath();

        // Saving body
        final StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            final InputStream inputStream = request.getInputStream();

            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                final char[] charBuffer = new char[128];
                int bytesRead = -1;

                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
            else {
                stringBuilder.append("");
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        this.body = stringBuilder.toString();
    }

    public String getBody() {
        return this.body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.savedReq._getInputStream();
    }

    private ServletInputStream _getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

        ServletInputStream servletInputStream = new ServletInputStream() {
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };

        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return this.savedReq._getReader();
    }

    private BufferedReader _getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this._getInputStream()));
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return this.savedReq._getHeaders(name);
     }

    private Enumeration<String> _getHeaders(String name) {
        return this.headers.get(name);
     }

    @Override
    public Enumeration<String> getHeaderNames() {
        return this.savedReq._getHeaderNames();
     }

    private Enumeration<String> _getHeaderNames() {
        return this.headers.keys();
    }

    @Override
    public String getHeader(String name) {
        return this.savedReq._getHeader(name);
    }

    private String _getHeader(String name) {
        final Enumeration<String> vals = this.headers.get(name);

        if((vals != null) && (vals.hasMoreElements())) {
            return vals.nextElement();
        }

        return null;
    }

    @Override
    public String getContextPath() {
        return this.savedReq._getContextPath();
    }

    private String _getContextPath() {
        return this.contextPath;
    }

    @Override
    public String getPathInfo() {
        return this.savedReq._getPathInfo();
    }

    private String _getPathInfo() {
        return this.pathInfo;
    }

    @Override
    public String getPathTranslated() {
        return this.savedReq._getPathTranslated();
    }

    private String _getPathTranslated() {
        return this.pathTranslated;
    }

    @Override
    public String getQueryString() {
        return this.savedReq._getQueryString();
    }

    private String _getQueryString() {
        return this.queryString;
    }

    @Override
    public String getRequestURI() {
        return this.savedReq._getRequestURI();
    }

    private String _getRequestURI() {
        return this.requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        return this.savedReq._getRequestURL();
    }

    private StringBuffer _getRequestURL() {
        return this.requestURL;
    }

    @Override
    public String getServletPath() {
        return this.savedReq._getServletPath();
    }

    private String _getServletPath() {
        return this.servletPath;
    }

    @Override
    public String getMethod() {
        return this.savedReq._getMethod();
    }

    public String _getMethod() {
        return this.method;
    }

    @Override
    public String getParameter(String name) {
        return this.savedReq._getParameter(name);
    }

    private String _getParameter(String name) {
        final String[] vals = this.params.get(name);

        if(vals != null) {
            return vals[0];
        }

        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return this.savedReq._getParameterValues(name);
    }

    private String[] _getParameterValues(String name) {
        return this.params.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.savedReq._getParameterMap();
    }

    private Map<String, String[]> _getParameterMap() {
        return this.params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return this.savedReq._getParameterNames();
    }

    private Enumeration<String> _getParameterNames() {
        return this.paramNames.elements();
    }
}
