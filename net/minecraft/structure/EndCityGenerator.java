/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class EndCityGenerator {
    private static final StructurePlacementData PLACEMENT_DATA = new StructurePlacementData().setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
    private static final StructurePlacementData IGNORE_AIR_PLACEMENT_DATA = new StructurePlacementData().setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
    private static final Part BUILDING = new Part(){

        @Override
        public void init() {
        }

        @Override
        public boolean create(StructureManager arg, int i, Piece arg2, BlockPos arg3, List<StructurePiece> list, Random random) {
            if (i > 8) {
                return false;
            }
            BlockRotation lv = arg2.placementData.getRotation();
            Piece lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, arg2, arg3, "base_floor", lv, true));
            int j = random.nextInt(3);
            if (j == 0) {
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-1, 4, -1), "base_roof", lv, true));
            } else if (j == 1) {
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-1, 0, -1), "second_floor_2", lv, false));
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-1, 8, -1), "second_roof", lv, false));
                EndCityGenerator.createPart(arg, SMALL_TOWER, i + 1, lv2, null, list, random);
            } else if (j == 2) {
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-1, 0, -1), "second_floor_2", lv, false));
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-1, 4, -1), "third_floor_2", lv, false));
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-1, 8, -1), "third_roof", lv, true));
                EndCityGenerator.createPart(arg, SMALL_TOWER, i + 1, lv2, null, list, random);
            }
            return true;
        }
    };
    private static final List<Pair<BlockRotation, BlockPos>> SMALL_TOWER_BRIDGE_ATTACHMENTS = Lists.newArrayList((Object[])new Pair[]{new Pair<BlockRotation, BlockPos>(BlockRotation.NONE, new BlockPos(1, -1, 0)), new Pair<BlockRotation, BlockPos>(BlockRotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Pair<BlockRotation, BlockPos>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Pair<BlockRotation, BlockPos>(BlockRotation.CLOCKWISE_180, new BlockPos(5, -1, 6))});
    private static final Part SMALL_TOWER = new Part(){

        @Override
        public void init() {
        }

        @Override
        public boolean create(StructureManager arg, int i, Piece arg2, BlockPos arg3, List<StructurePiece> list, Random random) {
            BlockRotation lv = arg2.placementData.getRotation();
            Piece lv2 = arg2;
            lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", lv, true));
            lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(0, 7, 0), "tower_piece", lv, true));
            Piece lv3 = random.nextInt(3) == 0 ? lv2 : null;
            int j = 1 + random.nextInt(3);
            for (int k = 0; k < j; ++k) {
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(0, 4, 0), "tower_piece", lv, true));
                if (k >= j - 1 || !random.nextBoolean()) continue;
                lv3 = lv2;
            }
            if (lv3 != null) {
                for (Pair lv4 : SMALL_TOWER_BRIDGE_ATTACHMENTS) {
                    if (!random.nextBoolean()) continue;
                    Piece lv5 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv3, (BlockPos)lv4.getRight(), "bridge_end", lv.rotate((BlockRotation)((Object)lv4.getLeft())), true));
                    EndCityGenerator.createPart(arg, BRIDGE_PIECE, i + 1, lv5, null, list, random);
                }
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-1, 4, -1), "tower_top", lv, true));
            } else if (i == 7) {
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-1, 4, -1), "tower_top", lv, true));
            } else {
                return EndCityGenerator.createPart(arg, FAT_TOWER, i + 1, lv2, null, list, random);
            }
            return true;
        }
    };
    private static final Part BRIDGE_PIECE = new Part(){
        public boolean shipGenerated;

        @Override
        public void init() {
            this.shipGenerated = false;
        }

        @Override
        public boolean create(StructureManager arg, int i, Piece arg2, BlockPos arg3, List<StructurePiece> list, Random random) {
            BlockRotation lv = arg2.placementData.getRotation();
            int j = random.nextInt(4) + 1;
            Piece lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, arg2, new BlockPos(0, 0, -4), "bridge_piece", lv, true));
            lv2.length = -1;
            int k = 0;
            for (int l = 0; l < j; ++l) {
                if (random.nextBoolean()) {
                    lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(0, k, -4), "bridge_piece", lv, true));
                    k = 0;
                    continue;
                }
                lv2 = random.nextBoolean() ? EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(0, k, -4), "bridge_steep_stairs", lv, true)) : EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(0, k, -8), "bridge_gentle_stairs", lv, true));
                k = 4;
            }
            if (this.shipGenerated || random.nextInt(10 - i) != 0) {
                if (!EndCityGenerator.createPart(arg, BUILDING, i + 1, lv2, new BlockPos(-3, k + 1, -11), list, random)) {
                    return false;
                }
            } else {
                EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-8 + random.nextInt(8), k, -70 + random.nextInt(10)), "ship", lv, true));
                this.shipGenerated = true;
            }
            lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(4, k, 0), "bridge_end", lv.rotate(BlockRotation.CLOCKWISE_180), true));
            lv2.length = -1;
            return true;
        }
    };
    private static final List<Pair<BlockRotation, BlockPos>> FAT_TOWER_BRIDGE_ATTACHMENTS = Lists.newArrayList((Object[])new Pair[]{new Pair<BlockRotation, BlockPos>(BlockRotation.NONE, new BlockPos(4, -1, 0)), new Pair<BlockRotation, BlockPos>(BlockRotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Pair<BlockRotation, BlockPos>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Pair<BlockRotation, BlockPos>(BlockRotation.CLOCKWISE_180, new BlockPos(8, -1, 12))});
    private static final Part FAT_TOWER = new Part(){

        @Override
        public void init() {
        }

        @Override
        public boolean create(StructureManager arg, int i, Piece arg2, BlockPos arg3, List<StructurePiece> list, Random random) {
            BlockRotation lv = arg2.placementData.getRotation();
            Piece lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, arg2, new BlockPos(-3, 4, -3), "fat_tower_base", lv, true));
            lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(0, 4, 0), "fat_tower_middle", lv, true));
            for (int j = 0; j < 2 && random.nextInt(3) != 0; ++j) {
                lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(0, 8, 0), "fat_tower_middle", lv, true));
                for (Pair lv3 : FAT_TOWER_BRIDGE_ATTACHMENTS) {
                    if (!random.nextBoolean()) continue;
                    Piece lv4 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, (BlockPos)lv3.getRight(), "bridge_end", lv.rotate((BlockRotation)((Object)lv3.getLeft())), true));
                    EndCityGenerator.createPart(arg, BRIDGE_PIECE, i + 1, lv4, null, list, random);
                }
            }
            lv2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv2, new BlockPos(-2, 8, -2), "fat_tower_top", lv, true));
            return true;
        }
    };

    private static Piece createPiece(StructureManager arg, Piece arg2, BlockPos arg3, String string, BlockRotation arg4, boolean bl) {
        Piece lv = new Piece(arg, string, arg2.pos, arg4, bl);
        BlockPos lv2 = arg2.structure.transformBox(arg2.placementData, arg3, lv.placementData, BlockPos.ORIGIN);
        lv.translate(lv2.getX(), lv2.getY(), lv2.getZ());
        return lv;
    }

    public static void addPieces(StructureManager arg, BlockPos arg2, BlockRotation arg3, List<StructurePiece> list, Random random) {
        FAT_TOWER.init();
        BUILDING.init();
        BRIDGE_PIECE.init();
        SMALL_TOWER.init();
        Piece lv = EndCityGenerator.addPiece(list, new Piece(arg, "base_floor", arg2, arg3, true));
        lv = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv, new BlockPos(-1, 0, -1), "second_floor_1", arg3, false));
        lv = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv, new BlockPos(-1, 4, -1), "third_floor_1", arg3, false));
        lv = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(arg, lv, new BlockPos(-1, 8, -1), "third_roof", arg3, true));
        EndCityGenerator.createPart(arg, SMALL_TOWER, 1, lv, null, list, random);
    }

    private static Piece addPiece(List<StructurePiece> list, Piece arg) {
        list.add(arg);
        return arg;
    }

    private static boolean createPart(StructureManager arg, Part arg2, int i, Piece arg3, BlockPos arg4, List<StructurePiece> list, Random random) {
        if (i > 8) {
            return false;
        }
        ArrayList list2 = Lists.newArrayList();
        if (arg2.create(arg, i, arg3, arg4, list2, random)) {
            boolean bl = false;
            int j = random.nextInt();
            for (StructurePiece lv : list2) {
                lv.length = j;
                StructurePiece lv2 = StructurePiece.getOverlappingPiece(list, lv.getBoundingBox());
                if (lv2 == null || lv2.length == arg3.length) continue;
                bl = true;
                break;
            }
            if (!bl) {
                list.addAll(list2);
                return true;
            }
        }
        return false;
    }

    static interface Part {
        public void init();

        public boolean create(StructureManager var1, int var2, Piece var3, BlockPos var4, List<StructurePiece> var5, Random var6);
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final String template;
        private final BlockRotation rotation;
        private final boolean ignoreAir;

        public Piece(StructureManager arg, String string, BlockPos arg2, BlockRotation arg3, boolean bl) {
            super(StructurePieceType.END_CITY, 0);
            this.template = string;
            this.pos = arg2;
            this.rotation = arg3;
            this.ignoreAir = bl;
            this.initializeStructureData(arg);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.END_CITY, arg2);
            this.template = arg2.getString("Template");
            this.rotation = BlockRotation.valueOf(arg2.getString("Rot"));
            this.ignoreAir = arg2.getBoolean("OW");
            this.initializeStructureData(arg);
        }

        private void initializeStructureData(StructureManager arg) {
            Structure lv = arg.getStructureOrBlank(new Identifier("end_city/" + this.template));
            StructurePlacementData lv2 = (this.ignoreAir ? PLACEMENT_DATA : IGNORE_AIR_PLACEMENT_DATA).copy().setRotation(this.rotation);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putString("Template", this.template);
            arg.putString("Rot", this.rotation.name());
            arg.putBoolean("OW", this.ignoreAir);
        }

        @Override
        protected void handleMetadata(String string, BlockPos arg, IWorld arg2, Random random, BlockBox arg3) {
            if (string.startsWith("Chest")) {
                BlockPos lv = arg.down();
                if (arg3.contains(lv)) {
                    LootableContainerBlockEntity.setLootTable(arg2, random, lv, LootTables.END_CITY_TREASURE_CHEST);
                }
            } else if (string.startsWith("Sentry")) {
                ShulkerEntity lv2 = EntityType.SHULKER.create(arg2.getWorld());
                lv2.updatePosition((double)arg.getX() + 0.5, (double)arg.getY() + 0.5, (double)arg.getZ() + 0.5);
                lv2.setAttachedBlock(arg);
                arg2.spawnEntity(lv2);
            } else if (string.startsWith("Elytra")) {
                ItemFrameEntity lv3 = new ItemFrameEntity(arg2.getWorld(), arg, this.rotation.rotate(Direction.SOUTH));
                lv3.setHeldItemStack(new ItemStack(Items.ELYTRA), false);
                arg2.spawnEntity(lv3);
            }
        }
    }
}

