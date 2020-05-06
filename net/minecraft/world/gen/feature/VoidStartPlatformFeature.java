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
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class VoidStartPlatformFeature
extends Feature<DefaultFeatureConfig> {
    private static final BlockPos START_BLOCK = new BlockPos(8, 3, 8);
    private static final ChunkPos START_CHUNK = new ChunkPos(START_BLOCK);

    public VoidStartPlatformFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    private static int getDistance(int i, int j, int k, int l) {
        return Math.max(Math.abs(i - k), Math.abs(j - l));
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        ChunkPos lv = new ChunkPos(arg4);
        if (VoidStartPlatformFeature.getDistance(lv.x, lv.z, VoidStartPlatformFeature.START_CHUNK.x, VoidStartPlatformFeature.START_CHUNK.z) > 1) {
            return true;
        }
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (int i = lv.getStartZ(); i <= lv.getEndZ(); ++i) {
            for (int j = lv.getStartX(); j <= lv.getEndX(); ++j) {
                if (VoidStartPlatformFeature.getDistance(START_BLOCK.getX(), START_BLOCK.getZ(), j, i) > 16) continue;
                lv2.set(j, START_BLOCK.getY(), i);
                if (lv2.equals(START_BLOCK)) {
                    arg.setBlockState(lv2, Blocks.COBBLESTONE.getDefaultState(), 2);
                    continue;
                }
                arg.setBlockState(lv2, Blocks.STONE.getDefaultState(), 2);
            }
        }
        return true;
    }
}

