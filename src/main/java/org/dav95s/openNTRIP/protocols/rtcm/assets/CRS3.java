package org.dav95s.openNTRIP.protocols.rtcm.assets;

/**
 * Upcast projection message
 */
public interface CRS3 {
    int getMessageNumber();
    byte[] getBytes();
}
