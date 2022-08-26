package org.song.ff.server.consumer.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.song.ff.common.model.MsgWrapper;
import org.song.ff.server.consumer.netty.client.IClientChangeListener;
import org.song.ff.server.consumer.netty.filter.INettyMsgFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 这里处理所有netty事件。
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MsgWrapper> {

    private static Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    /**
     * 客户端状态监听器
     */
    private IClientChangeListener clientEventListener;
    /**
     * 请自行维护Filter的添加顺序
     */
    private List<INettyMsgFilter> messageFilters = new ArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgWrapper msg) {
        if (msg == null) {
            return;
        }
        for (INettyMsgFilter messageFilter : messageFilters) {
            boolean doNext = messageFilter.chain(msg, ctx);
            if (!doNext) {
                return;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("some thing is error , " + cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (clientEventListener != null) {
            clientEventListener.loseClient(ctx);
        }
        ctx.close();
        super.channelInactive(ctx);
    }

    public void setClientEventListener(IClientChangeListener clientEventListener) {
        this.clientEventListener = clientEventListener;
    }

    public void addMessageFilter(INettyMsgFilter iNettyMsgFilter) {
        if (iNettyMsgFilter != null) {
            messageFilters.add(iNettyMsgFilter);
        }
    }

    public void addMessageFilters(List<INettyMsgFilter> iNettyMsgFilters) {

        if (!CollectionUtils.isEmpty(iNettyMsgFilters)) {
            messageFilters.addAll(iNettyMsgFilters);
        }
    }
}
