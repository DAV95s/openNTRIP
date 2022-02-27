package org.dav95s.openNTRIP;

import org.dav95s.openNTRIP.commons.StationRegistry;
import org.dav95s.openNTRIP.core.ntrip.handlers.NtripServer;
import org.dav95s.openNTRIP.core.referenceStation.ReferenceStationServer;
import org.dav95s.openNTRIP.commons.GlobalStats;
import org.dav95s.openNTRIP.utils.ServerProperties;

public class ServerLauncher {
    private final ServerProperties serverProperties;
    private final StationRegistry stationRegistry;
    private final GlobalStats stats;
    private final ReferenceStationServer referenceStationServer;
    private final NtripServer ntripServer;

    private ServerLauncher(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
        this.stationRegistry = new StationRegistry(serverProperties);
        this.stats = new GlobalStats(serverProperties);

        TransportTypeHolder transportType = new TransportTypeHolder(serverProperties);

        this.referenceStationServer = new ReferenceStationServer(serverProperties, stationRegistry, transportType);
        this.ntripServer = new NtripServer(serverProperties, stationRegistry, transportType);
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
