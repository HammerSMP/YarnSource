/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlockRotStructureProcessor
extends StructureProcessor {
    private final float integrity;

    public BlockRotStructureProcessor(float f) {
        this.integrity = f;
    }

    public BlockRotStructureProcessor(Dynamic<?> dynamic) {
        this(dynamic.get("integrity").asFloat(1.0f));
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView arg, BlockPos arg2, BlockPos arg3, Structure.StructureBlockInfo arg4, Structure.StructureBlockInfo arg5, StructurePlacementData arg6) {
        Random random = arg6.getRandom(arg5.pos);
        if (this.integrity >= 1.0f || random.nextFloat() <= this.integrity) {
            return arg5;
        }
        return null;
    }

    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.BLOCK_ROT;
    }

    @Override
    protected <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("integrity"), (Object)dynamicOps.createFloat(this.integrity))));
    }
}

