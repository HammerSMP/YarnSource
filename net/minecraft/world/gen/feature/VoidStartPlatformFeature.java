/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class VoidStartPlatformFeature
extends Feature<DefaultFeatureConfig> {
    private static final BlockPos START_BLOCK = new BlockPos(8, 3, 8);
    private static final ChunkPos START_CHUNK = new ChunkPos(START_BLOCK);

    public VoidStartPlatformFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    private static int getDistance(int x1, int z1, int x2, int z2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DefaultFeatureConfig arg4) {
        ChunkPos lv = new ChunkPos(arg3);
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

