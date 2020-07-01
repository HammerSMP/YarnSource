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

    public static void addPieces(StructureManager arg, List<StructurePiece> list, Random random, BlockPos arg2) {
        BlockRotation lv = BlockRotation.random(random);
        list.add(new Piece(arg, Util.getRandom(FOSSILS, random), arg2, lv));
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final Identifier template;
        private final BlockRotation structureRotation;

        public Piece(StructureManager arg, Identifier arg2, BlockPos arg3, BlockRotation arg4) {
            super(StructurePieceType.NETHER_FOSSIL, 0);
            this.template = arg2;
            this.pos = arg3;
            this.structureRotation = arg4;
            this.initializeStructureData(arg);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.NETHER_FOSSIL, arg2);
            this.template = new Identifier(arg2.getString("Template"));
            this.structureRotation = BlockRotation.valueOf(arg2.getString("Rot"));
            this.initializeStructureData(arg);
        }

        private void initializeStructureData(StructureManager arg) {
            Structure lv = arg.getStructureOrBlank(this.template);
            StructurePlacementData lv2 = new StructurePlacementData().setRotation(this.structureRotation).setMirror(BlockMirror.NONE).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putString("Template", this.template.toString());
            arg.putString("Rot", this.structureRotation.name());
        }

        @Override
        protected void handleMetadata(String string, BlockPos arg, class_5425 arg2, Random random, BlockBox arg3) {
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            arg4.encompass(this.structure.calculateBoundingBox(this.placementData, this.pos));
            return super.generate(arg, arg2, arg3, random, arg4, arg5, arg6);
        }
    }
}

