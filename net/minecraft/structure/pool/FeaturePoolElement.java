/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class FeaturePoolElement
extends StructurePoolElement {
    public static final Codec<FeaturePoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.CODEC.fieldOf("feature").forGetter(arg -> arg.feature), FeaturePoolElement.method_28883()).apply((Applicative)instance, FeaturePoolElement::new));
    private final ConfiguredFeature<?, ?> feature;
    private final CompoundTag tag;

    @Deprecated
    public FeaturePoolElement(ConfiguredFeature<?, ?> arg) {
        this(arg, StructurePool.Projection.RIGID);
    }

    private FeaturePoolElement(ConfiguredFeature<?, ?> arg, StructurePool.Projection arg2) {
        super(arg2);
        this.feature = arg;
        this.tag = this.createDefaultJigsawTag();
    }

    private CompoundTag createDefaultJigsawTag() {
        CompoundTag lv = new CompoundTag();
        lv.putString("name", "minecraft:bottom");
        lv.putString("final_state", "minecraft:air");
        lv.putString("pool", "minecraft:empty");
        lv.putString("target", "minecraft:empty");
        lv.putString("joint", JigsawBlockEntity.Joint.ROLLABLE.asString());
        return lv;
    }

    public BlockPos getStart(StructureManager arg, BlockRotation arg2) {
        return BlockPos.ORIGIN;
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager arg, BlockPos arg2, BlockRotation arg3, Random random) {
        ArrayList list = Lists.newArrayList();
        list.add(new Structure.StructureBlockInfo(arg2, (BlockState)Blocks.JIGSAW.getDefaultState().with(JigsawBlock.ORIENTATION, JigsawOrientation.byDirections(Direction.DOWN, Direction.SOUTH)), this.tag));
        return list;
    }

    @Override
    public BlockBox getBoundingBox(StructureManager arg, BlockPos arg2, BlockRotation arg3) {
        BlockPos lv = this.getStart(arg, arg3);
        return new BlockBox(arg2.getX(), arg2.getY(), arg2.getZ(), arg2.getX() + lv.getX(), arg2.getY() + lv.getY(), arg2.getZ() + lv.getZ());
    }

    @Override
    public boolean generate(StructureManager arg, ServerWorldAccess arg2, StructureAccessor arg3, ChunkGenerator arg4, BlockPos arg5, BlockPos arg6, BlockRotation arg7, BlockBox arg8, Random random, boolean bl) {
        return this.feature.generate(arg2, arg4, random, arg5);
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.FEATURE_POOL_ELEMENT;
    }

    public String toString() {
        return "Feature[" + Registry.FEATURE.getId((Feature<?>)this.feature.feature) + "]";
    }
}

