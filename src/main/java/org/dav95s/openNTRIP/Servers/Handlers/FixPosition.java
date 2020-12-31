package org.dav95s.openNTRIP.Servers.Handlers;

import org.dav95s.openNTRIP.Tools.Message;
import org.dav95s.openNTRIP.Tools.MessagePack;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1006;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixPosition implements Handler {

    BigDecimal ECEFX;
    BigDecimal ECEFY;
    BigDecimal ECEFZ;

    public FixPosition(BigDecimal ECEF_X, BigDecimal ECEF_Y, BigDecimal ECEF_Z) {
        ECEFX = ECEF_X.setScale(4, RoundingMode.HALF_EVEN);
        ECEFY = ECEF_Y.setScale(4, RoundingMode.HALF_EVEN);
        ECEFZ = ECEF_Z.setScale(4, RoundingMode.HALF_EVEN);
    }

    @Override
    public MessagePack handle(MessagePack pack) {
        Message msg = pack.getMessageByNmb(1005);
        if (msg != null) {
            MSG1006 msg1006 = new MSG1006(msg.getBytes());
        }

        msg = pack.getMessageByNmb(1006);
        if (msg == null)
            return pack;

        return pack;
    }
}
