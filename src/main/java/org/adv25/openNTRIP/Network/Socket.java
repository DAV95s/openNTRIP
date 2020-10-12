package org.adv25.openNTRIP.Network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Socket {
    final static private Logger logger = LogManager.getLogger(Socket.class.getName());
    private static long prevSocketId = 0;
    public long socketId;

    private static final byte[] OK_MESSAGE = "ICY 200 OK\r\n".getBytes();
    private static final byte[] BAD_MESSAGE = "ERROR - Bad Password\r\n".getBytes();

    public SocketChannel socketChannel = null;

    public boolean endOfStreamReached = false;

    public Socket(SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        prevSocketId++;
        this.socketId = prevSocketId;
        logger.info("New connection: " + socketChannel.getRemoteAddress() + " set id " + socketId);
    }

    public void sendOkMessage() throws IOException {
        this.write(ByteBuffer.wrap(OK_MESSAGE));

        if (logger.isDebugEnabled()) {
            logger.debug("Connection " + this.socketId + " response: ICY 200 OK");
        }
    }

    public void sendBadMessageAndClose() throws IOException {
        this.write(ByteBuffer.wrap(BAD_MESSAGE));
        this.close();
        if (logger.isDebugEnabled()) {
            logger.debug("Connection " + this.socketId + " response: ERROR - Bad Password");
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
        this.socketChannel.close();
    }

    public boolean isRegistered() {
        return this.socketChannel.isRegistered();
    }
}