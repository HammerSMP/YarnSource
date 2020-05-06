/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class PoolStructurePiece
extends StructurePiece {
    protected final StructurePoolElement poolElement;
    protected BlockPos pos;
    private final int groundLevelDelta;
    protected final BlockRotation rotation;
    private final List<JigsawJunction> junctions = Lists.newArrayList();
    private final StructureManager structureManager;

    public PoolStructurePiece(StructurePieceType arg, StructureManager arg2, StructurePoolElement arg3, BlockPos arg4, int i, BlockRotation arg5, BlockBox arg6) {
        super(arg, 0);
        this.structureManager = arg2;
        this.poolElement = arg3;
        this.pos = arg4;
        this.groundLevelDelta = i;
        this.rotation = arg5;
        this.boundingBox = arg6;
    }

    public PoolStructurePiece(StructureManager arg2, CompoundTag arg22, StructurePieceType arg3) {
        super(arg3, arg22);
        this.structureManager = arg2;
        this.pos = new BlockPos(arg22.getInt("PosX"), arg22.getInt("PosY"), arg22.getInt("PosZ"));
        this.groundLevelDelta = arg22.getInt("ground_level_delta");
        this.poolElement = DynamicDeserializer.deserialize(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)arg22.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE);
        this.rotation = BlockRotation.valueOf(arg22.getString("rotation"));
        this.boundingBox = this.poolElement.getBoundingBox(arg2, this.pos, this.rotation);
        ListTag lv = arg22.getList("junctions", 10);
        this.junctions.clear();
        lv.forEach(arg -> this.junctions.add(JigsawJunction.deserialize(new Dynamic((DynamicOps)NbtOps.INSTANCE, arg))));
    }

    @Override
    protected void toNbt(CompoundTag arg) {
        arg.putInt("PosX", this.pos.getX());
        arg.putInt("PosY", this.pos.getY());
        arg.putInt("PosZ", this.pos.getZ());
        arg.putInt("ground_level_delta", this.groundLevelDelta);
        arg.put("pool_element", (Tag)this.poolElement.toDynamic(NbtOps.INSTANCE).getValue());
        arg.putString("rotation", this.rotation.name());
        ListTag lv = new ListTag();
        for (JigsawJunction lv2 : this.junctions) {
            lv.add(lv2.serialize(NbtOps.INSTANCE).getValue());
        }
        arg.put("junctions", lv);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
        return this.method_27236(arg, arg2, arg3, random, arg4, arg6, false);
    }

    public boolean method_27236(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, BlockPos arg5, boolean bl) {
        return this.poolElement.generate(this.structureManager, arg, arg2, arg3, this.pos, arg5, this.rotation, arg4, random, bl);
    }

    @Override
    public void translate(int i, int j, int k) {
        super.translate(i, j, k);
        this.pos = this.pos.add(i, j, k);
    }

    @Override
    public BlockRotation getRotation() {
        return this.rotation;
    }

    public String toString() {
        return String.format("<%s | %s | %s | %s>", new Object[]{this.getClass().getSimpleName(), this.pos, this.rotation, this.poolElement});
    }

    public StructurePoolElement getPoolElement() {
        return this.poolElement;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getGroundLevelDelta() {
        return this.groundLevelDelta;
    }

    public void addJunction(JigsawJunction arg) {
        this.junctions.add(arg);
    }

    public List<JigsawJunction> getJunctions() {
        return this.junctions;
    }
}

