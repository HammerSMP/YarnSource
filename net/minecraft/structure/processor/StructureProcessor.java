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
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;

public abstract class StructureProcessor {
    @Nullable
    public abstract Structure.StructureBlockInfo process(WorldView var1, BlockPos var2, BlockPos var3, Structure.StructureBlockInfo var4, Structure.StructureBlockInfo var5, StructurePlacementData var6);

    protected abstract StructureProcessorType getType();

    protected abstract <T> Dynamic<T> rawToDynamic(DynamicOps<T> var1);

    public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.mergeInto(this.rawToDynamic(dynamicOps).getValue(), dynamicOps.createString("processor_type"), dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getId(this.getType()).toString())));
    }
}

