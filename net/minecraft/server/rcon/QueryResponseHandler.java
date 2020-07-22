/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.BufferHelper;
import net.minecraft.server.rcon.DataStreamHelper;
import net.minecraft.server.rcon.RconBase;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryResponseHandler
extends RconBase {
    private static final Logger field_23963 = LogManager.getLogger();
    private long lastQueryTime;
    private final int queryPort;
    private final int port;
    private final int maxPlayerCount;
    private final String motd;
    private final String levelName;
    private DatagramSocket socket;
    private final byte[] packetBuffer = new byte[1460];
    private String ip;
    private String hostname;
    private final Map<SocketAddress, Query> queries;
    private final DataStreamHelper data;
    private long lastResponseTime;
    private final DedicatedServer field_23964;

    public QueryResponseHandler(DedicatedServer server) {
        super("Query Listener");
        this.field_23964 = server;
        this.queryPort = server.getProperties().queryPort;
        this.hostname = server.getHostname();
        this.port = server.getPort();
        this.motd = server.getMotd();
        this.maxPlayerCount = server.getMaxPlayerCount();
        this.levelName = server.getLevelName();
        this.lastResponseTime = 0L;
        this.ip = "0.0.0.0";
        if (this.hostname.isEmpty() || this.ip.equals(this.hostname)) {
            this.hostname = "0.0.0.0";
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                this.ip = inetAddress.getHostAddress();
            }
            catch (UnknownHostException unknownHostException) {
                field_23963.warn("Unable to determine local host IP, please set server-ip in server.properties", (Throwable)unknownHostException);
            }
        } else {
            this.ip = this.hostname;
        }
        this.data = new DataStreamHelper(1460);
        this.queries = Maps.newHashMap();
    }

    private void reply(byte[] buf, DatagramPacket datagramPacket) throws IOException {
        this.socket.send(new DatagramPacket(buf, buf.length, datagramPacket.getSocketAddress()));
    }

    private boolean handle(DatagramPacket packet) throws IOException {
        byte[] bs = packet.getData();
        int i = packet.getLength();
        SocketAddress socketAddress = packet.getSocketAddress();
        field_23963.debug("Packet len {} [{}]", (Object)i, (Object)socketAddress);
        if (3 > i || -2 != bs[0] || -3 != bs[1]) {
            field_23963.debug("Invalid packet [{}]", (Object)socketAddress);
            return false;
        }
        field_23963.debug("Packet '{}' [{}]", (Object)BufferHelper.toHex(bs[2]), (Object)socketAddress);
        switch (bs[2]) {
            case 9: {
                this.createQuery(packet);
                field_23963.debug("Challenge [{}]", (Object)socketAddress);
                return true;
            }
            case 0: {
                if (!this.isValidQuery(packet).booleanValue()) {
                    field_23963.debug("Invalid challenge [{}]", (Object)socketAddress);
                    return false;
                }
                if (15 == i) {
                    this.reply(this.createRulesReply(packet), packet);
                    field_23963.debug("Rules [{}]", (Object)socketAddress);
                    break;
                }
                DataStreamHelper lv = new DataStreamHelper(1460);
                lv.write(0);
                lv.write(this.getMessageBytes(packet.getSocketAddress()));
                lv.writeBytes(this.motd);
                lv.writeBytes("SMP");
                lv.writeBytes(this.levelName);
                lv.writeBytes(Integer.toString(this.field_23964.getCurrentPlayerCount()));
                lv.writeBytes(Integer.toString(this.maxPlayerCount));
                lv.writeShort((short)this.port);
                lv.writeBytes(this.ip);
                this.reply(lv.bytes(), packet);
                field_23963.debug("Status [{}]", (Object)socketAddress);
            }
        }
        return true;
    }

    private byte[] createRulesReply(DatagramPacket packet) throws IOException {
        String[] strings;
        long l = Util.getMeasuringTimeMs();
        if (l < this.lastResponseTime + 5000L) {
            byte[] bs = this.data.bytes();
            byte[] cs = this.getMessageBytes(packet.getSocketAddress());
            bs[1] = cs[0];
            bs[2] = cs[1];
            bs[3] = cs[2];
            bs[4] = cs[3];
            return bs;
        }
        this.lastResponseTime = l;
        this.data.reset();
        this.data.write(0);
        this.data.write(this.getMessageBytes(packet.getSocketAddress()));
        this.data.writeBytes("splitnum");
        this.data.write(128);
        this.data.write(0);
        this.data.writeBytes("hostname");
        this.data.writeBytes(this.motd);
        this.data.writeBytes("gametype");
        this.data.writeBytes("SMP");
        this.data.writeBytes("game_id");
        this.data.writeBytes("MINECRAFT");
        this.data.writeBytes("version");
        this.data.writeBytes(this.field_23964.getVersion());
        this.data.writeBytes("plugins");
        this.data.writeBytes(this.field_23964.getPlugins());
        this.data.writeBytes("map");
        this.data.writeBytes(this.levelName);
        this.data.writeBytes("numplayers");
        this.data.writeBytes("" + this.field_23964.getCurrentPlayerCount());
        this.data.writeBytes("maxplayers");
        this.data.writeBytes("" + this.maxPlayerCount);
        this.data.writeBytes("hostport");
        this.data.writeBytes("" + this.port);
        this.data.writeBytes("hostip");
        this.data.writeBytes(this.ip);
        this.data.write(0);
        this.data.write(1);
        this.data.writeBytes("player_");
        this.data.write(0);
        for (String string : strings = this.field_23964.getPlayerNames()) {
            this.data.writeBytes(string);
        }
        this.data.write(0);
        return this.data.bytes();
    }

    private byte[] getMessageBytes(SocketAddress socketAddress) {
        return this.queries.get(socketAddress).getMessageBytes();
    }

    private Boolean isValidQuery(DatagramPacket datagramPacket) {
        SocketAddress socketAddress = datagramPacket.getSocketAddress();
        if (!this.queries.containsKey(socketAddress)) {
            return false;
        }
        byte[] bs = datagramPacket.getData();
        return this.queries.get(socketAddress).getId() == BufferHelper.getIntBE(bs, 7, datagramPacket.getLength());
    }

    private void createQuery(DatagramPacket datagramPacket) throws IOException {
        Query lv = new Query(datagramPacket);
        this.queries.put(datagramPacket.getSocketAddress(), lv);
        this.reply(lv.getReplyBuf(), datagramPacket);
    }

    private void cleanUp() {
        if (!this.running) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        if (l < this.lastQueryTime + 30000L) {
            return;
        }
        this.lastQueryTime = l;
        this.queries.values().removeIf(arg -> arg.startedBefore(l));
    }

    @Override
    public void run() {
        field_23963.info("Query running on {}:{}", (Object)this.hostname, (Object)this.queryPort);
        this.lastQueryTime = Util.getMeasuringTimeMs();
        DatagramPacket datagramPacket = new DatagramPacket(this.packetBuffer, this.packetBuffer.length);
        try {
            while (this.running) {
                try {
                    this.socket.receive(datagramPacket);
                    this.cleanUp();
                    this.handle(datagramPacket);
                }
                catch (SocketTimeoutException socketTimeoutException) {
                    this.cleanUp();
                }
                catch (PortUnreachableException socketTimeoutException) {
                }
                catch (IOException iOException) {
                    this.handleIoException(iOException);
                }
            }
        }
        finally {
            field_23963.debug("closeSocket: {}:{}", (Object)this.hostname, (Object)this.queryPort);
            this.socket.close();
        }
    }

    @Override
    public void start() {
        if (this.running) {
            return;
        }
        if (0 >= this.queryPort || 65535 < this.queryPort) {
            field_23963.warn("Invalid query port {} found in server.properties (queries disabled)", (Object)this.queryPort);
            return;
        }
        if (this.initialize()) {
            super.start();
        }
    }

    private void handleIoException(Exception e) {
        if (!this.running) {
            return;
        }
        field_23963.warn("Unexpected exception", (Throwable)e);
        if (!this.initialize()) {
            field_23963.error("Failed to recover from exception, shutting down!");
            this.running = false;
        }
    }

    private boolean initialize() {
        try {
            this.socket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.hostname));
            this.socket.setSoTimeout(500);
            return true;
        }
        catch (Exception exception) {
            field_23963.warn("Unable to initialise query system on {}:{}", (Object)this.hostname, (Object)this.queryPort, (Object)exception);
            return false;
        }
    }

    static class Query {
        private final long startTime = new Date().getTime();
        private final int id;
        private final byte[] messageBytes;
        private final byte[] replyBuf;
        private final String message;

        public Query(DatagramPacket datagramPacket) {
            byte[] bs = datagramPacket.getData();
            this.messageBytes = new byte[4];
            this.messageBytes[0] = bs[3];
            this.messageBytes[1] = bs[4];
            this.messageBytes[2] = bs[5];
            this.messageBytes[3] = bs[6];
            this.message = new String(this.messageBytes, StandardCharsets.UTF_8);
            this.id = new Random().nextInt(0x1000000);
            this.replyBuf = String.format("\t%s%d\u0000", this.message, this.id).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean startedBefore(long lastQueryTime) {
            return this.startTime < lastQueryTime;
        }

        public int getId() {
            return this.id;
        }

        public byte[] getReplyBuf() {
            return this.replyBuf;
        }

        public byte[] getMessageBytes() {
            return this.messageBytes;
        }
    }
}

