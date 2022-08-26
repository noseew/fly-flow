package org.song.ff.client.producer.netty.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.song.ff.common.model.MsgType;
import org.song.ff.common.model.MsgWrapper;

/**
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<MsgWrapper> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                // 向服务端发送消息
                ctx.writeAndFlush(new MsgWrapper(MsgType.ping.getType(), "", ""));
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new MsgWrapper());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MsgWrapper msg) throws Exception {
        if (MsgType.pong.getType() == msg.getMsgType()) {
            return;
        }
    }

}
