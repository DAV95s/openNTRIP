package org.dav95s.openNTRIP.Network;


import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkCore implements Runnable {
    final static private Logger logger = LoggerFactory.getLogger(NetworkCore.class.getName());

    private Selector selector;
    private Thread thread;
    private boolean isAlive = true;
    private static NetworkCore instance;
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static NetworkCore getInstance() {
        if (instance == null)
            instance = new NetworkCore();
        return instance;
    }

    private NetworkCore() {
        try {
            this.selector = Selector.open();
            this.thread = new Thread(this);
            this.thread.start();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void registerServerChannel(ServerSocketChannel channel, NtripCaster caster) throws IOException {
        channel.register(this.selector, SelectionKey.OP_ACCEPT, caster);
        selector.wakeup();
    }

    public void close() throws IOException {
        this.isAlive = false;
        this.selector.close();
        this.thread.interrupt();
    }

    public void run() {
        while (isAlive) {
            try {
                int count = selector.select();

                if (count < 1)
                    continue;

                logger.debug("New event, selector have " + count + " connections");

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();

                    if (!selectionKey.isValid()) {
                        continue;
                    }

                    if (selectionKey.isAcceptable()) {
                        accept(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        INetworkHandler handler = (INetworkHandler) selectionKey.attachment();
                        try {
                            handler.readChannel();
                            executor.submit(handler);
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                            handler.close();
                            selectionKey.cancel();
                        }
                    }
                    iterator.remove();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    private void accept(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
        SocketChannel connectSocket = server.accept();
        connectSocket.configureBlocking(false);
        SelectionKey clientKey = connectSocket.register(this.selector, SelectionKey.OP_READ);
        new ConnectHandler(new Socket(clientKey), (NtripCaster) selectionKey.attachment());
    }
}
