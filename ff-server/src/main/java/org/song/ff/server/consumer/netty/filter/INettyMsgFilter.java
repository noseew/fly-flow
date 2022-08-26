package org.song.ff.server.consumer.netty.filter;

import io.netty.channel.ChannelHandlerContext;
import org.song.ff.common.model.MsgWrapper;

/**
 * 对netty来的消息，进行过滤处理
 */
public interface INettyMsgFilter {
    boolean chain(MsgWrapper message, ChannelHandlerContext ctx);
}
