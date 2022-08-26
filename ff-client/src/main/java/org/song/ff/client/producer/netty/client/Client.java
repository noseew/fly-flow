package org.song.ff.client.producer.netty.client;


import com.google.common.collect.Lists;
import org.song.ff.client.producer.netty.WorkerInfoHolder;

import java.util.List;

public class Client {
    
    public static void clientStart(String remoteIp, int port) {
        // 添加和开始连接
        List<String> addresses = Lists.newArrayList(remoteIp + ":" + port);
        WorkerInfoHolder.mergeAndConnectNew(addresses);
    }
}
