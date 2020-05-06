/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.util.concurrent.GenericFutureListener
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListPinger {
    private static final Splitter ZERO_SPLITTER = Splitter.on((char)'\u0000').limit(6);
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());

    public void add(final ServerInfo arg) throws UnknownHostException {
        ServerAddress lv = ServerAddress.parse(arg.address);
        final ClientConnection lv2 = ClientConnection.connect(InetAddress.getByName(lv.getAddress()), lv.getPort(), false);
        this.clientConnections.add(lv2);
        arg.label = new TranslatableText("multiplayer.status.pinging");
        arg.ping = -1L;
        arg.playerListSummary = null;
        lv2.setPacketListener(new ClientQueryPacketListener(){
            private boolean sentQuery;
            private boolean received;
            private long startTime;

            @Override
            public void onResponse(QueryResponseS2CPacket arg2) {
                if (this.received) {
                    lv2.disconnect(new TranslatableText("multiplayer.status.unrequested"));
                    return;
                }
                this.received = true;
                ServerMetadata lv = arg2.getServerMetadata();
                arg.label = lv.getDescription() != null ? lv.getDescription() : LiteralText.EMPTY;
                if (lv.getVersion() != null) {
                    arg.version = new LiteralText(lv.getVersion().getGameVersion());
                    arg.protocolVersion = lv.getVersion().getProtocolVersion();
                } else {
                    arg.version = new TranslatableText("multiplayer.status.old");
                    arg.protocolVersion = 0;
                }
                if (lv.getPlayers() != null) {
                    arg.playerCountLabel = MultiplayerServerListPinger.method_27647(lv.getPlayers().getOnlinePlayerCount(), lv.getPlayers().getPlayerLimit());
                    ArrayList list = Lists.newArrayList();
                    if (ArrayUtils.isNotEmpty((Object[])lv.getPlayers().getSample())) {
                        for (GameProfile gameProfile : lv.getPlayers().getSample()) {
                            list.add(new LiteralText(gameProfile.getName()));
                        }
                        if (lv.getPlayers().getSample().length < lv.getPlayers().getOnlinePlayerCount()) {
                            list.add(new TranslatableText("multiplayer.status.and_more", lv.getPlayers().getOnlinePlayerCount() - lv.getPlayers().getSample().length));
                        }
                        arg.playerListSummary = list;
                    }
                } else {
                    arg.playerCountLabel = new TranslatableText("multiplayer.status.unknown").formatted(Formatting.DARK_GRAY);
                }
                if (lv.getFavicon() != null) {
                    String string = lv.getFavicon();
                    if (string.startsWith("data:image/png;base64,")) {
                        arg.setIcon(string.substring("data:image/png;base64,".length()));
                    } else {
                        LOGGER.error("Invalid server icon (unknown format)");
                    }
                } else {
                    arg.setIcon(null);
                }
                this.startTime = Util.getMeasuringTimeMs();
                lv2.send(new QueryPingC2SPacket(this.startTime));
                this.sentQuery = true;
            }

            @Override
            public void onPong(QueryPongS2CPacket arg2) {
                long l = this.startTime;
                long m = Util.getMeasuringTimeMs();
                arg.ping = m - l;
                lv2.disconnect(new TranslatableText("multiplayer.status.finished"));
            }

            @Override
            public void onDisconnected(Text arg2) {
                if (!this.sentQuery) {
                    LOGGER.error("Can't ping {}: {}", (Object)arg.address, (Object)arg2.getString());
                    arg.label = new TranslatableText("multiplayer.status.cannot_connect").formatted(Formatting.DARK_RED);
                    arg.playerCountLabel = LiteralText.EMPTY;
                    MultiplayerServerListPinger.this.ping(arg);
                }
            }

            @Override
            public ClientConnection getConnection() {
                return lv2;
            }
        });
        try {
            lv2.send(new HandshakeC2SPacket(lv.getAddress(), lv.getPort(), NetworkState.STATUS));
            lv2.send(new QueryRequestC2SPacket());
        }
        catch (Throwable throwable) {
            LOGGER.error((Object)throwable);
        }
    }

    private void ping(final ServerInfo arg) {
        final ServerAddress lv = ServerAddress.parse(arg.address);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)ClientConnection.CLIENT_IO_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                channel.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler<ByteBuf>(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                        super.channelActive(channelHandlerContext);
                        ByteBuf byteBuf = Unpooled.buffer();
                        try {
                            byteBuf.writeByte(254);
                            byteBuf.writeByte(1);
                            byteBuf.writeByte(250);
                            char[] cs = "MC|PingHost".toCharArray();
                            byteBuf.writeShort(cs.length);
                            for (char c : cs) {
                                byteBuf.writeChar((int)c);
                            }
                            byteBuf.writeShort(7 + 2 * lv.getAddress().length());
                            byteBuf.writeByte(127);
                            cs = lv.getAddress().toCharArray();
                            byteBuf.writeShort(cs.length);
                            for (char d : cs) {
                                byteBuf.writeChar((int)d);
                            }
                            byteBuf.writeInt(lv.getPort());
                            channelHandlerContext.channel().writeAndFlush((Object)byteBuf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                        }
                        finally {
                            byteBuf.release();
                        }
                    }

                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        short s = byteBuf.readUnsignedByte();
                        if (s == 255) {
                            String string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                            String[] strings = (String[])Iterables.toArray((Iterable)ZERO_SPLITTER.split((CharSequence)string), String.class);
                            if ("\u00a71".equals(strings[0])) {
                                int i = MathHelper.parseInt(strings[1], 0);
                                String string2 = strings[2];
                                String string3 = strings[3];
                                int j = MathHelper.parseInt(strings[4], -1);
                                int k = MathHelper.parseInt(strings[5], -1);
                                arg.protocolVersion = -1;
                                arg.version = new LiteralText(string2);
                                arg.label = new LiteralText(string3);
                                arg.playerCountLabel = MultiplayerServerListPinger.method_27647(j, k);
                            }
                        }
                        channelHandlerContext.close();
                    }

                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                        channelHandlerContext.close();
                    }

                    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
                        this.channelRead0(channelHandlerContext, (ByteBuf)object);
                    }
                }});
            }
        })).channel(NioSocketChannel.class)).connect(lv.getAddress(), lv.getPort());
    }

    private static Text method_27647(int i, int j) {
        return new LiteralText(Integer.toString(i)).append(new LiteralText("/").formatted(Formatting.DARK_GRAY)).append(Integer.toString(j)).formatted(Formatting.GRAY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<ClientConnection> list = this.clientConnections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection lv = iterator.next();
                if (lv.isOpen()) {
                    lv.tick();
                    continue;
                }
                iterator.remove();
                lv.handleDisconnection();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancel() {
        List<ClientConnection> list = this.clientConnections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection lv = iterator.next();
                if (!lv.isOpen()) continue;
                iterator.remove();
                lv.disconnect(new TranslatableText("multiplayer.status.cancelled"));
            }
        }
    }
}

