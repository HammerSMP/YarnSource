/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class JigsawBlockEntity
extends BlockEntity {
    private Identifier name = new Identifier("empty");
    private Identifier target = new Identifier("empty");
    private Identifier pool = new Identifier("empty");
    private Joint joint = Joint.ROLLABLE;
    private String finalState = "minecraft:air";

    public JigsawBlockEntity(BlockEntityType<?> arg) {
        super(arg);
    }

    public JigsawBlockEntity() {
        this(BlockEntityType.JIGSAW);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getTarget() {
        return this.target;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getPool() {
        return this.pool;
    }

    @Environment(value=EnvType.CLIENT)
    public String getFinalState() {
        return this.finalState;
    }

    @Environment(value=EnvType.CLIENT)
    public Joint getJoint() {
        return this.joint;
    }

    public void setAttachmentType(Identifier arg) {
        this.name = arg;
    }

    public void setTargetPool(Identifier arg) {
        this.target = arg;
    }

    public void setPool(Identifier arg) {
        this.pool = arg;
    }

    public void setFinalState(String string) {
        this.finalState = string;
    }

    public void setJoint(Joint arg) {
        this.joint = arg;
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        arg.putString("name", this.name.toString());
        arg.putString("target", this.target.toString());
        arg.putString("pool", this.pool.toString());
        arg.putString("final_state", this.finalState);
        arg.putString("joint", this.joint.asString());
        return arg;
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.name = new Identifier(arg2.getString("name"));
        this.target = new Identifier(arg2.getString("target"));
        this.pool = new Identifier(arg2.getString("pool"));
        this.finalState = arg2.getString("final_state");
        this.joint = Joint.byName(arg2.getString("joint")).orElseGet(() -> JigsawBlock.getFacing(arg).getAxis().isHorizontal() ? Joint.ALIGNED : Joint.ROLLABLE);
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 12, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public void generate(ServerWorld arg, int i, boolean bl) {
        ChunkGenerator lv = arg.getChunkManager().getChunkGenerator();
        StructureManager lv2 = arg.getStructureManager();
        StructureAccessor lv3 = arg.getStructureAccessor();
        Random random = arg.getRandom();
        BlockPos lv4 = this.getPos();
        ArrayList list = Lists.newArrayList();
        Structure lv5 = new Structure();
        lv5.saveFromWorld(arg, lv4, new BlockPos(1, 1, 1), false, null);
        SinglePoolElement lv6 = new SinglePoolElement(lv5, (List<StructureProcessor>)ImmutableList.of(), StructurePool.Projection.RIGID);
        RuntimeStructurePiece lv7 = new RuntimeStructurePiece(lv2, lv6, lv4, 1, BlockRotation.NONE, new BlockBox(lv4, lv4));
        StructurePoolBasedGenerator.method_27230(lv7, i, RuntimeStructurePiece::new, lv, lv2, list, random);
        for (PoolStructurePiece lv8 : list) {
            lv8.method_27236(arg, lv3, lv, random, BlockBox.infinite(), lv4, bl);
        }
    }

    public static final class RuntimeStructurePiece
    extends PoolStructurePiece {
        public RuntimeStructurePiece(StructureManager arg, StructurePoolElement arg2, BlockPos arg3, int i, BlockRotation arg4, BlockBox arg5) {
            super(StructurePieceType.RUNTIME, arg, arg2, arg3, i, arg4, arg5);
        }

        public RuntimeStructurePiece(StructureManager arg, CompoundTag arg2) {
            super(arg, arg2, StructurePieceType.RUNTIME);
        }
    }

    public static enum Joint implements StringIdentifiable
    {
        ROLLABLE("rollable"),
        ALIGNED("aligned");

        private final String name;

        private Joint(String string2) {
            this.name = string2;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static Optional<Joint> byName(String string) {
            return Arrays.stream(Joint.values()).filter(arg -> arg.asString().equals(string)).findFirst();
        }
    }
}

