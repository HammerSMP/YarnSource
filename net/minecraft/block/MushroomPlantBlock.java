/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.class_5464;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class MushroomPlantBlock
extends PlantBlock
implements Fertilizable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public MushroomPlantBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            int j = 4;
            for (BlockPos lv : BlockPos.iterate(arg3.add(-4, -1, -4), arg3.add(4, 1, 4))) {
                if (!arg2.getBlockState(lv).isOf(this) || --i > 0) continue;
                return;
            }
            BlockPos lv2 = arg3.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int k = 0; k < 4; ++k) {
                if (arg2.isAir(lv2) && arg.canPlaceAt(arg2, lv2)) {
                    arg3 = lv2;
                }
                lv2 = arg3.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (arg2.isAir(lv2) && arg.canPlaceAt(arg2, lv2)) {
                arg2.setBlockState(lv2, arg, 2);
            }
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isOpaqueFullCube(arg2, arg3);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.down();
        BlockState lv2 = arg2.getBlockState(lv);
        if (lv2.isIn(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        }
        return arg2.getBaseLightLevel(arg3, 0) < 13 && this.canPlantOnTop(lv2, arg2, lv);
    }

    /*
     * WARNING - void declaration
     */
    public boolean trySpawningBigMushroom(ServerWorld arg, BlockPos arg2, BlockState arg3, Random random) {
        void lv3;
        arg.removeBlock(arg2, false);
        if (this == Blocks.BROWN_MUSHROOM) {
            ConfiguredFeature<?, ?> lv = class_5464.HUGE_BROWN_MUSHROOM;
        } else if (this == Blocks.RED_MUSHROOM) {
            ConfiguredFeature<?, ?> lv2 = class_5464.HUGE_RED_MUSHROOM;
        } else {
            arg.setBlockState(arg2, arg3, 3);
            return false;
        }
        if (lv3.generate(arg, arg.getChunkManager().getChunkGenerator(), random, arg2)) {
            return true;
        }
        arg.setBlockState(arg2, arg3, 3);
        return false;
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        return true;
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return (double)random.nextFloat() < 0.4;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        this.trySpawningBigMushroom(arg, arg2, arg3, random);
    }
}

