/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class NopStructureProcessor
extends StructureProcessor {
    public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

    private NopStructureProcessor() {
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView arg, BlockPos arg2, BlockPos arg3, Structure.StructureBlockInfo arg4, Structure.StructureBlockInfo arg5, StructurePlacementData arg6) {
        return arg5;
    }

    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.NOP;
    }

    @Override
    protected <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.emptyMap());
    }
}

