/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.DefaultEventLoopGroup
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.epoll.Epoll
 *  io.netty.channel.epoll.EpollEventLoopGroup
 *  io.netty.channel.epoll.EpollSocketChannel
 *  io.netty.channel.local.LocalChannel
 *  io.netty.channel.local.LocalServerChannel
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.handler.timeout.ReadTimeoutHandler
 *  io.netty.handler.timeout.TimeoutException
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDeflater;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.PacketEncoderException;
import net.minecraft.network.PacketInflater;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ClientConnection
extends SimpleChannelInboundHandler<Packet<?>> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker MARKER_NETWORK = MarkerManager.getMarker((String)"NETWORK");
    public static final Marker MARKER_NETWORK_PACKETS = MarkerManager.getMarker((String)"NETWORK_PACKETS", (Marker)MARKER_NETWORK);
    public static final AttributeKey<NetworkState> ATTR_KEY_PROTOCOL = AttributeKey.valueOf((String)"protocol");
    public static final Lazy<NioEventLoopGroup> CLIENT_IO_GROUP = new Lazy<NioEventLoopGroup>(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()));
    public static final Lazy<EpollEventLoopGroup> CLIENT_IO_GROUP_EPOLL = new Lazy<EpollEventLoopGroup>(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build()));
    public static final Lazy<DefaultEventLoopGroup> CLIENT_IO_GROUP_LOCAL = new Lazy<DefaultEventLoopGroup>(() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build()));
    private final NetworkSide side;
    private final Queue<QueuedPacket> packetQueue = Queues.newConcurrentLinkedQueue();
    private Channel channel;
    private SocketAddress address;
    private PacketListener packetListener;
    private Text disconnectReason;
    private boolean encrypted;
    private boolean disconnected;
    private int packetsReceivedCounter;
    private int packetsSentCounter;
    private float avgPacketsReceived;
    private float avgPacketsSent;
    private int ticks;
    private boolean errored;

    public ClientConnection(NetworkSide arg) {
        this.side = arg;
    }

    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);
        this.channel = channelHandlerContext.channel();
        this.address = this.channel.remoteAddress();
        try {
            this.setState(NetworkState.HANDSHAKING);
        }
        catch (Throwable throwable) {
            LOGGER.fatal((Object)throwable);
        }
    }

    public void setState(NetworkState arg) {
        this.channel.attr(ATTR_KEY_PROTOCOL).set((Object)arg);
        this.channel.config().setAutoRead(true);
        LOGGER.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        this.disconnect(new TranslatableText("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        if (throwable instanceof PacketEncoderException) {
            LOGGER.debug("Skipping packet due to errors", throwable.getCause());
            return;
        }
        boolean bl = !this.errored;
        this.errored = true;
        if (!this.channel.isOpen()) {
            return;
        }
        if (throwable instanceof TimeoutException) {
            LOGGER.debug("Timeout", throwable);
            this.disconnect(new TranslatableText("disconnect.timeout"));
        } else {
            TranslatableText lv = new TranslatableText("disconnect.genericReason", "Internal Exception: " + throwable);
            if (bl) {
                LOGGER.debug("Failed to sent packet", throwable);
                this.send(new DisconnectS2CPacket(lv), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> this.disconnect(lv)));
                this.disableAutoRead();
            } else {
                LOGGER.debug("Double fault", throwable);
                this.disconnect(lv);
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> arg) throws Exception {
        if (this.channel.isOpen()) {
            try {
                ClientConnection.handlePacket(arg, this.packetListener);
            }
            catch (OffThreadException offThreadException) {
                // empty catch block
            }
            ++this.packetsReceivedCounter;
        }
    }

    private static <T extends PacketListener> void handlePacket(Packet<T> arg, PacketListener arg2) {
        arg.apply(arg2);
    }

    public void setPacketListener(PacketListener arg) {
        Validate.notNull((Object)arg, (String)"packetListener", (Object[])new Object[0]);
        this.packetListener = arg;
    }

    public void send(Packet<?> arg) {
        this.send(arg, null);
    }

    public void send(Packet<?> arg, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        if (this.isOpen()) {
            this.sendQueuedPackets();
            this.sendImmediately(arg, genericFutureListener);
        } else {
            this.packetQueue.add(new QueuedPacket(arg, genericFutureListener));
        }
    }

    private void sendImmediately(Packet<?> arg, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        NetworkState lv = NetworkState.getPacketHandlerState(arg);
        NetworkState lv2 = (NetworkState)((Object)this.channel.attr(ATTR_KEY_PROTOCOL).get());
        ++this.packetsSentCounter;
        if (lv2 != lv) {
            LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (lv != lv2) {
                this.setState(lv);
            }
            ChannelFuture channelFuture = this.channel.writeAndFlush(arg);
            if (genericFutureListener != null) {
                channelFuture.addListener(genericFutureListener);
            }
            channelFuture.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(() -> {
                if (lv != lv2) {
                    this.setState(lv);
                }
                ChannelFuture channelFuture = this.channel.writeAndFlush((Object)arg);
                if (genericFutureListener != null) {
                    channelFuture.addListener(genericFutureListener);
                }
                channelFuture.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendQueuedPackets() {
        if (this.channel == null || !this.channel.isOpen()) {
            return;
        }
        Queue<QueuedPacket> queue = this.packetQueue;
        synchronized (queue) {
            QueuedPacket lv;
            while ((lv = this.packetQueue.poll()) != null) {
                this.sendImmediately(lv.packet, (GenericFutureListener<? extends Future<? super Void>>)lv.callback);
            }
        }
    }

    public void tick() {
        this.sendQueuedPackets();
        if (this.packetListener instanceof ServerLoginNetworkHandler) {
            ((ServerLoginNetworkHandler)this.packetListener).tick();
        }
        if (this.packetListener instanceof ServerPlayNetworkHandler) {
            ((ServerPlayNetworkHandler)this.packetListener).tick();
        }
        if (this.channel != null) {
            this.channel.flush();
        }
        if (this.ticks++ % 20 == 0) {
            this.avgPacketsSent = this.avgPacketsSent * 0.75f + (float)this.packetsSentCounter * 0.25f;
            this.avgPacketsReceived = this.avgPacketsReceived * 0.75f + (float)this.packetsReceivedCounter * 0.25f;
            this.packetsSentCounter = 0;
            this.packetsReceivedCounter = 0;
        }
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    public void disconnect(Text arg) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectReason = arg;
        }
    }

    public boolean isLocal() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    @Environment(value=EnvType.CLIENT)
    public static ClientConnection connect(InetAddress inetAddress, int i, boolean bl) {
        Lazy<NioEventLoopGroup> lv3;
        Class<NioSocketChannel> class2;
        final ClientConnection lv = new ClientConnection(NetworkSide.CLIENTBOUND);
        if (Epoll.isAvailable() && bl) {
            Class<EpollSocketChannel> class_ = EpollSocketChannel.class;
            Lazy<EpollEventLoopGroup> lv2 = CLIENT_IO_GROUP_EPOLL;
        } else {
            class2 = NioSocketChannel.class;
            lv3 = CLIENT_IO_GROUP;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)lv3.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("splitter", (ChannelHandler)new SplitterHandler()).addLast("decoder", (ChannelHandler)new DecoderHandler(NetworkSide.CLIENTBOUND)).addLast("prepender", (ChannelHandler)new SizePrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(NetworkSide.SERVERBOUND)).addLast("packet_handler", (ChannelHandler)lv);
            }
        })).channel(class2)).connect(inetAddress, i).syncUninterruptibly();
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static ClientConnection connectLocal(SocketAddress socketAddress) {
        final ClientConnection lv = new ClientConnection(NetworkSide.CLIENTBOUND);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)CLIENT_IO_GROUP_LOCAL.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast("packet_handler", (ChannelHandler)lv);
            }
        })).channel(LocalChannel.class)).connect(socketAddress).syncUninterruptibly();
        return lv;
    }

    public void setupEncryption(SecretKey secretKey) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", (ChannelHandler)new PacketDecryptor(NetworkEncryptionUtils.cipherFromKey(2, secretKey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", (ChannelHandler)new PacketEncryptor(NetworkEncryptionUtils.cipherFromKey(1, secretKey)));
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isEncrypted() {
        return this.encrypted;
    }

    public boolean isOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean hasChannel() {
        return this.channel == null;
    }

    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    @Nullable
    public Text getDisconnectReason() {
        return this.disconnectReason;
    }

    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    public void setCompressionThreshold(int i) {
        if (i >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
                ((PacketInflater)this.channel.pipeline().get("decompress")).setCompressionThreshold(i);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", (ChannelHandler)new PacketInflater(i));
            }
            if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
                ((PacketDeflater)this.channel.pipeline().get("compress")).setCompressionThreshold(i);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", (ChannelHandler)new PacketDeflater(i));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
                this.channel.pipeline().remove("decompress");
            }
            if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void handleDisconnection() {
        if (this.channel == null || this.channel.isOpen()) {
            return;
        }
        if (this.disconnected) {
            LOGGER.warn("handleDisconnection() called twice");
        } else {
            this.disconnected = true;
            if (this.getDisconnectReason() != null) {
                this.getPacketListener().onDisconnected(this.getDisconnectReason());
            } else if (this.getPacketListener() != null) {
                this.getPacketListener().onDisconnected(new TranslatableText("multiplayer.disconnect.generic"));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getAveragePacketsReceived() {
        return this.avgPacketsReceived;
    }

    @Environment(value=EnvType.CLIENT)
    public float getAveragePacketsSent() {
        return this.avgPacketsSent;
    }

    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        this.channelRead0(channelHandlerContext, (Packet)object);
    }

    static class QueuedPacket {
        private final Packet<?> packet;
        @Nullable
        private final GenericFutureListener<? extends Future<? super Void>> callback;

        public QueuedPacket(Packet<?> arg, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
            this.packet = arg;
            this.callback = genericFutureListener;
        }
    }
}

