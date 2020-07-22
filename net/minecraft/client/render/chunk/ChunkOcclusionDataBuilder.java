/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ChunkOcclusionDataBuilder {
    private static final int STEP_X = (int)Math.pow(16.0, 0.0);
    private static final int STEP_Z = (int)Math.pow(16.0, 1.0);
    private static final int STEP_Y = (int)Math.pow(16.0, 2.0);
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BitSet closed = new BitSet(4096);
    private static final int[] EDGE_POINTS = Util.make(new int[1352], is -> {
        boolean i = false;
        int j = 15;
        int k = 0;
        for (int l = 0; l < 16; ++l) {
            for (int m = 0; m < 16; ++m) {
                for (int n = 0; n < 16; ++n) {
                    if (l != 0 && l != 15 && m != 0 && m != 15 && n != 0 && n != 15) continue;
                    is[k++] = ChunkOcclusionDataBuilder.pack(l, m, n);
                }
            }
        }
    });
    private int openCount = 4096;

    public void markClosed(BlockPos pos) {
        this.closed.set(ChunkOcclusionDataBuilder.pack(pos), true);
        --this.openCount;
    }

    private static int pack(BlockPos pos) {
        return ChunkOcclusionDataBuilder.pack(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
    }

    private static int pack(int x, int y, int z) {
        return x << 0 | y << 8 | z << 4;
    }

    public ChunkOcclusionData build() {
        ChunkOcclusionData lv = new ChunkOcclusionData();
        if (4096 - this.openCount < 256) {
            lv.fill(true);
        } else if (this.openCount == 0) {
            lv.fill(false);
        } else {
            for (int i : EDGE_POINTS) {
                if (this.closed.get(i)) continue;
                lv.addOpenEdgeFaces(this.getOpenFaces(i));
            }
        }
        return lv;
    }

    private Set<Direction> getOpenFaces(int pos) {
        EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
        IntArrayFIFOQueue intPriorityQueue = new IntArrayFIFOQueue();
        intPriorityQueue.enqueue(pos);
        this.closed.set(pos, true);
        while (!intPriorityQueue.isEmpty()) {
            int j = intPriorityQueue.dequeueInt();
            this.addEdgeFaces(j, set);
            for (Direction lv : DIRECTIONS) {
                int k = this.offset(j, lv);
                if (k < 0 || this.closed.get(k)) continue;
                this.closed.set(k, true);
                intPriorityQueue.enqueue(k);
            }
        }
        return set;
    }

    private void addEdgeFaces(int pos, Set<Direction> openFaces) {
        int j = pos >> 0 & 0xF;
        if (j == 0) {
            openFaces.add(Direction.WEST);
        } else if (j == 15) {
            openFaces.add(Direction.EAST);
        }
        int k = pos >> 8 & 0xF;
        if (k == 0) {
            openFaces.add(Direction.DOWN);
        } else if (k == 15) {
            openFaces.add(Direction.UP);
        }
        int l = pos >> 4 & 0xF;
        if (l == 0) {
            openFaces.add(Direction.NORTH);
        } else if (l == 15) {
            openFaces.add(Direction.SOUTH);
        }
    }

    private int offset(int pos, Direction arg) {
        switch (arg) {
            case DOWN: {
                if ((pos >> 8 & 0xF) == 0) {
                    return -1;
                }
                return pos - STEP_Y;
            }
            case UP: {
                if ((pos >> 8 & 0xF) == 15) {
                    return -1;
                }
                return pos + STEP_Y;
            }
            case NORTH: {
                if ((pos >> 4 & 0xF) == 0) {
                    return -1;
                }
                return pos - STEP_Z;
            }
            case SOUTH: {
                if ((pos >> 4 & 0xF) == 15) {
                    return -1;
                }
                return pos + STEP_Z;
            }
            case WEST: {
                if ((pos >> 0 & 0xF) == 0) {
                    return -1;
                }
                return pos - STEP_X;
            }
            case EAST: {
                if ((pos >> 0 & 0xF) == 15) {
                    return -1;
                }
                return pos + STEP_X;
            }
        }
        return -1;
    }
}

