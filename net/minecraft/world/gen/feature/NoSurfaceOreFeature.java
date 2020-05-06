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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class NoSurfaceOreFeature
extends Feature<OreFeatureConfig> {
    NoSurfaceOreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, OreFeatureConfig arg5) {
        int i = random.nextInt(arg5.size + 1);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int j = 0; j < i; ++j) {
            this.getStartPos(lv, random, arg4, Math.min(j, 7));
            if (!arg5.target.getCondition().test(arg.getBlockState(lv)) || this.checkAir(arg, lv)) continue;
            arg.setBlockState(lv, arg5.state, 2);
        }
        return true;
    }

    private void getStartPos(BlockPos.Mutable arg, Random random, BlockPos arg2, int i) {
        int j = this.randomCoord(random, i);
        int k = this.randomCoord(random, i);
        int l = this.randomCoord(random, i);
        arg.set(arg2, j, k, l);
    }

    private int randomCoord(Random random, int i) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float)i);
    }

    private boolean checkAir(IWorld arg, BlockPos arg2) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Direction lv2 : Direction.values()) {
            lv.set(arg2, lv2);
            if (!arg.getBlockState(lv).isAir()) continue;
            return true;
        }
        return false;
    }
}

