/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class PlayerAbilitiesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;

    public PlayerAbilitiesS2CPacket() {
    }

    public PlayerAbilitiesS2CPacket(PlayerAbilities arg) {
        this.invulnerable = arg.invulnerable;
        this.flying = arg.flying;
        this.allowFlying = arg.allowFlying;
        this.creativeMode = arg.creativeMode;
        this.flySpeed = arg.getFlySpeed();
        this.walkSpeed = arg.getWalkSpeed();
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        byte b = buf.readByte();
        this.invulnerable = (b & 1) != 0;
        this.flying = (b & 2) != 0;
        this.allowFlying = (b & 4) != 0;
        this.creativeMode = (b & 8) != 0;
        this.flySpeed = buf.readFloat();
        this.walkSpeed = buf.readFloat();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        byte b = 0;
        if (this.invulnerable) {
            b = (byte)(b | true ? 1 : 0);
        }
        if (this.flying) {
            b = (byte)(b | 2);
        }
        if (this.allowFlying) {
            b = (byte)(b | 4);
        }
        if (this.creativeMode) {
            b = (byte)(b | 8);
        }
        buf.writeByte(b);
        buf.writeFloat(this.flySpeed);
        buf.writeFloat(this.walkSpeed);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerAbilities(this);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFlying() {
        return this.flying;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean allowFlying() {
        return this.allowFlying;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isCreativeMode() {
        return this.creativeMode;
    }

    @Environment(value=EnvType.CLIENT)
    public float getFlySpeed() {
        return this.flySpeed;
    }

    @Environment(value=EnvType.CLIENT)
    public float getWalkSpeed() {
        return this.walkSpeed;
    }
}

