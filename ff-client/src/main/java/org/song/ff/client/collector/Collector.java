package org.song.ff.client.collector;

import java.util.List;

public interface Collector<K, V> {

    public V get(K k);

    public void put(K k, V v);

    List<V> getResult();
}
