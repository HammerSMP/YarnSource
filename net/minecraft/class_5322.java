/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;

public class class_5322 {
    @Nullable
    private static BlockPos method_29194(ServerWorld arg, int i, int j, boolean bl) {
        BlockPos.Mutable lv = new BlockPos.Mutable(i, 0, j);
        Biome lv2 = arg.getBiome(lv);
        BlockState lv3 = lv2.getSurfaceConfig().getTopMaterial();
        if (bl && !lv3.getBlock().isIn(BlockTags.VALID_SPAWN)) {
            return null;
        }
        WorldChunk lv4 = arg.getChunk(i >> 4, j >> 4);
        int k = lv4.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, i & 0xF, j & 0xF);
        if (k < 0) {
            return null;
        }
        if (lv4.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i & 0xF, j & 0xF) > lv4.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, i & 0xF, j & 0xF)) {
            return null;
        }
        for (int l = k + 1; l >= 0; --l) {
            lv.set(i, l, j);
            BlockState lv5 = arg.getBlockState(lv);
            if (!lv5.getFluidState().isEmpty()) break;
            if (!lv5.equals(lv3)) continue;
            return lv.up().toImmutable();
        }
        return null;
    }

    @Nullable
    private static BlockPos method_29195(ServerWorld arg, long l, int i, int j) {
        ChunkPos lv = new ChunkPos(i >> 4, j >> 4);
        Random random = new Random(l);
        BlockPos lv2 = new BlockPos(lv.getStartX() + random.nextInt(15), 0, lv.getEndZ() + random.nextInt(15));
        return arg.getTopNonAirState(lv2).getMaterial().blocksMovement() ? lv2 : null;
    }

    @Nullable
    public static BlockPos method_29196(ServerWorld arg, ChunkPos arg2, boolean bl) {
        for (int i = arg2.getStartX(); i <= arg2.getEndX(); ++i) {
            for (int j = arg2.getStartZ(); j <= arg2.getEndZ(); ++j) {
                BlockPos lv = class_5322.method_29194(arg, i, j, bl);
                if (lv == null) continue;
                return lv;
            }
        }
        return null;
    }

    @Nullable
    protected static BlockPos method_29197(ServerWorld arg, BlockPos arg2, int i, int j, int k) {
        if (arg.getDimension().method_28541()) {
            return class_5322.method_29194(arg, arg2.getX() + j - i, arg2.getZ() + k - i, false);
        }
        if (arg.getDimension().method_28543()) {
            return class_5322.method_29195(arg, arg.getSeed(), arg2.getX() + j - i, arg2.getZ() + k - i);
        }
        return null;
    }
}

