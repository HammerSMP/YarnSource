/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

public class LoginSuccessS2CPacket
implements Packet<ClientLoginPacketListener> {
    private GameProfile profile;

    public LoginSuccessS2CPacket() {
    }

    public LoginSuccessS2CPacket(GameProfile gameProfile) {
        this.profile = gameProfile;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        int[] is = new int[4];
        for (int i = 0; i < is.length; ++i) {
            is[i] = arg.readInt();
        }
        UUID uUID = DynamicSerializableUuid.toUuid(is);
        String string = arg.readString(16);
        this.profile = new GameProfile(uUID, string);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        for (int i : DynamicSerializableUuid.toIntArray(this.profile.getId())) {
            arg.writeInt(i);
        }
        arg.writeString(this.profile.getName());
    }

    @Override
    public void apply(ClientLoginPacketListener arg) {
        arg.onLoginSuccess(this);
    }

    @Environment(value=EnvType.CLIENT)
    public GameProfile getProfile() {
        return this.profile;
    }
}

