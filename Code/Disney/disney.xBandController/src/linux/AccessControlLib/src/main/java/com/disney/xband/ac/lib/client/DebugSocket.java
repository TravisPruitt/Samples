package com.disney.xband.ac.lib.client;

import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 7/27/12
 * Time: 8:00 PM
 */
public class DebugSocket extends SSLSocket {
    private SSLSocket s;

    public DebugSocket(SSLSocket s) {
        this.s = s;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new TraceInputStream(new BufferedInputStream(this.s.getInputStream()), new FileOutputStream("/tmp/keystone.out", true));
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new TraceOutputStream(new BufferedOutputStream(this.s.getOutputStream()), new FileOutputStream("/tmp/keystone.out", true));
    }

    @Override
    public SocketChannel getChannel() {
        return this.s.getChannel();
    }

    @Override
    public void connect(SocketAddress remote) throws IOException {
        this.s.connect(remote);
    }

    @Override
    public void connect(SocketAddress remote, int timeout) throws IOException {
        this.s.connect(remote, timeout);
    }

    @Override
    public void bind(SocketAddress local) throws IOException {
        this.s.bind(local);
    }

    @Override
    public InetAddress getInetAddress() {
        return this.s.getInetAddress();
    }

    @Override
    public InetAddress getLocalAddress() {
        return this.s.getLocalAddress();
    }

    @Override
    public int getPort() {
        return this.s.getPort();
    }

    @Override
    public int getLocalPort() {
        return this.s.getLocalPort();
    }

    @Override
    public void setTcpNoDelay(boolean on) throws SocketException {
        this.s.setTcpNoDelay(on);
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return this.s.getTcpNoDelay();
    }

    @Override
    public void setSoLinger(boolean on, int linger) throws SocketException {
        this.s.setSoLinger(on, linger);
    }

    @Override
    public int getSoLinger() throws SocketException {
        return this.s.getSoLinger();
    }

    @Override
    public void sendUrgentData(int data) throws IOException {
        this.s.sendUrgentData(data);
    }

    @Override
    public void setOOBInline(boolean on) throws SocketException {
        this.s.setOOBInline(on);
    }

    @Override
    public boolean getOOBInline() throws SocketException {
        return this.s.getOOBInline();
    }

    @Override
    public void setSoTimeout(int timeout) throws SocketException {
        this.s.setSoTimeout(timeout);
    }

    @Override
    public int getSoTimeout() throws SocketException {
        return this.s.getSoTimeout();
    }

    @Override
    public void setSendBufferSize(int size) throws SocketException {
        this.s.setSendBufferSize(size);
    }

    @Override
    public int getSendBufferSize() throws SocketException {
        return this.s.getSendBufferSize();
    }

    @Override
    public void setReceiveBufferSize(int size) throws SocketException {
        this.s.setReceiveBufferSize(size);
    }

    @Override
    public int getReceiveBufferSize() throws SocketException {
        return this.s.getReceiveBufferSize();
    }

    @Override
    public void setKeepAlive(boolean on) throws SocketException {
        this.s.setKeepAlive(on);
    }

    @Override
    public boolean getKeepAlive() throws SocketException {
        return this.s.getKeepAlive();
    }

    @Override
    public void setTrafficClass(int tc) throws SocketException {
        this.s.setTrafficClass(tc);
    }

    @Override
    public int getTrafficClass() throws SocketException {
        return this.s.getTrafficClass();
    }

    @Override
    public void setReuseAddress(boolean on) throws SocketException {
        this.s.setReuseAddress(on);
    }

    @Override
    public boolean getReuseAddress() throws SocketException {
        return this.s.getReuseAddress();
    }

    @Override
    public void close() throws IOException {
        this.s.close();
    }

    @Override
    public void shutdownInput() throws IOException {
        this.s.shutdownOutput();
    }

    @Override
    public void shutdownOutput() throws IOException {
        this.s.shutdownOutput();
    }

