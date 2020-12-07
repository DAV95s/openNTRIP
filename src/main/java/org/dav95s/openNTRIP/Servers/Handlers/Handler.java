package org.dav95s.openNTRIP.Servers.Handlers;

import org.dav95s.openNTRIP.Tools.MessagePack;

public interface Handler {
    MessagePack handle();
}

enum Handlers {
    msg1006Replace
}
