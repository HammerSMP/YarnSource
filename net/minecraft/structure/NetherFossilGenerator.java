/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.class_5425;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFossilGenerator {
    private static final Identifier[] FOSSILS = new Identifier[]{new Identifier("nether_fossils/fossil_1"), new Identifier("nether_fossils/fossil_2"), new Identifier("nether_fossils/fossil_3"), new Identifier("nether_fossils/fossil_4"), new Identifier("nether_fossils/fossil_5"), new Identifier("nether_fossils/fossil_6"), new Identifier("nether_fossils/fossil_7"), new Identifier("nether_fossils/fossil_8"), new Identifier("nether_fossils/fossil_9"), new Identifier("nether_fossils/fossil_10"), new Identifier("nether_fossils/fossil_11"), new Identifier("nether_fossils/fossil_12"), new Identifier("nether_fossils/fossil_13"), new Identifier("nether_fossils/fossil_14")};

    public static void addPieces(StructureManager manager, List<StructurePiece> pieces, Random random, BlockPos pos) {
        BlockRotation lv = BlockRotation.random(random);
        pieces.add(new Piece(manager, Util.getRandom(FOSSILS, random), pos, lv));
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final Identifier template;
        private final BlockRotation structureRotation;

        public Piece(StructureManager manager, Identifier template, BlockPos pos, BlockRotation rotation) {
            super(StructurePieceType.NETHER_FOSSIL, 0);
            this.template = template;
            this.pos = pos;
            this.structureRotation = rotation;
            this.initializeStructureData(manager);
        }

        public Piece(StructureManager manager, CompoundTag tag) {
            super(StructurePieceType.NETHER_FOSSIL, tag);
            this.template = new Identifier(tag.getString("Template"));
            this.structureRotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.initializeStructureData(manager);
        }

        private void initializeStructureData(StructureManager manager) {
            Structure lv = manager.getStructureOrBlank(this.template);
            StructurePlacementData lv2 = new StructurePlacementData().setRotation(this.structureRotation).setMirror(BlockMirror.NONE).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template.toString());
            tag.putString("Rot", this.structureRotation.name());
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, class_5425 arg2, Random random, BlockBox boundingBox) {
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos arg5, BlockPos arg6) {
            boundingBox.encompass(this.structure.calculateBoundingBox(this.placementData, this.pos));
            return super.generate(arg, structureAccessor, chunkGenerator, random, boundingBox, arg5, arg6);
        }
    }
}

