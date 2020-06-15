package org.adv25.ADVNTRIP.Databases.Models;

import org.adv25.ADVNTRIP.Clients.Authentication.Authentication;
import org.adv25.ADVNTRIP.Clients.Authentication.Basic;
import org.adv25.ADVNTRIP.Clients.Authentication.Digest;
import org.adv25.ADVNTRIP.Clients.Authentication.None;
import org.adv25.ADVNTRIP.Spatial.Point_lla;

import java.math.BigDecimal;


public class MountPointModel {
    private long id;
    private String mountpoint;
    private String identifier;
    private String format;
    private String formatDetails;
    private int carrier;
    private String navSystem;
    private String network;
    private String country;
    private Point_lla lla;
    private boolean nmea;
    private boolean solution;
    private String generator;
    private String compression;
    private Authentication authentication;
    private boolean fee;
    private int bitrate;
    private String misc;
    private boolean available;
    private int casterId;
    private String basesIds;
    private int plugin_id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMountpoint() {
        return mountpoint == null ? "" : mountpoint;
    }

    public void setMountpoint(String mountpoint) {
        this.mountpoint = mountpoint;
    }

    public String getIdentifier() {
        return identifier == null ? "" : identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFormat() {
        return format == null ? "" : format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormatDetails() {
        return formatDetails == null ? "" : formatDetails;
    }

    public void setFormatDetails(String formatDetails) {
        this.formatDetails = formatDetails;
    }

    public int getCarrier() {
        return carrier;
    }

    public void setCarrier(int carrier) {
        this.carrier = carrier;
    }

    public String getNavSystem() {
        return navSystem == null ? "" : navSystem;
    }

    public void setNavSystem(String navSystem) {
        this.navSystem = navSystem;
    }

    public String getNetwork() {
        return network == null ? "" : network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getCountry() {
        return country == null ? "" : country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Point_lla getLla() {
        return lla;
    }

    public void setLatitude(Double lat) {
        if (this.lla == null) {
            lla = new Point_lla();
        }
        lla.setLat(new BigDecimal(lat));
    }

    public void setLongitude(Double lon) {
        if (this.lla == null) {
            lla = new Point_lla();
        }
        lla.setLon(new BigDecimal(lon));
    }

    public boolean isNmea() {
        return nmea;
    }

    public void setNmea(boolean nmea) {
        this.nmea = nmea;
    }

    public boolean isSolution() {
        return solution;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }

    public String getGenerator() {
        return generator == null ? "" : generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public String getCompression() {
        return compression == null ? "" : compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }

    public void setAuthentication(String authentication) {
        switch (authentication) {
            case "Basic":
                this.authentication = new Basic();
                break;
            case "Digest":
                this.authentication = new Digest();
                break;
            default:
                this.authentication = new None();
                break;
        }
    }


    public boolean isFee() {
        return fee;
    }

    public void setFee(boolean fee) {
        this.fee = fee;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public String getMisc() {
        return misc == null ? "none" : misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getCasterId() {
        return casterId;
    }

    public void setCasterId(int casterId) {
        this.casterId = casterId;
    }

    public String getBasesIds() {
        return basesIds == null ? "" : basesIds;
    }

    public void setBasesIds(String basesIds) {
        this.basesIds = basesIds;
    }

    public int getPlugin_id() {
        return plugin_id;
    }

    public void setPlugin_id(int plugin_id) {
        this.plugin_id = plugin_id;
    }
}