    @Override
    public String toString() {
        return this.s.toString();
    }

    @Override
    public boolean isConnected() {
        return this.s.isConnected();
    }

    @Override
    public boolean isBound() {
        return this.s.isBound();
    }

    @Override
    public boolean isClosed() {
        return this.s.isClosed();
    }

    @Override
    public boolean isInputShutdown() {
        return this.s.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown() {
        return this.s.isOutputShutdown();
    }

    @Override
    public java.net.SocketAddress getRemoteSocketAddress() {
        return this.s.getRemoteSocketAddress();
    }

    @Override
    public java.net.SocketAddress getLocalSocketAddress() {
        return this.s.getLocalSocketAddress();
    }

    @Override
    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        this.s.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.s.getSupportedCipherSuites();
    }

    @Override
    public String[] getEnabledCipherSuites() {
        return this.s.getEnabledCipherSuites();
    }

    @Override
    public void setEnabledCipherSuites(String[] strings) {
        this.s.setEnabledCipherSuites(strings);
    }

    @Override
    public String[] getSupportedProtocols() {
        return this.s.getSupportedProtocols();
    }

    @Override
    public String[] getEnabledProtocols() {
        return this.s.getEnabledProtocols();
    }

    @Override
    public void setEnabledProtocols(String[] strings) {
        this.s.setEnabledProtocols(strings);
    }

    @Override
    public SSLSession getSession() {
        return this.s.getSession();
    }

    @Override
    public void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {
        this.s.addHandshakeCompletedListener(handshakeCompletedListener);
    }

    @Override
    public void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {
        this.s.removeHandshakeCompletedListener(handshakeCompletedListener);
    }

    @Override
    public void startHandshake() throws IOException {
        this.s.startHandshake();
    }

    @Override
    public void setUseClientMode(boolean b) {
        this.s.setUseClientMode(b);
    }

    @Override
    public boolean getUseClientMode() {
        return this.s.getUseClientMode();
    }

    @Override
    public void setNeedClientAuth(boolean b) {
        this.s.setNeedClientAuth(b);
    }

    @Override
    public boolean getNeedClientAuth() {
        return this.s.getNeedClientAuth();
    }

    @Override
    public void setWantClientAuth(boolean b) {
        this.s.setWantClientAuth(b);
    }

    @Override
    public boolean getWantClientAuth() {
        return this.s.getWantClientAuth();
    }

    @Override
    public void setEnableSessionCreation(boolean b) {
        this.s.setEnableSessionCreation(b);
    }

    @Override
    public boolean getEnableSessionCreation() {
        return this.s.getEnableSessionCreation();
    }


    private static class TraceInputStream extends FilterInputStream {
        private boolean trace = true;
        private boolean quote = false;
        private OutputStream traceOut;

        /**
         * Creates an input stream filter built on top of the specified
         * input stream.
         *
         * @param in       the underlying input stream.
         * @param traceOut the trace stream
         */
        public TraceInputStream(InputStream in, OutputStream traceOut) {
            super(in);
            this.traceOut = traceOut;
        }

        /**
         * Set trace mode.
         *
         * @param trace the trace mode
         */
        public void setTrace(boolean trace) {
            this.trace = trace;
        }

        /**
         * Set quote mode.
         *
         * @param quote the quote mode
         */
        public void setQuote(boolean quote) {
            this.quote = quote;
        }

        /**
         * Reads the next byte of data from this input stream. Returns
         * <code>-1</code> if no data is available. Writes out the read
         * byte into the trace stream, if trace mode is <code>true</code>
         */
        public int read() throws IOException {
            int b = in.read();
            if (trace && b != -1) {
                if (quote) {
                    writeByte(b);
                }
                else {
                    traceOut.write(b);
                }
            }
            return b;
        }

