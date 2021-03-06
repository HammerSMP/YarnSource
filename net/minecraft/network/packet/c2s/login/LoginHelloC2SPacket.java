/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.network.packet.c2s.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;

public class LoginHelloC2SPacket
implements Packet<ServerLoginPacketListener> {
    private GameProfile profile;

    public LoginHelloC2SPacket() {
    }

    public LoginHelloC2SPacket(GameProfile profile) {
        this.profile = profile;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.profile = new GameProfile(null, buf.readString(16));
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeString(this.profile.getName());
    }

    @Override
    public void apply(ServerLoginPacketListener arg) {
        arg.onHello(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}

