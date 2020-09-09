package org.adv25.ADVNTRIP.Tools;

import org.apache.logging.log4j.core.util.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;

public class MessagePack extends ArrayList<Map.Entry<Integer, byte[]>> {

    public void addMessage(int nmb, byte[] bytes) {
        this.add(Map.entry(nmb, bytes));
    }

    public Map.Entry<Integer, byte[]> getMessageByNmb(int nmb) {
        for (Map.Entry<Integer, byte[]> entry : this) {
            if (entry.getKey() == nmb)
                return entry;
        }
        return null;
    }

    public void removeMessage(int nmb) {
        this.removeIf(msg -> msg.getKey() == nmb);
    }

    public ByteBuffer getFullBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
        for (Map.Entry<Integer, byte[]> msg : this) {
            buffer.put(msg.getValue());
        }
        return buffer;
    }
}
