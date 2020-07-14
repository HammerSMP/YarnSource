/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.class_5425;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IglooGenerator {
    private static final Identifier TOP_TEMPLATE = new Identifier("igloo/top");
    private static final Identifier MIDDLE_TEMPLATE = new Identifier("igloo/middle");
    private static final Identifier BOTTOM_TEMPLATE = new Identifier("igloo/bottom");
    private static final Map<Identifier, BlockPos> field_14408 = ImmutableMap.of((Object)TOP_TEMPLATE, (Object)new BlockPos(3, 5, 5), (Object)MIDDLE_TEMPLATE, (Object)new BlockPos(1, 3, 1), (Object)BOTTOM_TEMPLATE, (Object)new BlockPos(3, 6, 7));
    private static final Map<Identifier, BlockPos> field_14406 = ImmutableMap.of((Object)TOP_TEMPLATE, (Object)BlockPos.ORIGIN, (Object)MIDDLE_TEMPLATE, (Object)new BlockPos(2, -3, 4), (Object)BOTTOM_TEMPLATE, (Object)new BlockPos(0, -3, -2));

    public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random) {
        if (random.nextDouble() < 0.5) {
            int i = random.nextInt(8) + 4;
            pieces.add(new Piece(manager, BOTTOM_TEMPLATE, pos, rotation, i * 3));
            for (int j = 0; j < i - 1; ++j) {
                pieces.add(new Piece(manager, MIDDLE_TEMPLATE, pos, rotation, j * 3));
            }
        }
        pieces.add(new Piece(manager, TOP_TEMPLATE, pos, rotation, 0));
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final Identifier template;
        private final BlockRotation rotation;

        public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, int yOffset) {
            super(StructurePieceType.IGLOO, 0);
            this.template = identifier;
            BlockPos lv = (BlockPos)field_14406.get(identifier);
            this.pos = pos.add(lv.getX(), lv.getY() - yOffset, lv.getZ());
            this.rotation = rotation;
            this.initializeStructureData(manager);
        }

        public Piece(StructureManager manager, CompoundTag tag) {
            super(StructurePieceType.IGLOO, tag);
            this.template = new Identifier(tag.getString("Template"));
            this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.initializeStructureData(manager);
        }

        private void initializeStructureData(StructureManager manager) {
            Structure lv = manager.getStructureOrBlank(this.template);
            StructurePlacementData lv2 = new StructurePlacementData().setRotation(this.rotation).setMirror(BlockMirror.NONE).setPosition((BlockPos)field_14408.get(this.template)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template.toString());
            tag.putString("Rot", this.rotation.name());
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, class_5425 arg2, Random random, BlockBox boundingBox) {
            if (!"chest".equals(metadata)) {
                return;
            }
            arg2.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            BlockEntity lv = arg2.getBlockEntity(pos.down());
            if (lv instanceof ChestBlockEntity) {
                ((ChestBlockEntity)lv).setLootTable(LootTables.IGLOO_CHEST_CHEST, random.nextLong());
            }
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos arg5, BlockPos arg6) {
            BlockPos lv5;
            BlockState lv6;
            StructurePlacementData lv = new StructurePlacementData().setRotation(this.rotation).setMirror(BlockMirror.NONE).setPosition((BlockPos)field_14408.get(this.template)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            BlockPos lv2 = (BlockPos)field_14406.get(this.template);
            BlockPos lv3 = this.pos.add(Structure.transform(lv, new BlockPos(3 - lv2.getX(), 0, 0 - lv2.getZ())));
            int i = arg.getTopY(Heightmap.Type.WORLD_SURFACE_WG, lv3.getX(), lv3.getZ());
            BlockPos lv4 = this.pos;
            this.pos = this.pos.add(0, i - 90 - 1, 0);
            boolean bl = super.generate(arg, structureAccessor, chunkGenerator, random, boundingBox, arg5, arg6);
            if (this.template.equals(TOP_TEMPLATE) && !(lv6 = arg.getBlockState((lv5 = this.pos.add(Structure.transform(lv, new BlockPos(3, 0, 5)))).down())).isAir() && !lv6.isOf(Blocks.LADDER)) {
                arg.setBlockState(lv5, Blocks.SNOW_BLOCK.getDefaultState(), 3);
            }
            this.pos = lv4;
            return bl;
        }
    }
}

