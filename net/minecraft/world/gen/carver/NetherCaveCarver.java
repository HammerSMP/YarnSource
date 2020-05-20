/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CaveCarver;

public class NetherCaveCarver
extends CaveCarver {
    public NetherCaveCarver(Codec<ProbabilityConfig> codec) {
        super(codec, 128);
        this.alwaysCarvableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.BASALT, Blocks.BLACKSTONE});
        this.carvableFluids = ImmutableSet.of((Object)Fluids.LAVA, (Object)Fluids.WATER);
    }

    @Override
    protected int getMaxCaveCount() {
        return 10;
    }

    @Override
    protected float getTunnelSystemWidth(Random random) {
        return (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
    }

    @Override
    protected double getTunnelSystemHeightWidthRatio() {
        return 5.0;
    }

    @Override
    protected int getCaveY(Random random) {
        return random.nextInt(this.heightLimit);
    }

    @Override
    protected boolean carveAtPoint(Chunk arg, Function<BlockPos, Biome> function, BitSet bitSet, Random random, BlockPos.Mutable arg2, BlockPos.Mutable arg3, BlockPos.Mutable arg4, int i, int j, int k, int l, int m, int n, int o, int p, AtomicBoolean atomicBoolean) {
        int q = n | p << 4 | o << 8;
        if (bitSet.get(q)) {
            return false;
        }
        bitSet.set(q);
        arg2.set(l, o, m);
        if (this.canAlwaysCarveBlock(arg.getBlockState(arg2))) {
            BlockState lv2;
            if (o <= 31) {
                BlockState lv = LAVA.getBlockState();
            } else {
                lv2 = CAVE_AIR;
            }
            arg.setBlockState(arg2, lv2, false);
            return true;
        }
        return false;
    }
}

