package com.disney.xband.xbrms.common.caching;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 2/4/13
 * Time: 6:57 PM
 */
public class CacheResponseStream extends ServletOutputStream {
    protected boolean closed;
    protected HttpServletResponse response;
    protected OutputStream cache;

    public CacheResponseStream(final HttpServletResponse response, final OutputStream cache) throws IOException {
        super();
        this.response = response;
        this.cache = cache;
    }

    @Override
    public void write(final int b) throws IOException {
        if (this.closed) {
            throw new IOException("Failed to write to a closed output stream");
        }

        this.cache.write((byte) b);
    }

    @Override
    public void write(final byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(final byte b[], final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IOException("Failed to write to a closed output stream");
        }

        this.cache.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        this.cache.close();
        this.closed = true;
    }

    @Override
    public void flush() throws IOException {
        if (!this.closed) {
            this.cache.flush();
        }
    }

    public boolean closed() {
        return (this.closed);
    }

    public void reset() {
    }
}
