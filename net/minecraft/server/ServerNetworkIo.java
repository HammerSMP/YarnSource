/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  io.netty.bootstrap.ServerBootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.epoll.Epoll
 *  io.netty.channel.epoll.EpollEventLoopGroup
 *  io.netty.channel.epoll.EpollServerSocketChannel
 *  io.netty.channel.local.LocalAddress
 *  io.netty.channel.local.LocalServerChannel
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.nio.NioServerSocketChannel
 *  io.netty.handler.timeout.ReadTimeoutHandler
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.LegacyQueryHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.IntegratedServerHandshakeNetworkHandler;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Lazy;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerNetworkIo {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Lazy<NioEventLoopGroup> DEFAULT_CHANNEL = new Lazy<NioEventLoopGroup>(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build()));
    public static final Lazy<EpollEventLoopGroup> EPOLL_CHANNEL = new Lazy<EpollEventLoopGroup>(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build()));
    private final MinecraftServer server;
    public volatile boolean active;
    private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
    private final List<ClientConnection> connections = Collections.synchronizedList(Lists.newArrayList());

    public ServerNetworkIo(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        this.active = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void bind(@Nullable InetAddress inetAddress, int i) throws IOException {
        List<ChannelFuture> list = this.channels;
        synchronized (list) {
            Lazy<NioEventLoopGroup> lv4;
            Class<NioServerSocketChannel> lv3;
            if (Epoll.isAvailable() && this.server.isUsingNativeTransport()) {
                Class<EpollServerSocketChannel> lv = EpollServerSocketChannel.class;
                Lazy<EpollEventLoopGroup> lv2 = EPOLL_CHANNEL;
                LOGGER.info("Using epoll channel type");
            } else {
                lv3 = NioServerSocketChannel.class;
                lv4 = DEFAULT_CHANNEL;
                LOGGER.info("Using default channel type");
            }
            this.channels.add(((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(lv3)).childHandler((ChannelHandler)new ChannelInitializer<Channel>(){

                protected void initChannel(Channel channel) throws Exception {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                    }
                    catch (ChannelException channelException) {
                        // empty catch block
                    }
                    channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("legacy_query", (ChannelHandler)new LegacyQueryHandler(ServerNetworkIo.this)).addLast("splitter", (ChannelHandler)new SplitterHandler()).addLast("decoder", (ChannelHandler)new DecoderHandler(NetworkSide.SERVERBOUND)).addLast("prepender", (ChannelHandler)new SizePrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(NetworkSide.CLIENTBOUND));
                    ClientConnection lv = new ClientConnection(NetworkSide.SERVERBOUND);
                    ServerNetworkIo.this.connections.add(lv);
                    channel.pipeline().addLast("packet_handler", (ChannelHandler)lv);
                    lv.setPacketListener(new ServerHandshakeNetworkHandler(ServerNetworkIo.this.server, lv));
                }
            }).group((EventLoopGroup)lv4.get()).localAddress(inetAddress, i)).bind().syncUninterruptibly());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    @Environment(value=EnvType.CLIENT)
    public SocketAddress bindLocal() {
        void channelFuture2;
        List<ChannelFuture> list = this.channels;
        synchronized (list) {
            ChannelFuture channelFuture = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(LocalServerChannel.class)).childHandler((ChannelHandler)new ChannelInitializer<Channel>(){

                protected void initChannel(Channel channel) throws Exception {
                    ClientConnection lv = new ClientConnection(NetworkSide.SERVERBOUND);
                    lv.setPacketListener(new IntegratedServerHandshakeNetworkHandler(ServerNetworkIo.this.server, lv));
                    ServerNetworkIo.this.connections.add(lv);
                    channel.pipeline().addLast("packet_handler", (ChannelHandler)lv);
                }
            }).group((EventLoopGroup)DEFAULT_CHANNEL.get()).localAddress((SocketAddress)LocalAddress.ANY)).bind().syncUninterruptibly();
            this.channels.add(channelFuture);
        }
        return channelFuture2.channel().localAddress();
    }

    public void stop() {
        this.active = false;
        for (ChannelFuture channelFuture : this.channels) {
            try {
                channelFuture.channel().close().sync();
            }
            catch (InterruptedException interruptedException) {
                LOGGER.error("Interrupted whilst closing channel");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<ClientConnection> list = this.connections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.connections.iterator();
            while (iterator.hasNext()) {
                ClientConnection lv = iterator.next();
                if (lv.hasChannel()) continue;
                if (lv.isOpen()) {
                    try {
                        lv.tick();
                    }
                    catch (Exception exception) {
                        if (lv.isLocal()) {
                            CrashReport lv2 = CrashReport.create(exception, "Ticking memory connection");
                            CrashReportSection lv3 = lv2.addElement("Ticking connection");
                            lv3.add("Connection", lv::toString);
                            throw new CrashException(lv2);
                        }
                        LOGGER.warn("Failed to handle packet for {}", (Object)lv.getAddress(), (Object)exception);
                        LiteralText lv4 = new LiteralText("Internal server error");
                        lv.send(new DisconnectS2CPacket(lv4), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> lv.disconnect(lv4)));
                        lv.disableAutoRead();
                    }
                    continue;
                }
                iterator.remove();
                lv.handleDisconnection();
            }
        }
    }

    public MinecraftServer getServer() {
        return this.server;
    }
}

