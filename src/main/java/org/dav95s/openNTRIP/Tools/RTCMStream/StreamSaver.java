package org.dav95s.openNTRIP.Tools.RTCMStream;


import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Tools.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Date;

public class StreamSaver {
    final static private Logger logger = LoggerFactory.getLogger(StreamSaver.class.getName());

    Date date = new Date();
    Integer currentHours;
    RandomAccessFile file;
    private final ReferenceStation referenceStation;

    public StreamSaver(ReferenceStation referenceStation) {
        this.referenceStation = referenceStation;
    }

    public void save(MessagePack messagePack) {
        if (currentHours == null) {
            currentHours = date.getHours();

            String bufferPath = Config.getInstance().getProperties("defaultBufferSavePath");

            if (bufferPath == null) {
                logger.error("Please, specify path to the stream save folder");
                return;
            }

            try {
                file = new RandomAccessFile(bufferPath + "/" + referenceStation.getName(), "rw");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ByteBuffer buffer = messagePack.getByteBuffer();
            buffer.flip();
            file.getChannel().write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        file.close();
    }
}