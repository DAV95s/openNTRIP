package org.dav95s.openNTRIP.Tools.Geoids;

import org.dav95s.openNTRIP.Tools.NMEA;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class GGF implements IGeoidModel {

    Header header;
    float[][] model;

    public GGF(String path) {
        try (RandomAccessFile reader = new RandomAccessFile(path, "r")) {
            FileChannel channel = reader.getChannel();
            ByteBuffer headerBuffer = ByteBuffer.allocate(146);
            channel.read(headerBuffer);

            header = new Header(headerBuffer);
            model = new float[header.countLat][header.countLon];

            ByteBuffer buffer = ByteBuffer.allocate(header.countLon * 4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            for (int row = 0; row < model.length; row++) {
                channel.read(buffer); // read 1 row
                buffer.flip();
                for (int col = 0; col < model[0].length; col++) {
                    model[row][col] = buffer.getFloat();
                }
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validateGeoidData() {
        //lat
        double deltaLat = header.latMax - header.latMin;
        System.out.println(deltaLat);
        double resol = deltaLat / header.resolutionLat;
        if (Math.abs(resol - header.countLat) < 0.00027777)
            System.out.println(Double.compare(resol, 2300.0d));
    }

    // |01|02|03|04|
    // |05|06|07|08|
    // |09|10|11|12|
    // |13|14|15|16|
    public float[][] get16PointsAroundUser(NMEA.GPSPosition user) {
        //checkArgument(isInArea(user));

        float[][] points = new float[4][4];
        float f1 = (float) (getDeltaLatToUser(user.lat) / header.resolutionLat);
        float f2 = (float) (getDeltaLonToUser(user.lon) / header.resolutionLon);
        int latPointer = (int) f1 - 1;
        int lonPointer = (int) f2 - 1;

        for (int row = 0; row < 4; row++) {
            System.arraycopy(model[latPointer + row], lonPointer, points[row], 0, 4);
        }

        return points;
    }

    private double getDeltaLonToUser(float userLon) {
        //Pacific ocean case
        if (header.lonMin > 0 && userLon < 0) {
            return (180 + userLon) - (180 + header.lonMin) + 360;
        }
        //American case
        if (header.lonMin < 0 && userLon < 0) {
            return Math.abs(header.lonMin) - Math.abs(userLon);
        }
        return (180 + userLon) - (180 + header.lonMin);
    }

    private double getDeltaLatToUser(float userLat) {
        return Math.abs(header.latMax - userLat);
    }

    private boolean isInArea(NMEA.GPSPosition position) {
        return isInAreaLat(header.latMax, position.lat, header.latMin)
                && isInAreaLon(header.lonMin, position.lon, header.lonMax);
    }

    public boolean isInAreaLon(double leftLon, double userLon, double rightLon) {
        if (leftLon > 0 && rightLon < 0) {
            return (leftLon < userLon && userLon < 180.00027) || (-180.00027 < userLon && userLon < rightLon);
        } else {
            return (leftLon < userLon) && (userLon < rightLon);
        }
    }

    public boolean isInAreaLat(double topLat, double userLat, double bottomLat) {
        return (topLat > userLat) && (userLat > bottomLat);
    }

    @Override
    public String toString() {
        return header.toString();
    }

    class Header {
        String description;
        double latMin, latMax;
        double lonMin, lonMax;
        double resolutionLat, resolutionLon;
        int countLat, countLon;
        double NorthPole, SouthPole, Missing, Scalar;
        int window;
        byte flag0;
        byte flag1;
        byte flag2;
        byte flag3;
        byte flag4;
        byte flag5;
        byte flag6;
        byte flag7;

        public Header(ByteBuffer buffer) {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.flip();
            flag1 = buffer.get();
            flag2 = buffer.get();
            byte[] descriptionRaw = new byte[46];
            buffer.get(descriptionRaw, 0, 46);
            description = new String(descriptionRaw);
            latMin = buffer.getDouble();
            latMax = buffer.getDouble();
            lonMin = buffer.getDouble();
            lonMax = buffer.getDouble();
            resolutionLat = buffer.getDouble();
            resolutionLon = buffer.getDouble();
            countLat = buffer.getInt();
            countLon = buffer.getInt();
            NorthPole = buffer.getDouble();
            SouthPole = buffer.getDouble();
            Missing = buffer.getDouble();
            Scalar = buffer.getDouble();
            window = buffer.getShort();
            flag0 = buffer.get();
            flag1 = buffer.get();
            flag2 = buffer.get();
            flag3 = buffer.get();
            flag4 = buffer.get();
            flag5 = buffer.get();
            flag6 = buffer.get();
            flag7 = buffer.get();
        }

        public void qualityCheck() {

        }

        @Override
        public String toString() {
            return "Header{" +
                    "description='" + description + '\'' +
                    ", latMin=" + latMin +
                    ", latMax=" + latMax +
                    ", lonMin=" + lonMin +
                    ", lonMax=" + lonMax +
                    ", resolutionLat=" + resolutionLat +
                    ", resolutionLon=" + resolutionLon +
                    ", countLat=" + countLat +
                    ", countLon=" + countLon +
                    ", NorthPole=" + NorthPole +
                    ", SouthPole=" + SouthPole +
                    ", Missing=" + Missing +
                    ", Scalar=" + Scalar +
                    ", window=" + window +
                    ", flag0=" + flag0 +
                    ", flag1=" + flag1 +
                    ", flag2=" + flag2 +
                    ", flag3=" + flag3 +
                    ", flag4=" + flag4 +
                    ", flag5=" + flag5 +
                    ", flag6=" + flag6 +
                    ", flag7=" + flag7 +
                    '}';
        }
    }
}



