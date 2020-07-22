/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.chunk.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.SkyLightStorage;
import org.apache.commons.lang3.mutable.MutableInt;

public final class ChunkSkyLightProvider
extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
        super(chunkProvider, LightType.SKY, new SkyLightStorage(chunkProvider));
    }

    @Override
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        boolean bl2;
        Direction lv3;
        if (targetId == Long.MAX_VALUE) {
            return 15;
        }
        if (sourceId == Long.MAX_VALUE) {
            if (((SkyLightStorage)this.lightStorage).isTopmostBlock(targetId)) {
                level = 0;
            } else {
                return 15;
            }
        }
        if (level >= 15) {
            return level;
        }
        MutableInt mutableInt = new MutableInt();
        BlockState lv = this.getStateForLighting(targetId, mutableInt);
        if (mutableInt.getValue() >= 15) {
            return 15;
        }
        int j = BlockPos.unpackLongX(sourceId);
        int k = BlockPos.unpackLongY(sourceId);
        int n = BlockPos.unpackLongZ(sourceId);
        int o = BlockPos.unpackLongX(targetId);
        int p = BlockPos.unpackLongY(targetId);
        int q = BlockPos.unpackLongZ(targetId);
        boolean bl = j == o && n == q;
        int r = Integer.signum(o - j);
        int s = Integer.signum(p - k);
        int t = Integer.signum(q - n);
        if (sourceId == Long.MAX_VALUE) {
            Direction lv2 = Direction.DOWN;
        } else {
            lv3 = Direction.fromVector(r, s, t);
        }
        BlockState lv4 = this.getStateForLighting(sourceId, null);
        if (lv3 != null) {
            VoxelShape lv6;
            VoxelShape lv5 = this.getOpaqueShape(lv4, sourceId, lv3);
            if (VoxelShapes.unionCoversFullCube(lv5, lv6 = this.getOpaqueShape(lv, targetId, lv3.getOpposite()))) {
                return 15;
            }
        } else {
            VoxelShape lv7 = this.getOpaqueShape(lv4, sourceId, Direction.DOWN);
            if (VoxelShapes.unionCoversFullCube(lv7, VoxelShapes.empty())) {
                return 15;
            }
            int u = bl ? -1 : 0;
            Direction lv8 = Direction.fromVector(r, u, t);
            if (lv8 == null) {
                return 15;
            }
            VoxelShape lv9 = this.getOpaqueShape(lv, targetId, lv8.getOpposite());
            if (VoxelShapes.unionCoversFullCube(VoxelShapes.empty(), lv9)) {
                return 15;
            }
        }
        boolean bl3 = bl2 = sourceId == Long.MAX_VALUE || bl && k > p;
        if (bl2 && level == 0 && mutableInt.getValue() == 0) {
            return 0;
        }
        return level + Math.max(1, mutableInt.getValue());
    }

    @Override
    protected void propagateLevel(long id, int level, boolean decrease) {
        long t;
        long u;
        int q;
        long m = ChunkSectionPos.fromBlockPos(id);
        int j = BlockPos.unpackLongY(id);
        int k = ChunkSectionPos.getLocalCoord(j);
        int n = ChunkSectionPos.getSectionCoord(j);
        if (k != 0) {
            boolean o = false;
        } else {
            int p = 0;
            while (!((SkyLightStorage)this.lightStorage).hasSection(ChunkSectionPos.offset(m, 0, -p - 1, 0)) && ((SkyLightStorage)this.lightStorage).isAboveMinHeight(n - p - 1)) {
                ++p;
            }
            q = p;
        }
        long r = BlockPos.add(id, 0, -1 - q * 16, 0);
        long s = ChunkSectionPos.fromBlockPos(r);
        if (m == s || ((SkyLightStorage)this.lightStorage).hasSection(s)) {
            this.propagateLevel(id, r, level, decrease);
        }
        if (m == (u = ChunkSectionPos.fromBlockPos(t = BlockPos.offset(id, Direction.UP))) || ((SkyLightStorage)this.lightStorage).hasSection(u)) {
            this.propagateLevel(id, t, level, decrease);
        }
        block1: for (Direction lv : HORIZONTAL_DIRECTIONS) {
            int v = 0;
            do {
                long w;
                long x;
                if (m == (x = ChunkSectionPos.fromBlockPos(w = BlockPos.add(id, lv.getOffsetX(), -v, lv.getOffsetZ())))) {
                    this.propagateLevel(id, w, level, decrease);
                    continue block1;
                }
                if (!((SkyLightStorage)this.lightStorage).hasSection(x)) continue;
                this.propagateLevel(id, w, level, decrease);
            } while (++v <= q * 16);
        }
    }

    @Override
    protected int recalculateLevel(long id, long excludedId, int maxLevel) {
        int j = maxLevel;
        if (Long.MAX_VALUE != excludedId) {
            int k = this.getPropagatedLevel(Long.MAX_VALUE, id, 0);
            if (j > k) {
                j = k;
            }
            if (j == 0) {
                return j;
            }
        }
        long n = ChunkSectionPos.fromBlockPos(id);
        ChunkNibbleArray lv = ((SkyLightStorage)this.lightStorage).getLightSection(n, true);
        for (Direction lv2 : DIRECTIONS) {
            int s;
            ChunkNibbleArray lv4;
            long o = BlockPos.offset(id, lv2);
            long p = ChunkSectionPos.fromBlockPos(o);
            if (n == p) {
                ChunkNibbleArray lv3 = lv;
            } else {
                lv4 = ((SkyLightStorage)this.lightStorage).getLightSection(p, true);
            }
            if (lv4 != null) {
                if (o == excludedId) continue;
                int q = this.getPropagatedLevel(o, id, this.getCurrentLevelFromSection(lv4, o));
                if (j > q) {
                    j = q;
                }
                if (j != 0) continue;
                return j;
            }
            if (lv2 == Direction.DOWN) continue;
            o = BlockPos.removeChunkSectionLocalY(o);
            while (!((SkyLightStorage)this.lightStorage).hasSection(p) && !((SkyLightStorage)this.lightStorage).isAtOrAboveTopmostSection(p)) {
                p = ChunkSectionPos.offset(p, Direction.UP);
                o = BlockPos.add(o, 0, 16, 0);
            }
            ChunkNibbleArray lv5 = ((SkyLightStorage)this.lightStorage).getLightSection(p, true);
            if (o == excludedId) continue;
            if (lv5 != null) {
                int r = this.getPropagatedLevel(o, id, this.getCurrentLevelFromSection(lv5, o));
            } else {
                int n2 = s = ((SkyLightStorage)this.lightStorage).isSectionEnabled(p) ? 0 : 15;
            }
            if (j > s) {
                j = s;
            }
            if (j != 0) continue;
            return j;
        }
        return j;
    }

    @Override
    protected void resetLevel(long id) {
        ((SkyLightStorage)this.lightStorage).updateAll();
        long m = ChunkSectionPos.fromBlockPos(id);
        if (((SkyLightStorage)this.lightStorage).hasSection(m)) {
            super.resetLevel(id);
        } else {
            id = BlockPos.removeChunkSectionLocalY(id);
            while (!((SkyLightStorage)this.lightStorage).hasSection(m) && !((SkyLightStorage)this.lightStorage).isAtOrAboveTopmostSection(m)) {
                m = ChunkSectionPos.offset(m, Direction.UP);
                id = BlockPos.add(id, 0, 16, 0);
            }
            if (((SkyLightStorage)this.lightStorage).hasSection(m)) {
                super.resetLevel(id);
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String displaySectionLevel(long sectionPos) {
        return super.displaySectionLevel(sectionPos) + (((SkyLightStorage)this.lightStorage).isAtOrAboveTopmostSection(sectionPos) ? "*" : "");
    }
}

