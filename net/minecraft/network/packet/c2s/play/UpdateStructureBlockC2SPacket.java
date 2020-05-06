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
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class UpdateStructureBlockC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private StructureBlockBlockEntity.Action action;
    private StructureBlockMode mode;
    private String structureName;
    private BlockPos offset;
    private BlockPos size;
    private BlockMirror mirror;
    private BlockRotation rotation;
    private String metadata;
    private boolean ignoreEntities;
    private boolean showAir;
    private boolean showBoundingBox;
    private float integrity;
    private long seed;

    public UpdateStructureBlockC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateStructureBlockC2SPacket(BlockPos arg, StructureBlockBlockEntity.Action arg2, StructureBlockMode arg3, String string, BlockPos arg4, BlockPos arg5, BlockMirror arg6, BlockRotation arg7, String string2, boolean bl, boolean bl2, boolean bl3, float f, long l) {
        this.pos = arg;
        this.action = arg2;
        this.mode = arg3;
        this.structureName = string;
        this.offset = arg4;
        this.size = arg5;
        this.mirror = arg6;
        this.rotation = arg7;
        this.metadata = string2;
        this.ignoreEntities = bl;
        this.showAir = bl2;
        this.showBoundingBox = bl3;
        this.integrity = f;
        this.seed = l;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.pos = arg.readBlockPos();
        this.action = arg.readEnumConstant(StructureBlockBlockEntity.Action.class);
        this.mode = arg.readEnumConstant(StructureBlockMode.class);
        this.structureName = arg.readString(32767);
        this.offset = new BlockPos(MathHelper.clamp(arg.readByte(), -32, 32), MathHelper.clamp(arg.readByte(), -32, 32), MathHelper.clamp(arg.readByte(), -32, 32));
        this.size = new BlockPos(MathHelper.clamp(arg.readByte(), 0, 32), MathHelper.clamp(arg.readByte(), 0, 32), MathHelper.clamp(arg.readByte(), 0, 32));
        this.mirror = arg.readEnumConstant(BlockMirror.class);
        this.rotation = arg.readEnumConstant(BlockRotation.class);
        this.metadata = arg.readString(12);
        this.integrity = MathHelper.clamp(arg.readFloat(), 0.0f, 1.0f);
        this.seed = arg.readVarLong();
        byte i = arg.readByte();
        this.ignoreEntities = (i & 1) != 0;
        this.showAir = (i & 2) != 0;
        this.showBoundingBox = (i & 4) != 0;
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBlockPos(this.pos);
        arg.writeEnumConstant(this.action);
        arg.writeEnumConstant(this.mode);
        arg.writeString(this.structureName);
        arg.writeByte(this.offset.getX());
        arg.writeByte(this.offset.getY());
        arg.writeByte(this.offset.getZ());
        arg.writeByte(this.size.getX());
        arg.writeByte(this.size.getY());
        arg.writeByte(this.size.getZ());
        arg.writeEnumConstant(this.mirror);
        arg.writeEnumConstant(this.rotation);
        arg.writeString(this.metadata);
        arg.writeFloat(this.integrity);
        arg.writeVarLong(this.seed);
        int i = 0;
        if (this.ignoreEntities) {
            i |= 1;
        }
        if (this.showAir) {
            i |= 2;
        }
        if (this.showBoundingBox) {
            i |= 4;
        }
        arg.writeByte(i);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onStructureBlockUpdate(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public StructureBlockBlockEntity.Action getAction() {
        return this.action;
    }

    public StructureBlockMode getMode() {
        return this.mode;
    }

    public String getStructureName() {
        return this.structureName;
    }

    public BlockPos getOffset() {
        return this.offset;
    }

    public BlockPos getSize() {
        return this.size;
    }

    public BlockMirror getMirror() {
        return this.mirror;
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public boolean getIgnoreEntities() {
        return this.ignoreEntities;
    }

    public boolean shouldShowAir() {
        return this.showAir;
    }

    public boolean shouldShowBoundingBox() {
        return this.showBoundingBox;
    }

    public float getIntegrity() {
        return this.integrity;
    }

    public long getSeed() {
        return this.seed;
    }
}

