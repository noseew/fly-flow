package org.song.ff.client.producer.netty;

import org.song.ff.client.producer.ProducerConfig;

import java.util.List;

public class NettyProducerConfig extends ProducerConfig {
    private List<String> instances;

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }
}
