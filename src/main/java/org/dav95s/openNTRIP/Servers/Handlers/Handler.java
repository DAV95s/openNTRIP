package org.dav95s.openNTRIP.Servers.Handlers;

import org.dav95s.openNTRIP.Tools.MessagePack;

public interface Handler {
    MessagePack handle(MessagePack pack);
}

enum Handlers {
    msg1006Replace, msg1005Replace;
}
