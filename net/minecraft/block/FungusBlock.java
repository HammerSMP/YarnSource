/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;

public class FungusBlock
extends PlantBlock
implements Fertilizable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
    private final Supplier<ConfiguredFeature<HugeFungusFeatureConfig, ?>> field_22135;

    protected FungusBlock(AbstractBlock.Settings arg, Supplier<ConfiguredFeature<HugeFungusFeatureConfig, ?>> supplier) {
        super(arg);
        this.field_22135 = supplier;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isIn(BlockTags.NYLIUM) || arg.isOf(Blocks.SOUL_SOIL) || super.canPlantOnTop(arg, arg2, arg3);
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        Block lv = ((HugeFungusFeatureConfig)this.field_22135.get().config).validBaseBlock.getBlock();
        Block lv2 = arg.getBlockState(arg2.down()).getBlock();
        return lv2 == lv;
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return (double)random.nextFloat() < 0.4;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        this.field_22135.get().generate(arg, arg.getStructureAccessor(), arg.getChunkManager().getChunkGenerator(), random, arg2);
    }
}

