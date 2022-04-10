package org.dav95s.openNTRIP.database.modelsV2;

import org.dav95s.openNTRIP.database.models.assets.Authenticator;

public class MountpointModel {
    public final int id;
    public final String name;
    public final String format;
    public final String network;
    public final boolean nmea;
    public final boolean solution;
    public final String compression;
    public final Authenticator authenticator;
    public final boolean fee;

    public MountpointModel(int id, String name, String format, String network, boolean nmea, boolean solution, String compression, String authenticator, boolean fee) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.network = network;
        this.nmea = nmea;
        this.solution = solution;
        this.compression = compression;
        this.fee = fee;


        switch (authenticator) {
            case "B":
            case "Basic":
                this.authenticator = Authenticator.Basic;
                break;
            case "D":
            case "Digest":
                this.authenticator = Authenticator.Digest;
                break;
            default:
                this.authenticator = Authenticator.None;
                break;
        }
    }
}
