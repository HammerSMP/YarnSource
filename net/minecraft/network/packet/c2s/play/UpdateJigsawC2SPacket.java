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
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UpdateJigsawC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private Identifier attachmentType;
    private Identifier targetPool;
    private Identifier pool;
    private String finalState;
    private JigsawBlockEntity.Joint jointType;

    public UpdateJigsawC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateJigsawC2SPacket(BlockPos arg, Identifier arg2, Identifier arg3, Identifier arg4, String string, JigsawBlockEntity.Joint arg5) {
        this.pos = arg;
        this.attachmentType = arg2;
        this.targetPool = arg3;
        this.pool = arg4;
        this.finalState = string;
        this.jointType = arg5;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.pos = arg.readBlockPos();
        this.attachmentType = arg.readIdentifier();
        this.targetPool = arg.readIdentifier();
        this.pool = arg.readIdentifier();
        this.finalState = arg.readString(32767);
        this.jointType = JigsawBlockEntity.Joint.byName(arg.readString(32767)).orElse(JigsawBlockEntity.Joint.ALIGNED);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBlockPos(this.pos);
        arg.writeIdentifier(this.attachmentType);
        arg.writeIdentifier(this.targetPool);
        arg.writeIdentifier(this.pool);
        arg.writeString(this.finalState);
        arg.writeString(this.jointType.asString());
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onJigsawUpdate(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Identifier getAttachmentType() {
        return this.attachmentType;
    }

    public Identifier getTargetPool() {
        return this.targetPool;
    }

    public Identifier getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public JigsawBlockEntity.Joint getJointType() {
        return this.jointType;
    }
}

