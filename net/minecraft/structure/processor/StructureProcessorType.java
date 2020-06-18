/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.LavaSubmergedBlockStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType<P extends StructureProcessor> {
    public static final StructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = StructureProcessorType.register("block_ignore", BlockIgnoreStructureProcessor.CODEC);
    public static final StructureProcessorType<BlockRotStructureProcessor> BLOCK_ROT = StructureProcessorType.register("block_rot", BlockRotStructureProcessor.CODEC);
    public static final StructureProcessorType<GravityStructureProcessor> GRAVITY = StructureProcessorType.register("gravity", GravityStructureProcessor.CODEC);
    public static final StructureProcessorType<JigsawReplacementStructureProcessor> JIGSAW_REPLACEMENT = StructureProcessorType.register("jigsaw_replacement", JigsawReplacementStructureProcessor.CODEC);
    public static final StructureProcessorType<RuleStructureProcessor> RULE = StructureProcessorType.register("rule", RuleStructureProcessor.CODEC);
    public static final StructureProcessorType<NopStructureProcessor> NOP = StructureProcessorType.register("nop", NopStructureProcessor.CODEC);
    public static final StructureProcessorType<BlockAgeStructureProcessor> BLOCK_AGE = StructureProcessorType.register("block_age", BlockAgeStructureProcessor.CODEC);
    public static final StructureProcessorType<BlackstoneReplacementStructureProcessor> BLACKSTONE_REPLACE = StructureProcessorType.register("blackstone_replace", BlackstoneReplacementStructureProcessor.CODEC);
    public static final StructureProcessorType<LavaSubmergedBlockStructureProcessor> LAVA_SUBMERGED_BLOCK = StructureProcessorType.register("lava_submerged_block", LavaSubmergedBlockStructureProcessor.CODEC);
    public static final Codec<StructureProcessor> CODEC = Registry.STRUCTURE_PROCESSOR.dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);

    public Codec<P> codec();

    public static <P extends StructureProcessor> StructureProcessorType<P> register(String string, Codec<P> codec) {
        return Registry.register(Registry.STRUCTURE_PROCESSOR, string, () -> codec);
    }
}

