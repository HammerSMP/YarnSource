/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

public class OverworldDimension
extends Dimension {
    public OverworldDimension(World arg, DimensionType arg2) {
        super(arg, arg2, 0.0f);
    }

    @Override
    public DimensionType getType() {
        return DimensionType.OVERWORLD;
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(long l, ChunkPos arg, boolean bl) {
        for (int i = arg.getStartX(); i <= arg.getEndX(); ++i) {
            for (int j = arg.getStartZ(); j <= arg.getEndZ(); ++j) {
                BlockPos lv = this.getTopSpawningBlockPosition(l, i, j, bl);
                if (lv == null) continue;
                return lv;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(long l, int i, int j, boolean bl) {
        BlockPos.Mutable lv = new BlockPos.Mutable(i, 0, j);
        Biome lv2 = this.world.getBiome(lv);
        BlockState lv3 = lv2.getSurfaceConfig().getTopMaterial();
        if (bl && !lv3.getBlock().isIn(BlockTags.VALID_SPAWN)) {
            return null;
        }
        WorldChunk lv4 = this.world.getChunk(i >> 4, j >> 4);
        int k = lv4.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, i & 0xF, j & 0xF);
        if (k < 0) {
            return null;
        }
        if (lv4.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i & 0xF, j & 0xF) > lv4.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, i & 0xF, j & 0xF)) {
            return null;
        }
        for (int m = k + 1; m >= 0; --m) {
            lv.set(i, m, j);
            BlockState lv5 = this.world.getBlockState(lv);
            if (!lv5.getFluidState().isEmpty()) break;
            if (!lv5.equals(lv3)) continue;
            return lv.up().toImmutable();
        }
        return null;
    }

    @Override
    public float getSkyAngle(long l, float f) {
        double d = MathHelper.fractionalPart((double)l / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0f;
    }

    @Override
    public boolean hasVisibleSky() {
        return true;
    }

    @Override
    public boolean canPlayersSleep() {
        return true;
    }
}

