/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
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
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SinglePoolElement
extends StructurePoolElement {
    protected final Either<Identifier, Structure> field_24015;
    protected final ImmutableList<StructureProcessor> processors;

    @Deprecated
    public SinglePoolElement(String string, List<StructureProcessor> list) {
        this(string, list, StructurePool.Projection.RIGID);
    }

    public SinglePoolElement(String string, List<StructureProcessor> list, StructurePool.Projection arg) {
        super(arg);
        this.field_24015 = Either.left((Object)new Identifier(string));
        this.processors = ImmutableList.copyOf(list);
    }

    public SinglePoolElement(Structure arg, List<StructureProcessor> list, StructurePool.Projection arg2) {
        super(arg2);
        this.field_24015 = Either.right((Object)arg);
        this.processors = ImmutableList.copyOf(list);
    }

    @Deprecated
    public SinglePoolElement(String string) {
        this(string, (List<StructureProcessor>)ImmutableList.of());
    }

    public SinglePoolElement(Dynamic<?> dynamic2) {
        super(dynamic2);
        this.field_24015 = Either.left((Object)new Identifier(dynamic2.get("location").asString("")));
        this.processors = ImmutableList.copyOf((Collection)dynamic2.get("processors").asList(dynamic -> DynamicDeserializer.deserialize(dynamic, Registry.STRUCTURE_PROCESSOR, "processor_type", NopStructureProcessor.INSTANCE)));
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
        if (lv.place(arg2, arg5, arg6, lv2 = this.createPlacementData(arg7, arg8, bl), 18)) {
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
        this.processors.forEach(lv::addProcessor);
        this.getProjection().getProcessors().forEach(lv::addProcessor);
        return lv;
    }

    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.SINGLE_POOL_ELEMENT;
    }

    @Override
    public <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("location"), (Object)dynamicOps.createString(((Identifier)this.field_24015.left().orElseThrow(RuntimeException::new)).toString()), (Object)dynamicOps.createString("processors"), (Object)dynamicOps.createList(this.processors.stream().map(arg -> arg.toDynamic(dynamicOps).getValue())))));
    }

    public String toString() {
        return "Single[" + this.field_24015 + "]";
    }
}

