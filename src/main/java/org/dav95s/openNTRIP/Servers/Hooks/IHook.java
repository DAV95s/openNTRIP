package org.dav95s.openNTRIP.Servers.Hooks;

import org.dav95s.openNTRIP.Servers.MountPoint;
import org.dav95s.openNTRIP.Tools.MessagePack;

public interface IHook {


    MessagePack handle(MessagePack pack);
}

