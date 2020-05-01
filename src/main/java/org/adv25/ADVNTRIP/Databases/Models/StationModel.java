package org.adv25.ADVNTRIP.Databases.Models;

public class StationModel {
    private long id;
    private String Type = "STR";
    private String Mountpoint;
    private String Identifier;
    private String Format;
    private String FormatDetails;
    private int Carrier;
    private String NavSystem;
    private String Network;
    private String Country;
    private Double Latitude;
    private Double Longitude;
    private boolean Nmea;
    private boolean Solution;
    private String Generator;
    private String Compression;
    private boolean Authentication;
    private boolean Fee;
    private int Bitrate;
    private String Misc;
    private String Password;
    private String Properties;
    private String Is_online;


    public String getType() {
        return Type;
    }

    public void setType(String type) {
        if(type == null)
            type = "STR";

        Type = type;
    }

    public String getMountpoint() {
        return Mountpoint;
    }

    public void setMountpoint(String mountpoint) {
        if(mountpoint == null){
            mountpoint = "";
        }
        Mountpoint = mountpoint;
    }

    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String identifier) {
        if(identifier == null){
            identifier = "";
        }
        Identifier = identifier;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        if(format == null){
            format = "";
        }
        Format = format;
    }

    public String getFormatDetails() {
        return FormatDetails;
    }

    public void setFormatDetails(String formatDetails) {
        if(formatDetails == null){
            formatDetails = "";
        }
        FormatDetails = formatDetails;
    }

    public int getCarrier() {
        return Carrier;
    }

    public void setCarrier(int carrier) {
        Carrier = carrier;
    }

    public String getNavSystem() {
        return NavSystem;
    }

    public void setNavSystem(String navSystem) {
        if(navSystem == null){
            navSystem = "";
        }
        NavSystem = navSystem;
    }

    public String getNetwork() {
        return Network;
    }

    public void setNetwork(String network) {
        if(network == null){
            network = "";
        }
        Network = network;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        if(country == null){
            country = "";
        }
        Country = country;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Boolean getNmea() {
        return Nmea;
    }

    public void setNmea(String nmea) {
        if (nmea == null) {
            Nmea = false;
        } else {
            if (nmea.charAt(0) == 'Y') {
                Nmea = true;
            } else {
                Nmea = false;
            }
        }
    }

    public Boolean getSolution() {
        return Solution;
    }

    public void setSolution(String solution) {
        if (solution == null) {
            Solution = false;
        } else {
            if (solution.charAt(0) == 'Y') {
                Solution = true;
            } else {
                Solution = false;
            }
        }
    }

    public String getGenerator() {
        return Generator;
    }

    public void setGenerator(String generator) {
        if(generator == null){
            generator = "";
        }
        Generator = generator;
    }

    public String getCompression() {
        return Compression;
    }

    public void setCompression(String compression) {
        if(compression == null){
            compression = "";
        }
        Compression = compression;
    }

    public boolean getAuthentication() {
        return Authentication;
    }

    public void setAuthentication(String authentication) {
        if (authentication == null) {
            Authentication = false;
        } else {
            if (authentication.charAt(0) == 'Y') {
                Authentication = true;
            } else {
                Authentication = false;
            }
        }

    }

    public boolean getFee() {
        return Fee;
    }

    public void setFee(String fee) {
        if (fee == null) {
            Fee = false;
        } else {
            if (fee.charAt(0) == 'Y') {
                Fee = true;
            } else {
                Fee = false;
            }
        }
    }

    public int getBitrate() {
        return Bitrate;
    }

    public void setBitrate(int bitrate) {
        Bitrate = bitrate;
    }

    public String getMisc() {
        return Misc;
    }

    public void setMisc(String misc) {
        if(misc == null){
            misc = "";
        }
        Misc = misc;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIs_online() {
        return Is_online;
    }

    public void setIs_online(String is_online) {
        if(is_online == null){
            is_online = "";
        }
        Is_online = is_online;
    }

    public String getProperties() {
        return Properties;
    }

    public void setProperties(String properties) {
        if(properties == null){
            properties = "";
        }
        Properties = properties;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        if(password == null){
            password = "";
        }
        Password = password;
    }

    @Override
    public String toString() {
        return Type + ';' + Mountpoint + ';' + Identifier + ';' + Format + ';' + FormatDetails + ';' + Carrier + ';' + NavSystem + ';' + Network + ';' + Country
                + ';' + Latitude.toString() + ';' + Longitude.toString() + ';' + (Nmea ? 'Y' : 'N') + ';' + (Solution ? 'Y' : 'N') + ';' + Generator + ';' + Compression
                + ';' + (Authentication ? 'Y' : 'N') + ';' + (Fee ? 'Y' : 'N') + ';' + Bitrate + ';' + Misc;
    }
}
