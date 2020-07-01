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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.class_5425;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;

public class OceanRuinGenerator {
    private static final Identifier[] WARM_RUINS = new Identifier[]{new Identifier("underwater_ruin/warm_1"), new Identifier("underwater_ruin/warm_2"), new Identifier("underwater_ruin/warm_3"), new Identifier("underwater_ruin/warm_4"), new Identifier("underwater_ruin/warm_5"), new Identifier("underwater_ruin/warm_6"), new Identifier("underwater_ruin/warm_7"), new Identifier("underwater_ruin/warm_8")};
    private static final Identifier[] BRICK_RUINS = new Identifier[]{new Identifier("underwater_ruin/brick_1"), new Identifier("underwater_ruin/brick_2"), new Identifier("underwater_ruin/brick_3"), new Identifier("underwater_ruin/brick_4"), new Identifier("underwater_ruin/brick_5"), new Identifier("underwater_ruin/brick_6"), new Identifier("underwater_ruin/brick_7"), new Identifier("underwater_ruin/brick_8")};
    private static final Identifier[] CRACKED_RUINS = new Identifier[]{new Identifier("underwater_ruin/cracked_1"), new Identifier("underwater_ruin/cracked_2"), new Identifier("underwater_ruin/cracked_3"), new Identifier("underwater_ruin/cracked_4"), new Identifier("underwater_ruin/cracked_5"), new Identifier("underwater_ruin/cracked_6"), new Identifier("underwater_ruin/cracked_7"), new Identifier("underwater_ruin/cracked_8")};
    private static final Identifier[] MOSSY_RUINS = new Identifier[]{new Identifier("underwater_ruin/mossy_1"), new Identifier("underwater_ruin/mossy_2"), new Identifier("underwater_ruin/mossy_3"), new Identifier("underwater_ruin/mossy_4"), new Identifier("underwater_ruin/mossy_5"), new Identifier("underwater_ruin/mossy_6"), new Identifier("underwater_ruin/mossy_7"), new Identifier("underwater_ruin/mossy_8")};
    private static final Identifier[] BIG_BRICK_RUINS = new Identifier[]{new Identifier("underwater_ruin/big_brick_1"), new Identifier("underwater_ruin/big_brick_2"), new Identifier("underwater_ruin/big_brick_3"), new Identifier("underwater_ruin/big_brick_8")};
    private static final Identifier[] BIG_MOSSY_RUINS = new Identifier[]{new Identifier("underwater_ruin/big_mossy_1"), new Identifier("underwater_ruin/big_mossy_2"), new Identifier("underwater_ruin/big_mossy_3"), new Identifier("underwater_ruin/big_mossy_8")};
    private static final Identifier[] BIG_CRACKED_RUINS = new Identifier[]{new Identifier("underwater_ruin/big_cracked_1"), new Identifier("underwater_ruin/big_cracked_2"), new Identifier("underwater_ruin/big_cracked_3"), new Identifier("underwater_ruin/big_cracked_8")};
    private static final Identifier[] BIG_WARM_RUINS = new Identifier[]{new Identifier("underwater_ruin/big_warm_4"), new Identifier("underwater_ruin/big_warm_5"), new Identifier("underwater_ruin/big_warm_6"), new Identifier("underwater_ruin/big_warm_7")};

    private static Identifier getRandomWarmRuin(Random random) {
        return Util.getRandom(WARM_RUINS, random);
    }

    private static Identifier getRandomBigWarmRuin(Random random) {
        return Util.getRandom(BIG_WARM_RUINS, random);
    }

    public static void addPieces(StructureManager arg, BlockPos arg2, BlockRotation arg3, List<StructurePiece> list, Random random, OceanRuinFeatureConfig arg4) {
        boolean bl = random.nextFloat() <= arg4.largeProbability;
        float f = bl ? 0.9f : 0.8f;
        OceanRuinGenerator.method_14822(arg, arg2, arg3, list, random, arg4, bl, f);
        if (bl && random.nextFloat() <= arg4.clusterProbability) {
            OceanRuinGenerator.method_14825(arg, random, arg3, arg2, arg4, list);
        }
    }

