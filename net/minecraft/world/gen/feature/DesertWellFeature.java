/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DesertWellFeature
extends Feature<DefaultFeatureConfig> {
    private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.SAND);
    private final BlockState slab = Blocks.SANDSTONE_SLAB.getDefaultState();
    private final BlockState wall = Blocks.SANDSTONE.getDefaultState();
    private final BlockState fluidInside = Blocks.WATER.getDefaultState();

    public DesertWellFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DefaultFeatureConfig arg4) {
        arg3 = arg3.up();
        while (arg.isAir(arg3) && arg3.getY() > 2) {
            arg3 = arg3.down();
        }
        if (!CAN_GENERATE.test(arg.getBlockState(arg3))) {
            return false;
        }
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (!arg.isAir(arg3.add(i, -1, j)) || !arg.isAir(arg3.add(i, -2, j))) continue;
                return false;
            }
        }
        for (int k = -1; k <= 0; ++k) {
            for (int l = -2; l <= 2; ++l) {
                for (int m = -2; m <= 2; ++m) {
                    arg.setBlockState(arg3.add(l, k, m), this.wall, 2);
                }
            }
        }
        arg.setBlockState(arg3, this.fluidInside, 2);
        for (Direction lv : Direction.Type.HORIZONTAL) {
            arg.setBlockState(arg3.offset(lv), this.fluidInside, 2);
        }
        for (int n = -2; n <= 2; ++n) {
            for (int o = -2; o <= 2; ++o) {
                if (n != -2 && n != 2 && o != -2 && o != 2) continue;
                arg.setBlockState(arg3.add(n, 1, o), this.wall, 2);
            }
        }
        arg.setBlockState(arg3.add(2, 1, 0), this.slab, 2);
        arg.setBlockState(arg3.add(-2, 1, 0), this.slab, 2);
        arg.setBlockState(arg3.add(0, 1, 2), this.slab, 2);
        arg.setBlockState(arg3.add(0, 1, -2), this.slab, 2);
        for (int p = -1; p <= 1; ++p) {
            for (int q = -1; q <= 1; ++q) {
                if (p == 0 && q == 0) {
                    arg.setBlockState(arg3.add(p, 4, q), this.wall, 2);
                    continue;
                }
                arg.setBlockState(arg3.add(p, 4, q), this.slab, 2);
            }
        }
        for (int r = 1; r <= 3; ++r) {
            arg.setBlockState(arg3.add(-1, r, -1), this.wall, 2);
            arg.setBlockState(arg3.add(-1, r, 1), this.wall, 2);
            arg.setBlockState(arg3.add(1, r, -1), this.wall, 2);
            arg.setBlockState(arg3.add(1, r, 1), this.wall, 2);
        }
        return true;
    }
}

