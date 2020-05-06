/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.server.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class IntegratedServerHandshakeNetworkHandler
implements ServerHandshakePacketListener {
    private final MinecraftServer server;
    private final ClientConnection connection;

    public IntegratedServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection arg) {
        this.server = minecraftServer;
        this.connection = arg;
    }

    @Override
    public void onHandshake(HandshakeC2SPacket arg) {
        this.connection.setState(arg.getIntendedState());
        this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
    }

    @Override
    public void onDisconnected(Text arg) {
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }
}

