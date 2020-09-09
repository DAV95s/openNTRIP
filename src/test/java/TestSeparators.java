import org.adv25.ADVNTRIP.Tools.Decoders.RTCM_3X;
import org.adv25.ADVNTRIP.Tools.MessagePack;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestSeparators {

    @Test
    public void rtcm3x_separator() throws IOException {
        String path = "src/test/resources/RTCM_30";
        RTCM_3X rtcm3X = new RTCM_3X();

        ByteBuffer buffer = ByteBuffer.allocate(100_000);

        RandomAccessFile reader = new RandomAccessFile(path, "r");
        FileChannel fileChannel = reader.getChannel();

        fileChannel.read(buffer);
        MessagePack messagePack = rtcm3X.separate(buffer);
        Assert.assertEquals(buffer.flip(), messagePack.getFullBytes().flip());
        Assert.assertNotNull(messagePack.getMessageByNmb(1004));
        Assert.assertNotNull(messagePack.getMessageByNmb(1005));
        Assert.assertNotNull(messagePack.getMessageByNmb(1007));
        Assert.assertNotNull(messagePack.getMessageByNmb(1033));
        messagePack.removeMessage(1033);
        Assert.assertNull(messagePack.getMessageByNmb(1033));
        fileChannel.close();

        buffer.clear();
        path = "src/test/resources/RTCM_31";
        reader = new RandomAccessFile(path, "r");
        fileChannel = reader.getChannel();
        fileChannel.read(buffer);
        messagePack = rtcm3X.separate(buffer);
        Assert.assertEquals(buffer.flip(), messagePack.getFullBytes().flip());
        Assert.assertNotNull(messagePack.getMessageByNmb(1004));
        fileChannel.close();

        buffer.clear();
        path = "src/test/resources/RTCM_32";
        reader = new RandomAccessFile(path, "r");
        fileChannel = reader.getChannel();
        fileChannel.read(buffer);
        messagePack = rtcm3X.separate(buffer);
        Assert.assertEquals(buffer.flip(), messagePack.getFullBytes().flip());
        Assert.assertNotNull(messagePack.getMessageByNmb(1006));
        Assert.assertNotNull(messagePack.getMessageByNmb(1004));
        Assert.assertNotNull(messagePack.getMessageByNmb(1012));
        Assert.assertNotNull(messagePack.getMessageByNmb(1008));
        Assert.assertNotNull(messagePack.getMessageByNmb(1033));
        Assert.assertNotNull(messagePack.getMessageByNmb(1230));
        Assert.assertNotNull(messagePack.getMessageByNmb(1004));
        fileChannel.close();

        buffer.clear();
        path = "src/test/resources/RTCM_33";
        reader = new RandomAccessFile(path, "r");
        fileChannel = reader.getChannel();

        fileChannel.read(buffer);
        messagePack = rtcm3X.separate(buffer);
        Assert.assertEquals(buffer.flip(), messagePack.getFullBytes().flip());
    }
}
