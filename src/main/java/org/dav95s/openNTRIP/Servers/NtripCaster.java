package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.dav95s.openNTRIP.Databases.Models.NtripCasterModel;
import org.dav95s.openNTRIP.Network.NetworkCore;
import org.dav95s.openNTRIP.ServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class NtripCaster {
    final static private Logger logger = LoggerFactory.getLogger(NtripCaster.class.getName());

    final private ServerSocketChannel serverChannel;
    final private NtripCasterModel model;
    final private HashMap<String, MountPoint> mountPoints = new HashMap<>();

    public NtripCaster(NtripCasterModel model) throws IOException, SQLException {
        logger.debug("Caster [Port:" + model.getPort() + "] starts initialization...");
        this.model = model;

        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(model.getPort()));
        this.serverChannel.configureBlocking(false);

        NetworkCore.getInstance().registerServerChannel(serverChannel, this);

        ArrayList<Integer> mountpointsId = this.model.readMountpointsId();
        this.model.readMountpointsId().forEach((id)-> {
            MountPointModel mountPointModel = new MountPointModel(id);
            mountPoints.put(mountPointModel.getName(), new MountPoint(mountPointModel));
        });

        logger.info("NtripCaster :" + model.getPort() + " is running!");
    }

    public void close() {
        try {
            ServerBootstrap.getInstance().removeCaster(this);
            serverChannel.close();
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }

    private byte[] sourceTable() {
        String header = "SOURCETABLE 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n";

        StringBuilder body = new StringBuilder();
        for (MountPoint mountPoint : this.mountPoints.values()) {
            body.append(mountPoint.toString());
        }

        body.append("ENDSOURCETABLE\r\n");
        String bodyString = body.toString();
        header += "Content-Length: " + bodyString.getBytes().length + "\r\n\n";

        return (header + bodyString).getBytes();
    }

    /**
     * This method will be call on get request and after NMEA message from a client.
     *
     * @param user
     * @throws IOException
     */
    public void clientAuthorization(User user) throws IOException, SQLException {
        MountPoint mountPoint = this.getMountpoint(user.getHttpHeader("GET"));
        logger.debug(user.toString() + " requested mountpoint " + user.getHttpHeader("GET"));

        //The requested point does not exist. Send sourcetable.
        if (mountPoint == null) {
            user.write(ByteBuffer.wrap(sourceTable()));
            user.close();
            return;
        }
        user.setMountPoint(mountPoint);
        mountPoint.clientAuthorization(user);

        logger.debug("Caster " + model.getPort() + ": MountPoint " + user.getHttpHeader("GET") + " is not exists!");
    }

    private MountPoint getMountpoint(String name) throws IllegalArgumentException {
        return this.mountPoints.get(name);
    }

    public int getId() {
        return model.getId();
    }

    public void refresh() throws SQLException {
        this.model.read();
        model.readMountpointsId();
        for (MountPoint mp : mountPoints.values()) {
            mp.model.read();
            mp.model.readAccessibleReferenceStations();
        }
    }
}