    private static void method_14825(StructureManager arg, Random random, BlockRotation arg2, BlockPos arg3, OceanRuinFeatureConfig arg4, List<StructurePiece> list) {
        int i = arg3.getX();
        int j = arg3.getZ();
        BlockPos lv = Structure.transformAround(new BlockPos(15, 0, 15), BlockMirror.NONE, arg2, BlockPos.ORIGIN).add(i, 0, j);
        BlockBox lv2 = BlockBox.create(i, 0, j, lv.getX(), 0, lv.getZ());
        BlockPos lv3 = new BlockPos(Math.min(i, lv.getX()), 0, Math.min(j, lv.getZ()));
        List<BlockPos> list2 = OceanRuinGenerator.getRoomPositions(random, lv3.getX(), lv3.getZ());
        int k = MathHelper.nextInt(random, 4, 8);
        for (int l = 0; l < k; ++l) {
            BlockRotation lv5;
            BlockPos lv6;
            int o;
            int m;
            BlockPos lv4;
            int n;
            BlockBox lv7;
            if (list2.isEmpty() || (lv7 = BlockBox.create(n = (lv4 = list2.remove(m = random.nextInt(list2.size()))).getX(), 0, o = lv4.getZ(), (lv6 = Structure.transformAround(new BlockPos(5, 0, 6), BlockMirror.NONE, lv5 = BlockRotation.random(random), BlockPos.ORIGIN).add(n, 0, o)).getX(), 0, lv6.getZ())).intersects(lv2)) continue;
            OceanRuinGenerator.method_14822(arg, lv4, lv5, list, random, arg4, false, 0.8f);
        }
    }

