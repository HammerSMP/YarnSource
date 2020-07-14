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
import net.minecraft.class_5425;
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

public class EndCityGenerator {
    private static final StructurePlacementData PLACEMENT_DATA = new StructurePlacementData().setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
    private static final StructurePlacementData IGNORE_AIR_PLACEMENT_DATA = new StructurePlacementData().setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
    private static final Part BUILDING = new Part(){

        @Override
        public void init() {
        }

        @Override
        public boolean create(StructureManager manager, int depth, Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            if (depth > 8) {
                return false;
            }
            BlockRotation lv = root.placementData.getRotation();
            Piece lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, pos, "base_floor", lv, true));
            int j = random.nextInt(3);
            if (j == 0) {
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-1, 4, -1), "base_roof", lv, true));
            } else if (j == 1) {
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-1, 0, -1), "second_floor_2", lv, false));
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-1, 8, -1), "second_roof", lv, false));
                EndCityGenerator.createPart(manager, SMALL_TOWER, depth + 1, lv2, null, pieces, random);
            } else if (j == 2) {
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-1, 0, -1), "second_floor_2", lv, false));
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-1, 4, -1), "third_floor_2", lv, false));
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-1, 8, -1), "third_roof", lv, true));
                EndCityGenerator.createPart(manager, SMALL_TOWER, depth + 1, lv2, null, pieces, random);
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
        public boolean create(StructureManager manager, int depth, Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            BlockRotation lv = root.placementData.getRotation();
            Piece lv2 = root;
            lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", lv, true));
            lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(0, 7, 0), "tower_piece", lv, true));
            Piece lv3 = random.nextInt(3) == 0 ? lv2 : null;
            int j = 1 + random.nextInt(3);
            for (int k = 0; k < j; ++k) {
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(0, 4, 0), "tower_piece", lv, true));
                if (k >= j - 1 || !random.nextBoolean()) continue;
                lv3 = lv2;
            }
            if (lv3 != null) {
                for (Pair lv4 : SMALL_TOWER_BRIDGE_ATTACHMENTS) {
                    if (!random.nextBoolean()) continue;
                    Piece lv5 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv3, (BlockPos)lv4.getRight(), "bridge_end", lv.rotate((BlockRotation)((Object)lv4.getLeft())), true));
                    EndCityGenerator.createPart(manager, BRIDGE_PIECE, depth + 1, lv5, null, pieces, random);
                }
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-1, 4, -1), "tower_top", lv, true));
            } else if (depth == 7) {
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-1, 4, -1), "tower_top", lv, true));
            } else {
                return EndCityGenerator.createPart(manager, FAT_TOWER, depth + 1, lv2, null, pieces, random);
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
        public boolean create(StructureManager manager, int depth, Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            BlockRotation lv = root.placementData.getRotation();
            int j = random.nextInt(4) + 1;
            Piece lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(0, 0, -4), "bridge_piece", lv, true));
            lv2.length = -1;
            int k = 0;
            for (int l = 0; l < j; ++l) {
                if (random.nextBoolean()) {
                    lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(0, k, -4), "bridge_piece", lv, true));
                    k = 0;
                    continue;
                }
                lv2 = random.nextBoolean() ? EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(0, k, -4), "bridge_steep_stairs", lv, true)) : EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(0, k, -8), "bridge_gentle_stairs", lv, true));
                k = 4;
            }
            if (this.shipGenerated || random.nextInt(10 - depth) != 0) {
                if (!EndCityGenerator.createPart(manager, BUILDING, depth + 1, lv2, new BlockPos(-3, k + 1, -11), pieces, random)) {
                    return false;
                }
            } else {
                EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-8 + random.nextInt(8), k, -70 + random.nextInt(10)), "ship", lv, true));
                this.shipGenerated = true;
            }
            lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(4, k, 0), "bridge_end", lv.rotate(BlockRotation.CLOCKWISE_180), true));
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
        public boolean create(StructureManager manager, int depth, Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            BlockRotation lv = root.placementData.getRotation();
            Piece lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(-3, 4, -3), "fat_tower_base", lv, true));
            lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(0, 4, 0), "fat_tower_middle", lv, true));
            for (int j = 0; j < 2 && random.nextInt(3) != 0; ++j) {
                lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(0, 8, 0), "fat_tower_middle", lv, true));
                for (Pair lv3 : FAT_TOWER_BRIDGE_ATTACHMENTS) {
                    if (!random.nextBoolean()) continue;
                    Piece lv4 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, (BlockPos)lv3.getRight(), "bridge_end", lv.rotate((BlockRotation)((Object)lv3.getLeft())), true));
                    EndCityGenerator.createPart(manager, BRIDGE_PIECE, depth + 1, lv4, null, pieces, random);
                }
            }
            lv2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, lv2, new BlockPos(-2, 8, -2), "fat_tower_top", lv, true));
            return true;
        }
    };

    private static Piece createPiece(StructureManager structureManager, Piece lastPiece, BlockPos relativePosition, String template, BlockRotation rotation, boolean ignoreAir) {
        Piece lv = new Piece(structureManager, template, lastPiece.pos, rotation, ignoreAir);
        BlockPos lv2 = lastPiece.structure.transformBox(lastPiece.placementData, relativePosition, lv.placementData, BlockPos.ORIGIN);
        lv.translate(lv2.getX(), lv2.getY(), lv2.getZ());
        return lv;
    }

    public static void addPieces(StructureManager structureManager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random) {
        FAT_TOWER.init();
        BUILDING.init();
        BRIDGE_PIECE.init();
        SMALL_TOWER.init();
        Piece lv = EndCityGenerator.addPiece(pieces, new Piece(structureManager, "base_floor", pos, rotation, true));
        lv = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(structureManager, lv, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
        lv = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(structureManager, lv, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
        lv = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(structureManager, lv, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
        EndCityGenerator.createPart(structureManager, SMALL_TOWER, 1, lv, null, pieces, random);
    }

    private static Piece addPiece(List<StructurePiece> pieces, Piece piece) {
        pieces.add(piece);
        return piece;
    }

    private static boolean createPart(StructureManager manager, Part piece, int depth, Piece parent, BlockPos pos, List<StructurePiece> pieces, Random random) {
        if (depth > 8) {
            return false;
        }
        ArrayList list2 = Lists.newArrayList();
        if (piece.create(manager, depth, parent, pos, list2, random)) {
            boolean bl = false;
            int j = random.nextInt();
            for (StructurePiece lv : list2) {
                lv.length = j;
                StructurePiece lv2 = StructurePiece.getOverlappingPiece(pieces, lv.getBoundingBox());
                if (lv2 == null || lv2.length == parent.length) continue;
                bl = true;
                break;
            }
            if (!bl) {
                pieces.addAll(list2);
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

        public Piece(StructureManager manager, String template, BlockPos pos, BlockRotation rotation, boolean ignoreAir) {
            super(StructurePieceType.END_CITY, 0);
            this.template = template;
            this.pos = pos;
            this.rotation = rotation;
            this.ignoreAir = ignoreAir;
            this.initializeStructureData(manager);
        }

        public Piece(StructureManager manager, CompoundTag tag) {
            super(StructurePieceType.END_CITY, tag);
            this.template = tag.getString("Template");
            this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.ignoreAir = tag.getBoolean("OW");
            this.initializeStructureData(manager);
        }

        private void initializeStructureData(StructureManager manager) {
            Structure lv = manager.getStructureOrBlank(new Identifier("end_city/" + this.template));
            StructurePlacementData lv2 = (this.ignoreAir ? PLACEMENT_DATA : IGNORE_AIR_PLACEMENT_DATA).copy().setRotation(this.rotation);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template);
            tag.putString("Rot", this.rotation.name());
            tag.putBoolean("OW", this.ignoreAir);
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, class_5425 arg2, Random random, BlockBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                BlockPos lv = pos.down();
                if (boundingBox.contains(lv)) {
                    LootableContainerBlockEntity.setLootTable(arg2, random, lv, LootTables.END_CITY_TREASURE_CHEST);
                }
            } else if (metadata.startsWith("Sentry")) {
                ShulkerEntity lv2 = EntityType.SHULKER.create(arg2.getWorld());
                lv2.updatePosition((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
                lv2.setAttachedBlock(pos);
                arg2.spawnEntity(lv2);
            } else if (metadata.startsWith("Elytra")) {
                ItemFrameEntity lv3 = new ItemFrameEntity(arg2.getWorld(), pos, this.rotation.rotate(Direction.SOUTH));
                lv3.setHeldItemStack(new ItemStack(Items.ELYTRA), false);
                arg2.spawnEntity(lv3);
            }
        }
    }
}

