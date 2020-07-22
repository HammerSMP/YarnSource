/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureGenerator {

    public static class Piece
    extends StructurePiece {
        public Piece(BlockPos pos) {
            super(StructurePieceType.BURIED_TREASURE, 0);
            this.boundingBox = new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }

        public Piece(StructureManager manager, CompoundTag tag) {
            super(StructurePieceType.BURIED_TREASURE, tag);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos arg5, BlockPos arg6) {
            int i = arg.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.minX, this.boundingBox.minZ);
            BlockPos.Mutable lv = new BlockPos.Mutable(this.boundingBox.minX, i, this.boundingBox.minZ);
            while (lv.getY() > 0) {
                BlockState lv2 = arg.getBlockState(lv);
                BlockState lv3 = arg.getBlockState((BlockPos)lv.down());
                if (lv3 == Blocks.SANDSTONE.getDefaultState() || lv3 == Blocks.STONE.getDefaultState() || lv3 == Blocks.ANDESITE.getDefaultState() || lv3 == Blocks.GRANITE.getDefaultState() || lv3 == Blocks.DIORITE.getDefaultState()) {
                    BlockState lv4 = lv2.isAir() || this.isLiquid(lv2) ? Blocks.SAND.getDefaultState() : lv2;
                    for (Direction lv5 : Direction.values()) {
                        BlockPos lv6 = lv.offset(lv5);
                        BlockState lv7 = arg.getBlockState(lv6);
                        if (!lv7.isAir() && !this.isLiquid(lv7)) continue;
                        BlockPos lv8 = lv6.down();
                        BlockState lv9 = arg.getBlockState(lv8);
                        if ((lv9.isAir() || this.isLiquid(lv9)) && lv5 != Direction.UP) {
                            arg.setBlockState(lv6, lv3, 3);
                            continue;
                        }
                        arg.setBlockState(lv6, lv4, 3);
                    }
                    this.boundingBox = new BlockBox(lv.getX(), lv.getY(), lv.getZ(), lv.getX(), lv.getY(), lv.getZ());
                    return this.addChest(arg, boundingBox, random, lv, LootTables.BURIED_TREASURE_CHEST, null);
                }
                lv.move(0, -1, 0);
            }
            return false;
        }

        private boolean isLiquid(BlockState state) {
            return state == Blocks.WATER.getDefaultState() || state == Blocks.LAVA.getDefaultState();
        }
    }
}

