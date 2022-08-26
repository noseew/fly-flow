package org.song.ff.client.collector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectorManager {

    private static Map<String, Collector> collectorMap = new ConcurrentHashMap<>();

    private CollectorManager() {

    }


    public static void addCollector(String name, Collector collector) {
        if (name == null || collector == null) {
            return;
        }
        collectorMap.put(name, collector);
    }


}
