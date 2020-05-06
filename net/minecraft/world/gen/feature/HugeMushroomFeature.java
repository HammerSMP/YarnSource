/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;

public abstract class HugeMushroomFeature
extends Feature<HugeMushroomFeatureConfig> {
    public HugeMushroomFeature(Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> function) {
        super(function);
    }

    protected void generateStem(IWorld arg, Random random, BlockPos arg2, HugeMushroomFeatureConfig arg3, int i, BlockPos.Mutable arg4) {
        for (int j = 0; j < i; ++j) {
            arg4.set(arg2).move(Direction.UP, j);
            if (arg.getBlockState(arg4).isOpaqueFullCube(arg, arg4)) continue;
            this.setBlockState(arg, arg4, arg3.stemProvider.getBlockState(random, arg2));
        }
    }

    protected int getHeight(Random random) {
        int i = random.nextInt(3) + 4;
        if (random.nextInt(12) == 0) {
            i *= 2;
        }
        return i;
    }

    protected boolean canGenerate(IWorld arg, BlockPos arg2, int i, BlockPos.Mutable arg3, HugeMushroomFeatureConfig arg4) {
        int j = arg2.getY();
        if (j < 1 || j + i + 1 >= 256) {
            return false;
        }
        Block lv = arg.getBlockState(arg2.down()).getBlock();
        if (!HugeMushroomFeature.isDirt(lv)) {
            return false;
        }
        for (int k = 0; k <= i; ++k) {
            int l = this.getCapSize(-1, -1, arg4.capSize, k);
            for (int m = -l; m <= l; ++m) {
                for (int n = -l; n <= l; ++n) {
                    BlockState lv2 = arg.getBlockState(arg3.set(arg2, m, k, n));
                    if (lv2.isAir() || lv2.isIn(BlockTags.LEAVES)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, HugeMushroomFeatureConfig arg5) {
        BlockPos.Mutable lv;
        int i = this.getHeight(random);
        if (!this.canGenerate(arg, arg4, i, lv = new BlockPos.Mutable(), arg5)) {
            return false;
        }
        this.generateCap(arg, random, arg4, i, lv, arg5);
        this.generateStem(arg, random, arg4, arg5, i, lv);
        return true;
    }

    protected abstract int getCapSize(int var1, int var2, int var3, int var4);

    protected abstract void generateCap(IWorld var1, Random var2, BlockPos var3, int var4, BlockPos.Mutable var5, HugeMushroomFeatureConfig var6);
}

