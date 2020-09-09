package org.adv25.ADVNTRIP.Databases.Models;

import org.adv25.ADVNTRIP.Clients.Authentication.Authentication;
import org.adv25.ADVNTRIP.Clients.Authentication.Basic;
import org.adv25.ADVNTRIP.Clients.Authentication.Digest;
import org.adv25.ADVNTRIP.Clients.Authentication.None;
import org.adv25.ADVNTRIP.Servers.ReferenceStation;
import org.adv25.ADVNTRIP.Spatial.PointLla;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MountPointModel {
    final static private Logger logger = LogManager.getLogger(MountPointModel.class.getName());
    private long id;
    private String mountpoint;
    private String identifier;
    private String format;
    private String formatDetails;
    private int carrier;
    private String navSystem;
    private String network;
    private String country;
    private PointLla lla;
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
    private ArrayList<ReferenceStation> baseIds;
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

    public PointLla getLla() {
        return lla;
    }

    public void setLatitude(Double lat) {
        if (this.lla == null) {
            lla = new PointLla();
        }
        lla.setLat(new BigDecimal(lat));
    }

    public void setLongitude(Double lon) {
        if (this.lla == null) {
            lla = new PointLla();
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
        return compression == null ? "none" : compression;
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

    public ArrayList<ReferenceStation> getBasesIds() {
        return baseIds;
    }

    public String getBasesIdsJoin() {
        StringBuilder sb = new StringBuilder();

        for (ReferenceStation referenceStation : baseIds) {
            sb.append(referenceStation.getId());
        }
        return sb.toString();
    }

    /* for unit test */
    public void setBaseIds(ArrayList<ReferenceStation> referenceStations) {
        this.baseIds = referenceStations;
    }

    public void setBasesIds(String ids) {
        this.baseIds = new ArrayList<>();

        for (String id : ids.split(",")) {
            int i_id = Integer.parseInt(id);
            ReferenceStation station = ReferenceStation.getStationById(i_id);
            if (station != null) {
                baseIds.add(station);
            } else {
                logger.error(mountpoint + ": station by id " + i_id + " not exists.");
            }
        }
    }

    public int getPlugin_id() {
        return plugin_id;
    }

    public void setPlugin_id(int plugin_id) {
        this.plugin_id = plugin_id;
    }

    @Override
    public String toString() {
        return "STR" + ';' + getMountpoint() + ';' + getIdentifier() + ';' + getFormat() + ';' + getFormatDetails() + ';' + getCarrier() + ';' + getNavSystem() + ';' + getNetwork() + ';' + getCountry()
                + ';' + String.format("%.2f", getLla().getLat()) + ';' + String.format("%.2f", getLla().getLon()) + ';' + (isNmea() ? 1 : 0) + ';' + (isSolution() ? 1 : 0) + ';' + getGenerator() + ';' + getCompression()
                + ';' + getAuthentication().toString() + ';' + (isFee() ? 'Y' : 'N') + ';' + getBitrate() + ';' + getMisc() + "\r\n";
    }
}
