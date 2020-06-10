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
    protected static BlockPos findOverworldSpawn(ServerWorld arg, int i, int j, boolean bl) {
        int k;
        BlockPos.Mutable lv = new BlockPos.Mutable(i, 0, j);
        Biome lv2 = arg.getBiome(lv);
        boolean bl2 = arg.getDimension().hasCeiling();
        BlockState lv3 = lv2.getSurfaceConfig().getTopMaterial();
        if (bl && !lv3.getBlock().isIn(BlockTags.VALID_SPAWN)) {
            return null;
        }
        WorldChunk lv4 = arg.getChunk(i >> 4, j >> 4);
        int n = k = bl2 ? arg.getChunkManager().getChunkGenerator().getSpawnHeight() : lv4.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, i & 0xF, j & 0xF);
        if (k < 0) {
            return null;
        }
        int l = lv4.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i & 0xF, j & 0xF);
        if (l <= k && l > lv4.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, i & 0xF, j & 0xF)) {
            return null;
        }
        for (int m = k + 1; m >= 0; --m) {
            lv.set(i, m, j);
            BlockState lv5 = arg.getBlockState(lv);
            if (!lv5.getFluidState().isEmpty()) break;
            if (!lv5.equals(lv3)) continue;
            return lv.up().toImmutable();
        }
        return null;
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
}

