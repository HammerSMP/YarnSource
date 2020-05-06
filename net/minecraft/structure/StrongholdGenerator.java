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
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityType;
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

public class StrongholdGenerator {
    private static final PieceSetting[] ALL_PIECE_SETTINGS = new PieceSetting[]{new PieceSetting(Corridor.class, 40, 0), new PieceSetting(PrisonHall.class, 5, 5), new PieceSetting(LeftTurn.class, 20, 0), new PieceSetting(RightTurn.class, 20, 0), new PieceSetting(SquareRoom.class, 10, 6), new PieceSetting(Stairs.class, 5, 5), new PieceSetting(SpiralStaircase.class, 5, 5), new PieceSetting(FiveWayCrossing.class, 5, 4), new PieceSetting(ChestCorridor.class, 5, 4), new PieceSetting(Library.class, 10, 2){

        @Override
        public boolean canGenerate(int i) {
            return super.canGenerate(i) && i > 4;
        }
    }, new PieceSetting(PortalRoom.class, 20, 1){

        @Override
        public boolean canGenerate(int i) {
            return super.canGenerate(i) && i > 5;
        }
    }};
    private static List<PieceSetting> possiblePieceSettings;
    private static Class<? extends Piece> activePieceType;
    private static int field_15264;
    private static final StoneBrickRandomizer STONE_BRICK_RANDOMIZER;

    public static void init() {
        possiblePieceSettings = Lists.newArrayList();
        for (PieceSetting lv : ALL_PIECE_SETTINGS) {
            lv.generatedCount = 0;
            possiblePieceSettings.add(lv);
        }
        activePieceType = null;
    }

    private static boolean method_14852() {
        boolean bl = false;
        field_15264 = 0;
        for (PieceSetting lv : possiblePieceSettings) {
            if (lv.limit > 0 && lv.generatedCount < lv.limit) {
                bl = true;
            }
            field_15264 += lv.field_15278;
        }
        return bl;
    }

    private static Piece method_14847(Class<? extends Piece> arg, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction arg2, int l) {
        Piece lv = null;
        if (arg == Corridor.class) {
            lv = Corridor.method_14867(list, random, i, j, k, arg2, l);
        } else if (arg == PrisonHall.class) {
            lv = PrisonHall.method_14864(list, random, i, j, k, arg2, l);
        } else if (arg == LeftTurn.class) {
            lv = LeftTurn.method_14859(list, random, i, j, k, arg2, l);
        } else if (arg == RightTurn.class) {
            lv = RightTurn.method_16652(list, random, i, j, k, arg2, l);
        } else if (arg == SquareRoom.class) {
            lv = SquareRoom.method_14865(list, random, i, j, k, arg2, l);
        } else if (arg == Stairs.class) {
            lv = Stairs.method_14868(list, random, i, j, k, arg2, l);
        } else if (arg == SpiralStaircase.class) {
            lv = SpiralStaircase.method_14866(list, random, i, j, k, arg2, l);
        } else if (arg == FiveWayCrossing.class) {
            lv = FiveWayCrossing.method_14858(list, random, i, j, k, arg2, l);
        } else if (arg == ChestCorridor.class) {
            lv = ChestCorridor.method_14856(list, random, i, j, k, arg2, l);
        } else if (arg == Library.class) {
            lv = Library.method_14860(list, random, i, j, k, arg2, l);
        } else if (arg == PortalRoom.class) {
            lv = PortalRoom.method_14863(list, i, j, k, arg2, l);
        }
        return lv;
    }

    private static Piece method_14851(Start arg, List<StructurePiece> list, Random random, int i, int j, int k, Direction arg2, int l) {
        if (!StrongholdGenerator.method_14852()) {
            return null;
        }
        if (activePieceType != null) {
            Piece lv = StrongholdGenerator.method_14847(activePieceType, list, random, i, j, k, arg2, l);
            activePieceType = null;
            if (lv != null) {
                return lv;
            }
        }
        int m = 0;
        block0 : while (m < 5) {
            ++m;
            int n = random.nextInt(field_15264);
            for (PieceSetting lv2 : possiblePieceSettings) {
                if ((n -= lv2.field_15278) >= 0) continue;
                if (!lv2.canGenerate(l) || lv2 == arg.field_15284) continue block0;
                Piece lv3 = StrongholdGenerator.method_14847(lv2.pieceType, list, random, i, j, k, arg2, l);
                if (lv3 == null) continue;
                ++lv2.generatedCount;
                arg.field_15284 = lv2;
                if (!lv2.canGenerate()) {
                    possiblePieceSettings.remove(lv2);
                }
                return lv3;
            }
        }
        BlockBox lv4 = SmallCorridor.method_14857(list, random, i, j, k, arg2);
        if (lv4 != null && lv4.minY > 1) {
            return new SmallCorridor(l, lv4, arg2);
        }
        return null;
    }