    private static List<BlockPos> getRoomPositions(Random random, int i, int j) {
        ArrayList list = Lists.newArrayList();
        list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
        list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + MathHelper.nextInt(random, 1, 7)));
        list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
        list.add(new BlockPos(i + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
        list.add(new BlockPos(i + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 6)));
        list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 3, 8)));
        list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + MathHelper.nextInt(random, 1, 7)));
        list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
        return list;
    }

    private static void method_14822(StructureManager arg, BlockPos arg2, BlockRotation arg3, List<StructurePiece> list, Random random, OceanRuinFeatureConfig arg4, boolean bl, float f) {
        if (arg4.biomeType == OceanRuinFeature.BiomeType.WARM) {
            Identifier lv = bl ? OceanRuinGenerator.getRandomBigWarmRuin(random) : OceanRuinGenerator.getRandomWarmRuin(random);
            list.add(new Piece(arg, lv, arg2, arg3, f, arg4.biomeType, bl));
        } else if (arg4.biomeType == OceanRuinFeature.BiomeType.COLD) {
            Identifier[] lvs = bl ? BIG_BRICK_RUINS : BRICK_RUINS;
            Identifier[] lvs2 = bl ? BIG_CRACKED_RUINS : CRACKED_RUINS;
            Identifier[] lvs3 = bl ? BIG_MOSSY_RUINS : MOSSY_RUINS;
            int i = random.nextInt(lvs.length);
            list.add(new Piece(arg, lvs[i], arg2, arg3, f, arg4.biomeType, bl));
            list.add(new Piece(arg, lvs2[i], arg2, arg3, 0.7f, arg4.biomeType, bl));
            list.add(new Piece(arg, lvs3[i], arg2, arg3, 0.5f, arg4.biomeType, bl));
        }
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final OceanRuinFeature.BiomeType biomeType;
        private final float integrity;
        private final Identifier template;
        private final BlockRotation rotation;
        private final boolean large;

        public Piece(StructureManager arg, Identifier arg2, BlockPos arg3, BlockRotation arg4, float f, OceanRuinFeature.BiomeType arg5, boolean bl) {
            super(StructurePieceType.OCEAN_TEMPLE, 0);
            this.template = arg2;
            this.pos = arg3;
            this.rotation = arg4;
            this.integrity = f;
            this.biomeType = arg5;
            this.large = bl;
            this.initialize(arg);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.OCEAN_TEMPLE, arg2);
            this.template = new Identifier(arg2.getString("Template"));
            this.rotation = BlockRotation.valueOf(arg2.getString("Rot"));
            this.integrity = arg2.getFloat("Integrity");
            this.biomeType = OceanRuinFeature.BiomeType.valueOf(arg2.getString("BiomeType"));
            this.large = arg2.getBoolean("IsLarge");
            this.initialize(arg);
        }

        private void initialize(StructureManager arg) {
            Structure lv = arg.getStructureOrBlank(this.template);
            StructurePlacementData lv2 = new StructurePlacementData().setRotation(this.rotation).setMirror(BlockMirror.NONE).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putString("Template", this.template.toString());
            arg.putString("Rot", this.rotation.name());
            arg.putFloat("Integrity", this.integrity);
            arg.putString("BiomeType", this.biomeType.toString());
            arg.putBoolean("IsLarge", this.large);
        }

        @Override
        protected void handleMetadata(String string, BlockPos arg, class_5425 arg2, Random random, BlockBox arg3) {
            if ("chest".equals(string)) {
                arg2.setBlockState(arg, (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, arg2.getFluidState(arg).isIn(FluidTags.WATER)), 2);
                BlockEntity lv = arg2.getBlockEntity(arg);
                if (lv instanceof ChestBlockEntity) {
                    ((ChestBlockEntity)lv).setLootTable(this.large ? LootTables.UNDERWATER_RUIN_BIG_CHEST : LootTables.UNDERWATER_RUIN_SMALL_CHEST, random.nextLong());
                }
            } else if ("drowned".equals(string)) {
                DrownedEntity lv2 = EntityType.DROWNED.create(arg2.getWorld());
                lv2.setPersistent();
                lv2.refreshPositionAndAngles(arg, 0.0f, 0.0f);
                lv2.initialize(arg2, arg2.getLocalDifficulty(arg), SpawnReason.STRUCTURE, null, null);
                arg2.spawnEntity(lv2);
                if (arg.getY() > arg2.getSeaLevel()) {
                    arg2.setBlockState(arg, Blocks.AIR.getDefaultState(), 2);
                } else {
                    arg2.setBlockState(arg, Blocks.WATER.getDefaultState(), 2);
                }
            }
        }

        @Override
        public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            this.placementData.clearProcessors().addProcessor(new BlockRotStructureProcessor(this.integrity)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
            int i = arg.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.pos.getX(), this.pos.getZ());
            this.pos = new BlockPos(this.pos.getX(), i, this.pos.getZ());
            BlockPos lv = Structure.transformAround(new BlockPos(this.structure.getSize().getX() - 1, 0, this.structure.getSize().getZ() - 1), BlockMirror.NONE, this.rotation, BlockPos.ORIGIN).add(this.pos);
            this.pos = new BlockPos(this.pos.getX(), this.method_14829(this.pos, arg, lv), this.pos.getZ());
            return super.generate(arg, arg2, arg3, random, arg4, arg5, arg6);
        }

        private int method_14829(BlockPos arg, BlockView arg2, BlockPos arg3) {
            int i = arg.getY();
            int j = 512;
            int k = i - 1;
            int l = 0;
            for (BlockPos lv : BlockPos.iterate(arg, arg3)) {
                int m = lv.getX();
                int n = lv.getZ();
                int o = arg.getY() - 1;
                BlockPos.Mutable lv2 = new BlockPos.Mutable(m, o, n);
                BlockState lv3 = arg2.getBlockState(lv2);
                FluidState lv4 = arg2.getFluidState(lv2);
                while ((lv3.isAir() || lv4.isIn(FluidTags.WATER) || lv3.getBlock().isIn(BlockTags.ICE)) && o > 1) {
                    lv2.set(m, --o, n);
                    lv3 = arg2.getBlockState(lv2);
                    lv4 = arg2.getFluidState(lv2);
                }
                j = Math.min(j, o);
                if (o >= k - 2) continue;
                ++l;
            }
            int p = Math.abs(arg.getX() - arg3.getX());
            if (k - j > 2 && l > p - 2) {
                i = j + 1;
            }
            return i;
        }
    }
}

