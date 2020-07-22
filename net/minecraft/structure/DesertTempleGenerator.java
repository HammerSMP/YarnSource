/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceWithDimensions;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertTempleGenerator
extends StructurePieceWithDimensions {
    private final boolean[] hasPlacedChest = new boolean[4];

    public DesertTempleGenerator(Random random, int x, int z) {
        super(StructurePieceType.DESERT_TEMPLE, random, x, 64, z, 21, 15, 21);
    }

    public DesertTempleGenerator(StructureManager manager, CompoundTag tag) {
        super(StructurePieceType.DESERT_TEMPLE, tag);
        this.hasPlacedChest[0] = tag.getBoolean("hasPlacedChest0");
        this.hasPlacedChest[1] = tag.getBoolean("hasPlacedChest1");
        this.hasPlacedChest[2] = tag.getBoolean("hasPlacedChest2");
        this.hasPlacedChest[3] = tag.getBoolean("hasPlacedChest3");
    }

    @Override
    protected void toNbt(CompoundTag tag) {
        super.toNbt(tag);
        tag.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
        tag.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
        tag.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
        tag.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos arg5, BlockPos arg6) {
        this.fillWithOutline(arg, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        for (int i = 1; i <= 9; ++i) {
            this.fillWithOutline(arg, boundingBox, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithOutline(arg, boundingBox, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        }
        for (int j = 0; j < this.width; ++j) {
            for (int k = 0; k < this.depth; ++k) {
                int l = -5;
                this.method_14936(arg, Blocks.SANDSTONE.getDefaultState(), j, -5, k, boundingBox);
            }
        }
        BlockState lv = (BlockState)Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
        BlockState lv2 = (BlockState)Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
        BlockState lv3 = (BlockState)Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
        BlockState lv4 = (BlockState)Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
        this.fillWithOutline(arg, boundingBox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.addBlock(arg, lv, 2, 10, 0, boundingBox);
        this.addBlock(arg, lv2, 2, 10, 4, boundingBox);
        this.addBlock(arg, lv3, 0, 10, 2, boundingBox);
        this.addBlock(arg, lv4, 4, 10, 2, boundingBox);
        this.fillWithOutline(arg, boundingBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.addBlock(arg, lv, this.width - 3, 10, 0, boundingBox);
        this.addBlock(arg, lv2, this.width - 3, 10, 4, boundingBox);
        this.addBlock(arg, lv3, this.width - 5, 10, 2, boundingBox);
        this.addBlock(arg, lv4, this.width - 1, 10, 2, boundingBox);
        this.fillWithOutline(arg, boundingBox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 1, 1, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 2, 1, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 3, 1, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 10, 3, 1, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 3, 1, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 2, 1, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 1, 1, boundingBox);
        this.fillWithOutline(arg, boundingBox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 5, 5, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 5, 6, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 6, 6, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), this.width - 6, 5, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), this.width - 6, 6, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), this.width - 7, 6, 10, boundingBox);
        this.fillWithOutline(arg, boundingBox, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(arg, lv, 2, 4, 5, boundingBox);
        this.addBlock(arg, lv, 2, 3, 4, boundingBox);
        this.addBlock(arg, lv, this.width - 3, 4, 5, boundingBox);
        this.addBlock(arg, lv, this.width - 3, 3, 4, boundingBox);
        this.fillWithOutline(arg, boundingBox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.addBlock(arg, Blocks.SANDSTONE.getDefaultState(), 1, 1, 2, boundingBox);
        this.addBlock(arg, Blocks.SANDSTONE.getDefaultState(), this.width - 2, 1, 2, boundingBox);
        this.addBlock(arg, Blocks.SANDSTONE_SLAB.getDefaultState(), 1, 2, 2, boundingBox);
        this.addBlock(arg, Blocks.SANDSTONE_SLAB.getDefaultState(), this.width - 2, 2, 2, boundingBox);
        this.addBlock(arg, lv4, 2, 1, 2, boundingBox);
        this.addBlock(arg, lv3, this.width - 3, 1, 2, boundingBox);
        this.fillWithOutline(arg, boundingBox, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        for (int m = 5; m <= 17; m += 2) {
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 4, 1, m, boundingBox);
            this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), 4, 2, m, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), this.width - 5, 1, m, boundingBox);
            this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), this.width - 5, 2, m, boundingBox);
        }
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 7, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 8, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 9, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 9, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 8, 0, 10, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 12, 0, 10, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 7, 0, 10, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 13, 0, 10, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 11, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 11, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 12, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 13, boundingBox);
        this.addBlock(arg, Blocks.BLUE_TERRACOTTA.getDefaultState(), 10, 0, 10, boundingBox);
        for (int n = 0; n <= this.width - 1; n += this.width - 1) {
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 2, 1, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 2, 2, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 2, 3, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 3, 1, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 3, 2, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 3, 3, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 4, 1, boundingBox);
            this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), n, 4, 2, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 4, 3, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 5, 1, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 5, 2, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 5, 3, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 6, 1, boundingBox);
            this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), n, 6, 2, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 6, 3, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 7, 1, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 7, 2, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 7, 3, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 8, 1, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 8, 2, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), n, 8, 3, boundingBox);
        }
        for (int o = 2; o <= this.width - 3; o += this.width - 3 - 2) {
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o - 1, 2, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o, 2, 0, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o + 1, 2, 0, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o - 1, 3, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o, 3, 0, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o + 1, 3, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o - 1, 4, 0, boundingBox);
            this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), o, 4, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o + 1, 4, 0, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o - 1, 5, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o, 5, 0, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o + 1, 5, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o - 1, 6, 0, boundingBox);
            this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), o, 6, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o + 1, 6, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o - 1, 7, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o, 7, 0, boundingBox);
            this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o + 1, 7, 0, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o - 1, 8, 0, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o, 8, 0, boundingBox);
            this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), o + 1, 8, 0, boundingBox);
        }
        this.fillWithOutline(arg, boundingBox, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 8, 6, 0, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 12, 6, 0, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 5, 0, boundingBox);
        this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, 5, 0, boundingBox);
        this.addBlock(arg, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 5, 0, boundingBox);
        this.fillWithOutline(arg, boundingBox, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.getDefaultState(), Blocks.CHISELED_SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
        this.fillWithOutline(arg, boundingBox, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(arg, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, boundingBox);
        this.fillWithOutline(arg, boundingBox, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 8, -11, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 8, -10, 10, boundingBox);
        this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), 7, -10, 10, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 7, -11, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 12, -11, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 12, -10, 10, boundingBox);
        this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), 13, -10, 10, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 13, -11, 10, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 10, -11, 8, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 10, -10, 8, boundingBox);
        this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 7, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 7, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 10, -11, 12, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 10, -10, 12, boundingBox);
        this.addBlock(arg, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 13, boundingBox);
        this.addBlock(arg, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 13, boundingBox);
        for (Direction lv5 : Direction.Type.HORIZONTAL) {
            if (this.hasPlacedChest[lv5.getHorizontal()]) continue;
            int p = lv5.getOffsetX() * 2;
            int q = lv5.getOffsetZ() * 2;
            this.hasPlacedChest[lv5.getHorizontal()] = this.addChest(arg, boundingBox, random, 10 + p, -11, 10 + q, LootTables.DESERT_PYRAMID_CHEST);
        }
        return true;
    }
}

