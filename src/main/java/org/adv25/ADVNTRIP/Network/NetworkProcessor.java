package org.adv25.ADVNTRIP.Network;

import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Servers.NtripCaster;
import org.adv25.ADVNTRIP.Servers.RefStation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkProcessor implements Runnable {
    final static private Logger logger = LogManager.getLogger(NetworkProcessor.class.getName());

    private Selector selector;
    private Thread thread;
    private ExecutorService executor = Executors.newCachedThreadPool();

    private static NetworkProcessor instance;

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
            logger.debug("Socket acceptor has initialized!");
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
        }
    }

    //    update ports
    public SelectionKey registerChannel(ServerSocketChannel socket, NtripCaster caster) throws IOException {
        selector.wakeup();
        return socket.register(this.selector, SelectionKey.OP_ACCEPT, caster);
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

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel socket = server.accept();
                        socket.configureBlocking(false);
                        socket.register(selector, SelectionKey.OP_READ, key.attachment());   //attached caster
                        logger.debug("Socket accept.");

                    } else if (key.isReadable()) {
                        if (key.attachment() instanceof RefStation) {
                            //Reference station sends gnss data
                            RefStation refStation = (RefStation) key.attachment();
                            if (refStation.readSelf())
                                executor.submit(refStation);

                        } else if (key.attachment() instanceof Client) {
                            //Client sends nmea message
                            Client client = (Client) key.attachment();
                            client.read();
                            executor.submit(client);
                        } else {
                            //New connect sends request message
                            ConnectHandler connectHandler = new ConnectHandler(key);
                            connectHandler.read();
                            executor.submit(connectHandler);
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
