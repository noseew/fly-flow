package org.song.ff.server.consumer.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.song.ff.server.consumer.netty.filter.INettyMsgFilter;

import java.util.List;

public class Server {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private List<INettyMsgFilter> messageFilters;
    
    public void startServer(int port) {

        NettyServer nettyServer = new NettyServer();
        nettyServer.setMessageFilters(messageFilters);
        try {
            nettyServer.startNettyServer(port);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
