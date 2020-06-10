/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerInteractEntityC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int entityId;
    private InteractionType type;
    private Vec3d hitPos;
    private Hand hand;
    private boolean field_25660;

    public PlayerInteractEntityC2SPacket() {
    }

    public PlayerInteractEntityC2SPacket(Entity arg) {
        this.entityId = arg.getEntityId();
        this.type = InteractionType.ATTACK;
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerInteractEntityC2SPacket(Entity arg, Hand arg2, boolean bl) {
        this.entityId = arg.getEntityId();
        this.type = InteractionType.INTERACT;
        this.hand = arg2;
        this.field_25660 = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerInteractEntityC2SPacket(Entity arg, Hand arg2, Vec3d arg3, boolean bl) {
        this.entityId = arg.getEntityId();
        this.type = InteractionType.INTERACT_AT;
        this.hand = arg2;
        this.hitPos = arg3;
        this.field_25660 = bl;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entityId = arg.readVarInt();
        this.type = arg.readEnumConstant(InteractionType.class);
        if (this.type == InteractionType.INTERACT_AT) {
            this.hitPos = new Vec3d(arg.readFloat(), arg.readFloat(), arg.readFloat());
        }
        if (this.type == InteractionType.INTERACT || this.type == InteractionType.INTERACT_AT) {
            this.hand = arg.readEnumConstant(Hand.class);
            this.field_25660 = arg.readBoolean();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entityId);
        arg.writeEnumConstant(this.type);
        if (this.type == InteractionType.INTERACT_AT) {
            arg.writeFloat((float)this.hitPos.x);
            arg.writeFloat((float)this.hitPos.y);
            arg.writeFloat((float)this.hitPos.z);
        }
        if (this.type == InteractionType.INTERACT || this.type == InteractionType.INTERACT_AT) {
            arg.writeEnumConstant(this.hand);
            arg.writeBoolean(this.field_25660);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onPlayerInteractEntity(this);
    }

    @Nullable
    public Entity getEntity(World arg) {
        return arg.getEntityById(this.entityId);
    }

    public InteractionType getType() {
        return this.type;
    }

    public Hand getHand() {
        return this.hand;
    }

    public Vec3d getHitPosition() {
        return this.hitPos;
    }

    public boolean method_30007() {
        return this.field_25660;
    }

    public static enum InteractionType {
        INTERACT,
        ATTACK,
        INTERACT_AT;

    }
}

