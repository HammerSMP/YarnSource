/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerNetworkIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LegacyQueryHandler
extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerNetworkIo networkIo;

    public LegacyQueryHandler(ServerNetworkIo networkIo) {
        this.networkIo = networkIo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        ByteBuf byteBuf = (ByteBuf)object;
        byteBuf.markReaderIndex();
        boolean bl = true;
        try {
            if (byteBuf.readUnsignedByte() != 254) {
                return;
            }
            InetSocketAddress inetSocketAddress = (InetSocketAddress)channelHandlerContext.channel().remoteAddress();
            MinecraftServer minecraftServer = this.networkIo.getServer();
            int i = byteBuf.readableBytes();
            switch (i) {
                case 0: {
                    LOGGER.debug("Ping: (<1.3.x) from {}:{}", (Object)inetSocketAddress.getAddress(), (Object)inetSocketAddress.getPort());
                    String string = String.format("%s\u00a7%d\u00a7%d", minecraftServer.getServerMotd(), minecraftServer.getCurrentPlayerCount(), minecraftServer.getMaxPlayerCount());
                    this.reply(channelHandlerContext, this.toBuffer(string));
                    break;
                }
                case 1: {
                    if (byteBuf.readUnsignedByte() != 1) {
                        return;
                    }
                    LOGGER.debug("Ping: (1.4-1.5.x) from {}:{}", (Object)inetSocketAddress.getAddress(), (Object)inetSocketAddress.getPort());
                    String string2 = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftServer.getVersion(), minecraftServer.getServerMotd(), minecraftServer.getCurrentPlayerCount(), minecraftServer.getMaxPlayerCount());
                    this.reply(channelHandlerContext, this.toBuffer(string2));
                    break;
                }
                default: {
                    boolean bl2 = byteBuf.readUnsignedByte() == 1;
                    bl2 &= byteBuf.readUnsignedByte() == 250;
                    bl2 &= "MC|PingHost".equals(new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE));
                    int j = byteBuf.readUnsignedShort();
                    bl2 &= byteBuf.readUnsignedByte() >= 73;
                    bl2 &= 3 + byteBuf.readBytes(byteBuf.readShort() * 2).array().length + 4 == j;
                    bl2 &= byteBuf.readInt() <= 65535;
                    if (!(bl2 &= byteBuf.readableBytes() == 0)) {
                        return;
                    }
                    LOGGER.debug("Ping: (1.6) from {}:{}", (Object)inetSocketAddress.getAddress(), (Object)inetSocketAddress.getPort());
                    String string3 = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftServer.getVersion(), minecraftServer.getServerMotd(), minecraftServer.getCurrentPlayerCount(), minecraftServer.getMaxPlayerCount());
                    ByteBuf byteBuf2 = this.toBuffer(string3);
                    try {
                        this.reply(channelHandlerContext, byteBuf2);
                        break;
                    }
                    finally {
                        byteBuf2.release();
                    }
                }
            }
            byteBuf.release();
            bl = false;
        }
        catch (RuntimeException runtimeException) {
        }
        finally {
            if (bl) {
                byteBuf.resetReaderIndex();
                channelHandlerContext.channel().pipeline().remove("legacy_query");
                channelHandlerContext.fireChannelRead(object);
            }
        }
    }

    private void reply(ChannelHandlerContext ctx, ByteBuf buf) {
        ctx.pipeline().firstContext().writeAndFlush((Object)buf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
    }

    private ByteBuf toBuffer(String s) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(255);
        char[] cs = s.toCharArray();
        byteBuf.writeShort(cs.length);
        for (char c : cs) {
            byteBuf.writeChar((int)c);
        }
        return byteBuf;
    }
}

