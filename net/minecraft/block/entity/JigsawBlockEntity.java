/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
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
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
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

    public void setAttachmentType(Identifier value) {
        this.name = value;
    }

    public void setTargetPool(Identifier target) {
        this.target = target;
    }

    public void setPool(Identifier pool) {
        this.pool = pool;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("name", this.name.toString());
        tag.putString("target", this.target.toString());
        tag.putString("pool", this.pool.toString());
        tag.putString("final_state", this.finalState);
        tag.putString("joint", this.joint.asString());
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.name = new Identifier(tag.getString("name"));
        this.target = new Identifier(tag.getString("target"));
        this.pool = new Identifier(tag.getString("pool"));
        this.finalState = tag.getString("final_state");
        this.joint = Joint.byName(tag.getString("joint")).orElseGet(() -> JigsawBlock.getFacing(state).getAxis().isHorizontal() ? Joint.ALIGNED : Joint.ROLLABLE);
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

    public void generate(ServerWorld world, int maxDepth, boolean keepJigsaws) {
        ChunkGenerator lv = world.getChunkManager().getChunkGenerator();
        StructureManager lv2 = world.getStructureManager();
        StructureAccessor lv3 = world.getStructureAccessor();
        Random random = world.getRandom();
        BlockPos lv4 = this.getPos();
        ArrayList list = Lists.newArrayList();
        Structure lv5 = new Structure();
        lv5.saveFromWorld(world, lv4, new BlockPos(1, 1, 1), false, null);
        SinglePoolElement lv6 = new SinglePoolElement(lv5);
        PoolStructurePiece lv7 = new PoolStructurePiece(lv2, lv6, lv4, 1, BlockRotation.NONE, new BlockBox(lv4, lv4));
        StructurePoolBasedGenerator.method_27230(world.getRegistryManager(), lv7, maxDepth, PoolStructurePiece::new, lv, lv2, list, random);
        for (PoolStructurePiece lv8 : list) {
            lv8.method_27236(world, lv3, lv, random, BlockBox.infinite(), lv4, keepJigsaws);
        }
    }

    public static enum Joint implements StringIdentifiable
    {
        ROLLABLE("rollable"),
        ALIGNED("aligned");

        private final String name;

        private Joint(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static Optional<Joint> byName(String name) {
            return Arrays.stream(Joint.values()).filter(arg -> arg.asString().equals(name)).findFirst();
        }
    }
}

