package org.dav95s.openNTRIP;

import org.dav95s.openNTRIP.commons.GlobalStats;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.core.userServer.NtripServer;
import org.dav95s.openNTRIP.core.stationsServer.ReferenceStationServer;
import org.dav95s.openNTRIP.database.repository.NetworkRepository;
import org.dav95s.openNTRIP.database.repository.ReferenceStationRepository;
import org.dav95s.openNTRIP.utils.ServerProperties;

public class ServerLauncher {
    private final ServerProperties serverProperties;
    private final Registry registry;
    private final GlobalStats stats;
    private final ReferenceStationServer referenceStationServer;
    private final NtripServer ntripServer;

    private ServerLauncher(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
        this.registry = new Registry(serverProperties, new NetworkRepository());
        this.stats = new GlobalStats(serverProperties);

        TransportTypeHolder transportType = new TransportTypeHolder(serverProperties);

        this.referenceStationServer = new ReferenceStationServer(serverProperties, registry, transportType, new ReferenceStationRepository());
        this.ntripServer = new NtripServer(serverProperties, registry, transportType);
    }

    private void start() {
        //start servers
        new Thread(referenceStationServer).start();
        new Thread(ntripServer).start();
    }

    public static void main(String[] args) {
        ServerProperties serverProperties = new ServerProperties();
        new ServerLauncher(serverProperties).start();
    }
}
