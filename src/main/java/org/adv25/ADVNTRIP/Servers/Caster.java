package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.DAO.CastersDAO;
import org.adv25.ADVNTRIP.Databases.DAO.MountPointDAO;
import org.adv25.ADVNTRIP.Databases.Models.CasterModel;
import org.adv25.ADVNTRIP.Databases.Models.MountPointModel;
import org.adv25.ADVNTRIP.Network.ConnectHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Caster implements Runnable {

    final static private Logger logger = LogManager.getLogger(Caster.class.getName());
    final public static ArrayList<Caster> casters = new ArrayList<>();

    Map<String, MountPoint> mountPoints = new HashMap<>();
    CasterModel model;
    ServerSocketChannel serverChannel;
    Selector selector;
    Thread thread;

    public Caster(CasterModel model) {
        this.model = model;
        this.thread = new Thread(this);
        this.thread.start();

        MountPointDAO mountPointDAO = new MountPointDAO();
        ArrayList<MountPointModel> models = mountPointDAO.getAllByCasterId(model.getId());

        for (MountPointModel mp_model : models) {
            this.mountPoints.put(mp_model.getMountpoint(), new MountPoint(mp_model, this));
        }

        logger.info("Caster :" + model.getPort() + " has been initiated! " +
                "Mountpoints: " + Arrays.toString(this.mountPoints.keySet().toArray()));
    }

    public int getPort() {
        return model.getPort();
    }


    public void newClient(Client client) throws IOException {
        String get = client.getRequestLine().split(" ")[1].replaceAll("/", "");

        MountPoint requested = this.mountPoints.get(get);
        logger.info("Caster " + model.getPort() + " requested station " + get);
        if (requested == null) {
            logger.info("Caster " + model.getPort() + " has send sourcetable");
            sendSourceTable(client);
        } else {
            requested.clientAuthorization(client);
        }
    }

    public void clientInput(Client client) throws IOException {
        SocketChannel clientChannel = client.getChannel();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ, client);
    }

    private void sendSourceTable(Client client) throws IOException {
        String header = "SOURCETABLE 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n";

        String body = "";

        for (MountPoint mp : this.mountPoints.values()) {
            body += mp.toString();
        }

        body += "ENDSOURCETABLE\r\n";
        header += "Content-Length: " + body.getBytes().length + "\r\n\n";

        client.sendMessageAndClose((header + body).getBytes());
    }

    private static ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            this.serverChannel = ServerSocketChannel.open();
            this.serverChannel.configureBlocking(false);
            this.serverChannel.bind(new InetSocketAddress(model.getPort()));
            this.selector = Selector.open();
            this.serverChannel.register(selector, SelectionKey.OP_ACCEPT, this);

            while (true) {
                int count = this.selector.select(250);

                if (count < 1)
                    continue;

                Iterator it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();

                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable()) {
                        executor.submit(new ConnectHandler(serverChannel.accept(), this));
                    }

                    if (key.isReadable()) {
                        Client client = (Client) key.attachment();
                        try {
                            byteBuffer.clear();
                            int read = client.getChannel().read(byteBuffer);

                            if (read == -1) {
                                client.safeClose();
                            }

                            byteBuffer.flip();

                            byte[] chars = new byte[byteBuffer.remaining()];
                            byteBuffer.get(chars);

                            client.setPosition(new String(chars));

                            logger.debug("Client input " + new String(byteBuffer.array()));

                            if (MountPoint.nmeaClientQueue.contains(client)) {
                                MountPoint.nmeaClientQueue.remove(client);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            client.safeClose();
                        }
                    }
                    it.remove();
                }
            }

        } catch (IOException e) {
            logger.error("Connection aborted", e);
        }
    }

}
