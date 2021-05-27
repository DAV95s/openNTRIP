package org.dav95s.openNTRIP.Network;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

public class Socket {
    final static private Logger logger = LoggerFactory.getLogger(Socket.class.getName());

    final private static AtomicLong socketIdCounter = new AtomicLong();

    final private static byte[] OK_MESSAGE = "ICY 200 OK\r\n".getBytes();
    final private static byte[] BAD_MESSAGE = "ERROR - Bad Password\r\n".getBytes();

    final private long socketId;
    final private SelectionKey selectionKey;
    final private SocketChannel socketChannel;

    public boolean endOfStreamReached = false;

    public Socket(SelectionKey selectionKey) throws IOException {
        this.selectionKey = selectionKey;
        this.socketId = socketIdCounter.incrementAndGet();
        this.socketChannel = (SocketChannel) selectionKey.channel();

        logger.info("New connection: " + this + socketChannel.getRemoteAddress());
    }


    public void sendOkMessage() throws IOException {
        this.write(ByteBuffer.wrap(OK_MESSAGE));

        if (logger.isDebugEnabled()) {
            logger.debug(this + " response: ICY 200 OK");
        }
    }

    public void sendBadMessageAndClose() {
        try {
            this.write(ByteBuffer.wrap(BAD_MESSAGE));
            this.close();
        } catch (IOException ignored) {
        }

        if (logger.isDebugEnabled()) {
            logger.debug(this + " response: ERROR - Bad Password");
        }
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);
        int totalBytesRead = bytesRead;

        while (bytesRead > 0) {
            bytesRead = this.socketChannel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        if (bytesRead == -1) {
            this.endOfStreamReached = true;
            throw new IOException(this + "endOfStreamReached");
        }

        return totalBytesRead;
    }

    public int write(ByteBuffer byteBuffer) throws IOException {
        int bytesWritten = this.socketChannel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;

        while (bytesWritten > 0 && byteBuffer.hasRemaining()) {
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }

    public void close() throws IOException {
        this.selectionKey.cancel();
        this.socketChannel.close();
    }

    public void attach(INetworkHandler handler) {
        this.selectionKey.attach(handler);
    }

    public boolean isRegistered() {
        return this.socketChannel.isRegistered();
    }

    @Override
    public String toString() {
        return "Socket{" + socketId + "} ";
    }
}