/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class BlockAgeStructureProcessor
extends StructureProcessor {
    public static final Codec<BlockAgeStructureProcessor> CODEC = Codec.FLOAT.fieldOf("mossiness").xmap(BlockAgeStructureProcessor::new, arg -> Float.valueOf(arg.mossiness)).codec();
    private final float mossiness;

    public BlockAgeStructureProcessor(float mossiness) {
        this.mossiness = mossiness;
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView arg, BlockPos pos, BlockPos arg3, Structure.StructureBlockInfo arg4, Structure.StructureBlockInfo arg5, StructurePlacementData arg6) {
        Random random = arg6.getRandom(arg5.pos);
        BlockState lv = arg5.state;
        BlockPos lv2 = arg5.pos;
        BlockState lv3 = null;
        if (lv.isOf(Blocks.STONE_BRICKS) || lv.isOf(Blocks.STONE) || lv.isOf(Blocks.CHISELED_STONE_BRICKS)) {
            lv3 = this.processBlocks(random);
        } else if (lv.isIn(BlockTags.STAIRS)) {
            lv3 = this.processStairs(random, arg5.state);
        } else if (lv.isIn(BlockTags.SLABS)) {
            lv3 = this.processSlabs(random);
        } else if (lv.isIn(BlockTags.WALLS)) {
            lv3 = this.processWalls(random);
        } else if (lv.isOf(Blocks.OBSIDIAN)) {
            lv3 = this.processObsidian(random);
        }
        if (lv3 != null) {
            return new Structure.StructureBlockInfo(lv2, lv3, arg5.tag);
        }
        return arg5;
    }

    @Nullable
    private BlockState processBlocks(Random random) {
        if (random.nextFloat() >= 0.5f) {
            return null;
        }
        BlockState[] lvs = new BlockState[]{Blocks.CRACKED_STONE_BRICKS.getDefaultState(), BlockAgeStructureProcessor.randomStairProperties(random, Blocks.STONE_BRICK_STAIRS)};
        BlockState[] lvs2 = new BlockState[]{Blocks.MOSSY_STONE_BRICKS.getDefaultState(), BlockAgeStructureProcessor.randomStairProperties(random, Blocks.MOSSY_STONE_BRICK_STAIRS)};
        return this.process(random, lvs, lvs2);
    }

    @Nullable
    private BlockState processStairs(Random random, BlockState state) {
        Direction lv = state.get(StairsBlock.FACING);
        BlockHalf lv2 = state.get(StairsBlock.HALF);
        if (random.nextFloat() >= 0.5f) {
            return null;
        }
        BlockState[] lvs = new BlockState[]{Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_BRICK_SLAB.getDefaultState()};
        BlockState[] lvs2 = new BlockState[]{(BlockState)((BlockState)Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, lv)).with(StairsBlock.HALF, lv2), Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState()};
        return this.process(random, lvs, lvs2);
    }

    @Nullable
    private BlockState processSlabs(Random random) {
        if (random.nextFloat() < this.mossiness) {
            return Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState();
        }
        return null;
    }

    @Nullable
    private BlockState processWalls(Random random) {
        if (random.nextFloat() < this.mossiness) {
            return Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState();
        }
        return null;
    }

    @Nullable
    private BlockState processObsidian(Random random) {
        if (random.nextFloat() < 0.15f) {
            return Blocks.CRYING_OBSIDIAN.getDefaultState();
        }
        return null;
    }

    private static BlockState randomStairProperties(Random random, Block stairs) {
        return (BlockState)((BlockState)stairs.getDefaultState().with(StairsBlock.FACING, Direction.Type.HORIZONTAL.random(random))).with(StairsBlock.HALF, BlockHalf.values()[random.nextInt(BlockHalf.values().length)]);
    }

    private BlockState process(Random random, BlockState[] regularStates, BlockState[] mossyStates) {
        if (random.nextFloat() < this.mossiness) {
            return BlockAgeStructureProcessor.randomState(random, mossyStates);
        }
        return BlockAgeStructureProcessor.randomState(random, regularStates);
    }

    private static BlockState randomState(Random random, BlockState[] states) {
        return states[random.nextInt(states.length)];
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_AGE;
    }
}

