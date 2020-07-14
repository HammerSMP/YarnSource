/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;
import net.minecraft.network.PacketByteBuf;

public class PacketDeflater
extends MessageToByteEncoder<ByteBuf> {
    private final byte[] deflateBuffer = new byte[8192];
    private final Deflater deflater;
    private int compressionThreshold;

    public PacketDeflater(int i) {
        this.compressionThreshold = i;
        this.deflater = new Deflater();
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        int i = byteBuf.readableBytes();
        PacketByteBuf lv = new PacketByteBuf(byteBuf2);
        if (i < this.compressionThreshold) {
            lv.writeVarInt(0);
            lv.writeBytes(byteBuf);
        } else {
            byte[] bs = new byte[i];
            byteBuf.readBytes(bs);
            lv.writeVarInt(bs.length);
            this.deflater.setInput(bs, 0, i);
            this.deflater.finish();
            while (!this.deflater.finished()) {
                int j = this.deflater.deflate(this.deflateBuffer);
                lv.writeBytes(this.deflateBuffer, 0, j);
            }
            this.deflater.reset();
        }
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    protected /* synthetic */ void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        this.encode(channelHandlerContext, (ByteBuf)object, byteBuf);
    }
}

