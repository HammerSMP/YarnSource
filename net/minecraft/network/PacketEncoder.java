/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketEncoderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketEncoder
extends MessageToByteEncoder<Packet<?>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker((String)"PACKET_SENT", (Marker)ClientConnection.MARKER_NETWORK_PACKETS);
    private final NetworkSide side;

    public PacketEncoder(NetworkSide arg) {
        this.side = arg;
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> arg, ByteBuf byteBuf) throws Exception {
        NetworkState lv = (NetworkState)((Object)channelHandlerContext.channel().attr(ClientConnection.ATTR_KEY_PROTOCOL).get());
        if (lv == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + arg);
        }
        Integer integer = lv.getPacketId(this.side, arg);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MARKER, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr(ClientConnection.ATTR_KEY_PROTOCOL).get(), (Object)integer, (Object)arg.getClass().getName());
        }
        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        }
        PacketByteBuf lv2 = new PacketByteBuf(byteBuf);
        lv2.writeVarInt(integer);
        try {
            arg.write(lv2);
        }
        catch (Throwable throwable) {
            LOGGER.error((Object)throwable);
            if (arg.isWritingErrorSkippable()) {
                throw new PacketEncoderException(throwable);
            }
            throw throwable;
        }
    }

    protected /* synthetic */ void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        this.encode(channelHandlerContext, (Packet)object, byteBuf);
    }
}

