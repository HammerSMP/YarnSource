/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerQueryNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ServerHandshakeNetworkHandler
implements ServerHandshakePacketListener {
    private static final Text IGNORING_STATUS_REQUEST_MESSAGE = new LiteralText("Ignoring status request");
    private final MinecraftServer server;
    private final ClientConnection connection;

    public ServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection arg) {
        this.server = minecraftServer;
        this.connection = arg;
    }

    @Override
    public void onHandshake(HandshakeC2SPacket arg) {
        switch (arg.getIntendedState()) {
            case LOGIN: {
                this.connection.setState(NetworkState.LOGIN);
                if (arg.getProtocolVersion() > SharedConstants.getGameVersion().getProtocolVersion()) {
                    TranslatableText lv = new TranslatableText("multiplayer.disconnect.outdated_server", SharedConstants.getGameVersion().getName());
                    this.connection.send(new LoginDisconnectS2CPacket(lv));
                    this.connection.disconnect(lv);
                    break;
                }
                if (arg.getProtocolVersion() < SharedConstants.getGameVersion().getProtocolVersion()) {
                    TranslatableText lv2 = new TranslatableText("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
                    this.connection.send(new LoginDisconnectS2CPacket(lv2));
                    this.connection.disconnect(lv2);
                    break;
                }
                this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
                break;
            }
            case STATUS: {
                if (this.server.acceptsStatusQuery()) {
                    this.connection.setState(NetworkState.STATUS);
                    this.connection.setPacketListener(new ServerQueryNetworkHandler(this.server, this.connection));
                    break;
                }
                this.connection.disconnect(IGNORING_STATUS_REQUEST_MESSAGE);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Invalid intention " + (Object)((Object)arg.getIntendedState()));
            }
        }
    }

    @Override
    public void onDisconnected(Text arg) {
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }
}

