package org.song.ff.server.consumer.netty.filter;

import io.netty.channel.ChannelHandlerContext;
import org.song.ff.common.model.MsgType;
import org.song.ff.common.model.MsgWrapper;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 热key消息，包括从netty来的和mq来的。收到消息，都发到队列去
 */
public class HotKeyFilter implements INettyMsgFilter {

    public static AtomicLong totalReceiveKeyCount = new AtomicLong();

    @Override
    public boolean chain(MsgWrapper message, ChannelHandlerContext ctx) {
        if (MsgType.data.getType() == message.getMsgType()) {
            totalReceiveKeyCount.incrementAndGet();
            publishMsg(message, ctx);
            return false;
        }
        return true;
    }

    private void publishMsg(MsgWrapper message, ChannelHandlerContext ctx) {
        //            ReactorConfig.NETTY_TO_QUEUE.put(model);
    }

}