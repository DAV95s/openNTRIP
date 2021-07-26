package org.dav95s.openNTRIP.Tools.Observer;

import java.nio.ByteBuffer;

public interface IObservable {
    void registerObserver(IObserver o);

    void removeObserver(IObserver o);

    void notifyObservers(ByteBuffer buffer);
}
