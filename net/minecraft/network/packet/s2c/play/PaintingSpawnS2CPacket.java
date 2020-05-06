/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class PaintingSpawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private UUID uuid;
    private BlockPos pos;
    private Direction facing;
    private int motiveId;

    public PaintingSpawnS2CPacket() {
    }

    public PaintingSpawnS2CPacket(PaintingEntity arg) {
        this.id = arg.getEntityId();
        this.uuid = arg.getUuid();
        this.pos = arg.getDecorationBlockPos();
        this.facing = arg.getHorizontalFacing();
        this.motiveId = Registry.PAINTING_MOTIVE.getRawId(arg.motive);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.uuid = arg.readUuid();
        this.motiveId = arg.readVarInt();
        this.pos = arg.readBlockPos();
        this.facing = Direction.fromHorizontal(arg.readUnsignedByte());
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeUuid(this.uuid);
        arg.writeVarInt(this.motiveId);
        arg.writeBlockPos(this.pos);
        arg.writeByte(this.facing.getHorizontal());
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPaintingSpawn(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public UUID getPaintingUuid() {
        return this.uuid;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public Direction getFacing() {
        return this.facing;
    }

    @Environment(value=EnvType.CLIENT)
    public PaintingMotive getMotive() {
        return Registry.PAINTING_MOTIVE.get(this.motiveId);
    }
}

