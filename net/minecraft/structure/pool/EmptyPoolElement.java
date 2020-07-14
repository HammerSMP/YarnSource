/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EmptyPoolElement
extends StructurePoolElement {
    public static final Codec<EmptyPoolElement> field_24947 = Codec.unit(() -> INSTANCE);
    public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

    private EmptyPoolElement() {
        super(StructurePool.Projection.TERRAIN_MATCHING);
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
        return Collections.emptyList();
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        return BlockBox.empty();
    }

    @Override
    public boolean generate(StructureManager structureManager, ServerWorldAccess arg2, StructureAccessor arg3, ChunkGenerator arg4, BlockPos arg5, BlockPos arg6, BlockRotation arg7, BlockBox arg8, Random random, boolean keepJigsaws) {
        return true;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.EMPTY_POOL_ELEMENT;
    }

    public String toString() {
        return "Empty";
    }
}

