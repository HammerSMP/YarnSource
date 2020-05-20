/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class VillageGenerator {
    public static void init() {
        PlainsVillageData.init();
        SnowyVillageData.init();
        SavannaVillageData.init();
        DesertVillageData.init();
        TaigaVillageData.init();
    }

    public static void addPieces(ChunkGenerator arg, StructureManager arg2, BlockPos arg3, List<StructurePiece> list, ChunkRandom arg4, StructurePoolFeatureConfig arg5) {
        VillageGenerator.init();
        StructurePoolBasedGenerator.addPieces(arg5.startPool, arg5.size, Piece::new, arg, arg2, arg3, list, arg4, true, true);
    }

    public static class Piece
    extends PoolStructurePiece {
        public Piece(StructureManager arg, StructurePoolElement arg2, BlockPos arg3, int i, BlockRotation arg4, BlockBox arg5) {
            super(StructurePieceType.VILLAGE, arg, arg2, arg3, i, arg4, arg5);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(arg, arg2, StructurePieceType.VILLAGE);
        }
    }
}

