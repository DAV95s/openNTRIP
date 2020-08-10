package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Clients.ClientListener;
import org.adv25.ADVNTRIP.Servers.ReferenceStation;
import org.adv25.ADVNTRIP.Tools.Decoders.RTCM_3X;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyzeListener implements ClientListener, Runnable {
    final static private org.apache.logging.log4j.Logger logger = LogManager.getLogger(AnalyzeListener.class.getName());

    final static private ExecutorService service = Executors.newCachedThreadPool();
    final static private Timer timer = new Timer();


    private Queue<Long> timeMarks = new ArrayDeque<>();
    private Queue<byte[]> bytes = new ArrayDeque<>();

    private MessagePool messagePool = new MessagePool();
    private AnalyzeTasks tasks;
    private ReferenceStation referenceStation;

    public AnalyzeListener(ReferenceStation referenceStation) {
        this.referenceStation = referenceStation;
        tasks = new AnalyzeTasks(referenceStation, messagePool);
    }

    @Override
    public void safeClose() throws IOException {
        referenceStation.removeListener(this);
    }

    public void analyzePlanner(TimerTask timerTask) {
        //for (int delay : timers) {
            timer.schedule(timerTask, 30_000, 220_000);
        //}
    }

    boolean hasAnalyzed = false;

    @Override
    public void send(ByteBuffer bytes, ReferenceStation referenceStation) throws IOException {

        if (!hasAnalyzed) {
            logger.debug("IN");
            timer.schedule(tasks.carrier, 30_000, 220_000);
            timer.schedule(tasks.FormatDetails, 30_000, 220_000);
            timer.schedule(tasks.navSystems, 30_000, 220_000);
            timer.schedule(tasks.position, 30_000, 220_000);
            timer.schedule(tasks.positionMetaInfo, 60_000);
            timer.schedule(tasks.rtcmVersion, 30_000, 220_000);
            hasAnalyzed = true;
        }

        byte[] temp = new byte[bytes.remaining()];
        bytes.get(temp);
        this.bytes.add(temp);

        this.timeMarks.add(System.currentTimeMillis());

        if (this.bytes.size() > 20) {
            this.bytes.clear();
            this.timeMarks.clear();
        }

        service.submit(this);
    }

    public MessagePool getMessagePool() {
        return messagePool;
    }

    RTCM_3X rtcmSeparator = new RTCM_3X();

    @Override
    public void run() {
        ArrayList<Message> arrayMessage = rtcmSeparator.separate(bytes.poll());
        messagePool.putData(arrayMessage, timeMarks.poll());
    }
}

class MessagePool {
    ArrayList<Integer> msgNumberPack = new ArrayList<>();
    ArrayList<Long> lastTimeMark = new ArrayList<>();
    ArrayList<Integer> delays = new ArrayList<>();
    HashMap<Integer, byte[]> bytePool = new HashMap<>();

    public void putData(Integer number, Long time) {
        //rewrite with "get" and "null" check
        if (msgNumberPack.contains(number)) {
            int index = msgNumberPack.indexOf(number);
            int delay = Math.round((time - lastTimeMark.get(index)) / 1000f);
            delays.set(index, Math.max(delay, 1));
            lastTimeMark.set(index, time);
        } else {
            msgNumberPack.add(number);
            lastTimeMark.add(time);
            delays.add(0);
        }
    }

    public void putData(ArrayList<Message> messages, Long time) {
        for (Message message : messages) {
            bytePool.put(message.nmb, message.bytes);
            putData(message.nmb, time);
        }
    }

    public byte[] getMessageBytes(int nmb) {
        return bytePool.get(nmb);
    }

    @Override
    public String toString() {

        TreeMap<Integer, Integer> temp = new TreeMap<>();
        for (int i = 0; i < msgNumberPack.size(); i++) {
            temp.put(msgNumberPack.get(i), delays.get(i));
        }

        String response = "";

        for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {
            response += ("," + entry.getKey() + "(" + entry.getValue() + ")");
        }

        return response.substring(1);
    }

    public ArrayList<Integer> getMsgNumberPack() {
        return msgNumberPack;
    }
}