    private static StructurePiece method_14854(Start arg, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction arg2, int l) {
        if (l > 50) {
            return null;
        }
        if (Math.abs(i - arg.getBoundingBox().minX) > 112 || Math.abs(k - arg.getBoundingBox().minZ) > 112) {
            return null;
        }
        Piece lv = StrongholdGenerator.method_14851(arg, list, random, i, j, k, arg2, l + 1);
        if (lv != null) {
            list.add(lv);
            arg.field_15282.add(lv);
        }
        return lv;
    }

    static {
        STONE_BRICK_RANDOMIZER = new StoneBrickRandomizer();
    }

    static class StoneBrickRandomizer
    extends StructurePiece.BlockRandomizer {
        private StoneBrickRandomizer() {
        }

        @Override
        public void setBlock(Random random, int i, int j, int k, boolean bl) {
            float f;
            this.block = bl ? ((f = random.nextFloat()) < 0.2f ? Blocks.CRACKED_STONE_BRICKS.getDefaultState() : (f < 0.5f ? Blocks.MOSSY_STONE_BRICKS.getDefaultState() : (f < 0.55f ? Blocks.INFESTED_STONE_BRICKS.getDefaultState() : Blocks.STONE_BRICKS.getDefaultState()))) : Blocks.CAVE_AIR.getDefaultState();
        }
    }

    public static class PortalRoom
    extends Piece {
        private boolean spawnerPlaced;

        public PortalRoom(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public PortalRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, arg2);
            this.spawnerPlaced = arg2.getBoolean("Mob");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("Mob", this.spawnerPlaced);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            if (arg != null) {
                ((Start)arg).field_15283 = this;
            }
        }

