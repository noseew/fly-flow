package org.song.ff.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.song.ff.common.Constant;
import org.song.ff.common.model.MsgWrapper;
import org.song.ff.common.utils.ProtostuffUtils;

/**
 *
 */
public class MsgEncoder extends MessageToByteEncoder {

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
        if (in instanceof MsgWrapper) {
            byte[] bytes = ProtostuffUtils.serialize(in);
            byte[] delimiter = Constant.DELIMITER.getBytes();

            byte[] total = new byte[bytes.length + delimiter.length];
            System.arraycopy(bytes, 0, total, 0, bytes.length);
            System.arraycopy(delimiter, 0, total, bytes.length, delimiter.length);

            out.writeBytes(total);
        }
    }
}