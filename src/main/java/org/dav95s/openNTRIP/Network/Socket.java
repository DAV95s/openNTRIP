package org.dav95s.openNTRIP.Network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Socket {
    final static private Logger logger = LogManager.getLogger(Socket.class.getName());

    private static long prevSocketId;

    final private static byte[] OK_MESSAGE = "ICY 200 OK\r\n".getBytes();
    final private static byte[] BAD_MESSAGE = "ERROR - Bad Password\r\n".getBytes();

    final private long socketId;
    final private SelectionKey key;
    final private SocketChannel socketChannel;

    public boolean endOfStreamReached = false;

    public Socket(SelectionKey key) throws IOException {
        prevSocketId++;
        this.key = key;
        this.socketId = prevSocketId;
        this.socketChannel = (SocketChannel) key.channel();

        logger.info("New connection: " + this.toString() + socketChannel.getRemoteAddress());
    }

    public void sendOkMessage() throws IOException {
        this.write(ByteBuffer.wrap(OK_MESSAGE));

        if (logger.isDebugEnabled()) {
            logger.debug(this.toString() + " response: ICY 200 OK");
        }
    }

    public void sendBadMessageAndClose() {
        try {
            this.write(ByteBuffer.wrap(BAD_MESSAGE));
            this.close();
        } catch (IOException ignored) {
        }

        if (logger.isDebugEnabled()) {
            logger.debug(this.toString() + " response: ERROR - Bad Password");
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
            throw new IOException(this.toString() + "endOfStreamReached");
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
        this.key.cancel();
        this.socketChannel.close();
    }

    public void attach(INetworkHandler handler) {
        this.key.attach(handler);
    }

    public boolean isRegistered() {
        return this.socketChannel.isRegistered();
    }

    @Override
    public String toString() {
        return "Socket{" + socketId + "} ";
    }
}