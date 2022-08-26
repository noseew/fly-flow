package org.song.ff.client.producer.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.song.ff.client.producer.netty.WorkerInfoHolder;
import org.song.ff.common.Constant;
import org.song.ff.common.coder.MsgDecoder;
import org.song.ff.common.coder.MsgEncoder;

import java.util.List;

/**
 * netty连接器
 */
public class NettyClient {
    
    private static final NettyClient nettyClient = new NettyClient();

    private Bootstrap bootstrap = initBootstrap();

    public static NettyClient getInstance() {
        return nettyClient;
    }

    /**
     * 初始化 客户端 Bootstrap
     * 
     * @return
     */
    private Bootstrap initBootstrap() {
        //少线程
        EventLoopGroup group = new NioEventLoopGroup(2);

        Bootstrap bootstrap = new Bootstrap();
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER.getBytes());
                        ch.pipeline()
                                .addLast(new DelimiterBasedFrameDecoder(Constant.MAX_LENGTH, delimiter))
                                .addLast(new MsgDecoder())
                                .addLast(new MsgEncoder())
                                // 10秒没消息时，就发心跳包过去
                                .addLast(new IdleStateHandler(0, 0, 30))
                                .addLast(nettyClientHandler);
                    }
                });
        return bootstrap;
    }

    /**
     * 连接远程
     * 
     * @param addresses IP:PORT
     * @return
     */
    public synchronized boolean connect(List<String> addresses) {
        boolean allSuccess = true;
        for (String address : addresses) {
            if (WorkerInfoHolder.hasConnected(address)) {
                continue;
            }
            String[] ss = address.split(":");
            try {
                ChannelFuture channelFuture = bootstrap.connect(ss[0], Integer.parseInt(ss[1])).sync();
                Channel channel = channelFuture.channel();
                // 将客户端Channel, 放入 WorkerInfoHolder
                WorkerInfoHolder.put(address, channel);
            } catch (Exception e) {
                WorkerInfoHolder.put(address, null);
                allSuccess = false;
            }
        }
        return allSuccess;
    }

}
