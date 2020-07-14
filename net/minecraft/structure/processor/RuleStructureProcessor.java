/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldView;

public class RuleStructureProcessor
extends StructureProcessor {
    public static final Codec<RuleStructureProcessor> CODEC = StructureProcessorRule.CODEC.listOf().fieldOf("rules").xmap(RuleStructureProcessor::new, arg -> arg.rules).codec();
    private final ImmutableList<StructureProcessorRule> rules;

    public RuleStructureProcessor(List<? extends StructureProcessorRule> list) {
        this.rules = ImmutableList.copyOf(list);
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView arg, BlockPos pos, BlockPos arg3, Structure.StructureBlockInfo arg4, Structure.StructureBlockInfo arg5, StructurePlacementData arg6) {
        Random random = new Random(MathHelper.hashCode(arg5.pos));
        BlockState lv = arg.getBlockState(arg5.pos);
        for (StructureProcessorRule lv2 : this.rules) {
            if (!lv2.test(arg5.state, lv, arg4.pos, arg5.pos, arg3, random)) continue;
            return new Structure.StructureBlockInfo(arg5.pos, lv2.getOutputState(), lv2.getTag());
        }
        return arg5;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.RULE;
    }
}

