package org.dav95s.openNTRIP.Tools.RTCM.Assets;

/**
 * Upcast projection message
 */
public interface CRS3 {
    int getMessageNumber();
    byte[] write();
}
