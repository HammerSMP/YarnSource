/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OceanMonumentGenerator {

    static class DoubleYZRoomFactory
    implements PieceFactory {
        private DoubleYZRoomFactory() {
        }

        @Override
        public boolean canGenerate(PieceSetting arg) {
            if (arg.neighborPresences[Direction.NORTH.getId()] && !arg.neighbors[Direction.NORTH.getId()].used && arg.neighborPresences[Direction.UP.getId()] && !arg.neighbors[Direction.UP.getId()].used) {
                PieceSetting lv = arg.neighbors[Direction.NORTH.getId()];
                return lv.neighborPresences[Direction.UP.getId()] && !lv.neighbors[Direction.UP.getId()].used;
            }
            return false;
        }

        @Override
        public Piece generate(Direction arg, PieceSetting arg2, Random random) {
            arg2.used = true;
            arg2.neighbors[Direction.NORTH.getId()].used = true;
            arg2.neighbors[Direction.UP.getId()].used = true;
            arg2.neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
            return new DoubleYZRoom(arg, arg2);
        }
    }

    static class DoubleXYRoomFactory
    implements PieceFactory {
        private DoubleXYRoomFactory() {
        }

        @Override
        public boolean canGenerate(PieceSetting arg) {
            if (arg.neighborPresences[Direction.EAST.getId()] && !arg.neighbors[Direction.EAST.getId()].used && arg.neighborPresences[Direction.UP.getId()] && !arg.neighbors[Direction.UP.getId()].used) {
                PieceSetting lv = arg.neighbors[Direction.EAST.getId()];
                return lv.neighborPresences[Direction.UP.getId()] && !lv.neighbors[Direction.UP.getId()].used;
            }
            return false;
        }

        @Override
        public Piece generate(Direction arg, PieceSetting arg2, Random random) {
            arg2.used = true;
            arg2.neighbors[Direction.EAST.getId()].used = true;
            arg2.neighbors[Direction.UP.getId()].used = true;
            arg2.neighbors[Direction.EAST.getId()].neighbors[Direction.UP.getId()].used = true;
            return new DoubleXYRoom(arg, arg2);
        }
    }

    static class DoubleZRoomFactory
    implements PieceFactory {
        private DoubleZRoomFactory() {
        }

        @Override
        public boolean canGenerate(PieceSetting arg) {
            return arg.neighborPresences[Direction.NORTH.getId()] && !arg.neighbors[Direction.NORTH.getId()].used;
        }

        @Override
        public Piece generate(Direction arg, PieceSetting arg2, Random random) {
            PieceSetting lv = arg2;
            if (!arg2.neighborPresences[Direction.NORTH.getId()] || arg2.neighbors[Direction.NORTH.getId()].used) {
                lv = arg2.neighbors[Direction.SOUTH.getId()];
            }
            lv.used = true;
            lv.neighbors[Direction.NORTH.getId()].used = true;
            return new DoubleZRoom(arg, lv);
        }
    }

    static class DoubleXRoomFactory
    implements PieceFactory {
        private DoubleXRoomFactory() {
        }

        @Override
        public boolean canGenerate(PieceSetting arg) {
            return arg.neighborPresences[Direction.EAST.getId()] && !arg.neighbors[Direction.EAST.getId()].used;
        }

        @Override
        public Piece generate(Direction arg, PieceSetting arg2, Random random) {
            arg2.used = true;
            arg2.neighbors[Direction.EAST.getId()].used = true;
            return new DoubleXRoom(arg, arg2);
        }
    }

    static class DoubleYRoomFactory
    implements PieceFactory {
        private DoubleYRoomFactory() {
        }

        @Override
        public boolean canGenerate(PieceSetting arg) {
            return arg.neighborPresences[Direction.UP.getId()] && !arg.neighbors[Direction.UP.getId()].used;
        }

        @Override
        public Piece generate(Direction arg, PieceSetting arg2, Random random) {
            arg2.used = true;
            arg2.neighbors[Direction.UP.getId()].used = true;
            return new DoubleYRoom(arg, arg2);
        }
    }

    static class SimpleRoomTopFactory
    implements PieceFactory {
        private SimpleRoomTopFactory() {
        }

        @Override
        public boolean canGenerate(PieceSetting arg) {
            return !arg.neighborPresences[Direction.WEST.getId()] && !arg.neighborPresences[Direction.EAST.getId()] && !arg.neighborPresences[Direction.NORTH.getId()] && !arg.neighborPresences[Direction.SOUTH.getId()] && !arg.neighborPresences[Direction.UP.getId()];
        }

        @Override
        public Piece generate(Direction arg, PieceSetting arg2, Random random) {
            arg2.used = true;
            return new SimpleRoomTop(arg, arg2);
        }
    }

    static class SimpleRoomFactory
    implements PieceFactory {
        private SimpleRoomFactory() {
        }

        @Override
        public boolean canGenerate(PieceSetting arg) {
            return true;
        }

        @Override
        public Piece generate(Direction arg, PieceSetting arg2, Random random) {
            arg2.used = true;
            return new SimpleRoom(arg, arg2, random);
        }
    }

    static interface PieceFactory {
        public boolean canGenerate(PieceSetting var1);

        public Piece generate(Direction var1, PieceSetting var2, Random var3);
    }

    static class PieceSetting {
        private final int roomIndex;
        private final PieceSetting[] neighbors = new PieceSetting[6];
        private final boolean[] neighborPresences = new boolean[6];
        private boolean used;
        private boolean field_14484;
        private int field_14483;

        public PieceSetting(int i) {
            this.roomIndex = i;
        }

        public void setNeighbor(Direction arg, PieceSetting arg2) {
            this.neighbors[arg.getId()] = arg2;
            arg2.neighbors[arg.getOpposite().getId()] = this;
        }

        public void checkNeighborStates() {
            for (int i = 0; i < 6; ++i) {
                this.neighborPresences[i] = this.neighbors[i] != null;
            }
        }

        public boolean method_14783(int i) {
            if (this.field_14484) {
                return true;
            }
            this.field_14483 = i;
            for (int j = 0; j < 6; ++j) {
                if (this.neighbors[j] == null || !this.neighborPresences[j] || this.neighbors[j].field_14483 == i || !this.neighbors[j].method_14783(i)) continue;
                return true;
            }
            return false;
        }

        public boolean isAboveLevelThree() {
            return this.roomIndex >= 75;
        }

        public int countNeighbors() {
            int i = 0;
            for (int j = 0; j < 6; ++j) {
                if (!this.neighborPresences[j]) continue;
                ++i;
            }
            return i;
        }
    }

    public static class Penthouse
    extends Piece {
        public Penthouse(Direction arg, BlockBox arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, arg, arg2);
        }

        public Penthouse(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline((WorldAccess)arg, arg4, 2, -1, 2, 11, -1, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, -1, 0, 1, -1, 11, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 12, -1, 0, 13, -1, 11, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, -1, 0, 11, -1, 1, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, -1, 12, 11, -1, 13, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 0, 0, 0, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 13, 0, 0, 13, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 0, 0, 12, 0, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 0, 13, 12, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            for (int i = 2; i <= 11; i += 3) {
                this.addBlock(arg, SEA_LANTERN, 0, 0, i, arg4);
                this.addBlock(arg, SEA_LANTERN, 13, 0, i, arg4);
                this.addBlock(arg, SEA_LANTERN, i, 0, 0, arg4);
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 0, 3, 4, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 9, 0, 3, 11, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 4, 0, 9, 9, 0, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(arg, PRISMARINE_BRICKS, 5, 0, 8, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 8, 0, 8, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 10, 0, 10, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 3, 0, 10, arg4);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
            int j = 3;
            for (int k = 0; k < 2; ++k) {
                for (int l = 2; l <= 8; l += 3) {
                    this.fillWithOutline((WorldAccess)arg, arg4, j, 0, l, j, 2, l, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                j = 10;
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 0, 10, 5, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 8, 0, 10, 8, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.setAirAndWater(arg, arg4, 6, -1, 3, 7, -1, 4);
            this.method_14772(arg, arg4, 6, 1, 6);
            return true;
        }
    }

    public static class WingRoom
    extends Piece {
        private int field_14481;

        public WingRoom(Direction arg, BlockBox arg2, int i) {
            super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, arg, arg2);
            this.field_14481 = i & 1;
        }

        public WingRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            if (this.field_14481 == 0) {
                for (int i = 0; i < 4; ++i) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 0, 6, 15, 0, 16, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, 0, 6, 6, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 16, 0, 6, 16, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 7, 7, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 15, 1, 7, 15, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 6, 9, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 13, 1, 6, 15, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 8, 1, 7, 9, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 13, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 9, 0, 5, 13, 0, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
                for (int j = 18; j >= 7; j -= 3) {
                    this.addBlock(arg, SEA_LANTERN, 6, 3, j, arg4);
                    this.addBlock(arg, SEA_LANTERN, 16, 3, j, arg4);
                }
                this.addBlock(arg, SEA_LANTERN, 10, 0, 10, arg4);
                this.addBlock(arg, SEA_LANTERN, 12, 0, 10, arg4);
                this.addBlock(arg, SEA_LANTERN, 10, 0, 12, arg4);
                this.addBlock(arg, SEA_LANTERN, 12, 0, 12, arg4);
                this.addBlock(arg, SEA_LANTERN, 8, 3, 6, arg4);
                this.addBlock(arg, SEA_LANTERN, 14, 3, 6, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 4, 2, 4, arg4);
                this.addBlock(arg, SEA_LANTERN, 4, 1, 4, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 4, 0, 4, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 18, 2, 4, arg4);
                this.addBlock(arg, SEA_LANTERN, 18, 1, 4, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 18, 0, 4, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 4, 2, 18, arg4);
                this.addBlock(arg, SEA_LANTERN, 4, 1, 18, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 4, 0, 18, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 18, 2, 18, arg4);
                this.addBlock(arg, SEA_LANTERN, 18, 1, 18, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 18, 0, 18, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 9, 7, 20, arg4);
                this.addBlock(arg, PRISMARINE_BRICKS, 13, 7, 20, arg4);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, 0, 21, 7, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 15, 0, 21, 16, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.method_14772(arg, arg4, 11, 2, 16);
            } else if (this.field_14481 == 1) {
                this.fillWithOutline((WorldAccess)arg, arg4, 9, 3, 18, 13, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 9, 0, 18, 9, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 13, 0, 18, 13, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                int k = 9;
                int l = 20;
                int m = 5;
                for (int n = 0; n < 2; ++n) {
                    this.addBlock(arg, PRISMARINE_BRICKS, k, 6, 20, arg4);
                    this.addBlock(arg, SEA_LANTERN, k, 5, 20, arg4);
                    this.addBlock(arg, PRISMARINE_BRICKS, k, 4, 20, arg4);
                    k = 13;
                }
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 3, 7, 15, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                k = 10;
                for (int o = 0; o < 2; ++o) {
                    this.fillWithOutline((WorldAccess)arg, arg4, k, 0, 10, k, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, 0, 12, k, 6, 12, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.addBlock(arg, SEA_LANTERN, k, 0, 10, arg4);
                    this.addBlock(arg, SEA_LANTERN, k, 0, 12, arg4);
                    this.addBlock(arg, SEA_LANTERN, k, 4, 10, arg4);
                    this.addBlock(arg, SEA_LANTERN, k, 4, 12, arg4);
                    k = 12;
                }
                k = 8;
                for (int p = 0; p < 2; ++p) {
                    this.fillWithOutline((WorldAccess)arg, arg4, k, 0, 7, k, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, 0, 14, k, 2, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    k = 14;
                }
                this.fillWithOutline((WorldAccess)arg, arg4, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.method_14772(arg, arg4, 11, 5, 13);
            }
            return true;
        }
    }

    public static class CoreRoom
    extends Piece {
        public CoreRoom(Direction arg, PieceSetting arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, arg, arg2, 2, 2, 2);
        }

        public CoreRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.method_14771(arg, arg4, 1, 8, 0, 14, 8, 14, PRISMARINE);
            int i = 7;
            BlockState lv = PRISMARINE_BRICKS;
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 7, 0, 0, 7, 15, lv, lv, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 15, 7, 0, 15, 7, 15, lv, lv, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 7, 0, 15, 7, 0, lv, lv, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 7, 15, 14, 7, 15, lv, lv, false);
            for (int j = 1; j <= 6; ++j) {
                BlockState lv2 = PRISMARINE_BRICKS;
                if (j == 2 || j == 6) {
                    lv2 = PRISMARINE;
                }
                for (int k = 0; k <= 15; k += 15) {
                    this.fillWithOutline((WorldAccess)arg, arg4, k, j, 0, k, j, 1, lv2, lv2, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, j, 6, k, j, 9, lv2, lv2, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, j, 14, k, j, 15, lv2, lv2, false);
                }
                this.fillWithOutline((WorldAccess)arg, arg4, 1, j, 0, 1, j, 0, lv2, lv2, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, j, 0, 9, j, 0, lv2, lv2, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 14, j, 0, 14, j, 0, lv2, lv2, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, j, 15, 14, j, 15, lv2, lv2, false);
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), false);
            for (int l = 3; l <= 6; l += 3) {
                for (int m = 6; m <= 9; m += 3) {
                    this.addBlock(arg, SEA_LANTERN, m, l, 6, arg4);
                    this.addBlock(arg, SEA_LANTERN, m, l, 9, arg4);
                }
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 6, 5, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 9, 5, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 1, 6, 10, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 1, 9, 10, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 5, 6, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 9, 1, 5, 9, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 10, 6, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 9, 1, 10, 9, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 2, 5, 5, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 2, 10, 5, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 2, 5, 10, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 2, 10, 10, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 7, 1, 5, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 7, 1, 10, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 7, 9, 5, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 7, 9, 10, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 7, 5, 6, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 7, 10, 6, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 9, 7, 5, 14, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 9, 7, 10, 14, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 1, 2, 2, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 2, 3, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 13, 1, 2, 13, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 12, 1, 2, 12, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 1, 12, 2, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 13, 3, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 13, 1, 12, 13, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 12, 1, 13, 12, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            return true;
        }
    }

    public static class DoubleYZRoom
    extends Piece {
        public DoubleYZRoom(Direction arg, PieceSetting arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, 1, arg, arg2, 1, 2, 2);
        }

        public DoubleYZRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            PieceSetting lv = this.setting.neighbors[Direction.NORTH.getId()];
            PieceSetting lv2 = this.setting;
            PieceSetting lv3 = lv.neighbors[Direction.UP.getId()];
            PieceSetting lv4 = lv2.neighbors[Direction.UP.getId()];
            if (this.setting.roomIndex / 25 > 0) {
                this.method_14774(arg, arg4, 0, 8, lv.neighborPresences[Direction.DOWN.getId()]);
                this.method_14774(arg, arg4, 0, 0, lv2.neighborPresences[Direction.DOWN.getId()]);
            }
            if (lv4.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 8, 1, 6, 8, 7, PRISMARINE);
            }
            if (lv3.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 8, 8, 6, 8, 14, PRISMARINE);
            }
            for (int i = 1; i <= 7; ++i) {
                BlockState lv5 = PRISMARINE_BRICKS;
                if (i == 2 || i == 6) {
                    lv5 = PRISMARINE;
                }
                this.fillWithOutline((WorldAccess)arg, arg4, 0, i, 0, 0, i, 15, lv5, lv5, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, i, 0, 7, i, 15, lv5, lv5, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, i, 0, 6, i, 0, lv5, lv5, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, i, 15, 6, i, 15, lv5, lv5, false);
            }
            for (int j = 1; j <= 7; ++j) {
                BlockState lv6 = DARK_PRISMARINE;
                if (j == 2 || j == 6) {
                    lv6 = SEA_LANTERN;
                }
                this.fillWithOutline((WorldAccess)arg, arg4, 3, j, 7, 4, j, 8, lv6, lv6, false);
            }
            if (lv2.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 0, 4, 2, 0);
            }
            if (lv2.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 7, 1, 3, 7, 2, 4);
            }
            if (lv2.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 1, 3, 0, 2, 4);
            }
            if (lv.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 15, 4, 2, 15);
            }
            if (lv.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 1, 11, 0, 2, 12);
            }
            if (lv.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 7, 1, 11, 7, 2, 12);
            }
            if (lv4.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 5, 0, 4, 6, 0);
            }
            if (lv4.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 7, 5, 3, 7, 6, 4);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 4, 2, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 2, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 5, 6, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (lv4.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 5, 3, 0, 6, 4);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 4, 2, 2, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 2, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 5, 1, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (lv3.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 5, 15, 4, 6, 15);
            }
            if (lv3.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 5, 11, 0, 6, 12);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 4, 10, 2, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 10, 1, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 13, 1, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (lv3.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 7, 5, 11, 7, 6, 12);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 4, 10, 6, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 10, 6, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 13, 6, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            return true;
        }
    }

    public static class DoubleXYRoom
    extends Piece {
        public DoubleXYRoom(Direction arg, PieceSetting arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, 1, arg, arg2, 2, 2, 1);
        }

        public DoubleXYRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            PieceSetting lv = this.setting.neighbors[Direction.EAST.getId()];
            PieceSetting lv2 = this.setting;
            PieceSetting lv3 = lv2.neighbors[Direction.UP.getId()];
            PieceSetting lv4 = lv.neighbors[Direction.UP.getId()];
            if (this.setting.roomIndex / 25 > 0) {
                this.method_14774(arg, arg4, 8, 0, lv.neighborPresences[Direction.DOWN.getId()]);
                this.method_14774(arg, arg4, 0, 0, lv2.neighborPresences[Direction.DOWN.getId()]);
            }
            if (lv3.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 8, 1, 7, 8, 6, PRISMARINE);
            }
            if (lv4.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 8, 8, 1, 14, 8, 6, PRISMARINE);
            }
            for (int i = 1; i <= 7; ++i) {
                BlockState lv5 = PRISMARINE_BRICKS;
                if (i == 2 || i == 6) {
                    lv5 = PRISMARINE;
                }
                this.fillWithOutline((WorldAccess)arg, arg4, 0, i, 0, 0, i, 7, lv5, lv5, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 15, i, 0, 15, i, 7, lv5, lv5, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, i, 0, 15, i, 0, lv5, lv5, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, i, 7, 14, i, 7, lv5, lv5, false);
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 1, 3, 2, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 2, 4, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 5, 4, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 13, 1, 3, 13, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 11, 1, 2, 12, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 11, 1, 5, 12, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 3, 5, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 1, 3, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 7, 2, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 5, 2, 5, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 5, 2, 10, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 5, 5, 5, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 10, 5, 5, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(arg, PRISMARINE_BRICKS, 6, 6, 2, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 9, 6, 2, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 6, 6, 5, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 9, 6, 5, arg4);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 4, 3, 6, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 9, 4, 3, 10, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(arg, SEA_LANTERN, 5, 4, 2, arg4);
            this.addBlock(arg, SEA_LANTERN, 5, 4, 5, arg4);
            this.addBlock(arg, SEA_LANTERN, 10, 4, 2, arg4);
            this.addBlock(arg, SEA_LANTERN, 10, 4, 5, arg4);
            if (lv2.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 0, 4, 2, 0);
            }
            if (lv2.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 7, 4, 2, 7);
            }
            if (lv2.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 1, 3, 0, 2, 4);
            }
            if (lv.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 11, 1, 0, 12, 2, 0);
            }
            if (lv.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 11, 1, 7, 12, 2, 7);
            }
            if (lv.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 15, 1, 3, 15, 2, 4);
            }
            if (lv3.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 5, 0, 4, 6, 0);
            }
            if (lv3.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 5, 7, 4, 6, 7);
            }
            if (lv3.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 5, 3, 0, 6, 4);
            }
            if (lv4.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 11, 5, 0, 12, 6, 0);
            }
            if (lv4.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 11, 5, 7, 12, 6, 7);
            }
            if (lv4.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 15, 5, 3, 15, 6, 4);
            }
            return true;
        }
    }

    public static class DoubleZRoom
    extends Piece {
        public DoubleZRoom(Direction arg, PieceSetting arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, arg, arg2, 1, 1, 2);
        }

        public DoubleZRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            PieceSetting lv = this.setting.neighbors[Direction.NORTH.getId()];
            PieceSetting lv2 = this.setting;
            if (this.setting.roomIndex / 25 > 0) {
                this.method_14774(arg, arg4, 0, 8, lv.neighborPresences[Direction.DOWN.getId()]);
                this.method_14774(arg, arg4, 0, 0, lv2.neighborPresences[Direction.DOWN.getId()]);
            }
            if (lv2.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 4, 1, 6, 4, 7, PRISMARINE);
            }
            if (lv.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 4, 8, 6, 4, 14, PRISMARINE);
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 0, 0, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 3, 0, 7, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 15, 6, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 0, 0, 2, 15, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 2, 0, 7, 2, 15, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 0, 7, 2, 0, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 15, 6, 2, 15, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 0, 0, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 0, 7, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 0, 7, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 15, 6, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 1, 1, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 1, 6, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 1, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 3, 1, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 13, 1, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 13, 6, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 13, 1, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 3, 13, 6, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 1, 6, 2, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 6, 5, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 1, 9, 2, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 9, 5, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 2, 6, 4, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 2, 9, 4, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 2, 7, 2, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 2, 7, 5, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(arg, SEA_LANTERN, 2, 2, 5, arg4);
            this.addBlock(arg, SEA_LANTERN, 5, 2, 5, arg4);
            this.addBlock(arg, SEA_LANTERN, 2, 2, 10, arg4);
            this.addBlock(arg, SEA_LANTERN, 5, 2, 10, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 2, 3, 5, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 5, 3, 5, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 2, 3, 10, arg4);
            this.addBlock(arg, PRISMARINE_BRICKS, 5, 3, 10, arg4);
            if (lv2.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 0, 4, 2, 0);
            }
            if (lv2.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 7, 1, 3, 7, 2, 4);
            }
            if (lv2.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 1, 3, 0, 2, 4);
            }
            if (lv.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 15, 4, 2, 15);
            }
            if (lv.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 1, 11, 0, 2, 12);
            }
            if (lv.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 7, 1, 11, 7, 2, 12);
            }
            return true;
        }
    }

    public static class DoubleXRoom
    extends Piece {
        public DoubleXRoom(Direction arg, PieceSetting arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, arg, arg2, 2, 1, 1);
        }

        public DoubleXRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            PieceSetting lv = this.setting.neighbors[Direction.EAST.getId()];
            PieceSetting lv2 = this.setting;
            if (this.setting.roomIndex / 25 > 0) {
                this.method_14774(arg, arg4, 8, 0, lv.neighborPresences[Direction.DOWN.getId()]);
                this.method_14774(arg, arg4, 0, 0, lv2.neighborPresences[Direction.DOWN.getId()]);
            }
            if (lv2.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 4, 1, 7, 4, 6, PRISMARINE);
            }
            if (lv.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 8, 4, 1, 14, 4, 6, PRISMARINE);
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 15, 3, 0, 15, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 0, 15, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 7, 14, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 0, 0, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 15, 2, 0, 15, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 0, 15, 2, 0, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 7, 14, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 15, 1, 0, 15, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 0, 15, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 0, 10, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 2, 0, 9, 2, 3, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 3, 0, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(arg, SEA_LANTERN, 6, 2, 3, arg4);
            this.addBlock(arg, SEA_LANTERN, 9, 2, 3, arg4);
            if (lv2.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 0, 4, 2, 0);
            }
            if (lv2.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 7, 4, 2, 7);
            }
            if (lv2.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 1, 3, 0, 2, 4);
            }
            if (lv.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 11, 1, 0, 12, 2, 0);
            }
            if (lv.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 11, 1, 7, 12, 2, 7);
            }
            if (lv.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 15, 1, 3, 15, 2, 4);
            }
            return true;
        }
    }

    public static class DoubleYRoom
    extends Piece {
        public DoubleYRoom(Direction arg, PieceSetting arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, arg, arg2, 1, 2, 1);
        }

        public DoubleYRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            PieceSetting lv;
            if (this.setting.roomIndex / 25 > 0) {
                this.method_14774(arg, arg4, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
            }
            if ((lv = this.setting.neighbors[Direction.UP.getId()]).neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 8, 1, 6, 8, 6, PRISMARINE);
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 4, 0, 0, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 4, 0, 7, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 4, 0, 6, 4, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 4, 7, 6, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 4, 1, 2, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 4, 2, 1, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 4, 1, 5, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 4, 2, 6, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 2, 4, 5, 2, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 4, 5, 1, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 4, 5, 5, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 4, 5, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            PieceSetting lv2 = this.setting;
            for (int i = 1; i <= 5; i += 4) {
                int j = 0;
                if (lv2.neighborPresences[Direction.SOUTH.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
                }
                j = 7;
                if (lv2.neighborPresences[Direction.NORTH.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
                }
                int k = 0;
                if (lv2.neighborPresences[Direction.WEST.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
                }
                k = 7;
                if (lv2.neighborPresences[Direction.EAST.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
                }
                lv2 = lv;
            }
            return true;
        }
    }

    public static class SimpleRoomTop
    extends Piece {
        public SimpleRoomTop(Direction arg, PieceSetting arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, arg, arg2, 1, 1, 1);
        }

        public SimpleRoomTop(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            if (this.setting.roomIndex / 25 > 0) {
                this.method_14774(arg, arg4, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
            }
            if (this.setting.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 4, 1, 6, 4, 6, PRISMARINE);
            }
            for (int i = 1; i <= 6; ++i) {
                for (int j = 1; j <= 6; ++j) {
                    if (random.nextInt(3) == 0) continue;
                    int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
                    BlockState lv = Blocks.WET_SPONGE.getDefaultState();
                    this.fillWithOutline((WorldAccess)arg, arg4, i, k, j, i, 3, j, lv, lv, false);
                }
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 0, 4, 2, 0);
            }
            return true;
        }
    }

    public static class SimpleRoom
    extends Piece {
        private int field_14480;

        public SimpleRoom(Direction arg, PieceSetting arg2, Random random) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, arg, arg2, 1, 1, 1);
            this.field_14480 = random.nextInt(3);
        }

        public SimpleRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            boolean bl;
            if (this.setting.roomIndex / 25 > 0) {
                this.method_14774(arg, arg4, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
            }
            if (this.setting.neighbors[Direction.UP.getId()] == null) {
                this.method_14771(arg, arg4, 1, 4, 1, 6, 4, 6, PRISMARINE);
            }
            boolean bl2 = bl = this.field_14480 != 0 && random.nextBoolean() && !this.setting.neighborPresences[Direction.DOWN.getId()] && !this.setting.neighborPresences[Direction.UP.getId()] && this.setting.countNeighbors() > 1;
            if (this.field_14480 == 0) {
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 0, 2, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 0, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 0, 0, 2, 2, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 0, 2, 2, 0, PRISMARINE, PRISMARINE, false);
                this.addBlock(arg, SEA_LANTERN, 1, 2, 1, arg4);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 0, 7, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 3, 0, 7, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 2, 0, 7, 2, 2, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
                this.addBlock(arg, SEA_LANTERN, 6, 2, 1, arg4);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 5, 2, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 5, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 5, 0, 2, 7, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 7, 2, 2, 7, PRISMARINE, PRISMARINE, false);
                this.addBlock(arg, SEA_LANTERN, 1, 2, 6, arg4);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 5, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 3, 5, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 2, 5, 7, 2, 7, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
                this.addBlock(arg, SEA_LANTERN, 6, 2, 6, arg4);
                if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, 3, 0, 4, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, 3, 0, 4, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, 2, 0, 4, 2, 0, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 0, 4, 1, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, 3, 7, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, 3, 6, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, 2, 7, 4, 2, 7, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 6, 4, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (this.setting.neighborPresences[Direction.WEST.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 3, 0, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 3, 1, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 3, 0, 2, 4, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 3, 1, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (this.setting.neighborPresences[Direction.EAST.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 7, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline((WorldAccess)arg, arg4, 6, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 7, 2, 3, 7, 2, 4, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 3, 7, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
            } else if (this.field_14480 == 1) {
                this.fillWithOutline((WorldAccess)arg, arg4, 2, 1, 2, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 2, 1, 5, 2, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 5, 5, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 2, 5, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(arg, SEA_LANTERN, 2, 2, 2, arg4);
                this.addBlock(arg, SEA_LANTERN, 2, 2, 5, arg4);
                this.addBlock(arg, SEA_LANTERN, 5, 2, 5, arg4);
                this.addBlock(arg, SEA_LANTERN, 5, 2, 2, arg4);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 0, 1, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 1, 0, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 7, 1, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 6, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 6, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 6, 1, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 1, 7, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(arg, PRISMARINE, 1, 2, 0, arg4);
                this.addBlock(arg, PRISMARINE, 0, 2, 1, arg4);
                this.addBlock(arg, PRISMARINE, 1, 2, 7, arg4);
                this.addBlock(arg, PRISMARINE, 0, 2, 6, arg4);
                this.addBlock(arg, PRISMARINE, 6, 2, 7, arg4);
                this.addBlock(arg, PRISMARINE, 7, 2, 6, arg4);
                this.addBlock(arg, PRISMARINE, 6, 2, 0, arg4);
                this.addBlock(arg, PRISMARINE, 7, 2, 1, arg4);
                if (!this.setting.neighborPresences[Direction.SOUTH.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (!this.setting.neighborPresences[Direction.NORTH.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (!this.setting.neighborPresences[Direction.WEST.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 1, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 1, 0, 2, 6, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 1, 0, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (!this.setting.neighborPresences[Direction.EAST.getId()]) {
                    this.fillWithOutline((WorldAccess)arg, arg4, 7, 3, 1, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 7, 2, 1, 7, 2, 6, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 1, 7, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
            } else if (this.field_14480 == 2) {
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
                    this.setAirAndWater(arg, arg4, 3, 1, 0, 4, 2, 0);
                }
                if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
                    this.setAirAndWater(arg, arg4, 3, 1, 7, 4, 2, 7);
                }
                if (this.setting.neighborPresences[Direction.WEST.getId()]) {
                    this.setAirAndWater(arg, arg4, 0, 1, 3, 0, 2, 4);
                }
                if (this.setting.neighborPresences[Direction.EAST.getId()]) {
                    this.setAirAndWater(arg, arg4, 7, 1, 3, 7, 2, 4);
                }
            }
            if (bl) {
                this.fillWithOutline((WorldAccess)arg, arg4, 3, 1, 3, 4, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 3, 2, 3, 4, 2, 4, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline((WorldAccess)arg, arg4, 3, 3, 3, 4, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            return true;
        }
    }

    public static class Entry
    extends Piece {
        public Entry(Direction arg, PieceSetting arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, arg, arg2, 1, 1, 1);
        }

        public Entry(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, arg2);
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 3, 0, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 2, 0, 1, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 6, 2, 0, 7, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 1, 1, 0, 2, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 5, 1, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
                this.setAirAndWater(arg, arg4, 3, 1, 7, 4, 2, 7);
            }
            if (this.setting.neighborPresences[Direction.WEST.getId()]) {
                this.setAirAndWater(arg, arg4, 0, 1, 3, 1, 2, 4);
            }
            if (this.setting.neighborPresences[Direction.EAST.getId()]) {
                this.setAirAndWater(arg, arg4, 6, 1, 3, 7, 2, 4);
            }
            return true;
        }
    }

    public static class Base
    extends Piece {
        private PieceSetting field_14464;
        private PieceSetting field_14466;
        private final List<Piece> field_14465 = Lists.newArrayList();

        public Base(Random random, int i, int j, Direction arg) {
            super(StructurePieceType.OCEAN_MONUMENT_BASE, 0);
            this.setOrientation(arg);
            Direction lv = this.getFacing();
            this.boundingBox = lv.getAxis() == Direction.Axis.Z ? new BlockBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1) : new BlockBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
            List<PieceSetting> list = this.method_14760(random);
            this.field_14464.used = true;
            this.field_14465.add(new Entry(lv, this.field_14464));
            this.field_14465.add(new CoreRoom(lv, this.field_14466));
            ArrayList list2 = Lists.newArrayList();
            list2.add(new DoubleXYRoomFactory());
            list2.add(new DoubleYZRoomFactory());
            list2.add(new DoubleZRoomFactory());
            list2.add(new DoubleXRoomFactory());
            list2.add(new DoubleYRoomFactory());
            list2.add(new SimpleRoomTopFactory());
            list2.add(new SimpleRoomFactory());
            block0: for (PieceSetting lv2 : list) {
                if (lv2.used || lv2.isAboveLevelThree()) continue;
                for (Object lv3 : list2) {
                    if (!lv3.canGenerate(lv2)) continue;
                    this.field_14465.add(lv3.generate(lv, lv2, random));
                    continue block0;
                }
            }
            int k = this.boundingBox.minY;
            int l = this.applyXTransform(9, 22);
            int m = this.applyZTransform(9, 22);
            for (Piece lv4 : this.field_14465) {
                lv4.getBoundingBox().offset(l, k, m);
            }
            BlockBox lv5 = BlockBox.create(this.applyXTransform(1, 1), this.applyYTransform(1), this.applyZTransform(1, 1), this.applyXTransform(23, 21), this.applyYTransform(8), this.applyZTransform(23, 21));
            BlockBox lv6 = BlockBox.create(this.applyXTransform(34, 1), this.applyYTransform(1), this.applyZTransform(34, 1), this.applyXTransform(56, 21), this.applyYTransform(8), this.applyZTransform(56, 21));
            BlockBox lv7 = BlockBox.create(this.applyXTransform(22, 22), this.applyYTransform(13), this.applyZTransform(22, 22), this.applyXTransform(35, 35), this.applyYTransform(17), this.applyZTransform(35, 35));
            int n = random.nextInt();
            this.field_14465.add(new WingRoom(lv, lv5, n++));
            this.field_14465.add(new WingRoom(lv, lv6, n++));
            this.field_14465.add(new Penthouse(lv, lv7));
        }

        public Base(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_MONUMENT_BASE, arg2);
        }

        private List<PieceSetting> method_14760(Random random) {
            PieceSetting[] lvs = new PieceSetting[75];
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 4; ++j) {
                    boolean k = false;
                    int l = Base.getIndex(i, 0, j);
                    lvs[l] = new PieceSetting(l);
                }
            }
            for (int m = 0; m < 5; ++m) {
                for (int n = 0; n < 4; ++n) {
                    boolean o = true;
                    int p = Base.getIndex(m, 1, n);
                    lvs[p] = new PieceSetting(p);
                }
            }
            for (int q = 1; q < 4; ++q) {
                for (int r = 0; r < 2; ++r) {
                    int s = 2;
                    int t = Base.getIndex(q, 2, r);
                    lvs[t] = new PieceSetting(t);
                }
            }
            this.field_14464 = lvs[TWO_ZERO_ZERO_INDEX];
            for (int u = 0; u < 5; ++u) {
                for (int v = 0; v < 5; ++v) {
                    for (int w = 0; w < 3; ++w) {
                        int x = Base.getIndex(u, w, v);
                        if (lvs[x] == null) continue;
                        for (Direction lv : Direction.values()) {
                            int ab;
                            int y = u + lv.getOffsetX();
                            int z = w + lv.getOffsetY();
                            int aa = v + lv.getOffsetZ();
                            if (y < 0 || y >= 5 || aa < 0 || aa >= 5 || z < 0 || z >= 3 || lvs[ab = Base.getIndex(y, z, aa)] == null) continue;
                            if (aa == v) {
                                lvs[x].setNeighbor(lv, lvs[ab]);
                                continue;
                            }
                            lvs[x].setNeighbor(lv.getOpposite(), lvs[ab]);
                        }
                    }
                }
            }
            PieceSetting lv2 = new PieceSetting(1003);
            PieceSetting lv3 = new PieceSetting(1001);
            PieceSetting lv4 = new PieceSetting(1002);
            lvs[TWO_TWO_ZERO_INDEX].setNeighbor(Direction.UP, lv2);
            lvs[ZERO_ONE_ZERO_INDEX].setNeighbor(Direction.SOUTH, lv3);
            lvs[FOUR_ONE_ZERO_INDEX].setNeighbor(Direction.SOUTH, lv4);
            lv2.used = true;
            lv3.used = true;
            lv4.used = true;
            this.field_14464.field_14484 = true;
            this.field_14466 = lvs[Base.getIndex(random.nextInt(4), 0, 2)];
            this.field_14466.used = true;
            this.field_14466.neighbors[Direction.EAST.getId()].used = true;
            this.field_14466.neighbors[Direction.NORTH.getId()].used = true;
            this.field_14466.neighbors[Direction.EAST.getId()].neighbors[Direction.NORTH.getId()].used = true;
            this.field_14466.neighbors[Direction.UP.getId()].used = true;
            this.field_14466.neighbors[Direction.EAST.getId()].neighbors[Direction.UP.getId()].used = true;
            this.field_14466.neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
            this.field_14466.neighbors[Direction.EAST.getId()].neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
            ArrayList list = Lists.newArrayList();
            for (PieceSetting lv5 : lvs) {
                if (lv5 == null) continue;
                lv5.checkNeighborStates();
                list.add(lv5);
            }
            lv2.checkNeighborStates();
            Collections.shuffle(list, random);
            int ac = 1;
            for (PieceSetting lv6 : list) {
                int ad = 0;
                for (int ae = 0; ad < 2 && ae < 5; ++ae) {
                    int af = random.nextInt(6);
                    if (!lv6.neighborPresences[af]) continue;
                    int ag = Direction.byId(af).getOpposite().getId();
                    ((PieceSetting)lv6).neighborPresences[af] = false;
                    ((PieceSetting)((PieceSetting)lv6).neighbors[af]).neighborPresences[ag] = false;
                    if (lv6.method_14783(ac++) && lv6.neighbors[af].method_14783(ac++)) {
                        ++ad;
                        continue;
                    }
                    ((PieceSetting)lv6).neighborPresences[af] = true;
                    ((PieceSetting)((PieceSetting)lv6).neighbors[af]).neighborPresences[ag] = true;
                }
            }
            list.add(lv2);
            list.add(lv3);
            list.add(lv4);
            return list;
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            int i = Math.max(arg.getSeaLevel(), 64) - this.boundingBox.minY;
            this.setAirAndWater(arg, arg4, 0, 0, 0, 58, i, 58);
            this.method_14761(false, 0, arg, random, arg4);
            this.method_14761(true, 33, arg, random, arg4);
            this.method_14763(arg, random, arg4);
            this.method_14762(arg, random, arg4);
            this.method_14765(arg, random, arg4);
            this.method_14764(arg, random, arg4);
            this.method_14766(arg, random, arg4);
            this.method_14767(arg, random, arg4);
            for (int j = 0; j < 7; ++j) {
                int k = 0;
                while (k < 7) {
                    if (k == 0 && j == 3) {
                        k = 6;
                    }
                    int l = j * 9;
                    int m = k * 9;
                    for (int n = 0; n < 4; ++n) {
                        for (int o = 0; o < 4; ++o) {
                            this.addBlock(arg, PRISMARINE_BRICKS, l + n, 0, m + o, arg4);
                            this.method_14936(arg, PRISMARINE_BRICKS, l + n, -1, m + o, arg4);
                        }
                    }
                    if (j == 0 || j == 6) {
                        ++k;
                        continue;
                    }
                    k += 6;
                }
            }
            for (int p = 0; p < 5; ++p) {
                this.setAirAndWater(arg, arg4, -1 - p, 0 + p * 2, -1 - p, -1 - p, 23, 58 + p);
                this.setAirAndWater(arg, arg4, 58 + p, 0 + p * 2, -1 - p, 58 + p, 23, 58 + p);
                this.setAirAndWater(arg, arg4, 0 - p, 0 + p * 2, -1 - p, 57 + p, 23, -1 - p);
                this.setAirAndWater(arg, arg4, 0 - p, 0 + p * 2, 58 + p, 57 + p, 23, 58 + p);
            }
            for (Piece lv : this.field_14465) {
                if (!lv.getBoundingBox().intersects(arg4)) continue;
                lv.generate(arg, arg2, arg3, random, arg4, arg5, arg6);
            }
            return true;
        }

        private void method_14761(boolean bl, int i, WorldAccess arg, Random random, BlockBox arg2) {
            int j = 24;
            if (this.method_14775(arg2, i, 0, i + 23, 20)) {
                this.fillWithOutline(arg, arg2, i + 0, 0, 0, i + 24, 0, 20, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, i + 0, 1, 0, i + 24, 10, 20);
                for (int k = 0; k < 4; ++k) {
                    this.fillWithOutline(arg, arg2, i + k, k + 1, k, i + k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, i + k + 7, k + 5, k + 7, i + k + 7, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, i + 17 - k, k + 5, k + 7, i + 17 - k, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, i + 24 - k, k + 1, k, i + 24 - k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, i + k + 1, k + 1, k, i + 23 - k, k + 1, k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, i + k + 8, k + 5, k + 7, i + 16 - k, k + 5, k + 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(arg, arg2, i + 4, 4, 4, i + 6, 4, 20, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, i + 7, 4, 4, i + 17, 4, 6, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, i + 18, 4, 4, i + 20, 4, 20, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, i + 11, 8, 11, i + 13, 8, 20, PRISMARINE, PRISMARINE, false);
                this.addBlock(arg, field_14470, i + 12, 9, 12, arg2);
                this.addBlock(arg, field_14470, i + 12, 9, 15, arg2);
                this.addBlock(arg, field_14470, i + 12, 9, 18, arg2);
                int l = i + (bl ? 19 : 5);
                int m = i + (bl ? 5 : 19);
                for (int n = 20; n >= 5; n -= 3) {
                    this.addBlock(arg, field_14470, l, 5, n, arg2);
                }
                for (int o = 19; o >= 7; o -= 3) {
                    this.addBlock(arg, field_14470, m, 5, o, arg2);
                }
                for (int p = 0; p < 4; ++p) {
                    int q = bl ? i + 24 - (17 - p * 3) : i + 17 - p * 3;
                    this.addBlock(arg, field_14470, q, 5, 5, arg2);
                }
                this.addBlock(arg, field_14470, m, 5, 5, arg2);
                this.fillWithOutline(arg, arg2, i + 11, 1, 12, i + 13, 7, 12, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, i + 12, 1, 11, i + 12, 7, 13, PRISMARINE, PRISMARINE, false);
            }
        }

        private void method_14763(WorldAccess arg, Random random, BlockBox arg2) {
            if (this.method_14775(arg2, 22, 5, 35, 17)) {
                this.setAirAndWater(arg, arg2, 25, 0, 0, 32, 8, 20);
                for (int i = 0; i < 4; ++i) {
                    this.fillWithOutline(arg, arg2, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.addBlock(arg, PRISMARINE_BRICKS, 25, 5, 5 + i * 4, arg2);
                    this.addBlock(arg, PRISMARINE_BRICKS, 26, 6, 5 + i * 4, arg2);
                    this.addBlock(arg, SEA_LANTERN, 26, 5, 5 + i * 4, arg2);
                    this.fillWithOutline(arg, arg2, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.addBlock(arg, PRISMARINE_BRICKS, 32, 5, 5 + i * 4, arg2);
                    this.addBlock(arg, PRISMARINE_BRICKS, 31, 6, 5 + i * 4, arg2);
                    this.addBlock(arg, SEA_LANTERN, 31, 5, 5 + i * 4, arg2);
                    this.fillWithOutline(arg, arg2, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, PRISMARINE, PRISMARINE, false);
                }
            }
        }

        private void method_14762(WorldAccess arg, Random random, BlockBox arg2) {
            if (this.method_14775(arg2, 15, 20, 42, 21)) {
                this.fillWithOutline(arg, arg2, 15, 0, 21, 42, 0, 21, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 26, 1, 21, 31, 3, 21);
                this.fillWithOutline(arg, arg2, 21, 12, 21, 36, 12, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 17, 11, 21, 40, 11, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 16, 10, 21, 41, 10, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 15, 7, 21, 42, 9, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 16, 6, 21, 41, 6, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 17, 5, 21, 40, 5, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 21, 4, 21, 36, 4, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 22, 3, 21, 26, 3, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 31, 3, 21, 35, 3, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 23, 2, 21, 25, 2, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 32, 2, 21, 34, 2, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 28, 4, 20, 29, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(arg, PRISMARINE_BRICKS, 27, 3, 21, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 30, 3, 21, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 26, 2, 21, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 31, 2, 21, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 25, 1, 21, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 32, 1, 21, arg2);
                for (int i = 0; i < 7; ++i) {
                    this.addBlock(arg, DARK_PRISMARINE, 28 - i, 6 + i, 21, arg2);
                    this.addBlock(arg, DARK_PRISMARINE, 29 + i, 6 + i, 21, arg2);
                }
                for (int j = 0; j < 4; ++j) {
                    this.addBlock(arg, DARK_PRISMARINE, 28 - j, 9 + j, 21, arg2);
                    this.addBlock(arg, DARK_PRISMARINE, 29 + j, 9 + j, 21, arg2);
                }
                this.addBlock(arg, DARK_PRISMARINE, 28, 12, 21, arg2);
                this.addBlock(arg, DARK_PRISMARINE, 29, 12, 21, arg2);
                for (int k = 0; k < 3; ++k) {
                    this.addBlock(arg, DARK_PRISMARINE, 22 - k * 2, 8, 21, arg2);
                    this.addBlock(arg, DARK_PRISMARINE, 22 - k * 2, 9, 21, arg2);
                    this.addBlock(arg, DARK_PRISMARINE, 35 + k * 2, 8, 21, arg2);
                    this.addBlock(arg, DARK_PRISMARINE, 35 + k * 2, 9, 21, arg2);
                }
                this.setAirAndWater(arg, arg2, 15, 13, 21, 42, 15, 21);
                this.setAirAndWater(arg, arg2, 15, 1, 21, 15, 6, 21);
                this.setAirAndWater(arg, arg2, 16, 1, 21, 16, 5, 21);
                this.setAirAndWater(arg, arg2, 17, 1, 21, 20, 4, 21);
                this.setAirAndWater(arg, arg2, 21, 1, 21, 21, 3, 21);
                this.setAirAndWater(arg, arg2, 22, 1, 21, 22, 2, 21);
                this.setAirAndWater(arg, arg2, 23, 1, 21, 24, 1, 21);
                this.setAirAndWater(arg, arg2, 42, 1, 21, 42, 6, 21);
                this.setAirAndWater(arg, arg2, 41, 1, 21, 41, 5, 21);
                this.setAirAndWater(arg, arg2, 37, 1, 21, 40, 4, 21);
                this.setAirAndWater(arg, arg2, 36, 1, 21, 36, 3, 21);
                this.setAirAndWater(arg, arg2, 33, 1, 21, 34, 1, 21);
                this.setAirAndWater(arg, arg2, 35, 1, 21, 35, 2, 21);
            }
        }

        private void method_14765(WorldAccess arg, Random random, BlockBox arg2) {
            if (this.method_14775(arg2, 21, 21, 36, 36)) {
                this.fillWithOutline(arg, arg2, 21, 0, 22, 36, 0, 36, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 21, 1, 22, 36, 23, 36);
                for (int i = 0; i < 4; ++i) {
                    this.fillWithOutline(arg, arg2, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(arg, arg2, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(arg, arg2, 25, 16, 25, 32, 16, 32, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 25, 17, 25, 25, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(arg, arg2, 32, 17, 25, 32, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(arg, arg2, 25, 17, 32, 25, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(arg, arg2, 32, 17, 32, 32, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(arg, PRISMARINE_BRICKS, 26, 20, 26, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 27, 21, 27, arg2);
                this.addBlock(arg, SEA_LANTERN, 27, 20, 27, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 26, 20, 31, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 27, 21, 30, arg2);
                this.addBlock(arg, SEA_LANTERN, 27, 20, 30, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 31, 20, 31, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 30, 21, 30, arg2);
                this.addBlock(arg, SEA_LANTERN, 30, 20, 30, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 31, 20, 26, arg2);
                this.addBlock(arg, PRISMARINE_BRICKS, 30, 21, 27, arg2);
                this.addBlock(arg, SEA_LANTERN, 30, 20, 27, arg2);
                this.fillWithOutline(arg, arg2, 28, 21, 27, 29, 21, 27, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 27, 21, 28, 27, 21, 29, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 28, 21, 30, 29, 21, 30, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 30, 21, 28, 30, 21, 29, PRISMARINE, PRISMARINE, false);
            }
        }

        private void method_14764(WorldAccess arg, Random random, BlockBox arg2) {
            if (this.method_14775(arg2, 0, 21, 6, 58)) {
                this.fillWithOutline(arg, arg2, 0, 0, 21, 6, 0, 57, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 0, 1, 21, 6, 7, 57);
                this.fillWithOutline(arg, arg2, 4, 4, 21, 6, 4, 53, PRISMARINE, PRISMARINE, false);
                for (int i = 0; i < 4; ++i) {
                    this.fillWithOutline(arg, arg2, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (int j = 23; j < 53; j += 3) {
                    this.addBlock(arg, field_14470, 5, 5, j, arg2);
                }
                this.addBlock(arg, field_14470, 5, 5, 52, arg2);
                for (int k = 0; k < 4; ++k) {
                    this.fillWithOutline(arg, arg2, k, k + 1, 21, k, k + 1, 57 - k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(arg, arg2, 4, 1, 52, 6, 3, 52, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 5, 1, 51, 5, 3, 53, PRISMARINE, PRISMARINE, false);
            }
            if (this.method_14775(arg2, 51, 21, 58, 58)) {
                this.fillWithOutline(arg, arg2, 51, 0, 21, 57, 0, 57, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 51, 1, 21, 57, 7, 57);
                this.fillWithOutline(arg, arg2, 51, 4, 21, 53, 4, 53, PRISMARINE, PRISMARINE, false);
                for (int l = 0; l < 4; ++l) {
                    this.fillWithOutline(arg, arg2, 57 - l, l + 1, 21, 57 - l, l + 1, 57 - l, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (int m = 23; m < 53; m += 3) {
                    this.addBlock(arg, field_14470, 52, 5, m, arg2);
                }
                this.addBlock(arg, field_14470, 52, 5, 52, arg2);
                this.fillWithOutline(arg, arg2, 51, 1, 52, 53, 3, 52, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 52, 1, 51, 52, 3, 53, PRISMARINE, PRISMARINE, false);
            }
            if (this.method_14775(arg2, 0, 51, 57, 57)) {
                this.fillWithOutline(arg, arg2, 7, 0, 51, 50, 0, 57, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 7, 1, 51, 50, 10, 57);
                for (int n = 0; n < 4; ++n) {
                    this.fillWithOutline(arg, arg2, n + 1, n + 1, 57 - n, 56 - n, n + 1, 57 - n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
            }
        }

        private void method_14766(WorldAccess arg, Random random, BlockBox arg2) {
            if (this.method_14775(arg2, 7, 21, 13, 50)) {
                this.fillWithOutline(arg, arg2, 7, 0, 21, 13, 0, 50, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 7, 1, 21, 13, 10, 50);
                this.fillWithOutline(arg, arg2, 11, 8, 21, 13, 8, 53, PRISMARINE, PRISMARINE, false);
                for (int i = 0; i < 4; ++i) {
                    this.fillWithOutline(arg, arg2, i + 7, i + 5, 21, i + 7, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (int j = 21; j <= 45; j += 3) {
                    this.addBlock(arg, field_14470, 12, 9, j, arg2);
                }
            }
            if (this.method_14775(arg2, 44, 21, 50, 54)) {
                this.fillWithOutline(arg, arg2, 44, 0, 21, 50, 0, 50, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 44, 1, 21, 50, 10, 50);
                this.fillWithOutline(arg, arg2, 44, 8, 21, 46, 8, 53, PRISMARINE, PRISMARINE, false);
                for (int k = 0; k < 4; ++k) {
                    this.fillWithOutline(arg, arg2, 50 - k, k + 5, 21, 50 - k, k + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (int l = 21; l <= 45; l += 3) {
                    this.addBlock(arg, field_14470, 45, 9, l, arg2);
                }
            }
            if (this.method_14775(arg2, 8, 44, 49, 54)) {
                this.fillWithOutline(arg, arg2, 14, 0, 44, 43, 0, 50, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 14, 1, 44, 43, 10, 50);
                for (int m = 12; m <= 45; m += 3) {
                    this.addBlock(arg, field_14470, m, 9, 45, arg2);
                    this.addBlock(arg, field_14470, m, 9, 52, arg2);
                    if (m != 12 && m != 18 && m != 24 && m != 33 && m != 39 && m != 45) continue;
                    this.addBlock(arg, field_14470, m, 9, 47, arg2);
                    this.addBlock(arg, field_14470, m, 9, 50, arg2);
                    this.addBlock(arg, field_14470, m, 10, 45, arg2);
                    this.addBlock(arg, field_14470, m, 10, 46, arg2);
                    this.addBlock(arg, field_14470, m, 10, 51, arg2);
                    this.addBlock(arg, field_14470, m, 10, 52, arg2);
                    this.addBlock(arg, field_14470, m, 11, 47, arg2);
                    this.addBlock(arg, field_14470, m, 11, 50, arg2);
                    this.addBlock(arg, field_14470, m, 12, 48, arg2);
                    this.addBlock(arg, field_14470, m, 12, 49, arg2);
                }
                for (int n = 0; n < 3; ++n) {
                    this.fillWithOutline(arg, arg2, 8 + n, 5 + n, 54, 49 - n, 5 + n, 54, PRISMARINE, PRISMARINE, false);
                }
                this.fillWithOutline(arg, arg2, 11, 8, 54, 46, 8, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(arg, arg2, 14, 8, 44, 43, 8, 53, PRISMARINE, PRISMARINE, false);
            }
        }

        private void method_14767(WorldAccess arg, Random random, BlockBox arg2) {
            if (this.method_14775(arg2, 14, 21, 20, 43)) {
                this.fillWithOutline(arg, arg2, 14, 0, 21, 20, 0, 43, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 14, 1, 22, 20, 14, 43);
                this.fillWithOutline(arg, arg2, 18, 12, 22, 20, 12, 39, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 18, 12, 21, 20, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                for (int i = 0; i < 4; ++i) {
                    this.fillWithOutline(arg, arg2, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (int j = 23; j <= 39; j += 3) {
                    this.addBlock(arg, field_14470, 19, 13, j, arg2);
                }
            }
            if (this.method_14775(arg2, 37, 21, 43, 43)) {
                this.fillWithOutline(arg, arg2, 37, 0, 21, 43, 0, 43, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 37, 1, 22, 43, 14, 43);
                this.fillWithOutline(arg, arg2, 37, 12, 22, 39, 12, 39, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, 37, 12, 21, 39, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                for (int k = 0; k < 4; ++k) {
                    this.fillWithOutline(arg, arg2, 43 - k, k + 9, 21, 43 - k, k + 9, 43 - k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (int l = 23; l <= 39; l += 3) {
                    this.addBlock(arg, field_14470, 38, 13, l, arg2);
                }
            }
            if (this.method_14775(arg2, 15, 37, 42, 43)) {
                this.fillWithOutline(arg, arg2, 21, 0, 37, 36, 0, 43, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(arg, arg2, 21, 1, 37, 36, 14, 43);
                this.fillWithOutline(arg, arg2, 21, 12, 37, 36, 12, 39, PRISMARINE, PRISMARINE, false);
                for (int m = 0; m < 4; ++m) {
                    this.fillWithOutline(arg, arg2, 15 + m, m + 9, 43 - m, 42 - m, m + 9, 43 - m, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (int n = 21; n <= 36; n += 3) {
                    this.addBlock(arg, field_14470, n, 13, 38, arg2);
                }
            }
        }
    }

    public static abstract class Piece
    extends StructurePiece {
        protected static final BlockState PRISMARINE = Blocks.PRISMARINE.getDefaultState();
        protected static final BlockState PRISMARINE_BRICKS = Blocks.PRISMARINE_BRICKS.getDefaultState();
        protected static final BlockState DARK_PRISMARINE = Blocks.DARK_PRISMARINE.getDefaultState();
        protected static final BlockState field_14470 = PRISMARINE_BRICKS;
        protected static final BlockState SEA_LANTERN = Blocks.SEA_LANTERN.getDefaultState();
        protected static final BlockState WATER = Blocks.WATER.getDefaultState();
        protected static final Set<Block> ICE_BLOCKS = ImmutableSet.builder().add((Object)Blocks.ICE).add((Object)Blocks.PACKED_ICE).add((Object)Blocks.BLUE_ICE).add((Object)WATER.getBlock()).build();
        protected static final int TWO_ZERO_ZERO_INDEX = Piece.getIndex(2, 0, 0);
        protected static final int TWO_TWO_ZERO_INDEX = Piece.getIndex(2, 2, 0);
        protected static final int ZERO_ONE_ZERO_INDEX = Piece.getIndex(0, 1, 0);
        protected static final int FOUR_ONE_ZERO_INDEX = Piece.getIndex(4, 1, 0);
        protected PieceSetting setting;

        protected static final int getIndex(int i, int j, int k) {
            return j * 25 + k * 5 + i;
        }

        public Piece(StructurePieceType arg, int i) {
            super(arg, i);
        }

        public Piece(StructurePieceType arg, Direction arg2, BlockBox arg3) {
            super(arg, 1);
            this.setOrientation(arg2);
            this.boundingBox = arg3;
        }

        protected Piece(StructurePieceType arg, int i, Direction arg2, PieceSetting arg3, int j, int k, int l) {
            super(arg, i);
            this.setOrientation(arg2);
            this.setting = arg3;
            int m = arg3.roomIndex;
            int n = m % 5;
            int o = m / 5 % 5;
            int p = m / 25;
            this.boundingBox = arg2 == Direction.NORTH || arg2 == Direction.SOUTH ? new BlockBox(0, 0, 0, j * 8 - 1, k * 4 - 1, l * 8 - 1) : new BlockBox(0, 0, 0, l * 8 - 1, k * 4 - 1, j * 8 - 1);
            switch (arg2) {
                case NORTH: {
                    this.boundingBox.offset(n * 8, p * 4, -(o + l) * 8 + 1);
                    break;
                }
                case SOUTH: {
                    this.boundingBox.offset(n * 8, p * 4, o * 8);
                    break;
                }
                case WEST: {
                    this.boundingBox.offset(-(o + l) * 8 + 1, p * 4, n * 8);
                    break;
                }
                default: {
                    this.boundingBox.offset(o * 8, p * 4, n * 8);
                }
            }
        }

        public Piece(StructurePieceType arg, CompoundTag arg2) {
            super(arg, arg2);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
        }

        protected void setAirAndWater(WorldAccess arg, BlockBox arg2, int i, int j, int k, int l, int m, int n) {
            for (int o = j; o <= m; ++o) {
                for (int p = i; p <= l; ++p) {
                    for (int q = k; q <= n; ++q) {
                        BlockState lv = this.getBlockAt(arg, p, o, q, arg2);
                        if (ICE_BLOCKS.contains(lv.getBlock())) continue;
                        if (this.applyYTransform(o) >= arg.getSeaLevel() && lv != WATER) {
                            this.addBlock(arg, Blocks.AIR.getDefaultState(), p, o, q, arg2);
                            continue;
                        }
                        this.addBlock(arg, WATER, p, o, q, arg2);
                    }
                }
            }
        }

        protected void method_14774(WorldAccess arg, BlockBox arg2, int i, int j, boolean bl) {
            if (bl) {
                this.fillWithOutline(arg, arg2, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, i + 3, 0, j + 0, i + 4, 0, j + 2, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(arg, arg2, i + 3, 0, j + 2, i + 4, 0, j + 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(arg, arg2, i + 3, 0, j + 5, i + 4, 0, j + 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(arg, arg2, i + 2, 0, j + 3, i + 2, 0, j + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(arg, arg2, i + 5, 0, j + 3, i + 5, 0, j + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(arg, arg2, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
            }
        }

        protected void method_14771(WorldAccess arg, BlockBox arg2, int i, int j, int k, int l, int m, int n, BlockState arg3) {
            for (int o = j; o <= m; ++o) {
                for (int p = i; p <= l; ++p) {
                    for (int q = k; q <= n; ++q) {
                        if (this.getBlockAt(arg, p, o, q, arg2) != WATER) continue;
                        this.addBlock(arg, arg3, p, o, q, arg2);
                    }
                }
            }
        }

        protected boolean method_14775(BlockBox arg, int i, int j, int k, int l) {
            int m = this.applyXTransform(i, j);
            int n = this.applyZTransform(i, j);
            int o = this.applyXTransform(k, l);
            int p = this.applyZTransform(k, l);
            return arg.intersectsXZ(Math.min(m, o), Math.min(n, p), Math.max(m, o), Math.max(n, p));
        }

        protected boolean method_14772(WorldAccess arg, BlockBox arg2, int i, int j, int k) {
            int n;
            int m;
            int l = this.applyXTransform(i, k);
            if (arg2.contains(new BlockPos(l, m = this.applyYTransform(j), n = this.applyZTransform(i, k)))) {
                ElderGuardianEntity lv = EntityType.ELDER_GUARDIAN.create(arg.getWorld());
                lv.heal(lv.getMaxHealth());
                lv.refreshPositionAndAngles((double)l + 0.5, m, (double)n + 0.5, 0.0f, 0.0f);
                lv.initialize(arg, arg.getLocalDifficulty(lv.getBlockPos()), SpawnReason.STRUCTURE, null, null);
                arg.spawnEntity(lv);
                return true;
            }
            return false;
        }
    }
}

