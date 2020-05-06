/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceWithDimensions;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SwampHutGenerator
extends StructurePieceWithDimensions {
    private boolean hasWitch;
    private boolean hasCat;

    public SwampHutGenerator(Random random, int i, int j) {
        super(StructurePieceType.SWAMP_HUT, random, i, 64, j, 7, 7, 9);
    }

    public SwampHutGenerator(StructureManager arg, CompoundTag arg2) {
        super(StructurePieceType.SWAMP_HUT, arg2);
        this.hasWitch = arg2.getBoolean("Witch");
        this.hasCat = arg2.getBoolean("Cat");
    }

    @Override
    protected void toNbt(CompoundTag arg) {
        super.toNbt(arg);
        arg.putBoolean("Witch", this.hasWitch);
        arg.putBoolean("Cat", this.hasCat);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
        int m;
        int l;
        int k;
        if (!this.method_14839(arg, arg4, 0)) {
            return false;
        }
        this.fillWithOutline(arg, arg4, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
        this.fillWithOutline(arg, arg4, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
        this.addBlock(arg, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, arg4);
        this.addBlock(arg, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, arg4);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 1, 3, 4, arg4);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 5, 3, 4, arg4);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 5, 3, 5, arg4);
        this.addBlock(arg, Blocks.POTTED_RED_MUSHROOM.getDefaultState(), 1, 3, 5, arg4);
        this.addBlock(arg, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, arg4);
        this.addBlock(arg, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, arg4);
        this.addBlock(arg, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, arg4);
        this.addBlock(arg, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, arg4);
        BlockState lv = (BlockState)Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
        BlockState lv2 = (BlockState)Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
        BlockState lv3 = (BlockState)Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
        BlockState lv4 = (BlockState)Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
        this.fillWithOutline(arg, arg4, 0, 4, 1, 6, 4, 1, lv, lv, false);
        this.fillWithOutline(arg, arg4, 0, 4, 2, 0, 4, 7, lv2, lv2, false);
        this.fillWithOutline(arg, arg4, 6, 4, 2, 6, 4, 7, lv3, lv3, false);
        this.fillWithOutline(arg, arg4, 0, 4, 8, 6, 4, 8, lv4, lv4, false);
        this.addBlock(arg, (BlockState)lv.with(StairsBlock.SHAPE, StairShape.OUTER_RIGHT), 0, 4, 1, arg4);
        this.addBlock(arg, (BlockState)lv.with(StairsBlock.SHAPE, StairShape.OUTER_LEFT), 6, 4, 1, arg4);
        this.addBlock(arg, (BlockState)lv4.with(StairsBlock.SHAPE, StairShape.OUTER_LEFT), 0, 4, 8, arg4);
        this.addBlock(arg, (BlockState)lv4.with(StairsBlock.SHAPE, StairShape.OUTER_RIGHT), 6, 4, 8, arg4);
        for (int i = 2; i <= 7; i += 5) {
            for (int j = 1; j <= 5; j += 4) {
                this.method_14936(arg, Blocks.OAK_LOG.getDefaultState(), j, -1, i, arg4);
            }
        }
        if (!this.hasWitch && arg4.contains(new BlockPos(k = this.applyXTransform(2, 5), l = this.applyYTransform(2), m = this.applyZTransform(2, 5)))) {
            this.hasWitch = true;
            WitchEntity lv5 = EntityType.WITCH.create(arg.getWorld());
            lv5.setPersistent();
            lv5.refreshPositionAndAngles((double)k + 0.5, l, (double)m + 0.5, 0.0f, 0.0f);
            lv5.initialize(arg, arg.getLocalDifficulty(new BlockPos(k, l, m)), SpawnType.STRUCTURE, null, null);
            arg.spawnEntity(lv5);
        }
        this.method_16181(arg, arg4);
        return true;
    }

    private void method_16181(IWorld arg, BlockBox arg2) {
        int k;
        int j;
        int i;
        if (!this.hasCat && arg2.contains(new BlockPos(i = this.applyXTransform(2, 5), j = this.applyYTransform(2), k = this.applyZTransform(2, 5)))) {
            this.hasCat = true;
            CatEntity lv = EntityType.CAT.create(arg.getWorld());
            lv.setPersistent();
            lv.refreshPositionAndAngles((double)i + 0.5, j, (double)k + 0.5, 0.0f, 0.0f);
            lv.initialize(arg, arg.getLocalDifficulty(new BlockPos(i, j, k)), SpawnType.STRUCTURE, null, null);
            arg.spawnEntity(lv);
        }
    }
}

