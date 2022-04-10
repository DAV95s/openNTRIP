package org.dav95s.openNTRIP.core;

import io.netty.util.AttributeKey;
import org.dav95s.openNTRIP.database.modelsV2.MountpointModel;
import org.dav95s.openNTRIP.database.modelsV2.ReferenceStationModel;

public class ChannelState {
    public static final AttributeKey<MountpointModel> MOUNTPOINT = AttributeKey.valueOf("MOUNT_POINT");
    public static final AttributeKey<ReferenceStationModel> REFERENCE_STATION_MODEL = AttributeKey.valueOf("REFERENCE_STATION_MODEL");
    public static final AttributeKey<String> REFERENCE_STATION = AttributeKey.valueOf("REFERENCE_STATION");
    public static final AttributeKey<String> NETWORK = AttributeKey.valueOf("NETWORK");
    public static final AttributeKey<Boolean> AUTHENTICATION = AttributeKey.valueOf("AUTHENTICATION");
}
