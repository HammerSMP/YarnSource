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
    private float fovModifier;

    public PlayerAbilitiesS2CPacket() {
    }

    public PlayerAbilitiesS2CPacket(PlayerAbilities arg) {
        this.setInvulnerable(arg.invulnerable);
        this.setFlying(arg.flying);
        this.setAllowFlying(arg.allowFlying);
        this.setCreativeMode(arg.creativeMode);
        this.setFlySpeed(arg.getFlySpeed());
        this.setFovModifier(arg.getWalkSpeed());
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        byte b = arg.readByte();
        this.setInvulnerable((b & 1) > 0);
        this.setFlying((b & 2) > 0);
        this.setAllowFlying((b & 4) > 0);
        this.setCreativeMode((b & 8) > 0);
        this.setFlySpeed(arg.readFloat());
        this.setFovModifier(arg.readFloat());
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        byte b = 0;
        if (this.isInvulnerable()) {
            b = (byte)(b | true ? 1 : 0);
        }
        if (this.isFlying()) {
            b = (byte)(b | 2);
        }
        if (this.allowFlying()) {
            b = (byte)(b | 4);
        }
        if (this.isCreativeMode()) {
            b = (byte)(b | 8);
        }
        arg.writeByte(b);
        arg.writeFloat(this.flySpeed);
        arg.writeFloat(this.fovModifier);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerAbilities(this);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean bl) {
        this.invulnerable = bl;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public void setFlying(boolean bl) {
        this.flying = bl;
    }

    public boolean allowFlying() {
        return this.allowFlying;
    }

    public void setAllowFlying(boolean bl) {
        this.allowFlying = bl;
    }

    public boolean isCreativeMode() {
        return this.creativeMode;
    }

    public void setCreativeMode(boolean bl) {
        this.creativeMode = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public float getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(float f) {
        this.flySpeed = f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getFovModifier() {
        return this.fovModifier;
    }

    public void setFovModifier(float f) {
        this.fovModifier = f;
    }
}

