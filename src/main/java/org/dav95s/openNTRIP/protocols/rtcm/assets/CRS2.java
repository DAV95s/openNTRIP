package org.dav95s.openNTRIP.protocols.rtcm.assets;

/**
 * Upcast residuals grid message
 */
public interface CRS2 {
    int getMessageNumber();
    byte[] getBytes();
}
