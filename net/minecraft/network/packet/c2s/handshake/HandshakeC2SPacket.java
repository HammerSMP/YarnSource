/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.handshake;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerHandshakePacketListener;

public class HandshakeC2SPacket
implements Packet<ServerHandshakePacketListener> {
    private int protocolVersion;
    private String address;
    private int port;
    private NetworkState intendedState;

    public HandshakeC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public HandshakeC2SPacket(String string, int i, NetworkState arg) {
        this.protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
        this.address = string;
        this.port = i;
        this.intendedState = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.protocolVersion = arg.readVarInt();
        this.address = arg.readString(255);
        this.port = arg.readUnsignedShort();
        this.intendedState = NetworkState.byId(arg.readVarInt());
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.protocolVersion);
        arg.writeString(this.address);
        arg.writeShort(this.port);
        arg.writeVarInt(this.intendedState.getId());
    }

    @Override
    public void apply(ServerHandshakePacketListener arg) {
        arg.onHandshake(this);
    }

    public NetworkState getIntendedState() {
        return this.intendedState;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}