        public static PortalRoom method_14863(List<StructurePiece> list, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -4, -1, 0, 11, 8, 16, arg);
            if (!PortalRoom.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new PortalRoom(l, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 10, 7, 15, false, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, Piece.EntranceType.GRATES, 4, 1, 0);
            int i = 6;
            this.fillWithOutline(arg, arg4, 1, i, 1, 1, i, 14, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 9, i, 1, 9, i, 14, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 2, i, 1, 8, i, 2, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 2, i, 14, 8, i, 14, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 1, 1, 1, 2, 1, 4, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 8, 1, 1, 9, 1, 4, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 3, 1, 8, 7, 1, 12, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            BlockState lv = (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true);
            BlockState lv2 = (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true);
            for (int j = 3; j < 14; j += 2) {
                this.fillWithOutline(arg, arg4, 0, 3, j, 0, 4, j, lv, lv, false);
                this.fillWithOutline(arg, arg4, 10, 3, j, 10, 4, j, lv, lv, false);
            }
            for (int k = 2; k < 9; k += 2) {
                this.fillWithOutline(arg, arg4, k, 3, 15, k, 4, 15, lv2, lv2, false);
            }
            BlockState lv3 = (BlockState)Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
            this.fillWithOutline(arg, arg4, 4, 1, 5, 6, 1, 7, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 4, 2, 6, 6, 2, 7, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 4, 3, 7, 6, 3, 7, false, random, STONE_BRICK_RANDOMIZER);
            for (int l = 4; l <= 6; ++l) {
                this.addBlock(arg, lv3, l, 1, 4, arg4);
                this.addBlock(arg, lv3, l, 2, 5, arg4);
                this.addBlock(arg, lv3, l, 3, 6, arg4);
            }
            BlockState lv4 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.NORTH);
            BlockState lv5 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.SOUTH);
            BlockState lv6 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.EAST);
            BlockState lv7 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.WEST);
            boolean bl = true;
            boolean[] bls = new boolean[12];
            for (int m = 0; m < bls.length; ++m) {
                bls[m] = random.nextFloat() > 0.9f;
                bl &= bls[m];
            }
            this.addBlock(arg, (BlockState)lv4.with(EndPortalFrameBlock.EYE, bls[0]), 4, 3, 8, arg4);
            this.addBlock(arg, (BlockState)lv4.with(EndPortalFrameBlock.EYE, bls[1]), 5, 3, 8, arg4);
            this.addBlock(arg, (BlockState)lv4.with(EndPortalFrameBlock.EYE, bls[2]), 6, 3, 8, arg4);
            this.addBlock(arg, (BlockState)lv5.with(EndPortalFrameBlock.EYE, bls[3]), 4, 3, 12, arg4);
            this.addBlock(arg, (BlockState)lv5.with(EndPortalFrameBlock.EYE, bls[4]), 5, 3, 12, arg4);
            this.addBlock(arg, (BlockState)lv5.with(EndPortalFrameBlock.EYE, bls[5]), 6, 3, 12, arg4);
            this.addBlock(arg, (BlockState)lv6.with(EndPortalFrameBlock.EYE, bls[6]), 3, 3, 9, arg4);
            this.addBlock(arg, (BlockState)lv6.with(EndPortalFrameBlock.EYE, bls[7]), 3, 3, 10, arg4);
            this.addBlock(arg, (BlockState)lv6.with(EndPortalFrameBlock.EYE, bls[8]), 3, 3, 11, arg4);
            this.addBlock(arg, (BlockState)lv7.with(EndPortalFrameBlock.EYE, bls[9]), 7, 3, 9, arg4);
            this.addBlock(arg, (BlockState)lv7.with(EndPortalFrameBlock.EYE, bls[10]), 7, 3, 10, arg4);
            this.addBlock(arg, (BlockState)lv7.with(EndPortalFrameBlock.EYE, bls[11]), 7, 3, 11, arg4);
            if (bl) {
                BlockState lv8 = Blocks.END_PORTAL.getDefaultState();
                this.addBlock(arg, lv8, 4, 3, 9, arg4);
                this.addBlock(arg, lv8, 5, 3, 9, arg4);
                this.addBlock(arg, lv8, 6, 3, 9, arg4);
                this.addBlock(arg, lv8, 4, 3, 10, arg4);
                this.addBlock(arg, lv8, 5, 3, 10, arg4);
                this.addBlock(arg, lv8, 6, 3, 10, arg4);
                this.addBlock(arg, lv8, 4, 3, 11, arg4);
                this.addBlock(arg, lv8, 5, 3, 11, arg4);
                this.addBlock(arg, lv8, 6, 3, 11, arg4);
            }
            if (!this.spawnerPlaced) {
                i = this.applyYTransform(3);
                BlockPos lv9 = new BlockPos(this.applyXTransform(5, 6), i, this.applyZTransform(5, 6));
                if (arg4.contains(lv9)) {
                    this.spawnerPlaced = true;
                    arg.setBlockState(lv9, Blocks.SPAWNER.getDefaultState(), 2);
                    BlockEntity lv10 = arg.getBlockEntity(lv9);
                    if (lv10 instanceof MobSpawnerBlockEntity) {
                        ((MobSpawnerBlockEntity)lv10).getLogic().setEntityId(EntityType.SILVERFISH);
                    }
                }
            }
            return true;
        }
    }

    public static class FiveWayCrossing
    extends Piece {
        private final boolean lowerLeftExists;
        private final boolean upperLeftExists;
        private final boolean lowerRightExists;
        private final boolean upperRightExists;

        public FiveWayCrossing(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
            this.lowerLeftExists = random.nextBoolean();
            this.upperLeftExists = random.nextBoolean();
            this.lowerRightExists = random.nextBoolean();
            this.upperRightExists = random.nextInt(3) > 0;
        }

        public FiveWayCrossing(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, arg2);
            this.lowerLeftExists = arg2.getBoolean("leftLow");
            this.upperLeftExists = arg2.getBoolean("leftHigh");
            this.lowerRightExists = arg2.getBoolean("rightLow");
            this.upperRightExists = arg2.getBoolean("rightHigh");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("leftLow", this.lowerLeftExists);
            arg.putBoolean("leftHigh", this.upperLeftExists);
            arg.putBoolean("rightLow", this.lowerRightExists);
            arg.putBoolean("rightHigh", this.upperRightExists);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            int i = 3;
            int j = 5;
            Direction lv = this.getFacing();
            if (lv == Direction.WEST || lv == Direction.NORTH) {
                i = 8 - i;
                j = 8 - j;
            }
            this.method_14874((Start)arg, list, random, 5, 1);
            if (this.lowerLeftExists) {
                this.method_14870((Start)arg, list, random, i, 1);
            }
            if (this.upperLeftExists) {
                this.method_14870((Start)arg, list, random, j, 7);
            }
            if (this.lowerRightExists) {
                this.method_14873((Start)arg, list, random, i, 1);
            }
            if (this.upperRightExists) {
                this.method_14873((Start)arg, list, random, j, 7);
            }
        }

        public static FiveWayCrossing method_14858(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -4, -3, 0, 10, 9, 11, arg);
            if (!FiveWayCrossing.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new FiveWayCrossing(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 9, 8, 10, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 4, 3, 0);
            if (this.lowerLeftExists) {
                this.fillWithOutline(arg, arg4, 0, 3, 1, 0, 5, 3, AIR, AIR, false);
            }
            if (this.lowerRightExists) {
                this.fillWithOutline(arg, arg4, 9, 3, 1, 9, 5, 3, AIR, AIR, false);
            }
            if (this.upperLeftExists) {
                this.fillWithOutline(arg, arg4, 0, 5, 7, 0, 7, 9, AIR, AIR, false);
            }
            if (this.upperRightExists) {
                this.fillWithOutline(arg, arg4, 9, 5, 7, 9, 7, 9, AIR, AIR, false);
            }
            this.fillWithOutline(arg, arg4, 5, 1, 10, 7, 3, 10, AIR, AIR, false);
            this.fillWithOutline(arg, arg4, 1, 2, 1, 8, 2, 6, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 4, 1, 5, 4, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 8, 1, 5, 8, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 1, 4, 7, 3, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 1, 3, 5, 3, 3, 6, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 1, 7, 7, 1, 8, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(arg, arg4, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), false);
            this.addBlock(arg, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, arg4);
            return true;
        }
    }

    public static class Library
    extends Piece {
        private final boolean tall;

        public Library(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
            this.tall = arg.getBlockCountY() > 6;
        }

        public Library(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, arg2);
            this.tall = arg2.getBoolean("Tall");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("Tall", this.tall);
        }

        public static Library method_14860(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -4, -1, 0, 14, 11, 15, arg);
            if (!(Library.method_14871(lv) && StructurePiece.getOverlappingPiece(list, lv) == null || Library.method_14871(lv = BlockBox.rotated(i, j, k, -4, -1, 0, 14, 6, 15, arg)) && StructurePiece.getOverlappingPiece(list, lv) == null)) {
                return null;
            }
            return new Library(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            int i = 11;
            if (!this.tall) {
                i = 6;
            }
            this.fillWithOutline(arg, arg4, 0, 0, 0, 13, i - 1, 14, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 4, 1, 0);
            this.fillWithOutlineUnderSealevel(arg, arg4, random, 0.07f, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false, false);
            boolean j = true;
            int k = 12;
            for (int l = 1; l <= 13; ++l) {
                if ((l - 1) % 4 == 0) {
                    this.fillWithOutline(arg, arg4, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.fillWithOutline(arg, arg4, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.addBlock(arg, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, arg4);
                    this.addBlock(arg, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, arg4);
                    if (!this.tall) continue;
                    this.fillWithOutline(arg, arg4, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.fillWithOutline(arg, arg4, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    continue;
                }
                this.fillWithOutline(arg, arg4, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                if (!this.tall) continue;
                this.fillWithOutline(arg, arg4, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            }
            for (int m = 3; m < 12; m += 2) {
                this.fillWithOutline(arg, arg4, 3, 1, m, 4, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 6, 1, m, 7, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 9, 1, m, 10, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            }
            if (this.tall) {
                this.fillWithOutline(arg, arg4, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline(arg, arg4, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 11, arg4);
                this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 8, 5, 11, arg4);
                this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 10, arg4);
                BlockState lv = (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
                BlockState lv2 = (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
                this.fillWithOutline(arg, arg4, 3, 6, 3, 3, 6, 11, lv2, lv2, false);
                this.fillWithOutline(arg, arg4, 10, 6, 3, 10, 6, 9, lv2, lv2, false);
                this.fillWithOutline(arg, arg4, 4, 6, 2, 9, 6, 2, lv, lv, false);
                this.fillWithOutline(arg, arg4, 4, 6, 12, 7, 6, 12, lv, lv, false);
                this.addBlock(arg, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 3, 6, 2, arg4);
                this.addBlock(arg, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 3, 6, 12, arg4);
                this.addBlock(arg, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 10, 6, 2, arg4);
                for (int n = 0; n <= 2; ++n) {
                    this.addBlock(arg, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 8 + n, 6, 12 - n, arg4);
                    if (n == 2) continue;
                    this.addBlock(arg, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 8 + n, 6, 11 - n, arg4);
                }
                BlockState lv3 = (BlockState)Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
                this.addBlock(arg, lv3, 10, 1, 13, arg4);
                this.addBlock(arg, lv3, 10, 2, 13, arg4);
                this.addBlock(arg, lv3, 10, 3, 13, arg4);
                this.addBlock(arg, lv3, 10, 4, 13, arg4);
                this.addBlock(arg, lv3, 10, 5, 13, arg4);
                this.addBlock(arg, lv3, 10, 6, 13, arg4);
                this.addBlock(arg, lv3, 10, 7, 13, arg4);
                int o = 7;
                int p = 7;
                BlockState lv4 = (BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, true);
                this.addBlock(arg, lv4, 6, 9, 7, arg4);
                BlockState lv5 = (BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, true);
                this.addBlock(arg, lv5, 7, 9, 7, arg4);
                this.addBlock(arg, lv4, 6, 8, 7, arg4);
                this.addBlock(arg, lv5, 7, 8, 7, arg4);
                BlockState lv6 = (BlockState)((BlockState)lv2.with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
                this.addBlock(arg, lv6, 6, 7, 7, arg4);
                this.addBlock(arg, lv6, 7, 7, 7, arg4);
                this.addBlock(arg, lv4, 5, 7, 7, arg4);
                this.addBlock(arg, lv5, 8, 7, 7, arg4);
                this.addBlock(arg, (BlockState)lv4.with(FenceBlock.NORTH, true), 6, 7, 6, arg4);
                this.addBlock(arg, (BlockState)lv4.with(FenceBlock.SOUTH, true), 6, 7, 8, arg4);
                this.addBlock(arg, (BlockState)lv5.with(FenceBlock.NORTH, true), 7, 7, 6, arg4);
                this.addBlock(arg, (BlockState)lv5.with(FenceBlock.SOUTH, true), 7, 7, 8, arg4);
                BlockState lv7 = Blocks.TORCH.getDefaultState();
                this.addBlock(arg, lv7, 5, 8, 7, arg4);
                this.addBlock(arg, lv7, 8, 8, 7, arg4);
                this.addBlock(arg, lv7, 6, 8, 6, arg4);
                this.addBlock(arg, lv7, 6, 8, 8, arg4);
                this.addBlock(arg, lv7, 7, 8, 6, arg4);
                this.addBlock(arg, lv7, 7, 8, 8, arg4);
            }
            this.addChest(arg, arg4, random, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY_CHEST);
            if (this.tall) {
                this.addBlock(arg, AIR, 12, 9, 1, arg4);
                this.addChest(arg, arg4, random, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY_CHEST);
            }
            return true;
        }
    }

    public static class PrisonHall
    extends Piece {
        public PrisonHall(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
        }

        public PrisonHall(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14874((Start)arg, list, random, 1, 1);
        }

        public static PrisonHall method_14864(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -1, 0, 9, 5, 11, arg);
            if (!PrisonHall.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new PrisonHall(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 8, 4, 10, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 1, 1, 0);
            this.fillWithOutline(arg, arg4, 1, 1, 10, 3, 3, 10, AIR, AIR, false);
            this.fillWithOutline(arg, arg4, 4, 1, 1, 4, 3, 1, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 4, 1, 3, 4, 3, 3, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 4, 1, 7, 4, 3, 7, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline(arg, arg4, 4, 1, 9, 4, 3, 9, false, random, STONE_BRICK_RANDOMIZER);
            for (int i = 1; i <= 3; ++i) {
                this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, i, 4, arg4);
                this.addBlock(arg, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)).with(PaneBlock.EAST, true), 4, i, 5, arg4);
                this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, i, 6, arg4);
                this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 5, i, 5, arg4);
                this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 6, i, 5, arg4);
                this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 7, i, 5, arg4);
            }
            this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, 3, 2, arg4);
            this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, 3, 8, arg4);
            BlockState lv = (BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST);
            BlockState lv2 = (BlockState)((BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST)).with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
            this.addBlock(arg, lv, 4, 1, 2, arg4);
            this.addBlock(arg, lv2, 4, 2, 2, arg4);
            this.addBlock(arg, lv, 4, 1, 8, arg4);
            this.addBlock(arg, lv2, 4, 2, 8, arg4);
            return true;
        }
    }

    public static class SquareRoom
    extends Piece {
        protected final int roomType;

        public SquareRoom(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
            this.roomType = random.nextInt(5);
        }

        public SquareRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, arg2);
            this.roomType = arg2.getInt("Type");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putInt("Type", this.roomType);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14874((Start)arg, list, random, 4, 1);
            this.method_14870((Start)arg, list, random, 1, 4);
            this.method_14873((Start)arg, list, random, 1, 4);
        }

        public static SquareRoom method_14865(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -4, -1, 0, 11, 7, 11, arg);
            if (!SquareRoom.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new SquareRoom(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 10, 6, 10, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 4, 1, 0);
            this.fillWithOutline(arg, arg4, 4, 1, 10, 6, 3, 10, AIR, AIR, false);
            this.fillWithOutline(arg, arg4, 0, 1, 4, 0, 3, 6, AIR, AIR, false);
            this.fillWithOutline(arg, arg4, 10, 1, 4, 10, 3, 6, AIR, AIR, false);
            switch (this.roomType) {
                default: {
                    break;
                }
                case 0: {
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, arg4);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, arg4);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, arg4);
                    this.addBlock(arg, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, arg4);
                    this.addBlock(arg, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, arg4);
                    this.addBlock(arg, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, arg4);
                    this.addBlock(arg, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, arg4);
                    this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 4, arg4);
                    this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 5, arg4);
                    this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 6, arg4);
                    this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 4, arg4);
                    this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 5, arg4);
                    this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 6, arg4);
                    this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 4, arg4);
                    this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 6, arg4);
                    break;
                }
                case 1: {
                    for (int i = 0; i < 5; ++i) {
                        this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i, arg4);
                        this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i, arg4);
                        this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 3, arg4);
                        this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 7, arg4);
                    }
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, arg4);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, arg4);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, arg4);
                    this.addBlock(arg, Blocks.WATER.getDefaultState(), 5, 4, 5, arg4);
                    break;
                }
                case 2: {
                    for (int j = 1; j <= 9; ++j) {
                        this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 1, 3, j, arg4);
                        this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 9, 3, j, arg4);
                    }
                    for (int k = 1; k <= 9; ++k) {
                        this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), k, 3, 1, arg4);
                        this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), k, 3, 9, arg4);
                    }
                    this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, arg4);
                    this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, arg4);
                    this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, arg4);
                    this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, arg4);
                    this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, arg4);
                    this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, arg4);
                    this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, arg4);
                    this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, arg4);
                    for (int l = 1; l <= 3; ++l) {
                        this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 4, l, 4, arg4);
                        this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 6, l, 4, arg4);
                        this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 4, l, 6, arg4);
                        this.addBlock(arg, Blocks.COBBLESTONE.getDefaultState(), 6, l, 6, arg4);
                    }
                    this.addBlock(arg, Blocks.TORCH.getDefaultState(), 5, 3, 5, arg4);
                    for (int m = 2; m <= 8; ++m) {
                        this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 2, 3, m, arg4);
                        this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 3, 3, m, arg4);
                        if (m <= 3 || m >= 7) {
                            this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 4, 3, m, arg4);
                            this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 5, 3, m, arg4);
                            this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 6, 3, m, arg4);
                        }
                        this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 7, 3, m, arg4);
                        this.addBlock(arg, Blocks.OAK_PLANKS.getDefaultState(), 8, 3, m, arg4);
                    }
                    BlockState lv = (BlockState)Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.WEST);
                    this.addBlock(arg, lv, 9, 1, 3, arg4);
                    this.addBlock(arg, lv, 9, 2, 3, arg4);
                    this.addBlock(arg, lv, 9, 3, 3, arg4);
                    this.addChest(arg, arg4, random, 3, 4, 8, LootTables.STRONGHOLD_CROSSING_CHEST);
                }
            }
            return true;
        }
    }

    public static class RightTurn
    extends Turn {
        public RightTurn(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
        }

        public RightTurn(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            Direction lv = this.getFacing();
            if (lv == Direction.NORTH || lv == Direction.EAST) {
                this.method_14873((Start)arg, list, random, 1, 1);
            } else {
                this.method_14870((Start)arg, list, random, 1, 1);
            }
        }

        public static RightTurn method_16652(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 5, arg);
            if (!RightTurn.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new RightTurn(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 4, 4, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 1, 1, 0);
            Direction lv = this.getFacing();
            if (lv == Direction.NORTH || lv == Direction.EAST) {
                this.fillWithOutline(arg, arg4, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
            } else {
                this.fillWithOutline(arg, arg4, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
            }
            return true;
        }
    }

    public static class LeftTurn
    extends Turn {
        public LeftTurn(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
        }

        public LeftTurn(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            Direction lv = this.getFacing();
            if (lv == Direction.NORTH || lv == Direction.EAST) {
                this.method_14870((Start)arg, list, random, 1, 1);
            } else {
                this.method_14873((Start)arg, list, random, 1, 1);
            }
        }

        public static LeftTurn method_14859(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 5, arg);
            if (!LeftTurn.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new LeftTurn(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 4, 4, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 1, 1, 0);
            Direction lv = this.getFacing();
            if (lv == Direction.NORTH || lv == Direction.EAST) {
                this.fillWithOutline(arg, arg4, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
            } else {
                this.fillWithOutline(arg, arg4, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
            }
            return true;
        }
    }

    public static abstract class Turn
    extends Piece {
        protected Turn(StructurePieceType arg, int i) {
            super(arg, i);
        }

        public Turn(StructurePieceType arg, CompoundTag arg2) {
            super(arg, arg2);
        }
    }

    public static class Stairs
    extends Piece {
        public Stairs(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_STAIRS, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
        }

        public Stairs(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_STAIRS, arg2);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14874((Start)arg, list, random, 1, 1);
        }

        public static Stairs method_14868(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -7, 0, 5, 11, 8, arg);
            if (!Stairs.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new Stairs(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 10, 7, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 1, 7, 0);
            this.generateEntrance(arg, random, arg4, Piece.EntranceType.OPENING, 1, 1, 7);
            BlockState lv = (BlockState)Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
            for (int i = 0; i < 6; ++i) {
                this.addBlock(arg, lv, 1, 6 - i, 1 + i, arg4);
                this.addBlock(arg, lv, 2, 6 - i, 1 + i, arg4);
                this.addBlock(arg, lv, 3, 6 - i, 1 + i, arg4);
                if (i >= 5) continue;
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - i, 1 + i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - i, 1 + i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - i, 1 + i, arg4);
            }
            return true;
        }
    }

    public static class ChestCorridor
    extends Piece {
        private boolean chestGenerated;

        public ChestCorridor(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
        }

        public ChestCorridor(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, arg2);
            this.chestGenerated = arg2.getBoolean("Chest");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("Chest", this.chestGenerated);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14874((Start)arg, list, random, 1, 1);
        }

        public static ChestCorridor method_14856(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 7, arg);
            if (!ChestCorridor.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new ChestCorridor(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 4, 6, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 1, 1, 0);
            this.generateEntrance(arg, random, arg4, Piece.EntranceType.OPENING, 1, 1, 6);
            this.fillWithOutline(arg, arg4, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
            this.addBlock(arg, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 1, arg4);
            this.addBlock(arg, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 5, arg4);
            this.addBlock(arg, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 2, arg4);
            this.addBlock(arg, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 4, arg4);
            for (int i = 2; i <= 4; ++i) {
                this.addBlock(arg, Blocks.STONE_BRICK_SLAB.getDefaultState(), 2, 1, i, arg4);
            }
            if (!this.chestGenerated && arg4.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
                this.chestGenerated = true;
                this.addChest(arg, arg4, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR_CHEST);
            }
            return true;
        }
    }

    public static class Corridor
    extends Piece {
        private final boolean leftExitExists;
        private final boolean rightExitExixts;

        public Corridor(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_CORRIDOR, i);
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
            this.leftExitExists = random.nextInt(2) == 0;
            this.rightExitExixts = random.nextInt(2) == 0;
        }

        public Corridor(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_CORRIDOR, arg2);
            this.leftExitExists = arg2.getBoolean("Left");
            this.rightExitExixts = arg2.getBoolean("Right");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("Left", this.leftExitExists);
            arg.putBoolean("Right", this.rightExitExixts);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            this.method_14874((Start)arg, list, random, 1, 1);
            if (this.leftExitExists) {
                this.method_14870((Start)arg, list, random, 1, 2);
            }
            if (this.rightExitExixts) {
                this.method_14873((Start)arg, list, random, 1, 2);
            }
        }

        public static Corridor method_14867(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 7, arg);
            if (!Corridor.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new Corridor(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 4, 6, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 1, 1, 0);
            this.generateEntrance(arg, random, arg4, Piece.EntranceType.OPENING, 1, 1, 6);
            BlockState lv = (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST);
            BlockState lv2 = (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST);
            this.addBlockWithRandomThreshold(arg, arg4, random, 0.1f, 1, 2, 1, lv);
            this.addBlockWithRandomThreshold(arg, arg4, random, 0.1f, 3, 2, 1, lv2);
            this.addBlockWithRandomThreshold(arg, arg4, random, 0.1f, 1, 2, 5, lv);
            this.addBlockWithRandomThreshold(arg, arg4, random, 0.1f, 3, 2, 5, lv2);
            if (this.leftExitExists) {
                this.fillWithOutline(arg, arg4, 0, 1, 2, 0, 3, 4, AIR, AIR, false);
            }
            if (this.rightExitExixts) {
                this.fillWithOutline(arg, arg4, 4, 1, 2, 4, 3, 4, AIR, AIR, false);
            }
            return true;
        }
    }

    public static class Start
    extends SpiralStaircase {
        public PieceSetting field_15284;
        @Nullable
        public PortalRoom field_15283;
        public final List<StructurePiece> field_15282 = Lists.newArrayList();

        public Start(Random random, int i, int j) {
            super(StructurePieceType.STRONGHOLD_START, 0, random, i, j);
        }

        public Start(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_START, arg2);
        }
    }

    public static class SpiralStaircase
    extends Piece {
        private final boolean isStructureStart;

        public SpiralStaircase(StructurePieceType arg, int i, Random random, int j, int k) {
            super(arg, i);
            this.isStructureStart = true;
            this.setOrientation(Direction.Type.HORIZONTAL.random(random));
            this.entryDoor = Piece.EntranceType.OPENING;
            this.boundingBox = this.getFacing().getAxis() == Direction.Axis.Z ? new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1) : new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
        }

        public SpiralStaircase(int i, Random random, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, i);
            this.isStructureStart = false;
            this.setOrientation(arg2);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = arg;
        }

        public SpiralStaircase(StructurePieceType arg, CompoundTag arg2) {
            super(arg, arg2);
            this.isStructureStart = arg2.getBoolean("Source");
        }

        public SpiralStaircase(StructureManager arg, CompoundTag arg2) {
            this(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, arg2);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("Source", this.isStructureStart);
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            if (this.isStructureStart) {
                activePieceType = FiveWayCrossing.class;
            }
            this.method_14874((Start)arg, list, random, 1, 1);
        }

        public static SpiralStaircase method_14866(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg, int l) {
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -7, 0, 5, 11, 5, arg);
            if (!SpiralStaircase.method_14871(lv) || StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return new SpiralStaircase(l, random, lv, arg);
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.fillWithOutline(arg, arg4, 0, 0, 0, 4, 10, 4, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(arg, random, arg4, this.entryDoor, 1, 7, 0);
            this.generateEntrance(arg, random, arg4, Piece.EntranceType.OPENING, 1, 1, 4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, arg4);
            this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 6, 1, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, arg4);
            this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 5, 3, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, arg4);
            this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 4, 3, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, arg4);
            this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 3, 1, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, arg4);
            this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 2, 1, arg4);
            this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, arg4);
            this.addBlock(arg, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 1, 3, arg4);
            return true;
        }
    }

    public static class SmallCorridor
    extends Piece {
        private final int length;

        public SmallCorridor(int i, BlockBox arg, Direction arg2) {
            super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, i);
            this.setOrientation(arg2);
            this.boundingBox = arg;
            this.length = arg2 == Direction.NORTH || arg2 == Direction.SOUTH ? arg.getBlockCountZ() : arg.getBlockCountX();
        }

        public SmallCorridor(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, arg2);
            this.length = arg2.getInt("Steps");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putInt("Steps", this.length);
        }

        public static BlockBox method_14857(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg) {
            int l = 3;
            BlockBox lv = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 4, arg);
            StructurePiece lv2 = StructurePiece.getOverlappingPiece(list, lv);
            if (lv2 == null) {
                return null;
            }
            if (lv2.getBoundingBox().minY == lv.minY) {
                for (int m = 3; m >= 1; --m) {
                    lv = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, m - 1, arg);
                    if (lv2.getBoundingBox().intersects(lv)) continue;
                    return BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, m, arg);
                }
            }
            return null;
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            for (int i = 0; i < this.length; ++i) {
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, arg4);
                for (int j = 1; j <= 3; ++j) {
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, arg4);
                    this.addBlock(arg, Blocks.CAVE_AIR.getDefaultState(), 1, j, i, arg4);
                    this.addBlock(arg, Blocks.CAVE_AIR.getDefaultState(), 2, j, i, arg4);
                    this.addBlock(arg, Blocks.CAVE_AIR.getDefaultState(), 3, j, i, arg4);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, arg4);
                }
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, arg4);
                this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, arg4);
            }
            return true;
        }
    }

    static abstract class Piece
    extends StructurePiece {
        protected EntranceType entryDoor = EntranceType.OPENING;

        protected Piece(StructurePieceType arg, int i) {
            super(arg, i);
        }

        public Piece(StructurePieceType arg, CompoundTag arg2) {
            super(arg, arg2);
            this.entryDoor = EntranceType.valueOf(arg2.getString("EntryDoor"));
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            arg.putString("EntryDoor", this.entryDoor.name());
        }

        protected void generateEntrance(IWorld arg, Random random, BlockBox arg2, EntranceType arg3, int i, int j, int k) {
            switch (arg3) {
                case OPENING: {
                    this.fillWithOutline(arg, arg2, i, j, k, i + 3 - 1, j + 3 - 1, k, AIR, AIR, false);
                    break;
                }
                case WOOD_DOOR: {
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i, j, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i, j + 1, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i, j + 2, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i + 1, j + 2, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 2, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 1, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j, k, arg2);
                    this.addBlock(arg, Blocks.OAK_DOOR.getDefaultState(), i + 1, j, k, arg2);
                    this.addBlock(arg, (BlockState)Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), i + 1, j + 1, k, arg2);
                    break;
                }
                case GRATES: {
                    this.addBlock(arg, Blocks.CAVE_AIR.getDefaultState(), i + 1, j, k, arg2);
                    this.addBlock(arg, Blocks.CAVE_AIR.getDefaultState(), i + 1, j + 1, k, arg2);
                    this.addBlock(arg, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true), i, j, k, arg2);
                    this.addBlock(arg, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true), i, j + 1, k, arg2);
                    this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), i, j + 2, k, arg2);
                    this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), i + 1, j + 2, k, arg2);
                    this.addBlock(arg, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), i + 2, j + 2, k, arg2);
                    this.addBlock(arg, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true), i + 2, j + 1, k, arg2);
                    this.addBlock(arg, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true), i + 2, j, k, arg2);
                    break;
                }
                case IRON_DOOR: {
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i, j, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i, j + 1, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i, j + 2, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i + 1, j + 2, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 2, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 1, k, arg2);
                    this.addBlock(arg, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j, k, arg2);
                    this.addBlock(arg, Blocks.IRON_DOOR.getDefaultState(), i + 1, j, k, arg2);
                    this.addBlock(arg, (BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), i + 1, j + 1, k, arg2);
                    this.addBlock(arg, (BlockState)Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.FACING, Direction.NORTH), i + 2, j + 1, k + 1, arg2);
                    this.addBlock(arg, (BlockState)Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.FACING, Direction.SOUTH), i + 2, j + 1, k - 1, arg2);
                }
            }
        }

        protected EntranceType getRandomEntrance(Random random) {
            int i = random.nextInt(5);
            switch (i) {
                default: {
                    return EntranceType.OPENING;
                }
                case 2: {
                    return EntranceType.WOOD_DOOR;
                }
                case 3: {
                    return EntranceType.GRATES;
                }
                case 4: 
            }
            return EntranceType.IRON_DOOR;
        }

        @Nullable
        protected StructurePiece method_14874(Start arg, List<StructurePiece> list, Random random, int i, int j) {
            Direction lv = this.getFacing();
            if (lv != null) {
                switch (lv) {
                    case NORTH: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.minZ - 1, lv, this.getLength());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.maxZ + 1, lv, this.getLength());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, lv, this.getLength());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, lv, this.getLength());
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece method_14870(Start arg, List<StructurePiece> list, Random random, int i, int j) {
            Direction lv = this.getFacing();
            if (lv != null) {
                switch (lv) {
                    case NORTH: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength());
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece method_14873(Start arg, List<StructurePiece> list, Random random, int i, int j) {
            Direction lv = this.getFacing();
            if (lv != null) {
                switch (lv) {
                    case NORTH: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(arg, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength());
                    }
                }
            }
            return null;
        }

        protected static boolean method_14871(BlockBox arg) {
            return arg != null && arg.minY > 10;
        }

        public static enum EntranceType {
            OPENING,
            WOOD_DOOR,
            GRATES,
            IRON_DOOR;

        }
    }

    static class PieceSetting {
        public final Class<? extends Piece> pieceType;
        public final int field_15278;
        public int generatedCount;
        public final int limit;

        public PieceSetting(Class<? extends Piece> arg, int i, int j) {
            this.pieceType = arg;
            this.field_15278 = i;
            this.limit = j;
        }

        public boolean canGenerate(int i) {
            return this.limit == 0 || this.generatedCount < this.limit;
        }

        public boolean canGenerate() {
            return this.limit == 0 || this.generatedCount < this.limit;
        }
    }
}

