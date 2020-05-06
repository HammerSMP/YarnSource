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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

public class RandomPatchFeature
extends Feature<RandomPatchFeatureConfig> {
    public RandomPatchFeature(Function<Dynamic<?>, ? extends RandomPatchFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, RandomPatchFeatureConfig arg5) {
        BlockPos lv3;
        BlockState lv = arg5.stateProvider.getBlockState(random, arg4);
        if (arg5.project) {
            BlockPos lv2 = arg.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, arg4);
        } else {
            lv3 = arg4;
        }
        int i = 0;
        BlockPos.Mutable lv4 = new BlockPos.Mutable();
        for (int j = 0; j < arg5.tries; ++j) {
            lv4.set(lv3, random.nextInt(arg5.spreadX + 1) - random.nextInt(arg5.spreadX + 1), random.nextInt(arg5.spreadY + 1) - random.nextInt(arg5.spreadY + 1), random.nextInt(arg5.spreadZ + 1) - random.nextInt(arg5.spreadZ + 1));
            Vec3i lv5 = lv4.down();
            BlockState lv6 = arg.getBlockState((BlockPos)lv5);
            if (!arg.isAir(lv4) && (!arg5.canReplace || !arg.getBlockState(lv4).getMaterial().isReplaceable()) || !lv.canPlaceAt(arg, lv4) || !arg5.whitelist.isEmpty() && !arg5.whitelist.contains(lv6.getBlock()) || arg5.blacklist.contains(lv6) || arg5.needsWater && !arg.getFluidState(((BlockPos)lv5).west()).matches(FluidTags.WATER) && !arg.getFluidState(((BlockPos)lv5).east()).matches(FluidTags.WATER) && !arg.getFluidState(((BlockPos)lv5).north()).matches(FluidTags.WATER) && !arg.getFluidState(((BlockPos)lv5).south()).matches(FluidTags.WATER)) continue;
            arg5.blockPlacer.method_23403(arg, lv4, lv, random);
            ++i;
        }
        return i > 0;
    }
}

