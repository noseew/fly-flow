package org.song.ff.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.song.ff.common.model.MsgWrapper;
import org.song.ff.common.utils.ProtostuffUtils;

import java.util.List;

/**
 */
public class MsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) {
        try {
            byte[] body = new byte[in.readableBytes()];  //传输正常
            in.readBytes(body);
            list.add(ProtostuffUtils.deserialize(body, MsgWrapper.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
