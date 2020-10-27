package org.dav95s.openNTRIP.Network;

import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * When the new caster has created, this method registered his server socket channel in the selector.
     *
     * @param socket
     * @param caster
     * @return SelectionKey
     * @throws IOException
     */
     public SelectionKey registerServerChannel(ServerSocketChannel socket, NtripCaster caster) throws IOException {
        SelectionKey key = socket.register(this.selector, SelectionKey.OP_ACCEPT, caster);
        selector.wakeup();
        return key;
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
                        logger.debug("The selected key was not valid!");
                        continue;
                    }

                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel connectSocket = server.accept();
                        connectSocket.configureBlocking(false);
                        SelectionKey clientKey = connectSocket.register(this.selector, SelectionKey.OP_READ);
                        new ConnectHandler(clientKey, (NtripCaster) selectionKey.attachment());

                    } else if (selectionKey.isReadable()) {
                        IWork work = (IWork) selectionKey.attachment();
                        try {
                            work.readSelf();
                            worker.addWork(work);
                        } catch (IOException e) {
                            logger.error("Event Error", e);
                            work.close();
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
