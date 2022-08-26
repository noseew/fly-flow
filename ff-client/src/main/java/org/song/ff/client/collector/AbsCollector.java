package org.song.ff.client.collector;

public abstract class AbsCollector<K, V> implements Collector<K, V> {

    protected int limit;

    public AbsCollector(int limit) {
        this.limit = limit;
    }
}
