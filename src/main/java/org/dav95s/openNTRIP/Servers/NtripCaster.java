package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.Bootstrap;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.dav95s.openNTRIP.Databases.Models.NtripCasterModel;
import org.dav95s.openNTRIP.Network.NetworkCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;

public class NtripCaster {
    final static private Logger logger = LoggerFactory.getLogger(NtripCaster.class.getName());

    final private ServerSocketChannel serverChannel;
    final private NtripCasterModel model;
    final private Bootstrap bootstrap;
    final private HashMap<Integer, ReferenceStation> referenceStations;
    final private HashMap<String, MountPoint> mountPoints = new HashMap<>();

    public NtripCaster(int id, NetworkCore networkCore, Bootstrap bootstrap) throws IOException {
        this.model = new NtripCasterModel(id);
        this.bootstrap = bootstrap;
        this.referenceStations = bootstrap.getReferenceStations();

        logger.debug("Caster [Port:" + model.getPort() + "] starts initialization...");
        this.updateListOfMountpoints();

        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(model.getPort()));
        this.serverChannel.configureBlocking(false);
        networkCore.registerServerChannel(serverChannel, this);

        logger.info("Caster [Port:" + model.getPort() + "] is running!");
    }

    private byte[] sourceTable() {
        String header = "SOURCETABLE 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n";

        StringBuilder body = new StringBuilder();

        this.mountPoints.values().forEach((mp) -> body.append(mp.toString()));
        body.append("ENDSOURCETABLE\r\n");
        String bodyString = body.toString();
        header += "Content-Length: " + bodyString.getBytes().length + "\r\n\n";

        return (header + bodyString).getBytes();
    }

    /**
     * @param user
     * @throws IOException
     */
    public void clientAuthorization(User user) throws IOException, IllegalAccessException {
        MountPoint mountPoint = this.mountPoints.get(user.getHttpHeader("GET"));
        logger.debug(user + " requested mountpoint " + user.getHttpHeader("GET"));

        //The requested point does not exist. Send sourcetable.
        if (mountPoint == null) {
            logger.debug("Caster " + model.getPort() + ": MountPoint " + user.getHttpHeader("GET") + " is not exists!");
            user.write(ByteBuffer.wrap(sourceTable()));
            user.close();
        } else {
            user.setMountPoint(mountPoint);
            mountPoint.clientAuthorization(user);
        }
    }

    public void updateListOfMountpoints() {
        HashMap<Integer, String> mountpointIds = this.model.getAccessibleMountpointIds();

        mountpointIds.forEach((id, name) -> {
            mountPoints.putIfAbsent(name, new MountPoint(new MountPointModel(id), referenceStations));
        });
    }

    public void close() {
        try {
            bootstrap.removeCaster(model.getId());
            serverChannel.close();
            mountPoints.forEach((k, v) -> {

            });
        } catch (IOException e) {
            logger.warn("Error", e);
        }
    }

    public ReferenceStation getReferenceStationByName(String name) {
        return referenceStations.values().stream().filter(r -> r.getName().equals(name)).findAny().orElse(null);
    }
}