        /**
         * Reads up to <code>len</code> bytes of data from this input stream
         * into an array of bytes. Returns <code>-1</code> if no more data
         * is available. Writes out the read bytes into the trace stream, if
         * trace mode is <code>true</code>
         */
        public int read(byte b[], int off, int len) throws IOException {
            int count = in.read(b, off, len);
            if (trace && count != -1) {
                if (quote) {
                    for (int i = 0; i < count; i++) {
                        writeByte(b[off + i]);
                    }
                }
                else {
                    traceOut.write(b, off, count);
                }
            }
            return count;
        }

        /**
         * Write a byte in a way that every byte value is printable ASCII.
         */
        private final void writeByte(int b) throws IOException {
            b &= 0xff;
            if (b > 0x7f) {
                traceOut.write('M');
                traceOut.write('-');
                b &= 0x7f;
            }
            if (b == '\r') {
                traceOut.write('\\');
                traceOut.write('r');
            }
            else if (b == '\n') {
                traceOut.write('\\');
                traceOut.write('n');
                traceOut.write('\n');
            }
            else if (b == '\t') {
                traceOut.write('\\');
                traceOut.write('t');
            }
            else if (b < ' ') {
                traceOut.write('^');
                traceOut.write('@' + b);
            }
            else {
                traceOut.write(b);
            }
        }
    }

    private static class TraceOutputStream extends FilterOutputStream {
        private boolean trace = true;
        private boolean quote = false;
        private OutputStream traceOut;

        /**
         * Creates an output stream filter built on top of the specified
         * underlying output stream.
         *
         * @param out      the underlying output stream.
         * @param traceOut the trace stream.
         */
        public TraceOutputStream(OutputStream out, OutputStream traceOut) {
            super(out);
            this.traceOut = traceOut;
        }

        /**
         * Set the trace mode.
         */
        public void setTrace(boolean trace) {
            this.trace = trace;
        }

        /**
         * Set quote mode.
         *
         * @param quote the quote mode
         */
        public void setQuote(boolean quote) {
            this.quote = quote;
        }

        /**
         * Writes the specified <code>byte</code> to this output stream.
         * Writes out the byte into the trace stream if the trace mode
         * is <code>true</code>
         */
        public void write(int b) throws IOException {
            if (trace) {
                if (quote) {
                    writeByte(b);
                }
                else {
                    traceOut.write(b);
                }
            }
            out.write(b);
        }

        /**
         * Writes <code>b.length</code> bytes to this output stream.
         * Writes out the bytes into the trace stream if the trace
         * mode is <code>true</code>
         */
        public void write(byte b[], int off, int len) throws IOException {
            if (trace) {
                if (quote) {
                    for (int i = 0; i < len; i++) {
                        writeByte(b[off + i]);
                    }
                }
                else {
                    traceOut.write(b, off, len);
                }
            }
            out.write(b, off, len);
        }

        /**
         * Write a byte in a way that every byte value is printable ASCII.
         */
        private final void writeByte(int b) throws IOException {
            b &= 0xff;
            if (b > 0x7f) {
                traceOut.write('M');
                traceOut.write('-');
                b &= 0x7f;
            }
            if (b == '\r') {
                traceOut.write('\\');
                traceOut.write('r');
            }
            else if (b == '\n') {
                traceOut.write('\\');
                traceOut.write('n');
                traceOut.write('\n');
            }
            else if (b == '\t') {
                traceOut.write('\\');
                traceOut.write('t');
            }
            else if (b < ' ') {
                traceOut.write('^');
                traceOut.write('@' + b);
            }
            else {
                traceOut.write(b);
            }
        }
    }

    /*
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("www.cnn.com", 80);
        System.out.println(s.isConnected());
        DebugSocket ds = new DebugSocket(s);
        System.out.println(ds.getLocalAddress());
        System.out.println(ds.getRemoteSocketAddress());
        ds.getOutputStream().write('H');
        ds.getOutputStream().flush();
    }
    */
}
