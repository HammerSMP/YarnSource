/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;

public class LegacySinglePoolElement
extends SinglePoolElement {
    @Deprecated
    public LegacySinglePoolElement(String string, List<StructureProcessor> list) {
        super(string, list, StructurePool.Projection.RIGID);
    }

    @Deprecated
    public LegacySinglePoolElement(String string) {
        super(string, (List<StructureProcessor>)ImmutableList.of());
    }

    public LegacySinglePoolElement(Dynamic<?> dynamic) {
        super(dynamic);
    }

    @Override
    protected StructurePlacementData createPlacementData(BlockRotation arg, BlockBox arg2, boolean bl) {
        StructurePlacementData lv = super.createPlacementData(arg, arg2, bl);
        lv.removeProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
        lv.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        return lv;
    }

    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT;
    }

    @Override
    public String toString() {
        return "LegacySingle[" + (Object)this.field_24015 + "]";
    }
}

