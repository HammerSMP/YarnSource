/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFortressGenerator {
    private static final PieceData[] field_14494 = new PieceData[]{new PieceData(Bridge.class, 30, 0, true), new PieceData(BridgeCrossing.class, 10, 4), new PieceData(BridgeSmallCrossing.class, 10, 4), new PieceData(BridgeStairs.class, 10, 3), new PieceData(BridgePlatform.class, 5, 2), new PieceData(CorridorExit.class, 5, 1)};
    private static final PieceData[] field_14493 = new PieceData[]{new PieceData(SmallCorridor.class, 25, 0, true), new PieceData(CorridorCrossing.class, 15, 5), new PieceData(CorridorRightTurn.class, 5, 10), new PieceData(CorridorLeftTurn.class, 5, 10), new PieceData(CorridorStairs.class, 10, 3, true), new PieceData(CorridorBalcony.class, 7, 2), new PieceData(CorridorNetherWartsRoom.class, 5, 2)};

    private static Piece generatePiece(PieceData arg, List<StructurePiece> list, Random random, int i, int j, int k, Direction arg2, int l) {
        Class<? extends Piece> lv = arg.pieceType;
        Piece lv2 = null;
        if (lv == Bridge.class) {
            lv2 = Bridge.method_14798(list, random, i, j, k, arg2, l);
        } else if (lv == BridgeCrossing.class) {
            lv2 = BridgeCrossing.method_14796(list, i, j, k, arg2, l);
        } else if (lv == BridgeSmallCrossing.class) {
            lv2 = BridgeSmallCrossing.method_14817(list, i, j, k, arg2, l);
        } else if (lv == BridgeStairs.class) {
            lv2 = BridgeStairs.method_14818(list, i, j, k, l, arg2);
        } else if (lv == BridgePlatform.class) {
            lv2 = BridgePlatform.method_14807(list, i, j, k, l, arg2);
        } else if (lv == CorridorExit.class) {
            lv2 = CorridorExit.method_14801(list, random, i, j, k, arg2, l);
        } else if (lv == SmallCorridor.class) {
            lv2 = SmallCorridor.method_14804(list, i, j, k, arg2, l);
        } else if (lv == CorridorRightTurn.class) {
            lv2 = CorridorRightTurn.method_14805(list, random, i, j, k, arg2, l);
        } else if (lv == CorridorLeftTurn.class) {
            lv2 = CorridorLeftTurn.method_14803(list, random, i, j, k, arg2, l);
        } else if (lv == CorridorStairs.class) {
            lv2 = CorridorStairs.method_14799(list, i, j, k, arg2, l);
        } else if (lv == CorridorBalcony.class) {
            lv2 = CorridorBalcony.method_14800(list, i, j, k, arg2, l);
        } else if (lv == CorridorCrossing.class) {
            lv2 = CorridorCrossing.method_14802(list, i, j, k, arg2, l);
        } else if (lv == CorridorNetherWartsRoom.class) {
            lv2 = CorridorNetherWartsRoom.method_14806(list, i, j, k, arg2, l);
        }
        return lv2;
    }

    public static class CorridorBalcony
    extends Piece {
        public CorridorBalcony(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public CorridorBalcony(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            int i = 1;
            Direction lv = this.getFacing();
            if (lv == Direction.WEST || lv == Direction.NORTH) {
                i = 5;
            }
            this.method_14812((Start)arg, list, random, 0, i, random.nextInt(8) > 0);
            this.method_14808((Start)arg, list, random, 0, i, random.nextInt(8) > 0);
        }

        public static CorridorBalcony method_14800(List<StructurePiece> list, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -3, 0, 0, 9, 7, 9, arg);
            if (!CorridorBalcony.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new CorridorBalcony(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            this.fillWithOutline(arg, arg4, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 8, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 3, 0, 1, 4, 0, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 7, 3, 0, 7, 4, 0, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 1, 4, 2, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 1, 4, 7, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 3, 8, 7, 3, 8, lv2, lv2, false);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.SOUTH, true), 0, 3, 8, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.SOUTH, true), 8, 3, 8, arg4);
            this.fillWithOutline(arg, arg4, 0, 3, 6, 0, 3, 7, lv, lv, false);
            this.fillWithOutline(arg, arg4, 8, 3, 6, 8, 3, 7, lv, lv, false);
            this.fillWithOutline(arg, arg4, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 4, 5, 1, 5, 5, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 7, 4, 5, 7, 5, 5, lv2, lv2, false);
            for (int i = 0; i <= 5; ++i) {
                for (int j = 0; j <= 8; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), j, -1, i, arg4);
                }
            }
            return true;
        }
    }

    public static class CorridorStairs
    extends Piece {
        public CorridorStairs(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public CorridorStairs(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14814((Start)arg, list, random, 1, 0, true);
        }

        public static CorridorStairs method_14799(List<StructurePiece> list, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -7, 0, 5, 14, 10, arg);
            if (!CorridorStairs.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new CorridorStairs(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            BlockState lv = (BlockState)Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            for (int i = 0; i <= 9; ++i) {
                int j = Math.max(1, 7 - i);
                int k = Math.min(Math.max(j + 5, 14 - i), 13);
                int l = i;
                this.fillWithOutline(arg, arg4, 0, 0, l, 4, j, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 1, j + 1, l, 3, k - 1, l, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                if (i <= 6) {
                    this.addBlock(arg, lv, 1, j + 1, l, arg4);
                    this.addBlock(arg, lv, 2, j + 1, l, arg4);
                    this.addBlock(arg, lv, 3, j + 1, l, arg4);
                }
                this.fillWithOutline(arg, arg4, 0, k, l, 4, k, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 0, j + 1, l, 0, k - 1, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 4, j + 1, l, 4, k - 1, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
                if ((i & 1) == 0) {
                    this.fillWithOutline(arg, arg4, 0, j + 2, l, 0, j + 3, l, lv2, lv2, false);
                    this.fillWithOutline(arg, arg4, 4, j + 2, l, 4, j + 3, l, lv2, lv2, false);
                }
                for (int m = 0; m <= 4; ++m) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, l, arg4);
                }
            }
            return true;
        }
    }

    public static class CorridorLeftTurn
    extends Piece {
        private boolean containsChest;

        public CorridorLeftTurn(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
            this.containsChest = random.nextInt(3) == 0;
        }

        public CorridorLeftTurn(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, arg2);
            this.containsChest = arg2.getBoolean("Chest");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("Chest", this.containsChest);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14812((Start)arg, list, random, 0, 1, true);
        }

        public static CorridorLeftTurn method_14803(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, 0, 0, 5, 7, 5, arg);
            if (!CorridorLeftTurn.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new CorridorLeftTurn(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            this.fillWithOutline(arg, arg4, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 3, 1, 4, 4, 1, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 4, 3, 3, 4, 4, 3, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 3, 4, 1, 4, 4, lv, lv, false);
            this.fillWithOutline(arg, arg4, 3, 3, 4, 3, 4, 4, lv, lv, false);
            if (this.containsChest && arg4.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
                this.containsChest = false;
                this.addChest(arg, arg4, random, 3, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
            }
            this.fillWithOutline(arg, arg4, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                }
            }
            return true;
        }
    }

    public static class CorridorRightTurn
    extends Piece {
        private boolean containsChest;

        public CorridorRightTurn(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
            this.containsChest = random.nextInt(3) == 0;
        }

        public CorridorRightTurn(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, arg2);
            this.containsChest = arg2.getBoolean("Chest");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("Chest", this.containsChest);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14808((Start)arg, list, random, 0, 1, true);
        }

        public static CorridorRightTurn method_14805(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, 0, 0, 5, 7, 5, arg);
            if (!CorridorRightTurn.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new CorridorRightTurn(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 3, 1, 0, 4, 1, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 0, 3, 3, 0, 4, 3, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 3, 4, 1, 4, 4, lv, lv, false);
            this.fillWithOutline(arg, arg4, 3, 3, 4, 3, 4, 4, lv, lv, false);
            if (this.containsChest && arg4.contains(new BlockPos(this.applyXTransform(1, 3), this.applyYTransform(2), this.applyZTransform(1, 3)))) {
                this.containsChest = false;
                this.addChest(arg, arg4, random, 1, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
            }
            this.fillWithOutline(arg, arg4, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                }
            }
            return true;
        }
    }

    public static class CorridorCrossing
    extends Piece {
        public CorridorCrossing(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public CorridorCrossing(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14814((Start)arg, list, random, 1, 0, true);
            this.method_14812((Start)arg, list, random, 0, 1, true);
            this.method_14808((Start)arg, list, random, 0, 1, true);
        }

        public static CorridorCrossing method_14802(List<StructurePiece> list, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, 0, 0, 5, 7, 5, arg);
            if (!CorridorCrossing.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new CorridorCrossing(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                }
            }
            return true;
        }
    }

    public static class SmallCorridor
    extends Piece {
        public SmallCorridor(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public SmallCorridor(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14814((Start)arg, list, random, 1, 0, true);
        }

        public static SmallCorridor method_14804(List<StructurePiece> list, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, 0, 0, 5, 7, 5, arg);
            if (!SmallCorridor.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new SmallCorridor(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 3, 1, 0, 4, 1, lv, lv, false);
            this.fillWithOutline(arg, arg4, 0, 3, 3, 0, 4, 3, lv, lv, false);
            this.fillWithOutline(arg, arg4, 4, 3, 1, 4, 4, 1, lv, lv, false);
            this.fillWithOutline(arg, arg4, 4, 3, 3, 4, 4, 3, lv, lv, false);
            this.fillWithOutline(arg, arg4, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                }
            }
            return true;
        }
    }

    public static class CorridorNetherWartsRoom
    extends Piece {
        public CorridorNetherWartsRoom(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public CorridorNetherWartsRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14814((Start)arg, list, random, 5, 3, true);
            this.method_14814((Start)arg, list, random, 5, 11, true);
        }

        public static CorridorNetherWartsRoom method_14806(List<StructurePiece> list, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -5, -3, 0, 13, 14, 13, arg);
            if (!CorridorNetherWartsRoom.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new CorridorNetherWartsRoom(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            BlockState lv3 = (BlockState)lv2.with(FenceBlock.WEST, true);
            BlockState lv4 = (BlockState)lv2.with(FenceBlock.EAST, true);
            for (int i = 1; i <= 11; i += 2) {
                this.fillWithOutline(arg, arg4, i, 10, 0, i, 11, 0, lv, lv, false);
                this.fillWithOutline(arg, arg4, i, 10, 12, i, 11, 12, lv, lv, false);
                this.fillWithOutline(arg, arg4, 0, 10, i, 0, 11, i, lv2, lv2, false);
                this.fillWithOutline(arg, arg4, 12, 10, i, 12, 11, i, lv2, lv2, false);
                this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, arg4);
                this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, arg4);
                this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, arg4);
                this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, arg4);
                if (i == 11) continue;
                this.addBlock(arg, lv, i + 1, 13, 0, arg4);
                this.addBlock(arg, lv, i + 1, 13, 12, arg4);
                this.addBlock(arg, lv2, 0, 13, i + 1, arg4);
                this.addBlock(arg, lv2, 12, 13, i + 1, arg4);
            }
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 0, 13, 0, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 0, 13, 12, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 12, 13, 12, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 12, 13, 0, arg4);
            for (int j = 3; j <= 9; j += 2) {
                this.fillWithOutline(arg, arg4, 1, 7, j, 1, 8, j, lv3, lv3, false);
                this.fillWithOutline(arg, arg4, 11, 7, j, 11, 8, j, lv4, lv4, false);
            }
            BlockState lv5 = (BlockState)Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
            for (int k = 0; k <= 6; ++k) {
                int l = k + 4;
                for (int m = 5; m <= 7; ++m) {
                    this.addBlock(arg, lv5, m, 5 + k, l, arg4);
                }
                if (l >= 5 && l <= 8) {
                    this.fillWithOutline(arg, arg4, 5, 5, l, 7, k + 4, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
                } else if (l >= 9 && l <= 10) {
                    this.fillWithOutline(arg, arg4, 5, 8, l, 7, k + 4, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
                }
                if (k < 1) continue;
                this.fillWithOutline(arg, arg4, 5, 6 + k, l, 7, 9 + k, l, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            for (int n = 5; n <= 7; ++n) {
                this.addBlock(arg, lv5, n, 12, 11, arg4);
            }
            this.fillWithOutline(arg, arg4, 5, 6, 7, 5, 7, 7, lv4, lv4, false);
            this.fillWithOutline(arg, arg4, 7, 6, 7, 7, 7, 7, lv3, lv3, false);
            this.fillWithOutline(arg, arg4, 5, 13, 12, 7, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            BlockState lv6 = (BlockState)lv5.with(StairsBlock.FACING, Direction.EAST);
            BlockState lv7 = (BlockState)lv5.with(StairsBlock.FACING, Direction.WEST);
            this.addBlock(arg, lv7, 4, 5, 2, arg4);
            this.addBlock(arg, lv7, 4, 5, 3, arg4);
            this.addBlock(arg, lv7, 4, 5, 9, arg4);
            this.addBlock(arg, lv7, 4, 5, 10, arg4);
            this.addBlock(arg, lv6, 8, 5, 2, arg4);
            this.addBlock(arg, lv6, 8, 5, 3, arg4);
            this.addBlock(arg, lv6, 8, 5, 9, arg4);
            this.addBlock(arg, lv6, 8, 5, 10, arg4);
            this.fillWithOutline(arg, arg4, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int o = 4; o <= 8; ++o) {
                for (int p = 0; p <= 2; ++p) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), o, -1, p, arg4);
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), o, -1, 12 - p, arg4);
                }
            }
            for (int q = 0; q <= 2; ++q) {
                for (int r = 4; r <= 8; ++r) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), q, -1, r, arg4);
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), 12 - q, -1, r, arg4);
                }
            }
            return true;
        }
    }

    public static class CorridorExit
    extends Piece {
        public CorridorExit(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public CorridorExit(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14814((Start)arg, list, random, 5, 3, true);
        }

        public static CorridorExit method_14801(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -5, -3, 0, 13, 14, 13, arg);
            if (!CorridorExit.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new CorridorExit(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.getDefaultState(), Blocks.NETHER_BRICK_FENCE.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            for (int i = 1; i <= 11; i += 2) {
                this.fillWithOutline(arg, arg4, i, 10, 0, i, 11, 0, lv, lv, false);
                this.fillWithOutline(arg, arg4, i, 10, 12, i, 11, 12, lv, lv, false);
                this.fillWithOutline(arg, arg4, 0, 10, i, 0, 11, i, lv2, lv2, false);
                this.fillWithOutline(arg, arg4, 12, 10, i, 12, 11, i, lv2, lv2, false);
                this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, arg4);
                this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, arg4);
                this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, arg4);
                this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, arg4);
                if (i == 11) continue;
                this.addBlock(arg, lv, i + 1, 13, 0, arg4);
                this.addBlock(arg, lv, i + 1, 13, 12, arg4);
                this.addBlock(arg, lv2, 0, 13, i + 1, arg4);
                this.addBlock(arg, lv2, 12, 13, i + 1, arg4);
            }
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 0, 13, 0, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 0, 13, 12, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 12, 13, 12, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 12, 13, 0, arg4);
            for (int j = 3; j <= 9; j += 2) {
                this.fillWithOutline(arg, arg4, 1, 7, j, 1, 8, j, (BlockState)lv2.with(FenceBlock.WEST, true), (BlockState)lv2.with(FenceBlock.WEST, true), false);
                this.fillWithOutline(arg, arg4, 11, 7, j, 11, 8, j, (BlockState)lv2.with(FenceBlock.EAST, true), (BlockState)lv2.with(FenceBlock.EAST, true), false);
            }
            this.fillWithOutline(arg, arg4, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int k = 4; k <= 8; ++k) {
                for (int l = 0; l <= 2; ++l) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, l, arg4);
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, 12 - l, arg4);
                }
            }
            for (int m = 0; m <= 2; ++m) {
                for (int n = 4; n <= 8; ++n) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, n, arg4);
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), 12 - m, -1, n, arg4);
                }
            }
            this.fillWithOutline(arg, arg4, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 1, 6, 6, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), 6, 0, 6, arg4);
            this.addBlock(arg, Blocks.LAVA.getDefaultState(), 6, 5, 6, arg4);
            BlockPos lv3 = new BlockPos(this.applyXTransform(6, 6), this.applyYTransform(5), this.applyZTransform(6, 6));
            if (arg4.contains(lv3)) {
                arg.getFluidTickScheduler().schedule(lv3, Fluids.LAVA, 0);
            }
            return true;
        }
    }

    public static class BridgePlatform
    extends Piece {
        private boolean hasBlazeSpawner;

        public BridgePlatform(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public BridgePlatform(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, arg2);
            this.hasBlazeSpawner = arg2.getBoolean("Mob");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("Mob", this.hasBlazeSpawner);
        }

        public static BridgePlatform method_14807(List<StructurePiece> list, int i, int j, int k, int l, Direction arg) {
            BlockBox lv = BlockBox.rotated(i, j, k, -2, 0, 0, 7, 8, 9, arg);
            if (!BridgePlatform.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new BridgePlatform(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            BlockPos lv3;
            this.fillWithOutline(arg, arg4, 0, 2, 0, 6, 7, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            this.addBlock(arg, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 1, 6, 3, arg4);
            this.addBlock(arg, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 5, 6, 3, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.NORTH, true), 0, 6, 3, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.NORTH, true), 6, 6, 3, arg4);
            this.fillWithOutline(arg, arg4, 0, 6, 4, 0, 6, 7, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 6, 6, 4, 6, 6, 7, lv2, lv2, false);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.SOUTH, true), 0, 6, 8, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.SOUTH, true), 6, 6, 8, arg4);
            this.fillWithOutline(arg, arg4, 1, 6, 8, 5, 6, 8, lv, lv, false);
            this.addBlock(arg, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 1, 7, 8, arg4);
            this.fillWithOutline(arg, arg4, 2, 7, 8, 4, 7, 8, lv, lv, false);
            this.addBlock(arg, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 5, 7, 8, arg4);
            this.addBlock(arg, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 2, 8, 8, arg4);
            this.addBlock(arg, lv, 3, 8, 8, arg4);
            this.addBlock(arg, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 4, 8, 8, arg4);
            if (!this.hasBlazeSpawner && arg4.contains(lv3 = new BlockPos(this.applyXTransform(3, 5), this.applyYTransform(5), this.applyZTransform(3, 5)))) {
                this.hasBlazeSpawner = true;
                arg.setBlockState(lv3, Blocks.SPAWNER.getDefaultState(), 2);
                BlockEntity lv4 = arg.getBlockEntity(lv3);
                if (lv4 instanceof MobSpawnerBlockEntity) {
                    ((MobSpawnerBlockEntity)lv4).getLogic().setEntityId(EntityType.BLAZE);
                }
            }
            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                }
            }
            return true;
        }
    }

    public static class BridgeStairs
    extends Piece {
        public BridgeStairs(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public BridgeStairs(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14808((Start)arg, list, random, 6, 2, false);
        }

        public static BridgeStairs method_14818(List<StructurePiece> list, int i, int j, int k, int l, Direction arg) {
            BlockBox lv = BlockBox.rotated(i, j, k, -2, 0, 0, 7, 11, 7, arg);
            if (!BridgeStairs.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new BridgeStairs(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 6, 10, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            this.fillWithOutline(arg, arg4, 0, 3, 2, 0, 5, 4, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 6, 3, 2, 6, 5, 2, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 6, 3, 4, 6, 5, 4, lv2, lv2, false);
            this.addBlock(arg, Blocks.NETHER_BRICKS.getDefaultState(), 5, 2, 5, arg4);
            this.fillWithOutline(arg, arg4, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 8, 2, 6, 8, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 0, 4, 5, 0, lv, lv, false);
            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                }
            }
            return true;
        }
    }

    public static class BridgeSmallCrossing
    extends Piece {
        public BridgeSmallCrossing(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public BridgeSmallCrossing(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14814((Start)arg, list, random, 2, 0, false);
            this.method_14812((Start)arg, list, random, 0, 2, false);
            this.method_14808((Start)arg, list, random, 0, 2, false);
        }

        public static BridgeSmallCrossing method_14817(List<StructurePiece> list, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -2, 0, 0, 7, 9, 7, arg);
            if (!BridgeSmallCrossing.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new BridgeSmallCrossing(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 6, 7, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            this.fillWithOutline(arg, arg4, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 0, 4, 5, 0, lv, lv, false);
            this.fillWithOutline(arg, arg4, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 2, 5, 6, 4, 5, 6, lv, lv, false);
            this.fillWithOutline(arg, arg4, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 2, 0, 5, 4, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 6, 5, 2, 6, 5, 4, lv2, lv2, false);
            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                }
            }
            return true;
        }
    }

    public static class BridgeCrossing
    extends Piece {
        public BridgeCrossing(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        protected BridgeCrossing(Random random, int i, int j) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0);
            this.setOrientation(Direction.Type.HORIZONTAL.random(random));
            this.boundingBox = this.getFacing().getAxis() == Direction.Axis.Z ? new BlockBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1) : new BlockBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
        }

        protected BridgeCrossing(StructurePieceType arg, CompoundTag arg2) {
            super(arg, arg2);
        }

        public BridgeCrossing(StructureManager arg, CompoundTag arg2) {
            this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14814((Start)arg, list, random, 8, 3, false);
            this.method_14812((Start)arg, list, random, 3, 8, false);
            this.method_14808((Start)arg, list, random, 3, 8, false);
        }

        public static BridgeCrossing method_14796(List<StructurePiece> list, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -8, -3, 0, 19, 10, 19, arg);
            if (!BridgeCrossing.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new BridgeCrossing(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 5, 0, 10, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 8, 18, 7, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int i = 7; i <= 11; ++i) {
                for (int j = 0; j <= 2; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, arg4);
                }
            }
            this.fillWithOutline(arg, arg4, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int k = 0; k <= 2; ++k) {
                for (int l = 7; l <= 11; ++l) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, l, arg4);
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), 18 - k, -1, l, arg4);
                }
            }
            return true;
        }
    }

    public static class BridgeEnd
    extends Piece {
        private final int seed;

        public BridgeEnd(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
            this.seed = random.nextInt();
        }

        public BridgeEnd(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, arg2);
            this.seed = arg2.getInt("Seed");
        }

        public static BridgeEnd method_14797(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -3, 0, 5, 10, 8, arg);
            if (!BridgeEnd.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new BridgeEnd(l, random, lv, arg);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putInt("Seed", this.seed);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            Random random2 = new Random(this.seed);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 3; j <= 4; ++j) {
                    int k = random2.nextInt(8);
                    this.fillWithOutline(arg, arg4, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
                }
            }
            int l = random2.nextInt(8);
            this.fillWithOutline(arg, arg4, 0, 5, 0, 0, 5, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            int m = random2.nextInt(8);
            this.fillWithOutline(arg, arg4, 4, 5, 0, 4, 5, m, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int n = 0; n <= 4; ++n) {
                int o = random2.nextInt(5);
                this.fillWithOutline(arg, arg4, n, 2, 0, n, 2, o, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            }
            for (int p = 0; p <= 4; ++p) {
                for (int q = 0; q <= 1; ++q) {
                    int r = random2.nextInt(3);
                    this.fillWithOutline(arg, arg4, p, q, 0, p, q, r, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
                }
            }
            return true;
        }
    }

    public static class Bridge
    extends Piece {
        public Bridge(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public Bridge(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14814((Start)arg, list, random, 1, 3, false);
        }

        public static Bridge method_14798(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -3, 0, 5, 10, 19, arg);
            if (!Bridge.method_14809(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new Bridge(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 5, 0, 3, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 2; ++j) {
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, arg4);
                    this.method_14936(arg, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, arg4);
                }
            }
            BlockState lv = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            BlockState lv2 = (BlockState)lv.with(FenceBlock.EAST, true);
            BlockState lv3 = (BlockState)lv.with(FenceBlock.WEST, true);
            this.fillWithOutline(arg, arg4, 0, 1, 1, 0, 4, 1, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 0, 3, 4, 0, 4, 4, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 0, 3, 14, 0, 4, 14, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 0, 1, 17, 0, 4, 17, lv2, lv2, false);
            this.fillWithOutline(arg, arg4, 4, 1, 1, 4, 4, 1, lv3, lv3, false);
            this.fillWithOutline(arg, arg4, 4, 3, 4, 4, 4, 4, lv3, lv3, false);
            this.fillWithOutline(arg, arg4, 4, 3, 14, 4, 4, 14, lv3, lv3, false);
            this.fillWithOutline(arg, arg4, 4, 1, 17, 4, 4, 17, lv3, lv3, false);
            return true;
        }
    }

    public static class Start
    extends BridgeCrossing {
        public PieceData field_14506;
        public List<PieceData> bridgePieces;
        public List<PieceData> corridorPieces;
        public final List<StructurePiece> field_14505 = Lists.newArrayList();

        public Start(Random random, int i, int j) {
            super(random, i, j);
            this.bridgePieces = Lists.newArrayList();
            for (PieceData lv : field_14494) {
                lv.field_14502 = 0;
                this.bridgePieces.add(lv);
            }
            this.corridorPieces = Lists.newArrayList();
            for (PieceData lv2 : field_14493) {
                lv2.field_14502 = 0;
                this.corridorPieces.add(lv2);
            }
        }

        public Start(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FORTRESS_START, arg2);
        }
    }

    static abstract class Piece
    extends StructurePiece {
        protected Piece(StructurePieceType arg, int i) {
            super(arg, i);
        }

        public Piece(StructurePieceType arg, CompoundTag arg2) {
            super(arg, arg2);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
        }

        private int method_14810(List<PieceData> list) {
            boolean bl = false;
            int i = 0;
            for (PieceData lv : list) {
                if (lv.field_14499 > 0 && lv.field_14502 < lv.field_14499) {
                    bl = true;
                }
                i += lv.field_14503;
            }
            return bl ? i : -1;
        }

        private Piece method_14811(Start arg, List<PieceData> list, List<StructurePiece> list2, Random random, int i, int j, int k, Direction arg2, int l) {
            int m = this.method_14810(list);
            boolean bl = m > 0 && l <= 30;
            int n = 0;
            block0 : while (n < 5 && bl) {
                ++n;
                int o = random.nextInt(m);
                for (PieceData lv : list) {
                    if ((o -= lv.field_14503) >= 0) continue;
                    if (!lv.method_14816(l) || lv == arg.field_14506 && !lv.field_14500) continue block0;
                    Piece lv2 = NetherFortressGenerator.generatePiece(lv, list2, random, i, j, k, arg2, l);
                    if (lv2 == null) continue;
                    ++lv.field_14502;
                    arg.field_14506 = lv;
                    if (!lv.method_14815()) {
                        list.remove(lv);
                    }
                    return lv2;
                }
            }
            return BridgeEnd.method_14797(list2, random, i, j, k, arg2, l);
        }

        private StructurePiece method_14813(Start arg, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction arg2, int l, boolean bl) {
            Piece lv;
            if (Math.abs(i - arg.getBoundingBox().minX) > 112 || Math.abs(k - arg.getBoundingBox().minZ) > 112) {
                return BridgeEnd.method_14797(list, random, i, j, k, arg2, l);
            }
            List<PieceData> list2 = arg.bridgePieces;
            if (bl) {
                list2 = arg.corridorPieces;
            }
            if ((lv = this.method_14811(arg, list2, list, random, i, j, k, arg2, l + 1)) != null) {
                list.add(lv);
                arg.field_14505.add(lv);
            }
            return lv;
        }

        @Nullable
        protected StructurePiece method_14814(Start arg, List<StructurePiece> list, Random random, int i, int j, boolean bl) {
            Direction lv = this.getFacing();
            if (lv != null) {
                switch (lv) {
                    case NORTH: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.minZ - 1, lv, this.getLength(), bl);
                    }
                    case SOUTH: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.maxZ + 1, lv, this.getLength(), bl);
                    }
                    case WEST: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, lv, this.getLength(), bl);
                    }
                    case EAST: {
                        return this.method_14813(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, lv, this.getLength(), bl);
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece method_14812(Start arg, List<StructurePiece> list, Random random, int i, int j, boolean bl) {
            Direction lv = this.getFacing();
            if (lv != null) {
                switch (lv) {
                    case NORTH: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength(), bl);
                    }
                    case SOUTH: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength(), bl);
                    }
                    case WEST: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength(), bl);
                    }
                    case EAST: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength(), bl);
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece method_14808(Start arg, List<StructurePiece> list, Random random, int i, int j, boolean bl) {
            Direction lv = this.getFacing();
            if (lv != null) {
                switch (lv) {
                    case NORTH: {
                        return this.method_14813(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength(), bl);
                    }
                    case SOUTH: {
                        return this.method_14813(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength(), bl);
                    }
                    case WEST: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength(), bl);
                    }
                    case EAST: {
                        return this.method_14813(arg, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength(), bl);
                    }
                }
            }
            return null;
        }

        protected static boolean method_14809(BlockBox arg) {
            return arg != null && arg.minY > 10;
        }
    }

    static class PieceData {
        public final Class<? extends Piece> pieceType;
        public final int field_14503;
        public int field_14502;
        public final int field_14499;
        public final boolean field_14500;

        public PieceData(Class<? extends Piece> arg, int i, int j, boolean bl) {
            this.pieceType = arg;
            this.field_14503 = i;
            this.field_14499 = j;
            this.field_14500 = bl;
        }

        public PieceData(Class<? extends Piece> arg, int i, int j) {
            this(arg, i, j, false);
        }

        public boolean method_14816(int i) {
            return this.field_14499 == 0 || this.field_14502 < this.field_14499;
        }

        public boolean method_14815() {
            return this.field_14499 == 0 || this.field_14502 < this.field_14499;
        }
    }
}

