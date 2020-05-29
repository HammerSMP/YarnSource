/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.server.network;

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

public class SpawnLocating {
    @Nullable
    private static BlockPos findOverworldSpawn(ServerWorld arg, int i, int j, boolean bl) {
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
    private static BlockPos findEndSpawn(ServerWorld arg, long l, int i, int j) {
        ChunkPos lv = new ChunkPos(i >> 4, j >> 4);
        Random random = new Random(l);
        BlockPos lv2 = new BlockPos(lv.getStartX() + random.nextInt(15), 0, lv.getEndZ() + random.nextInt(15));
        return arg.getTopNonAirState(lv2).getMaterial().blocksMovement() ? lv2 : null;
    }

    @Nullable
    public static BlockPos findServerSpawnPoint(ServerWorld arg, ChunkPos arg2, boolean bl) {
        for (int i = arg2.getStartX(); i <= arg2.getEndX(); ++i) {
            for (int j = arg2.getStartZ(); j <= arg2.getEndZ(); ++j) {
                BlockPos lv = SpawnLocating.findOverworldSpawn(arg, i, j, bl);
                if (lv == null) continue;
                return lv;
            }
        }
        return null;
    }

    @Nullable
    protected static BlockPos findPlayerSpawn(ServerWorld arg, BlockPos arg2, int i, int j, int k) {
        if (arg.getDimension().isOverworld()) {
            return SpawnLocating.findOverworldSpawn(arg, arg2.getX() + j - i, arg2.getZ() + k - i, false);
        }
        if (arg.getDimension().isEnd()) {
            return SpawnLocating.findEndSpawn(arg, arg.getSeed(), arg2.getX() + j - i, arg2.getZ() + k - i);
        }
        return null;
    }
}

