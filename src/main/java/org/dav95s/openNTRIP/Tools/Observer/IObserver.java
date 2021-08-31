package org.dav95s.openNTRIP.Tools.Observer;

import org.dav95s.openNTRIP.Servers.ReferenceStation;

import java.nio.ByteBuffer;

public interface IObserver {
    void notify(ReferenceStation referenceStation, ByteBuffer buffer);

    String toString();
}
