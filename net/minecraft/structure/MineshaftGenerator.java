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
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftGenerator {
    private static MineshaftPart getRandomJigsaw(List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction arg, int l, MineshaftFeature.Type arg2) {
        int m = random.nextInt(100);
        if (m >= 80) {
            BlockBox lv = MineshaftCrossing.getBoundingBox(list, random, i, j, k, arg);
            if (lv != null) {
                return new MineshaftCrossing(l, lv, arg, arg2);
            }
        } else if (m >= 70) {
            BlockBox lv2 = MineshaftStairs.getBoundingBox(list, random, i, j, k, arg);
            if (lv2 != null) {
                return new MineshaftStairs(l, lv2, arg, arg2);
            }
        } else {
            BlockBox lv3 = MineshaftCorridor.getBoundingBox(list, random, i, j, k, arg);
            if (lv3 != null) {
                return new MineshaftCorridor(l, random, lv3, arg, arg2);
            }
        }
        return null;
    }

    private static MineshaftPart tryGenerateJigsaw(StructurePiece arg, List<StructurePiece> list, Random random, int i, int j, int k, Direction arg2, int l) {
        if (l > 8) {
            return null;
        }
        if (Math.abs(i - arg.getBoundingBox().minX) > 80 || Math.abs(k - arg.getBoundingBox().minZ) > 80) {
            return null;
        }
        MineshaftFeature.Type lv = ((MineshaftPart)arg).mineshaftType;
        MineshaftPart lv2 = MineshaftGenerator.getRandomJigsaw(list, random, i, j, k, arg2, l + 1, lv);
        if (lv2 != null) {
            list.add(lv2);
            lv2.placeJigsaw(arg, list, random);
        }
        return lv2;
    }

    public static class MineshaftStairs
    extends MineshaftPart {
        public MineshaftStairs(int i, BlockBox arg, Direction arg2, MineshaftFeature.Type arg3) {
            super(StructurePieceType.MINESHAFT_STAIRS, i, arg3);
            this.setOrientation(arg2);
            this.boundingBox = arg;
        }

        public MineshaftStairs(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.MINESHAFT_STAIRS, arg2);
        }

        public static BlockBox getBoundingBox(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg) {
            BlockBox lv = new BlockBox(i, j - 5, k, i, j + 3 - 1, k);
            switch (arg) {
                default: {
                    lv.maxX = i + 3 - 1;
                    lv.minZ = k - 8;
                    break;
                }
                case SOUTH: {
                    lv.maxX = i + 3 - 1;
                    lv.maxZ = k + 8;
                    break;
                }
                case WEST: {
                    lv.minX = i - 8;
                    lv.maxZ = k + 3 - 1;
                    break;
                }
                case EAST: {
                    lv.maxX = i + 8;
                    lv.maxZ = k + 3 - 1;
                }
            }
            if (StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return lv;
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            int i = this.getLength();
            Direction lv = this.getFacing();
            if (lv != null) {
                switch (lv) {
                    default: {
                        MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
                        break;
                    }
                    case SOUTH: {
                        MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                        break;
                    }
                    case WEST: {
                        MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.WEST, i);
                        break;
                    }
                    case EAST: {
                        MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.EAST, i);
                    }
                }
            }
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            if (this.method_14937(arg, arg4)) {
                return false;
            }
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 0, 7, 2, 2, 8, AIR, AIR, false);
            for (int i = 0; i < 5; ++i) {
                this.fillWithOutline((WorldAccess)arg, arg4, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
            }
            return true;
        }
    }

    public static class MineshaftCrossing
    extends MineshaftPart {
        private final Direction direction;
        private final boolean twoFloors;

        public MineshaftCrossing(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.MINESHAFT_CROSSING, arg2);
            this.twoFloors = arg2.getBoolean("tf");
            this.direction = Direction.fromHorizontal(arg2.getInt("D"));
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("tf", this.twoFloors);
            arg.putInt("D", this.direction.getHorizontal());
        }

        public MineshaftCrossing(int i, BlockBox arg, @Nullable Direction arg2, MineshaftFeature.Type arg3) {
            super(StructurePieceType.MINESHAFT_CROSSING, i, arg3);
            this.direction = arg2;
            this.boundingBox = arg;
            this.twoFloors = arg.getBlockCountY() > 3;
        }

        public static BlockBox getBoundingBox(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg) {
            BlockBox lv = new BlockBox(i, j, k, i, j + 3 - 1, k);
            if (random.nextInt(4) == 0) {
                lv.maxY += 4;
            }
            switch (arg) {
                default: {
                    lv.minX = i - 1;
                    lv.maxX = i + 3;
                    lv.minZ = k - 4;
                    break;
                }
                case SOUTH: {
                    lv.minX = i - 1;
                    lv.maxX = i + 3;
                    lv.maxZ = k + 3 + 1;
                    break;
                }
                case WEST: {
                    lv.minX = i - 4;
                    lv.minZ = k - 1;
                    lv.maxZ = k + 3;
                    break;
                }
                case EAST: {
                    lv.maxX = i + 3 + 1;
                    lv.minZ = k - 1;
                    lv.maxZ = k + 3;
                }
            }
            if (StructurePiece.getOverlappingPiece(list, lv) != null) {
                return null;
            }
            return lv;
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            int i = this.getLength();
            switch (this.direction) {
                default: {
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
                    break;
                }
                case SOUTH: {
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
                    break;
                }
                case WEST: {
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
                    break;
                }
                case EAST: {
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
                }
            }
            if (this.twoFloors) {
                if (random.nextBoolean()) {
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.NORTH, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.WEST, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.EAST, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                }
            }
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            if (this.method_14937(arg, arg4)) {
                return false;
            }
            BlockState lv = this.getPlanksType();
            if (this.twoFloors) {
                this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, AIR, AIR, false);
                this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, AIR, AIR, false);
                this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, AIR, false);
                this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, AIR, AIR, false);
                this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, AIR, AIR, false);
            } else {
                this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, AIR, false);
                this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, AIR, AIR, false);
            }
            this.method_14716(arg, arg4, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.method_14716(arg, arg4, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
            this.method_14716(arg, arg4, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.method_14716(arg, arg4, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
            for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i) {
                for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j) {
                    if (!this.getBlockAt(arg, i, this.boundingBox.minY - 1, j, arg4).isAir() || !this.isUnderSeaLevel(arg, i, this.boundingBox.minY - 1, j, arg4)) continue;
                    this.addBlock(arg, lv, i, this.boundingBox.minY - 1, j, arg4);
                }
            }
            return true;
        }

        private void method_14716(WorldAccess arg, BlockBox arg2, int i, int j, int k, int l) {
            if (!this.getBlockAt(arg, i, l + 1, k, arg2).isAir()) {
                this.fillWithOutline(arg, arg2, i, j, k, i, l, k, this.getPlanksType(), AIR, false);
            }
        }
    }

    public static class MineshaftCorridor
    extends MineshaftPart {
        private final boolean hasRails;
        private final boolean hasCobwebs;
        private boolean hasSpawner;
        private final int length;

        public MineshaftCorridor(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.MINESHAFT_CORRIDOR, arg2);
            this.hasRails = arg2.getBoolean("hr");
            this.hasCobwebs = arg2.getBoolean("sc");
            this.hasSpawner = arg2.getBoolean("hps");
            this.length = arg2.getInt("Num");
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putBoolean("hr", this.hasRails);
            arg.putBoolean("sc", this.hasCobwebs);
            arg.putBoolean("hps", this.hasSpawner);
            arg.putInt("Num", this.length);
        }

        public MineshaftCorridor(int i, Random random, BlockBox arg, Direction arg2, MineshaftFeature.Type arg3) {
            super(StructurePieceType.MINESHAFT_CORRIDOR, i, arg3);
            this.setOrientation(arg2);
            this.boundingBox = arg;
            this.hasRails = random.nextInt(3) == 0;
            this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
            this.length = this.getFacing().getAxis() == Direction.Axis.Z ? arg.getBlockCountZ() / 5 : arg.getBlockCountX() / 5;
        }

        public static BlockBox getBoundingBox(List<StructurePiece> list, Random random, int i, int j, int k, Direction arg) {
            int l;
            BlockBox lv = new BlockBox(i, j, k, i, j + 3 - 1, k);
            for (l = random.nextInt(3) + 2; l > 0; --l) {
                int m = l * 5;
                switch (arg) {
                    default: {
                        lv.maxX = i + 3 - 1;
                        lv.minZ = k - (m - 1);
                        break;
                    }
                    case SOUTH: {
                        lv.maxX = i + 3 - 1;
                        lv.maxZ = k + m - 1;
                        break;
                    }
                    case WEST: {
                        lv.minX = i - (m - 1);
                        lv.maxZ = k + 3 - 1;
                        break;
                    }
                    case EAST: {
                        lv.maxX = i + m - 1;
                        lv.maxZ = k + 3 - 1;
                    }
                }
                if (StructurePiece.getOverlappingPiece(list, lv) == null) break;
            }
            if (l > 0) {
                return lv;
            }
            return null;
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            block24: {
                int i = this.getLength();
                int j = random.nextInt(4);
                Direction lv = this.getFacing();
                if (lv != null) {
                    switch (lv) {
                        default: {
                            if (j <= 1) {
                                MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, lv, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.WEST, i);
                                break;
                            }
                            MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.EAST, i);
                            break;
                        }
                        case SOUTH: {
                            if (j <= 1) {
                                MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, lv, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.WEST, i);
                                break;
                            }
                            MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.EAST, i);
                            break;
                        }
                        case WEST: {
                            if (j <= 1) {
                                MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, lv, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i);
                                break;
                            }
                            MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                            break;
                        }
                        case EAST: {
                            if (j <= 1) {
                                MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, lv, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i);
                                break;
                            }
                            MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                        }
                    }
                }
                if (i >= 8) break block24;
                if (lv == Direction.NORTH || lv == Direction.SOUTH) {
                    int k = this.boundingBox.minZ + 3;
                    while (k + 3 <= this.boundingBox.maxZ) {
                        int l = random.nextInt(5);
                        if (l == 0) {
                            MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, k, Direction.WEST, i + 1);
                        } else if (l == 1) {
                            MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, k, Direction.EAST, i + 1);
                        }
                        k += 5;
                    }
                } else {
                    int m = this.boundingBox.minX + 3;
                    while (m + 3 <= this.boundingBox.maxX) {
                        int n = random.nextInt(5);
                        if (n == 0) {
                            MineshaftGenerator.tryGenerateJigsaw(arg, list, random, m, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i + 1);
                        } else if (n == 1) {
                            MineshaftGenerator.tryGenerateJigsaw(arg, list, random, m, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i + 1);
                        }
                        m += 5;
                    }
                }
            }
        }

        @Override
        protected boolean addChest(WorldAccess arg, BlockBox arg2, Random random, int i, int j, int k, Identifier arg3) {
            BlockPos lv = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
            if (arg2.contains(lv) && arg.getBlockState(lv).isAir() && !arg.getBlockState(lv.down()).isAir()) {
                BlockState lv2 = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
                this.addBlock(arg, lv2, i, j, k, arg2);
                ChestMinecartEntity lv3 = new ChestMinecartEntity(arg.getWorld(), (float)lv.getX() + 0.5f, (float)lv.getY() + 0.5f, (float)lv.getZ() + 0.5f);
                lv3.setLootTable(arg3, random.nextLong());
                arg.spawnEntity(lv3);
                return true;
            }
            return false;
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            if (this.method_14937(arg, arg4)) {
                return false;
            }
            boolean i = false;
            int j = 2;
            boolean k = false;
            int l = 2;
            int m = this.length * 5 - 1;
            BlockState lv = this.getPlanksType();
            this.fillWithOutline((WorldAccess)arg, arg4, 0, 0, 0, 2, 1, m, AIR, AIR, false);
            this.fillWithOutlineUnderSealevel(arg, arg4, random, 0.8f, 0, 2, 0, 2, 2, m, AIR, AIR, false, false);
            if (this.hasCobwebs) {
                this.fillWithOutlineUnderSealevel(arg, arg4, random, 0.6f, 0, 0, 0, 2, 1, m, Blocks.COBWEB.getDefaultState(), AIR, false, true);
            }
            for (int n = 0; n < this.length; ++n) {
                int s;
                int o = 2 + n * 5;
                this.method_14713(arg, arg4, 0, 0, o, 2, 2, random);
                this.method_14715(arg, arg4, random, 0.1f, 0, 2, o - 1);
                this.method_14715(arg, arg4, random, 0.1f, 2, 2, o - 1);
                this.method_14715(arg, arg4, random, 0.1f, 0, 2, o + 1);
                this.method_14715(arg, arg4, random, 0.1f, 2, 2, o + 1);
                this.method_14715(arg, arg4, random, 0.05f, 0, 2, o - 2);
                this.method_14715(arg, arg4, random, 0.05f, 2, 2, o - 2);
                this.method_14715(arg, arg4, random, 0.05f, 0, 2, o + 2);
                this.method_14715(arg, arg4, random, 0.05f, 2, 2, o + 2);
                if (random.nextInt(100) == 0) {
                    this.addChest(arg, arg4, random, 2, 0, o - 1, LootTables.ABANDONED_MINESHAFT_CHEST);
                }
                if (random.nextInt(100) == 0) {
                    this.addChest(arg, arg4, random, 0, 0, o + 1, LootTables.ABANDONED_MINESHAFT_CHEST);
                }
                if (!this.hasCobwebs || this.hasSpawner) continue;
                int p = this.applyYTransform(0);
                int q = o - 1 + random.nextInt(3);
                int r = this.applyXTransform(1, q);
                BlockPos lv2 = new BlockPos(r, p, s = this.applyZTransform(1, q));
                if (!arg4.contains(lv2) || !this.isUnderSeaLevel(arg, 1, 0, q, arg4)) continue;
                this.hasSpawner = true;
                arg.setBlockState(lv2, Blocks.SPAWNER.getDefaultState(), 2);
                BlockEntity lv3 = arg.getBlockEntity(lv2);
                if (!(lv3 instanceof MobSpawnerBlockEntity)) continue;
                ((MobSpawnerBlockEntity)lv3).getLogic().setEntityId(EntityType.CAVE_SPIDER);
            }
            for (int t = 0; t <= 2; ++t) {
                for (int u = 0; u <= m; ++u) {
                    int v = -1;
                    BlockState lv4 = this.getBlockAt(arg, t, -1, u, arg4);
                    if (!lv4.isAir() || !this.isUnderSeaLevel(arg, t, -1, u, arg4)) continue;
                    int w = -1;
                    this.addBlock(arg, lv, t, -1, u, arg4);
                }
            }
            if (this.hasRails) {
                BlockState lv5 = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH);
                for (int x = 0; x <= m; ++x) {
                    BlockState lv6 = this.getBlockAt(arg, 1, -1, x, arg4);
                    if (lv6.isAir() || !lv6.isOpaqueFullCube(arg, new BlockPos(this.applyXTransform(1, x), this.applyYTransform(-1), this.applyZTransform(1, x)))) continue;
                    float f = this.isUnderSeaLevel(arg, 1, 0, x, arg4) ? 0.7f : 0.9f;
                    this.addBlockWithRandomThreshold(arg, arg4, random, f, 1, 0, x, lv5);
                }
            }
            return true;
        }

        private void method_14713(WorldAccess arg, BlockBox arg2, int i, int j, int k, int l, int m, Random random) {
            if (!this.method_14719(arg, arg2, i, m, l, k)) {
                return;
            }
            BlockState lv = this.getPlanksType();
            BlockState lv2 = this.getFenceType();
            this.fillWithOutline(arg, arg2, i, j, k, i, l - 1, k, (BlockState)lv2.with(FenceBlock.WEST, true), AIR, false);
            this.fillWithOutline(arg, arg2, m, j, k, m, l - 1, k, (BlockState)lv2.with(FenceBlock.EAST, true), AIR, false);
            if (random.nextInt(4) == 0) {
                this.fillWithOutline(arg, arg2, i, l, k, i, l, k, lv, AIR, false);
                this.fillWithOutline(arg, arg2, m, l, k, m, l, k, lv, AIR, false);
            } else {
                this.fillWithOutline(arg, arg2, i, l, k, m, l, k, lv, AIR, false);
                this.addBlockWithRandomThreshold(arg, arg2, random, 0.05f, i + 1, l, k - 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH));
                this.addBlockWithRandomThreshold(arg, arg2, random, 0.05f, i + 1, l, k + 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH));
            }
        }

        private void method_14715(WorldAccess arg, BlockBox arg2, Random random, float f, int i, int j, int k) {
            if (this.isUnderSeaLevel(arg, i, j, k, arg2)) {
                this.addBlockWithRandomThreshold(arg, arg2, random, f, i, j, k, Blocks.COBWEB.getDefaultState());
            }
        }
    }

    public static class MineshaftRoom
    extends MineshaftPart {
        private final List<BlockBox> entrances = Lists.newLinkedList();

        public MineshaftRoom(int i, Random random, int j, int k, MineshaftFeature.Type arg) {
            super(StructurePieceType.MINESHAFT_ROOM, i, arg);
            this.mineshaftType = arg;
            this.boundingBox = new BlockBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
        }

        public MineshaftRoom(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.MINESHAFT_ROOM, arg2);
            ListTag lv = arg2.getList("Entrances", 11);
            for (int i = 0; i < lv.size(); ++i) {
                this.entrances.add(new BlockBox(lv.getIntArray(i)));
            }
        }

        @Override
        public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
            int k;
            int i = this.getLength();
            int j = this.boundingBox.getBlockCountY() - 3 - 1;
            if (j <= 0) {
                j = 1;
            }
            for (k = 0; k < this.boundingBox.getBlockCountX() && (k += random.nextInt(this.boundingBox.getBlockCountX())) + 3 <= this.boundingBox.getBlockCountX(); k += 4) {
                MineshaftPart lv = MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ - 1, Direction.NORTH, i);
                if (lv == null) continue;
                BlockBox lv2 = lv.getBoundingBox();
                this.entrances.add(new BlockBox(lv2.minX, lv2.minY, this.boundingBox.minZ, lv2.maxX, lv2.maxY, this.boundingBox.minZ + 1));
            }
            for (k = 0; k < this.boundingBox.getBlockCountX() && (k += random.nextInt(this.boundingBox.getBlockCountX())) + 3 <= this.boundingBox.getBlockCountX(); k += 4) {
                MineshaftPart lv3 = MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                if (lv3 == null) continue;
                BlockBox lv4 = lv3.getBoundingBox();
                this.entrances.add(new BlockBox(lv4.minX, lv4.minY, this.boundingBox.maxZ - 1, lv4.maxX, lv4.maxY, this.boundingBox.maxZ));
            }
            for (k = 0; k < this.boundingBox.getBlockCountZ() && (k += random.nextInt(this.boundingBox.getBlockCountZ())) + 3 <= this.boundingBox.getBlockCountZ(); k += 4) {
                MineshaftPart lv5 = MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.WEST, i);
                if (lv5 == null) continue;
                BlockBox lv6 = lv5.getBoundingBox();
                this.entrances.add(new BlockBox(this.boundingBox.minX, lv6.minY, lv6.minZ, this.boundingBox.minX + 1, lv6.maxY, lv6.maxZ));
            }
            for (k = 0; k < this.boundingBox.getBlockCountZ() && (k += random.nextInt(this.boundingBox.getBlockCountZ())) + 3 <= this.boundingBox.getBlockCountZ(); k += 4) {
                MineshaftPart lv7 = MineshaftGenerator.tryGenerateJigsaw(arg, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.EAST, i);
                if (lv7 == null) continue;
                BlockBox lv8 = lv7.getBoundingBox();
                this.entrances.add(new BlockBox(this.boundingBox.maxX - 1, lv8.minY, lv8.minZ, this.boundingBox.maxX, lv8.maxY, lv8.maxZ));
            }
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            if (this.method_14937(arg, arg4)) {
                return false;
            }
            this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.DIRT.getDefaultState(), AIR, true);
            this.fillWithOutline((WorldAccess)arg, arg4, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, AIR, AIR, false);
            for (BlockBox lv : this.entrances) {
                this.fillWithOutline((WorldAccess)arg, arg4, lv.minX, lv.maxY - 2, lv.minZ, lv.maxX, lv.maxY, lv.maxZ, AIR, AIR, false);
            }
            this.method_14919(arg, arg4, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, false);
            return true;
        }

        @Override
        public void translate(int i, int j, int k) {
            super.translate(i, j, k);
            for (BlockBox lv : this.entrances) {
                lv.offset(i, j, k);
            }
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            ListTag lv = new ListTag();
            for (BlockBox lv2 : this.entrances) {
                lv.add(lv2.toNbt());
            }
            arg.put("Entrances", lv);
        }
    }

    static abstract class MineshaftPart
    extends StructurePiece {
        protected MineshaftFeature.Type mineshaftType;

        public MineshaftPart(StructurePieceType arg, int i, MineshaftFeature.Type arg2) {
            super(arg, i);
            this.mineshaftType = arg2;
        }

        public MineshaftPart(StructurePieceType arg, CompoundTag arg2) {
            super(arg, arg2);
            this.mineshaftType = MineshaftFeature.Type.byIndex(arg2.getInt("MST"));
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            arg.putInt("MST", this.mineshaftType.ordinal());
        }

        protected BlockState getPlanksType() {
            switch (this.mineshaftType) {
                default: {
                    return Blocks.OAK_PLANKS.getDefaultState();
                }
                case MESA: 
            }
            return Blocks.DARK_OAK_PLANKS.getDefaultState();
        }

        protected BlockState getFenceType() {
            switch (this.mineshaftType) {
                default: {
                    return Blocks.OAK_FENCE.getDefaultState();
                }
                case MESA: 
            }
            return Blocks.DARK_OAK_FENCE.getDefaultState();
        }

        protected boolean method_14719(BlockView arg, BlockBox arg2, int i, int j, int k, int l) {
            for (int m = i; m <= j; ++m) {
                if (!this.getBlockAt(arg, m, k + 1, l, arg2).isAir()) continue;
                return false;
            }
            return true;
        }
    }
}

