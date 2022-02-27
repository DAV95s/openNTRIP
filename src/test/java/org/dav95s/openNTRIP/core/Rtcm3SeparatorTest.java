package org.dav95s.openNTRIP.core;

import org.dav95s.openNTRIP.commons.Message;
import org.dav95s.openNTRIP.protocols.rtcm.Rtcm3Separator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class Rtcm3SeparatorTest extends Rtcm3Separator {

    @Test
    public void fullMessage() {
        byte[] message = new byte[]{(byte) 0xD3, 0x00, 0x27, 0x40, 0x50, 0x17, 0x00, (byte) 0x84, 0x73, 0x6E, 0x15, 0x1E, 0x55, 0x54, 0x46, 0x2D
                , 0x38, 0x20, (byte) 0xD0, (byte) 0xBF, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBE, (byte) 0xD0, (byte) 0xB2, (byte) 0xD0, (byte) 0xB5, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBA
                , (byte) 0xD0, (byte) 0xB0, 0x20, 0x77, (byte) 0xC3, (byte) 0xB6, 0x72, 0x74, 0x65, 0x72, (byte) 0xED, (byte) 0xA3, 0x3B};

        Rtcm3Separator rtcm3X = new Rtcm3Separator();
        ArrayList<Message> messagePack = rtcm3X.separate(message);

        Assert.assertFalse(messagePack.isEmpty());
        Assert.assertEquals("1029", messagePack.get(0).name);
    }

    @Test
    public void separate2() {
        byte[] firstPart = new byte[]{(byte) 0xD3};
        byte[] lastPart = new byte[]{0x00, 0x27, 0x40, 0x50, 0x17, 0x00, (byte) 0x84, 0x73, 0x6E, 0x15, 0x1E, 0x55, 0x54, 0x46, 0x2D
                , 0x38, 0x20, (byte) 0xD0, (byte) 0xBF, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBE, (byte) 0xD0, (byte) 0xB2, (byte) 0xD0, (byte) 0xB5, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBA
                , (byte) 0xD0, (byte) 0xB0, 0x20, 0x77, (byte) 0xC3, (byte) 0xB6, 0x72, 0x74, 0x65, 0x72, (byte) 0xED, (byte) 0xA3, 0x3B};

        Rtcm3Separator rtcm3X = new Rtcm3Separator();
        ArrayList<Message> messagePack1 = rtcm3X.separate(firstPart);
        ArrayList<Message> messagePack2 = rtcm3X.separate(lastPart);
        Assert.assertTrue(messagePack1.isEmpty());
        Assert.assertFalse(messagePack2.isEmpty());
        Assert.assertEquals("1029", messagePack2.get(0).name);
    }

    @Test
    public void separate3() {
        byte[] firstPart = new byte[]{(byte) 0xD3, 0x00};
        byte[] lastPart = new byte[]{0x27, 0x40, 0x50, 0x17, 0x00, (byte) 0x84, 0x73, 0x6E, 0x15, 0x1E, 0x55, 0x54, 0x46, 0x2D
                , 0x38, 0x20, (byte) 0xD0, (byte) 0xBF, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBE, (byte) 0xD0, (byte) 0xB2, (byte) 0xD0, (byte) 0xB5, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBA
                , (byte) 0xD0, (byte) 0xB0, 0x20, 0x77, (byte) 0xC3, (byte) 0xB6, 0x72, 0x74, 0x65, 0x72, (byte) 0xED, (byte) 0xA3, 0x3B};

        Rtcm3Separator rtcm3X = new Rtcm3Separator();
        ArrayList<Message> messagePack1 = rtcm3X.separate(firstPart);
        ArrayList<Message> messagePack2 = rtcm3X.separate(lastPart);
        Assert.assertTrue(messagePack1.isEmpty());
        Assert.assertFalse(messagePack2.isEmpty());
        Assert.assertEquals("1029", messagePack2.get(0).name);
    }

    @Test
    public void separate4() {
        byte[] firstPart = new byte[]{(byte) 0xD3, 0x00, 0x27};
        byte[] lastPart = new byte[]{0x40, 0x50, 0x17, 0x00, (byte) 0x84, 0x73, 0x6E, 0x15, 0x1E, 0x55, 0x54, 0x46, 0x2D
                , 0x38, 0x20, (byte) 0xD0, (byte) 0xBF, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBE, (byte) 0xD0, (byte) 0xB2, (byte) 0xD0, (byte) 0xB5, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBA
                , (byte) 0xD0, (byte) 0xB0, 0x20, 0x77, (byte) 0xC3, (byte) 0xB6, 0x72, 0x74, 0x65, 0x72, (byte) 0xED, (byte) 0xA3, 0x3B};

        Rtcm3Separator rtcm3X = new Rtcm3Separator();
        ArrayList<Message> messagePack1 = rtcm3X.separate(firstPart);
        ArrayList<Message> messagePack2 = rtcm3X.separate(lastPart);
        Assert.assertTrue(messagePack1.isEmpty());
        Assert.assertFalse(messagePack2.isEmpty());
        Assert.assertEquals("1029", messagePack2.get(0).name);
    }

    @Test
    public void separate5() {
        byte[] firstPart = new byte[]{(byte) 0xD3, 0x00, 0x27, 0x40};
        byte[] lastPart = new byte[]{0x50, 0x17, 0x00, (byte) 0x84, 0x73, 0x6E, 0x15, 0x1E, 0x55, 0x54, 0x46, 0x2D
                , 0x38, 0x20, (byte) 0xD0, (byte) 0xBF, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBE, (byte) 0xD0, (byte) 0xB2, (byte) 0xD0, (byte) 0xB5, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBA
                , (byte) 0xD0, (byte) 0xB0, 0x20, 0x77, (byte) 0xC3, (byte) 0xB6, 0x72, 0x74, 0x65, 0x72, (byte) 0xED, (byte) 0xA3, 0x3B};

        Rtcm3Separator rtcm3X = new Rtcm3Separator();
        ArrayList<Message> messagePack1 = rtcm3X.separate(firstPart);
        ArrayList<Message> messagePack2 = rtcm3X.separate(lastPart);
        Assert.assertTrue(messagePack1.isEmpty());
        Assert.assertFalse(messagePack2.isEmpty());
        Assert.assertEquals("1029", messagePack2.get(0).name);
    }

    @Test
    public void separate6() {
        byte[] firstPart = new byte[]{(byte) 0xD3, 0x00, 0x27, 0x40, 0x50};
        byte[] lastPart = new byte[]{0x17, 0x00, (byte) 0x84, 0x73, 0x6E, 0x15, 0x1E, 0x55, 0x54, 0x46, 0x2D
                , 0x38, 0x20, (byte) 0xD0, (byte) 0xBF, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBE, (byte) 0xD0, (byte) 0xB2, (byte) 0xD0, (byte) 0xB5, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBA
                , (byte) 0xD0, (byte) 0xB0, 0x20, 0x77, (byte) 0xC3, (byte) 0xB6, 0x72, 0x74, 0x65, 0x72, (byte) 0xED, (byte) 0xA3, 0x3B};

        Rtcm3Separator rtcm3X = new Rtcm3Separator();
        ArrayList<Message> messagePack1 = rtcm3X.separate(firstPart);
        ArrayList<Message> messagePack2 = rtcm3X.separate(lastPart);
        Assert.assertTrue(messagePack1.isEmpty());
        Assert.assertFalse(messagePack2.isEmpty());
        Assert.assertEquals("1029", messagePack2.get(0).name);
    }

    @Test
    public void separate7() {
        byte[] firstPart = new byte[]{(byte) 0xD3, 0x00, 0x27, 0x40, 0x50, 0x17, 0x00, (byte) 0x84, 0x73, 0x6E, 0x15, 0x1E, 0x55};
        byte[] lastPart = new byte[]{0x54, 0x46, 0x2D
                , 0x38, 0x20, (byte) 0xD0, (byte) 0xBF, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBE, (byte) 0xD0, (byte) 0xB2, (byte) 0xD0, (byte) 0xB5, (byte) 0xD1, (byte) 0x80, (byte) 0xD0, (byte) 0xBA
                , (byte) 0xD0, (byte) 0xB0, 0x20, 0x77, (byte) 0xC3, (byte) 0xB6, 0x72, 0x74, 0x65, 0x72, (byte) 0xED, (byte) 0xA3, 0x3B};

        Rtcm3Separator rtcm3X = new Rtcm3Separator();
        ArrayList<Message> messagePack1 = rtcm3X.separate(firstPart);
        ArrayList<Message> messagePack2 = rtcm3X.separate(lastPart);
        Assert.assertTrue(messagePack1.isEmpty());
        Assert.assertFalse(messagePack2.isEmpty());
        Assert.assertEquals("1029", messagePack2.get(0).name);
    }

    @Test
    public void checkExistingMessageNumber() {
        Assert.assertFalse(super.checkExistsMessageNumber(1));
        Assert.assertFalse(super.checkExistsMessageNumber(1000));
        Assert.assertFalse(super.checkExistsMessageNumber(-1));
        Assert.assertFalse(super.checkExistsMessageNumber(-1000));
        Assert.assertFalse(super.checkExistsMessageNumber(1040));
        Assert.assertFalse(super.checkExistsMessageNumber(1056));
        Assert.assertFalse(super.checkExistsMessageNumber(1069));
        Assert.assertFalse(super.checkExistsMessageNumber(4000));
        Assert.assertFalse(super.checkExistsMessageNumber(4096));


        Assert.assertTrue(super.checkExistsMessageNumber(1001));
        Assert.assertTrue(super.checkExistsMessageNumber(1014));
        Assert.assertTrue(super.checkExistsMessageNumber(1028));
        Assert.assertTrue(super.checkExistsMessageNumber(1039));
        Assert.assertTrue(super.checkExistsMessageNumber(1057));
        Assert.assertTrue(super.checkExistsMessageNumber(1062));
        Assert.assertTrue(super.checkExistsMessageNumber(1070));
        Assert.assertTrue(super.checkExistsMessageNumber(1073));
        Assert.assertTrue(super.checkExistsMessageNumber(1078));
        Assert.assertTrue(super.checkExistsMessageNumber(1090));
        Assert.assertTrue(super.checkExistsMessageNumber(1097));
        Assert.assertTrue(super.checkExistsMessageNumber(1098));
        Assert.assertTrue(super.checkExistsMessageNumber(1229));
        Assert.assertTrue(super.checkExistsMessageNumber(1230));
        Assert.assertTrue(super.checkExistsMessageNumber(4001));
        Assert.assertTrue(super.checkExistsMessageNumber(4095));
    }
}