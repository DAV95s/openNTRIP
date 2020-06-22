package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Clients.ClientListener;
import org.adv25.ADVNTRIP.Servers.BaseStation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Analyzer {
    final static private Logger logger = LogManager.getLogger(Analyzer.class.getName());

    public static ConcurrentHashMap<BaseStation, ReportBook> cards = new ConcurrentHashMap<>();

    static Analyzer instance = new Analyzer();

    public static Listener registration(BaseStation baseStation) {
        cards.put(baseStation, new ReportBook(baseStation));
        return new Listener();
    }

    private Analyzer() {

    }
}


class Listener implements ClientListener, Runnable {
    final static private org.apache.logging.log4j.Logger logger = LogManager.getLogger(Listener.class.getName());

    static ExecutorService service = Executors.newCachedThreadPool();

    BaseStation baseStation;
    byte[] bytes;
    ReportBook reportBook;

    @Override
    public void send(ByteBuffer bytes, BaseStation baseStation) throws IOException {
        this.baseStation = baseStation;
        this.bytes = new byte[bytes.remaining()];
        this.reportBook = Analyzer.cards.get(baseStation);

        bytes.get(this.bytes);

        service.submit(this);
    }

    @Override
    public void safeClose() throws IOException {

    }

    @Override
    public void run() {
        ByteBuffer bb = ByteBuffer.wrap(this.bytes);

        if (bb.limit() == 0)
            return;

        int preambleIndex, shift, msgNmb;

        while (bb.hasRemaining()) {
            if (bb.get() != -45)
                continue;

            preambleIndex = bb.position() - 1;
            shift = bb.getShort(preambleIndex + 1) + 6;
            msgNmb = (bb.getShort(preambleIndex + 3) & 0xffff) >> 4;

            try {
                bb.position(preambleIndex);
                byte[] msg = new byte[shift];

                bb.get(msg, 0, shift);

                reportBook.addRawData(msgNmb, msg);

                bb.position(preambleIndex);
                bb.position(preambleIndex + shift);

            } catch (IllegalArgumentException | BufferUnderflowException e) {
                break;
            }
        }
        logger.debug(baseStation.getName() + " has send " + bytes.length + " bytes");
    }
}
