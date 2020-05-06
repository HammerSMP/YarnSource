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
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class GlowstoneBlobFeature
extends Feature<DefaultFeatureConfig> {
    public GlowstoneBlobFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        if (!arg.isAir(arg4)) {
            return false;
        }
        BlockState lv = arg.getBlockState(arg4.up());
        if (!(lv.isOf(Blocks.NETHERRACK) || lv.isOf(Blocks.BASALT) || lv.isOf(Blocks.BLACKSTONE))) {
            return false;
        }
        arg.setBlockState(arg4, Blocks.GLOWSTONE.getDefaultState(), 2);
        for (int i = 0; i < 1500; ++i) {
            BlockPos lv2 = arg4.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
            if (!arg.getBlockState(lv2).isAir()) continue;
            int j = 0;
            for (Direction lv3 : Direction.values()) {
                if (arg.getBlockState(lv2.offset(lv3)).isOf(Blocks.GLOWSTONE)) {
                    ++j;
                }
                if (j > 1) break;
            }
            if (j != true) continue;
            arg.setBlockState(lv2, Blocks.GLOWSTONE.getDefaultState(), 2);
        }
        return true;
    }
}

