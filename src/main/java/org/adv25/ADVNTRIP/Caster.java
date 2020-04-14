package org.adv25.ADVNTRIP;

import org.adv25.ADVNTRIP.Servers.GnssStation;
import org.adv25.ADVNTRIP.Tools.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class Caster implements Runnable {
    @Override
    public void run() {
        ServerSocketChannel server;

        Config config = Config.getInstance();

        String portString = config.getProperties("Port");
        String ip = config.getProperties("IpAdress");

        int port;

        if (portString == null)
            portString = "8500";

        port = Integer.parseInt(portString);

        if (ip == null) {
            ip = "localhost";
        }

        try {
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(ip, port));
            System.out.println("ADVNTRIP has start and listening " + ip + ":" + portString);
            while (server.isOpen())
                new ConnectHandler(server.accept()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //List of stations
    static HashMap<String, GnssStation> ServerList = new HashMap<>();

    public static void AddServer(GnssStation server) {
        ServerList.put(server.getStationName(), server);
    }

    public static HashMap<String, GnssStation> GetServerList() {
        return ServerList;
    }

    public static GnssStation getServer(String serverName) {
        GnssStation response = ServerList.get(serverName);
        if (response == null)
            throw new NoSuchElementException("The requested station not exists!");
        return ServerList.get(serverName);
    }

    public static byte[] GetSourceTable(){
        String Header = "SOURCETABLE 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n";

        String Body = "";

        for (GnssStation server : Caster.GetServerList().values()){
            Body += server.toString() + "\r\n";
        }

        Body += "ENDSOURCETABLE\r\n";
        Header += "Content-Length: " + Body.getBytes().length + "\r\n\n";

        return (Header + Body).getBytes();
    }
}