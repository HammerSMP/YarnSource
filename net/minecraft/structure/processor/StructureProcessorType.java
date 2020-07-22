/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
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
import net.minecraft.util.dynamic.RegistryElementCodec;
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
    public static final MapCodec<ImmutableList<StructureProcessor>> PROCESSORS = StructureProcessorType.method_30652("processors", CODEC.listOf().xmap(ImmutableList::copyOf, Function.identity()));
    public static final Codec<Supplier<ImmutableList<StructureProcessor>>> field_25877 = RegistryElementCodec.of(Registry.PROCESSOR_LIST_WORLDGEN, PROCESSORS);

    public Codec<P> codec();

    public static <E> MapCodec<E> method_30652(final String string, Codec<E> codec) {
        final MapCodec mapCodec = codec.fieldOf(string);
        return new MapCodec<E>(){

            public <O> Stream<O> keys(DynamicOps<O> dynamicOps) {
                return mapCodec.keys(dynamicOps);
            }

            public <O> DataResult<E> decode(DynamicOps<O> dynamicOps, MapLike<O> mapLike) {
                return mapCodec.decode(dynamicOps, mapLike);
            }

            public <O> RecordBuilder<O> encode(E object, DynamicOps<O> dynamicOps, RecordBuilder<O> recordBuilder) {
                return mapCodec.encode(object, dynamicOps, recordBuilder);
            }

            public Codec<E> codec() {
                final Codec codec = super.codec();
                return new Codec<E>(){

                    public <O> DataResult<Pair<E, O>> decode(DynamicOps<O> dynamicOps, O object) {
                        if (dynamicOps.compressMaps()) {
                            return codec.decode(dynamicOps, object);
                        }
                        DataResult dataResult = dynamicOps.getMap(object);
                        MapLike mapLike2 = (MapLike)dataResult.get().map(mapLike -> mapLike, partialResult -> MapLike.forMap((Map)ImmutableMap.of((Object)dynamicOps.createString(string), (Object)object), (DynamicOps)dynamicOps));
                        return mapCodec.decode(dynamicOps, mapLike2).map(object2 -> Pair.of((Object)object2, (Object)object));
                    }

                    public <O> DataResult<O> encode(E object, DynamicOps<O> dynamicOps, O object2) {
                        return codec.encode(object, dynamicOps, object2);
                    }
                };
            }
        };
    }

    public static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.STRUCTURE_PROCESSOR, id, () -> codec);
    }
}

