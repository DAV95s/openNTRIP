package org.dav95s.openNTRIP.Network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Worker implements Runnable {
    final static private Logger logger = LogManager.getLogger(Worker.class.getName());
    private static final int WORK_QUEUE = 1000;
    private static final int BLOCKING_TIMEOUT_MLS = 60000;

    private static Worker instance;

    private Worker() {
        Thread workingThread = new Thread(this);
        workingThread.start();
    }

    public static Worker getInstance() {
        if (instance == null)
            instance = new Worker();

        return instance;
    }

    private final ArrayBlockingQueue<INetworkHandler> queue = new ArrayBlockingQueue<>(WORK_QUEUE);

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void addWork(INetworkHandler work) {
        queue.add(work);
    }

    @Override
    public void run() {
        while (true) {
            try {
                INetworkHandler work = queue.poll(BLOCKING_TIMEOUT_MLS, TimeUnit.MILLISECONDS);

                if (work == null)
                    continue;

                executor.submit(work);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }
}
