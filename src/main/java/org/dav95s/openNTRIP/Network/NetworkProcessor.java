package org.dav95s.openNTRIP.Network;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dav95s.openNTRIP.Servers.NtripCaster;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NetworkProcessor implements Runnable {
    final static private Logger logger = LogManager.getLogger(NetworkProcessor.class.getName());

    private Selector selector;
    private Thread thread;

    private static NetworkProcessor instance;
    private final Worker worker = Worker.getInstance();

    public static NetworkProcessor getInstance() {
        if (instance == null)
            instance = new NetworkProcessor();
        return instance;
    }

    private NetworkProcessor() {
        try {
            this.selector = Selector.open();
            this.thread = new Thread(this);
            this.thread.start();
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
        }
    }

    /**
     * When new caster has created, this method registered his server channel in the selector.
     *
     * @param channel
     * @param caster
     * @throws IOException
     */
    public void registerServerChannel(ServerSocketChannel channel, NtripCaster caster) throws IOException {
        channel.register(this.selector, SelectionKey.OP_ACCEPT, caster);
        selector.wakeup();
    }

    public void close() throws IOException {
        this.selector.close();
        this.thread.interrupt();
    }

    public void run() {
        while (true) {
            try {
                int count = selector.select();

                if (count < 1)
                    continue;

                logger.debug(() -> "New event, selector have " + count + " connections");

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();

                    if (!selectionKey.isValid()) {
                        continue;
                    }

                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel connectSocket = server.accept();
                        connectSocket.configureBlocking(false);
                        SelectionKey clientKey = connectSocket.register(this.selector, SelectionKey.OP_READ);
                        new ConnectHandler(new Socket(clientKey), (NtripCaster) selectionKey.attachment());

                    } else if (selectionKey.isReadable()) {
                        INetworkHandler handler = (INetworkHandler) selectionKey.attachment();
                        try {
                            handler.readChannel();
                            worker.addWork(handler);
                        } catch (IOException e) {
                            logger.error("Event Error", e);
                            handler.close();
                            selectionKey.cancel();
                        }
                    }
                    iterator.remove();
                }
            } catch (IOException ex) {
                logger.log(Level.ERROR, ex);
            }
        }
    }
}
