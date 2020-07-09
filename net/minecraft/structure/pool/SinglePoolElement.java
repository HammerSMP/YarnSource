/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SinglePoolElement
extends StructurePoolElement {
    private static final Codec<Either<Identifier, Structure>> field_24951 = Codec.of(SinglePoolElement::method_28877, (Decoder)Identifier.CODEC.map(Either::left));
    public static final Codec<SinglePoolElement> field_24952 = RecordCodecBuilder.create(instance -> instance.group(SinglePoolElement.method_28882(), SinglePoolElement.method_28880(), SinglePoolElement.method_28883()).apply((Applicative)instance, SinglePoolElement::new));
    protected final Either<Identifier, Structure> field_24015;
    protected final Supplier<ImmutableList<StructureProcessor>> processors;

    private static <T> DataResult<T> method_28877(Either<Identifier, Structure> either, DynamicOps<T> dynamicOps, T object) {
        Optional optional = either.left();
        if (!optional.isPresent()) {
            return DataResult.error((String)"Can not serialize a runtime pool element");
        }
        return Identifier.CODEC.encode(optional.get(), dynamicOps, object);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Supplier<ImmutableList<StructureProcessor>>> method_28880() {
        return StructureProcessorType.field_25877.fieldOf("processors").forGetter(arg -> arg.processors);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<Identifier, Structure>> method_28882() {
        return field_24951.fieldOf("location").forGetter(arg -> arg.field_24015);
    }

    protected SinglePoolElement(Either<Identifier, Structure> either, Supplier<ImmutableList<StructureProcessor>> supplier, StructurePool.Projection arg) {
        super(arg);
        this.field_24015 = either;
        this.processors = supplier;
    }

    public SinglePoolElement(Structure arg) {
        this((Either<Identifier, Structure>)Either.right((Object)arg), ImmutableList::of, StructurePool.Projection.RIGID);
    }

    private Structure method_27233(StructureManager arg) {
        return (Structure)this.field_24015.map(arg::getStructureOrBlank, Function.identity());
    }

    public List<Structure.StructureBlockInfo> getDataStructureBlocks(StructureManager arg, BlockPos arg2, BlockRotation arg3, boolean bl) {
        Structure lv = this.method_27233(arg);
        List<Structure.StructureBlockInfo> list = lv.getInfosForBlock(arg2, new StructurePlacementData().setRotation(arg3), Blocks.STRUCTURE_BLOCK, bl);
        ArrayList list2 = Lists.newArrayList();
        for (Structure.StructureBlockInfo lv2 : list) {
            StructureBlockMode lv3;
            if (lv2.tag == null || (lv3 = StructureBlockMode.valueOf(lv2.tag.getString("mode"))) != StructureBlockMode.DATA) continue;
            list2.add(lv2);
        }
        return list2;
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager arg, BlockPos arg2, BlockRotation arg3, Random random) {
        Structure lv = this.method_27233(arg);
        List<Structure.StructureBlockInfo> list = lv.getInfosForBlock(arg2, new StructurePlacementData().setRotation(arg3), Blocks.JIGSAW, true);
        Collections.shuffle(list, random);
        return list;
    }

    @Override
    public BlockBox getBoundingBox(StructureManager arg, BlockPos arg2, BlockRotation arg3) {
        Structure lv = this.method_27233(arg);
        return lv.calculateBoundingBox(new StructurePlacementData().setRotation(arg3), arg2);
    }

    @Override
    public boolean generate(StructureManager arg, ServerWorldAccess arg2, StructureAccessor arg3, ChunkGenerator arg4, BlockPos arg5, BlockPos arg6, BlockRotation arg7, BlockBox arg8, Random random, boolean bl) {
        StructurePlacementData lv2;
        Structure lv = this.method_27233(arg);
        if (lv.place(arg2, arg5, arg6, lv2 = this.createPlacementData(arg7, arg8, bl), random, 18)) {
            List<Structure.StructureBlockInfo> list = Structure.process(arg2, arg5, arg6, lv2, this.getDataStructureBlocks(arg, arg5, arg7, false));
            for (Structure.StructureBlockInfo lv3 : list) {
                this.method_16756(arg2, lv3, arg5, arg7, random, arg8);
            }
            return true;
        }
        return false;
    }

    protected StructurePlacementData createPlacementData(BlockRotation arg, BlockBox arg2, boolean bl) {
        StructurePlacementData lv = new StructurePlacementData();
        lv.setBoundingBox(arg2);
        lv.setRotation(arg);
        lv.setUpdateNeighbors(true);
        lv.setIgnoreEntities(false);
        lv.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
        lv.method_27264(true);
        if (!bl) {
            lv.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
        }
        this.processors.get().forEach(lv::addProcessor);
        this.getProjection().getProcessors().forEach(lv::addProcessor);
        return lv;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.SINGLE_POOL_ELEMENT;
    }

    public String toString() {
        return "Single[" + this.field_24015 + "]";
    }
}

