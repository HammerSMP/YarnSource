/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;

public class LegacySinglePoolElement
extends SinglePoolElement {
    public static final Codec<LegacySinglePoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(LegacySinglePoolElement.method_28882(), LegacySinglePoolElement.method_28880(), LegacySinglePoolElement.method_28883()).apply((Applicative)instance, LegacySinglePoolElement::new));

    protected LegacySinglePoolElement(Either<Identifier, Structure> either, Supplier<ImmutableList<StructureProcessor>> supplier, StructurePool.Projection arg) {
        super(either, supplier, arg);
    }

    @Override
    protected StructurePlacementData createPlacementData(BlockRotation arg, BlockBox arg2, boolean keepJigsaws) {
        StructurePlacementData lv = super.createPlacementData(arg, arg2, keepJigsaws);
        lv.removeProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
        lv.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        return lv;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT;
    }

    @Override
    public String toString() {
        return "LegacySingle[" + (Object)this.field_24015 + "]";
    }
}

