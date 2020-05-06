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
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DiskFeature
extends Feature<DiskFeatureConfig> {
    public DiskFeature(Function<Dynamic<?>, ? extends DiskFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, DiskFeatureConfig arg5) {
        if (!arg.getFluidState(arg4).matches(FluidTags.WATER)) {
            return false;
        }
        int i = 0;
        int j = random.nextInt(arg5.radius - 2) + 2;
        for (int k = arg4.getX() - j; k <= arg4.getX() + j; ++k) {
            for (int l = arg4.getZ() - j; l <= arg4.getZ() + j; ++l) {
                int n;
                int m = k - arg4.getX();
                if (m * m + (n = l - arg4.getZ()) * n > j * j) continue;
                block2: for (int o = arg4.getY() - arg5.ySize; o <= arg4.getY() + arg5.ySize; ++o) {
                    BlockPos lv = new BlockPos(k, o, l);
                    BlockState lv2 = arg.getBlockState(lv);
                    for (BlockState lv3 : arg5.targets) {
                        if (!lv3.isOf(lv2.getBlock())) continue;
                        arg.setBlockState(lv, arg5.state, 2);
                        ++i;
                        continue block2;
                    }
                }
            }
        }
        return i > 0;
    }
}

