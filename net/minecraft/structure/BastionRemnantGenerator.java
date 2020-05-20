/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.BastionBridgeData;
import net.minecraft.structure.BastionData;
import net.minecraft.structure.BastionTreasureData;
import net.minecraft.structure.BastionUnitsData;
import net.minecraft.structure.HoglinStableData;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class BastionRemnantGenerator {
    public static final ImmutableMap<String, Integer> START_POOLS_TO_SIZES = ImmutableMap.builder().put((Object)"bastion/units/base", (Object)60).put((Object)"bastion/hoglin_stable/origin", (Object)60).put((Object)"bastion/treasure/starters", (Object)60).put((Object)"bastion/bridge/start", (Object)60).build();

    public static void init() {
        BastionUnitsData.init();
        HoglinStableData.init();
        BastionTreasureData.init();
        BastionBridgeData.init();
        BastionData.init();
    }

    public static void addPieces(ChunkGenerator arg, StructureManager arg2, BlockPos arg3, List<StructurePiece> list, ChunkRandom arg4, BastionRemnantFeatureConfig arg5) {
        BastionRemnantGenerator.init();
        StructurePoolFeatureConfig lv = arg5.getRandom(arg4);
        StructurePoolBasedGenerator.addPieces(lv.startPool, lv.size, Piece::new, arg, arg2, arg3, list, arg4, false, false);
    }

    public static class Piece
    extends PoolStructurePiece {
        public Piece(StructureManager arg, StructurePoolElement arg2, BlockPos arg3, int i, BlockRotation arg4, BlockBox arg5) {
            super(StructurePieceType.BASTION_REMNANT, arg, arg2, arg3, i, arg4, arg5);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(arg, arg2, StructurePieceType.BASTION_REMNANT);
        }
    }
}

