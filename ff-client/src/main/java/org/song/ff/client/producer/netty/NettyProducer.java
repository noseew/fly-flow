package org.song.ff.client.producer.netty;

import org.song.ff.client.collector.Collector;
import org.song.ff.client.producer.AbsProducer;

import java.util.List;

public class NettyProducer extends AbsProducer {

    public NettyProducer(NettyProducerConfig config, Collector collector) {
        super(config, collector);
    }

    @Override
    protected void produce(List<Object> datas) {

    }
}
