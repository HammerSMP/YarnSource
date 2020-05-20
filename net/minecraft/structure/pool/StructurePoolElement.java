/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class StructurePoolElement {
    public static final Codec<StructurePoolElement> field_24953 = Registry.STRUCTURE_POOL_ELEMENT.dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
    @Nullable
    private volatile StructurePool.Projection projection;

    protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructurePool.Projection> method_28883() {
        return StructurePool.Projection.field_24956.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
    }

    protected StructurePoolElement(StructurePool.Projection arg) {
        this.projection = arg;
    }

    public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager var1, BlockPos var2, BlockRotation var3, Random var4);

    public abstract BlockBox getBoundingBox(StructureManager var1, BlockPos var2, BlockRotation var3);

    public abstract boolean generate(StructureManager var1, ServerWorldAccess var2, StructureAccessor var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, BlockRotation var7, BlockBox var8, Random var9, boolean var10);

    public abstract StructurePoolElementType<?> getType();

    public void method_16756(WorldAccess arg, Structure.StructureBlockInfo arg2, BlockPos arg3, BlockRotation arg4, Random random, BlockBox arg5) {
    }

    public StructurePoolElement setProjection(StructurePool.Projection arg) {
        this.projection = arg;
        return this;
    }

    public StructurePool.Projection getProjection() {
        StructurePool.Projection lv = this.projection;
        if (lv == null) {
            throw new IllegalStateException();
        }
        return lv;
    }

    public int getGroundLevelDelta() {
        return 1;
    }
}

