package org.song.ff.server.consumer.netty.client;

import io.netty.channel.ChannelHandlerContext;

/**
 */
public interface IClientChangeListener {
    /**
     * 发现新连接
     */
    void newClient(String appName, String channelId, ChannelHandlerContext ctx);

    /**
     * 客户端掉线
     */
    void loseClient(ChannelHandlerContext ctx);
}