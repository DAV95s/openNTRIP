package org.dav95s.openNTRIP.Tools;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MessagePack {
    ArrayList<Message> messagePack = new ArrayList<>();

    public ArrayList<Message> getArray() {
        return messagePack;
    }

    public void addMessage(int nmb, byte[] bytes) {
        messagePack.add(new Message(nmb, bytes));
    }

    public Message getMessageByNmb(int nmb) {
        for (Message message : messagePack) {
            if (message.nmb == nmb)
                return message;
        }
        return null;
    }

    public void removeMessage(int nmb) {
        messagePack.removeIf(msg -> msg.nmb == nmb);
    }

    public ByteBuffer getByteBuffer() {
        int capacity = 0;
        for (Message msg : messagePack) {
            capacity += msg.bytes.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        for (Message msg : messagePack) {
            buffer.put(msg.getBytes());
        }

        return buffer;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (Message msg : messagePack) {
            stringBuilder.append(msg.getNmb());
            stringBuilder.append(" have ");
            stringBuilder.append(msg.getBytes().length);
            stringBuilder.append(" bytes, ");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public int size() {
        return messagePack.size();
    }
}
