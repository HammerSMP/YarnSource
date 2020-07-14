/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.server.network;

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
    protected static BlockPos findOverworldSpawn(ServerWorld world, int x, int z, boolean validSpawnNeeded) {
        int k;
        BlockPos.Mutable lv = new BlockPos.Mutable(x, 0, z);
        Biome lv2 = world.getBiome(lv);
        boolean bl2 = world.getDimension().hasCeiling();
        BlockState lv3 = lv2.getSurfaceConfig().getTopMaterial();
        if (validSpawnNeeded && !lv3.getBlock().isIn(BlockTags.VALID_SPAWN)) {
            return null;
        }
        WorldChunk lv4 = world.getChunk(x >> 4, z >> 4);
        int n = k = bl2 ? world.getChunkManager().getChunkGenerator().getSpawnHeight() : lv4.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 0xF, z & 0xF);
        if (k < 0) {
            return null;
        }
        int l = lv4.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 0xF, z & 0xF);
        if (l <= k && l > lv4.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 0xF, z & 0xF)) {
            return null;
        }
        for (int m = k + 1; m >= 0; --m) {
            lv.set(x, m, z);
            BlockState lv5 = world.getBlockState(lv);
            if (!lv5.getFluidState().isEmpty()) break;
            if (!lv5.equals(lv3)) continue;
            return lv.up().toImmutable();
        }
        return null;
    }

    @Nullable
    public static BlockPos findServerSpawnPoint(ServerWorld world, ChunkPos chunkPos, boolean validSpawnNeeded) {
        for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
            for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
                BlockPos lv = SpawnLocating.findOverworldSpawn(world, i, j, validSpawnNeeded);
                if (lv == null) continue;
                return lv;
            }
        }
        return null;
    }
}

