package org.song.ff.server.consumer.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.song.ff.common.Constant;
import org.song.ff.common.coder.MsgDecoder;
import org.song.ff.common.coder.MsgEncoder;
import org.song.ff.common.utils.RuntimeUtils;
import org.song.ff.server.consumer.netty.client.IClientChangeListener;
import org.song.ff.server.consumer.netty.filter.INettyMsgFilter;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 该server用于给各个微服务实例连接用。
 */
public class NettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private IClientChangeListener clientChangeListener;
    private List<INettyMsgFilter> messageFilters;

    public void startNettyServer(int port) throws Exception {
        //boss单线程
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(RuntimeUtils.workerCount());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //出来网络io事件，如记录日志、对消息编解码等
                    .childHandler(new ChildChannelHandler());

            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully(1000, 3000, TimeUnit.MILLISECONDS);
                workerGroup.shutdownGracefully(1000, 3000, TimeUnit.MILLISECONDS);
            }));

            //等待服务器监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("netty stop ", e);
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * handler类
     */
    private class ChildChannelHandler extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) {
            NettyServerHandler serverHandler = new NettyServerHandler();
            serverHandler.setClientEventListener(clientChangeListener);
            serverHandler.addMessageFilters(messageFilters);

            ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER.getBytes());
            ch.pipeline()
                    .addLast(new DelimiterBasedFrameDecoder(Constant.MAX_LENGTH, delimiter))
                    .addLast(new MsgDecoder())
                    .addLast(new MsgEncoder())
                    .addLast(serverHandler);
        }
    }

    public void setClientChangeListener(IClientChangeListener clientChangeListener) {
        this.clientChangeListener = clientChangeListener;
    }

    public void setMessageFilters(List<INettyMsgFilter> messageFilters) {
        this.messageFilters = messageFilters;
    }

}
