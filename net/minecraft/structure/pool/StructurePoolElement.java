/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
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
    @Nullable
    private volatile StructurePool.Projection projection;

    protected StructurePoolElement(StructurePool.Projection arg) {
        this.projection = arg;
    }

    protected StructurePoolElement(Dynamic<?> dynamic) {
        this.projection = StructurePool.Projection.getById(dynamic.get("projection").asString(StructurePool.Projection.RIGID.getId()));
    }

    public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager var1, BlockPos var2, BlockRotation var3, Random var4);

    public abstract BlockBox getBoundingBox(StructureManager var1, BlockPos var2, BlockRotation var3);

    public abstract boolean generate(StructureManager var1, ServerWorldAccess var2, StructureAccessor var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, BlockRotation var7, BlockBox var8, Random var9, boolean var10);

    public abstract StructurePoolElementType getType();

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

    protected abstract <T> Dynamic<T> rawToDynamic(DynamicOps<T> var1);

    public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
        Object object = this.rawToDynamic(dynamicOps).getValue();
        Object object2 = dynamicOps.mergeInto(object, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getId(this.getType()).toString()));
        return new Dynamic(dynamicOps, dynamicOps.mergeInto(object2, dynamicOps.createString("projection"), dynamicOps.createString(this.projection.getId())));
    }

    public int getGroundLevelDelta() {
        return 1;
    }
}

