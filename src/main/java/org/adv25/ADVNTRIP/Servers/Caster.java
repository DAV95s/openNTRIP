package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.DAO.CastersDAO;
import org.adv25.ADVNTRIP.Databases.DAO.MountPointDAO;
import org.adv25.ADVNTRIP.Databases.Models.CasterModel;
import org.adv25.ADVNTRIP.Databases.Models.MountPointModel;
import org.adv25.ADVNTRIP.Network.ConnectHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Caster implements Runnable {
    // <static>
    final static org.apache.log4j.Logger logger = Logger.getLogger(Caster.class);

    private static ArrayList<Caster> casters = new ArrayList<>();

    //add new caster if db contains
    public static void init() {
        logger.debug("Start caster init()");
        ArrayList<CasterModel> casters = new CastersDAO().readAll();

        for (CasterModel caster : casters) {
            logger.debug("Casters from db " + casters.size());

            if (checkPort(caster.getPort())) {

                if (caster.isStatus()) {
                    Caster.casters.add(new Caster(caster.getPort(), caster));
                } else {
                    logger.info("Caster id =" + caster.getId() + "port: " + caster.getPort() + "is off !");
                }
            }
        }
    }

    private static boolean checkPort(int port) {
        for (Caster caster : casters) {
            if (caster.getPort() == port)
                return false;
        }
        return true;
    }
    // </static>

    // Class
    Map<String, MountPoint> mountPoints = new HashMap<>();
    CasterModel model;
    ServerSocketChannel serverChannel;
    Selector selector;
    Thread thread;
    int port;

    private Caster(int port, CasterModel model) {
        this.port = port;
        this.model = model;
        this.thread = new Thread(this);
        this.thread.start();

        //Add mountpoints
        MountPointDAO mountPointDAO = new MountPointDAO();
        ArrayList<MountPointModel> models = mountPointDAO.getAllByCasterId(model.getId());

        for (MountPointModel mp_model : models) {
            this.mountPoints.put(mp_model.getMountpoint(), new MountPoint(mp_model, this));
        }
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            this.serverChannel = ServerSocketChannel.open();
            this.serverChannel.configureBlocking(false);
            this.serverChannel.bind(new InetSocketAddress(this.port));
            this.selector = Selector.open();
            this.serverChannel.register(selector, SelectionKey.OP_ACCEPT, this);

            logger.info("Caster id " + model.getId() + " was created and port " + this.port + " has bind");

            while (true) {
                int count = this.selector.select(1000);

                if (count < 1)
                    continue;

                Iterator it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();

                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable()) {
                        new Thread(new ConnectHandler(serverChannel.accept(), this)).start();
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

                            client.setPosition(new String(byteBuffer.array()));

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

    public void newClient(Client client) throws IOException {
        String get = client.getRequestLine().split(" ")[1].replaceAll("/", "");
        MountPoint requested = this.mountPoints.get(get);

        if (requested == null) {
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
}
