/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class PlayerInputC2SPacket
implements Packet<ServerPlayPacketListener> {
    private float sideways;
    private float forward;
    private boolean jumping;
    private boolean sneaking;

    public PlayerInputC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerInputC2SPacket(float f, float g, boolean bl, boolean bl2) {
        this.sideways = f;
        this.forward = g;
        this.jumping = bl;
        this.sneaking = bl2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.sideways = arg.readFloat();
        this.forward = arg.readFloat();
        byte b = arg.readByte();
        this.jumping = (b & 1) > 0;
        this.sneaking = (b & 2) > 0;
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeFloat(this.sideways);
        arg.writeFloat(this.forward);
        byte b = 0;
        if (this.jumping) {
            b = (byte)(b | true ? 1 : 0);
        }
        if (this.sneaking) {
            b = (byte)(b | 2);
        }
        arg.writeByte(b);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onPlayerInput(this);
    }

    public float getSideways() {
        return this.sideways;
    }

    public float getForward() {
        return this.forward;
    }

    public boolean isJumping() {
        return this.jumping;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }
}

