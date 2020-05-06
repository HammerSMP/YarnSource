/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.CorruptedFrameException
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.PacketByteBuf;

public class SplitterHandler
extends ByteToMessageDecoder {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        byte[] bs = new byte[3];
        for (int i = 0; i < bs.length; ++i) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }
            bs[i] = byteBuf.readByte();
            if (bs[i] < 0) continue;
            PacketByteBuf lv = new PacketByteBuf(Unpooled.wrappedBuffer((byte[])bs));
            try {
                int j = lv.readVarInt();
                if (byteBuf.readableBytes() < j) {
                    byteBuf.resetReaderIndex();
                    return;
                }
                list.add((Object)byteBuf.readBytes(j));
                return;
            }
            finally {
                lv.release();
            }
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }
}

