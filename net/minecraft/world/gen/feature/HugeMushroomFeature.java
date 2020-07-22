/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;

public abstract class HugeMushroomFeature
extends Feature<HugeMushroomFeatureConfig> {
    public HugeMushroomFeature(Codec<HugeMushroomFeatureConfig> codec) {
        super(codec);
    }

    protected void generateStem(WorldAccess world, Random random, BlockPos pos, HugeMushroomFeatureConfig config, int height, BlockPos.Mutable arg4) {
        for (int j = 0; j < height; ++j) {
            arg4.set(pos).move(Direction.UP, j);
            if (world.getBlockState(arg4).isOpaqueFullCube(world, arg4)) continue;
            this.setBlockState(world, arg4, config.stemProvider.getBlockState(random, pos));
        }
    }

    protected int getHeight(Random random) {
        int i = random.nextInt(3) + 4;
        if (random.nextInt(12) == 0) {
            i *= 2;
        }
        return i;
    }

    protected boolean canGenerate(WorldAccess world, BlockPos pos, int height, BlockPos.Mutable arg3, HugeMushroomFeatureConfig config) {
        int j = pos.getY();
        if (j < 1 || j + height + 1 >= 256) {
            return false;
        }
        Block lv = world.getBlockState(pos.down()).getBlock();
        if (!HugeMushroomFeature.isSoil(lv) && !lv.isIn(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return false;
        }
        for (int k = 0; k <= height; ++k) {
            int l = this.getCapSize(-1, -1, config.capSize, k);
            for (int m = -l; m <= l; ++m) {
                for (int n = -l; n <= l; ++n) {
                    BlockState lv2 = world.getBlockState(arg3.set(pos, m, k, n));
                    if (lv2.isAir() || lv2.isIn(BlockTags.LEAVES)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, HugeMushroomFeatureConfig arg4) {
        BlockPos.Mutable lv;
        int i = this.getHeight(random);
        if (!this.canGenerate(arg, arg3, i, lv = new BlockPos.Mutable(), arg4)) {
            return false;
        }
        this.generateCap(arg, random, arg3, i, lv, arg4);
        this.generateStem(arg, random, arg3, arg4, i, lv);
        return true;
    }

    protected abstract int getCapSize(int var1, int var2, int var3, int var4);

    protected abstract void generateCap(WorldAccess var1, Random var2, BlockPos var3, int var4, BlockPos.Mutable var5, HugeMushroomFeatureConfig var6);
}

