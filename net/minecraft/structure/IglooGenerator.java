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
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class IglooGenerator {
    private static final Identifier TOP_TEMPLATE = new Identifier("igloo/top");
    private static final Identifier MIDDLE_TEMPLATE = new Identifier("igloo/middle");
    private static final Identifier BOTTOM_TEMPLATE = new Identifier("igloo/bottom");
    private static final Map<Identifier, BlockPos> field_14408 = ImmutableMap.of((Object)TOP_TEMPLATE, (Object)new BlockPos(3, 5, 5), (Object)MIDDLE_TEMPLATE, (Object)new BlockPos(1, 3, 1), (Object)BOTTOM_TEMPLATE, (Object)new BlockPos(3, 6, 7));
    private static final Map<Identifier, BlockPos> field_14406 = ImmutableMap.of((Object)TOP_TEMPLATE, (Object)BlockPos.ORIGIN, (Object)MIDDLE_TEMPLATE, (Object)new BlockPos(2, -3, 4), (Object)BOTTOM_TEMPLATE, (Object)new BlockPos(0, -3, -2));

    public static void addPieces(StructureManager arg, BlockPos arg2, BlockRotation arg3, List<StructurePiece> list, Random random, DefaultFeatureConfig arg4) {
        if (random.nextDouble() < 0.5) {
            int i = random.nextInt(8) + 4;
            list.add(new Piece(arg, BOTTOM_TEMPLATE, arg2, arg3, i * 3));
            for (int j = 0; j < i - 1; ++j) {
                list.add(new Piece(arg, MIDDLE_TEMPLATE, arg2, arg3, j * 3));
            }
        }
        list.add(new Piece(arg, TOP_TEMPLATE, arg2, arg3, 0));
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final Identifier template;
        private final BlockRotation rotation;

        public Piece(StructureManager arg, Identifier arg2, BlockPos arg3, BlockRotation arg4, int i) {
            super(StructurePieceType.IGLOO, 0);
            this.template = arg2;
            BlockPos lv = (BlockPos)field_14406.get(arg2);
            this.pos = arg3.add(lv.getX(), lv.getY() - i, lv.getZ());
            this.rotation = arg4;
            this.initializeStructureData(arg);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.IGLOO, arg2);
            this.template = new Identifier(arg2.getString("Template"));
            this.rotation = BlockRotation.valueOf(arg2.getString("Rot"));
            this.initializeStructureData(arg);
        }

        private void initializeStructureData(StructureManager arg) {
            Structure lv = arg.getStructureOrBlank(this.template);
            StructurePlacementData lv2 = new StructurePlacementData().setRotation(this.rotation).setMirror(BlockMirror.NONE).setPosition((BlockPos)field_14408.get(this.template)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putString("Template", this.template.toString());
            arg.putString("Rot", this.rotation.name());
        }

        @Override
        protected void handleMetadata(String string, BlockPos arg, IWorld arg2, Random random, BlockBox arg3) {
            if (!"chest".equals(string)) {
                return;
            }
            arg2.setBlockState(arg, Blocks.AIR.getDefaultState(), 3);
            BlockEntity lv = arg2.getBlockEntity(arg.down());
            if (lv instanceof ChestBlockEntity) {
                ((ChestBlockEntity)lv).setLootTable(LootTables.IGLOO_CHEST_CHEST, random.nextLong());
            }
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            BlockPos lv5;
            BlockState lv6;
            StructurePlacementData lv = new StructurePlacementData().setRotation(this.rotation).setMirror(BlockMirror.NONE).setPosition((BlockPos)field_14408.get(this.template)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            BlockPos lv2 = (BlockPos)field_14406.get(this.template);
            BlockPos lv3 = this.pos.add(Structure.transform(lv, new BlockPos(3 - lv2.getX(), 0, 0 - lv2.getZ())));
            int i = arg.getTopY(Heightmap.Type.WORLD_SURFACE_WG, lv3.getX(), lv3.getZ());
            BlockPos lv4 = this.pos;
            this.pos = this.pos.add(0, i - 90 - 1, 0);
            boolean bl = super.generate(arg, arg2, arg3, random, arg4, arg5, arg6);
            if (this.template.equals(TOP_TEMPLATE) && !(lv6 = arg.getBlockState((lv5 = this.pos.add(Structure.transform(lv, new BlockPos(3, 0, 5)))).down())).isAir() && !lv6.isOf(Blocks.LADDER)) {
                arg.setBlockState(lv5, Blocks.SNOW_BLOCK.getDefaultState(), 3);
            }
            this.pos = lv4;
            return bl;
        }
    }
}

