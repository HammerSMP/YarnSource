/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CaveCarver;

public class UnderwaterCaveCarver
extends CaveCarver {
    public UnderwaterCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
        super(function, 256);
        this.alwaysCarvableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR, Blocks.PACKED_ICE});
    }

    @Override
    protected boolean isRegionUncarvable(Chunk arg, int i, int j, int k, int l, int m, int n, int o, int p) {
        return false;
    }

    @Override
    protected boolean carveAtPoint(Chunk arg, Function<BlockPos, Biome> function, BitSet bitSet, Random random, BlockPos.Mutable arg2, BlockPos.Mutable arg3, BlockPos.Mutable arg4, int i, int j, int k, int l, int m, int n, int o, int p, AtomicBoolean atomicBoolean) {
        return UnderwaterCaveCarver.carveAtPoint(this, arg, bitSet, random, arg2, i, j, k, l, m, n, o, p);
    }

    protected static boolean carveAtPoint(Carver<?> arg, Chunk arg2, BitSet bitSet, Random random, BlockPos.Mutable arg3, int i, int j, int k, int l, int m, int n, int o, int p) {
        if (o >= i) {
            return false;
        }
        int q = n | p << 4 | o << 8;
        if (bitSet.get(q)) {
            return false;
        }
        bitSet.set(q);
        arg3.set(l, o, m);
        BlockState lv = arg2.getBlockState(arg3);
        if (!arg.canAlwaysCarveBlock(lv)) {
            return false;
        }
        if (o == 10) {
            float f = random.nextFloat();
            if ((double)f < 0.25) {
                arg2.setBlockState(arg3, Blocks.MAGMA_BLOCK.getDefaultState(), false);
                arg2.getBlockTickScheduler().schedule(arg3, Blocks.MAGMA_BLOCK, 0);
            } else {
                arg2.setBlockState(arg3, Blocks.OBSIDIAN.getDefaultState(), false);
            }
            return true;
        }
        if (o < 10) {
            arg2.setBlockState(arg3, Blocks.LAVA.getDefaultState(), false);
            return false;
        }
        boolean bl = false;
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            int r = l + lv2.getOffsetX();
            int s = m + lv2.getOffsetZ();
            if (r >> 4 == j && s >> 4 == k && !arg2.getBlockState(arg3.set(r, o, s)).isAir()) continue;
            arg2.setBlockState(arg3, WATER.getBlockState(), false);
            arg2.getFluidTickScheduler().schedule(arg3, WATER.getFluid(), 0);
            bl = true;
            break;
        }
        arg3.set(l, o, m);
        if (!bl) {
            arg2.setBlockState(arg3, WATER.getBlockState(), false);
            return true;
        }
        return true;
    }
}

