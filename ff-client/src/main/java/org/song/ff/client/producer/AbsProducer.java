package org.song.ff.client.producer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.song.ff.client.collector.Collector;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbsProducer implements Producer {
    protected ProducerConfig producerConfig;
    protected Collector collector;
    protected ScheduledExecutorService scheduledExecutorService;

    public AbsProducer(ProducerConfig producerConfig, Collector collector) {
        this.producerConfig = producerConfig;
        this.collector = collector;
        scheduledExecutorService = Executors
                .newScheduledThreadPool(4, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("").build());
    }


    @Override
    public void start() {
        long millis = producerConfig.getWindow().toMillis();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            produce(collector.getResult());
        }, millis, millis, TimeUnit.MILLISECONDS);
    }

    protected abstract void produce(List<Object> datas);

}
