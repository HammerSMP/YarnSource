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

    public ChunkSkyLightProvider(ChunkProvider arg) {
        super(arg, LightType.SKY, new SkyLightStorage(arg));
    }

    @Override
    protected int getPropagatedLevel(long l, long m, int i) {
        boolean bl2;
        Direction lv3;
        if (m == Long.MAX_VALUE) {
            return 15;
        }
        if (l == Long.MAX_VALUE) {
            if (((SkyLightStorage)this.lightStorage).method_15565(m)) {
                i = 0;
            } else {
                return 15;
            }
        }
        if (i >= 15) {
            return i;
        }
        MutableInt mutableInt = new MutableInt();
        BlockState lv = this.getStateForLighting(m, mutableInt);
        if (mutableInt.getValue() >= 15) {
            return 15;
        }
        int j = BlockPos.unpackLongX(l);
        int k = BlockPos.unpackLongY(l);
        int n = BlockPos.unpackLongZ(l);
        int o = BlockPos.unpackLongX(m);
        int p = BlockPos.unpackLongY(m);
        int q = BlockPos.unpackLongZ(m);
        boolean bl = j == o && n == q;
        int r = Integer.signum(o - j);
        int s = Integer.signum(p - k);
        int t = Integer.signum(q - n);
        if (l == Long.MAX_VALUE) {
            Direction lv2 = Direction.DOWN;
        } else {
            lv3 = Direction.fromVector(r, s, t);
        }
        BlockState lv4 = this.getStateForLighting(l, null);
        if (lv3 != null) {
            VoxelShape lv6;
            VoxelShape lv5 = this.getOpaqueShape(lv4, l, lv3);
            if (VoxelShapes.unionCoversFullCube(lv5, lv6 = this.getOpaqueShape(lv, m, lv3.getOpposite()))) {
                return 15;
            }
        } else {
            VoxelShape lv7 = this.getOpaqueShape(lv4, l, Direction.DOWN);
            if (VoxelShapes.unionCoversFullCube(lv7, VoxelShapes.empty())) {
                return 15;
            }
            int u = bl ? -1 : 0;
            Direction lv8 = Direction.fromVector(r, u, t);
            if (lv8 == null) {
                return 15;
            }
            VoxelShape lv9 = this.getOpaqueShape(lv, m, lv8.getOpposite());
            if (VoxelShapes.unionCoversFullCube(VoxelShapes.empty(), lv9)) {
                return 15;
            }
        }
        boolean bl3 = bl2 = l == Long.MAX_VALUE || bl && k > p;
        if (bl2 && i == 0 && mutableInt.getValue() == 0) {
            return 0;
        }
        return i + Math.max(1, mutableInt.getValue());
    }

    @Override
    protected void propagateLevel(long l, int i, boolean bl) {
        long t;
        long u;
        int q;
        long m = ChunkSectionPos.fromGlobalPos(l);
        int j = BlockPos.unpackLongY(l);
        int k = ChunkSectionPos.getLocalCoord(j);
        int n = ChunkSectionPos.getSectionCoord(j);
        if (k != 0) {
            boolean o = false;
        } else {
            int p = 0;
            while (!((SkyLightStorage)this.lightStorage).hasLight(ChunkSectionPos.offset(m, 0, -p - 1, 0)) && ((SkyLightStorage)this.lightStorage).isAboveMinimumHeight(n - p - 1)) {
                ++p;
            }
            q = p;
        }
        long r = BlockPos.add(l, 0, -1 - q * 16, 0);
        long s = ChunkSectionPos.fromGlobalPos(r);
        if (m == s || ((SkyLightStorage)this.lightStorage).hasLight(s)) {
            this.propagateLevel(l, r, i, bl);
        }
        if (m == (u = ChunkSectionPos.fromGlobalPos(t = BlockPos.offset(l, Direction.UP))) || ((SkyLightStorage)this.lightStorage).hasLight(u)) {
            this.propagateLevel(l, t, i, bl);
        }
        block1: for (Direction lv : HORIZONTAL_DIRECTIONS) {
            int v = 0;
            do {
                long w;
                long x;
                if (m == (x = ChunkSectionPos.fromGlobalPos(w = BlockPos.add(l, lv.getOffsetX(), -v, lv.getOffsetZ())))) {
                    this.propagateLevel(l, w, i, bl);
                    continue block1;
                }
                if (!((SkyLightStorage)this.lightStorage).hasLight(x)) continue;
                this.propagateLevel(l, w, i, bl);
            } while (++v <= q * 16);
        }
    }

    @Override
    protected int recalculateLevel(long l, long m, int i) {
        int j = i;
        if (Long.MAX_VALUE != m) {
            int k = this.getPropagatedLevel(Long.MAX_VALUE, l, 0);
            if (j > k) {
                j = k;
            }
            if (j == 0) {
                return j;
            }
        }
        long n = ChunkSectionPos.fromGlobalPos(l);
        ChunkNibbleArray lv = ((SkyLightStorage)this.lightStorage).getLightArray(n, true);
        for (Direction lv2 : DIRECTIONS) {
            int s;
            ChunkNibbleArray lv4;
            long o = BlockPos.offset(l, lv2);
            long p = ChunkSectionPos.fromGlobalPos(o);
            if (n == p) {
                ChunkNibbleArray lv3 = lv;
            } else {
                lv4 = ((SkyLightStorage)this.lightStorage).getLightArray(p, true);
            }
            if (lv4 != null) {
                if (o == m) continue;
                int q = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(lv4, o));
                if (j > q) {
                    j = q;
                }
                if (j != 0) continue;
                return j;
            }
            if (lv2 == Direction.DOWN) continue;
            o = BlockPos.removeChunkSectionLocalY(o);
            while (!((SkyLightStorage)this.lightStorage).hasLight(p) && !((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(p)) {
                p = ChunkSectionPos.offset(p, Direction.UP);
                o = BlockPos.add(o, 0, 16, 0);
            }
            ChunkNibbleArray lv5 = ((SkyLightStorage)this.lightStorage).getLightArray(p, true);
            if (o == m) continue;
            if (lv5 != null) {
                int r = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(lv5, o));
            } else {
                int n2 = s = ((SkyLightStorage)this.lightStorage).isLightEnabled(p) ? 0 : 15;
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
    protected void resetLevel(long l) {
        ((SkyLightStorage)this.lightStorage).updateAll();
        long m = ChunkSectionPos.fromGlobalPos(l);
        if (((SkyLightStorage)this.lightStorage).hasLight(m)) {
            super.resetLevel(l);
        } else {
            l = BlockPos.removeChunkSectionLocalY(l);
            while (!((SkyLightStorage)this.lightStorage).hasLight(m) && !((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(m)) {
                m = ChunkSectionPos.offset(m, Direction.UP);
                l = BlockPos.add(l, 0, 16, 0);
            }
            if (((SkyLightStorage)this.lightStorage).hasLight(m)) {
                super.resetLevel(l);
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String method_22875(long l) {
        return super.method_22875(l) + (((SkyLightStorage)this.lightStorage).isAboveTopmostLightArray(l) ? "*" : "");
    }
}

