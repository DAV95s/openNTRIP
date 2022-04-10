package org.dav95s.openNTRIP.commons;

import org.dav95s.openNTRIP.database.modelsV2.NetworkModel;
import org.dav95s.openNTRIP.database.repository.NetworkRepository;
import org.dav95s.openNTRIP.utils.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Registry {
    private final static Logger logger = LoggerFactory.getLogger(Registry.class.getName());

    private final NetworkRepository networkRepository;

    public final Map<String, NetworkModel> networks = new ConcurrentHashMap<>();


    public Registry(ServerProperties serverProperties, NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
        updateNetworks();
    }

    public void updateNetworks() {
        ArrayList<NetworkModel> allReferenceStation = this.networkRepository.getAllReferenceStations();
        ArrayList<NetworkModel> allNetworks = this.networkRepository.getAllNetworks();
        allReferenceStation.forEach(network -> networks.putIfAbsent(network.identifier, network));
        allNetworks.forEach(network -> networks.putIfAbsent(network.identifier, network));
    }
}
