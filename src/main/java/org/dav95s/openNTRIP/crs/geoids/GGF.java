package org.dav95s.openNTRIP.crs.geoids;

import org.dav95s.openNTRIP.utils.binaryParse.Normalize;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class GGF implements IGeoid {

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


    public double getValueByPoint(double lat, double lon) {
        if (lon < 0 || header.lonMax > 360) {
            lon += 360;
        }

        int latP = (int) Normalize.normalize((lat - header.latMin) / header.resolutionLat, 4);
        int lonP = (int) (Normalize.normalize((lon - header.lonMin) / header.resolutionLon, 4));

        double dLon = lon - (lonP * header.resolutionLon + header.lonMin);
        double dLat = lat - (latP * header.resolutionLat + header.latMin);

        float[][] v = new float[][]{{model[latP][lonP], model[latP][lonP + 1]}, {model[latP + 1][lonP], model[latP + 1][lonP + 1]}};

        return bilinear(dLat / header.resolutionLat, dLon / header.resolutionLon, v);
    }

    public static float bilinear(double x, double y, float[][] v) {
        return (float) (v[0][0] * (1 - x) * (1 - y) + v[1][0] * x * (1 - y) + v[0][1] * (1 - x) * y + v[1][1] * x * y);
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



