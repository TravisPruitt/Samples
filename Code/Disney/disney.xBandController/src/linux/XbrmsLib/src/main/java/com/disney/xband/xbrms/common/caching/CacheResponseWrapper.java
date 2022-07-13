package com.disney.xband.xbrms.common.caching;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 2/4/13
 * Time: 6:59 PM
 */
public class CacheResponseWrapper extends HttpServletResponseWrapper {
    protected HttpServletResponse orgResponse;
    protected ServletOutputStream stream;
    protected PrintWriter writer;
    protected OutputStream cache;

    public CacheResponseWrapper(final HttpServletRequest request, final HttpServletResponse response, final OutputStream cache) {
        super(response);
        this.orgResponse = response;
        this.cache = cache;
    }

    public ServletOutputStream createOutputStream() throws IOException {
        return (new CacheResponseStream(this.orgResponse, this.cache));
    }

    @Override
    public void flushBuffer() throws IOException {
        this.stream.flush();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.writer != null) {
            throw new IllegalStateException("getWriter method has already been called!");
        }

        if (this.stream == null) {
            this.stream = createOutputStream();
        }

        return (this.stream);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.writer != null) {
            return (this.writer);
        }

        if (this.stream != null) {
            throw new IllegalStateException("getOutputStream method has already been called!");
        }

        this.stream = createOutputStream();
        this.writer = new PrintWriter(new OutputStreamWriter(this.stream, "UTF-8"));
        return (this.writer);
    }
}
