package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Servers.BaseStation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportBook {
    final static private Logger logger = LogManager.getLogger(org.adv25.ADVNTRIP.Tools.ReportBook.class.getName());

    static ExecutorService service = Executors.newCachedThreadPool();

    BaseStation baseStation;
    long bitRate;
    boolean externalApi = false;
    TreeMap<Integer, Integer> details = new TreeMap<>();
    public HashMap<Integer, byte[]> rawData = new HashMap<>();
    HashMap<Integer, Long> timeMarks = new HashMap<>();

    public ReportBook(BaseStation baseStation) {
        this.baseStation = baseStation;
    }

    public void addRawData(int msgNum, byte[] data) {
        rawData.put(msgNum, data);
        long curTime = System.currentTimeMillis();

        if (timeMarks.containsKey(msgNum)) {
            float deltaSec = (curTime - timeMarks.get(msgNum)) / 1000.0f;

            if (deltaSec < 1) {
                deltaSec = 1;
            }
            details.put(msgNum, Math.round(deltaSec));
        }
        timeMarks.put(msgNum, curTime);

        if (msgNum == 1005 || msgNum == 1006) {
            service.submit(new AnalyzeTasks.Position(baseStation));
        }
    }

}
