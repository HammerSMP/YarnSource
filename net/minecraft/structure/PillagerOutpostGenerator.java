/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class PillagerOutpostGenerator {
    public static void addPieces(ChunkGenerator arg, StructureManager arg2, BlockPos arg3, List<StructurePiece> list, ChunkRandom arg4) {
        StructurePoolBasedGenerator.addPieces(new Identifier("pillager_outpost/base_plates"), 7, Piece::new, arg, arg2, arg3, list, arg4, true, true);
    }

    public static void init() {
    }

    static {
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("pillager_outpost/base_plates"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new LegacySinglePoolElement("pillager_outpost/base_plate"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("pillager_outpost/towers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new ListPoolElement((List<StructurePoolElement>)ImmutableList.of((Object)new LegacySinglePoolElement("pillager_outpost/watchtower"), (Object)new LegacySinglePoolElement("pillager_outpost/watchtower_overgrown", (List<StructureProcessor>)ImmutableList.of((Object)new BlockRotStructureProcessor(0.05f))))), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("pillager_outpost/feature_plates"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new LegacySinglePoolElement("pillager_outpost/feature_plate"), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("pillager_outpost/features"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new LegacySinglePoolElement("pillager_outpost/feature_cage1"), (Object)1), (Object)Pair.of((Object)new LegacySinglePoolElement("pillager_outpost/feature_cage2"), (Object)1), (Object)Pair.of((Object)new LegacySinglePoolElement("pillager_outpost/feature_logs"), (Object)1), (Object)Pair.of((Object)new LegacySinglePoolElement("pillager_outpost/feature_tent1"), (Object)1), (Object)Pair.of((Object)new LegacySinglePoolElement("pillager_outpost/feature_tent2"), (Object)1), (Object)Pair.of((Object)new LegacySinglePoolElement("pillager_outpost/feature_targets"), (Object)1), (Object)Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)6)), StructurePool.Projection.RIGID));
    }

    public static class Piece
    extends PoolStructurePiece {
        public Piece(StructureManager arg, StructurePoolElement arg2, BlockPos arg3, int i, BlockRotation arg4, BlockBox arg5) {
            super(StructurePieceType.PILLAGER_OUTPOST, arg, arg2, arg3, i, arg4, arg5);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(arg, arg2, StructurePieceType.PILLAGER_OUTPOST);
        }
    }
}

