package org.song.ff.client.collector;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CaffeineCollector<K, V> extends AbsCollector<K, V> {

    private final Cache<K, V> map0;
    private final Cache<K, V> map1;

    private final AtomicLong atomicLong = new AtomicLong(0);

    public CaffeineCollector(int limit, int expireSeconds) {
        super(limit);
        map0 = Caffeine.newBuilder().maximumSize(limit).expireAfterWrite(expireSeconds, TimeUnit.SECONDS).weakKeys().weakValues()
                .build();
        map1 = Caffeine.newBuilder().maximumSize(limit).expireAfterWrite(expireSeconds, TimeUnit.SECONDS).weakKeys().weakValues()
                .build();
    }

    @Override
    public List<V> getResult() {
        atomicLong.addAndGet(1);
        List<V> list;
        if ((atomicLong.get() & 01) == 0) {
            list = Lists.newArrayList(map0.asMap().values());
            map0.cleanUp();
        } else {
            list = Lists.newArrayList(map1.asMap().values());
            map1.cleanUp();
        }
        return list;
    }

    @Override
    public void put(K k, V v) {
        if (k == null) {
            return;
        }
        if ((atomicLong.get() & 01) == 0) {
            map0.asMap().put(k, v);
        } else {
            map1.asMap().put(k, v);
        }
    }

    @Override
    public V get(K k) {
        if (k == null) {
            return null;
        }
        if ((atomicLong.get() & 01) == 0) {
            return map0.asMap().get(k);
        } else {
            return map1.asMap().get(k);
        }
    }

}
