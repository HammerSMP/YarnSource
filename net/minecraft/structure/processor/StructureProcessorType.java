/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure.processor;

import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType
extends DynamicDeserializer<StructureProcessor> {
    public static final StructureProcessorType BLOCK_IGNORE = StructureProcessorType.register("block_ignore", BlockIgnoreStructureProcessor::new);
    public static final StructureProcessorType BLOCK_ROT = StructureProcessorType.register("block_rot", BlockRotStructureProcessor::new);
    public static final StructureProcessorType GRAVITY = StructureProcessorType.register("gravity", GravityStructureProcessor::new);
    public static final StructureProcessorType JIGSAW_REPLACEMENT = StructureProcessorType.register("jigsaw_replacement", dynamic -> JigsawReplacementStructureProcessor.INSTANCE);
    public static final StructureProcessorType RULE = StructureProcessorType.register("rule", RuleStructureProcessor::new);
    public static final StructureProcessorType NOP = StructureProcessorType.register("nop", dynamic -> NopStructureProcessor.INSTANCE);
    public static final StructureProcessorType BLOCK_AGE = StructureProcessorType.register("block_age", BlockAgeStructureProcessor::new);
    public static final StructureProcessorType BLACKSTONE_REPLACE = StructureProcessorType.register("blackstone_replace", dynamic -> BlackstoneReplacementStructureProcessor.INSTANCE);

    public static StructureProcessorType register(String string, StructureProcessorType arg) {
        return Registry.register(Registry.STRUCTURE_PROCESSOR, string, arg);
    }
}

