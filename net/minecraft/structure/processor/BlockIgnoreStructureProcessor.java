/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlockIgnoreStructureProcessor
extends StructureProcessor {
    public static final Codec<BlockIgnoreStructureProcessor> field_24998 = BlockState.CODEC.xmap(AbstractBlock.AbstractBlockState::getBlock, Block::getDefaultState).listOf().fieldOf("blocks").xmap(BlockIgnoreStructureProcessor::new, arg -> arg.blocks).codec();
    public static final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.STRUCTURE_BLOCK));
    public static final BlockIgnoreStructureProcessor IGNORE_AIR = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.AIR));
    public static final BlockIgnoreStructureProcessor IGNORE_AIR_AND_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.AIR, (Object)Blocks.STRUCTURE_BLOCK));
    private final ImmutableList<Block> blocks;

    public BlockIgnoreStructureProcessor(List<Block> list) {
        this.blocks = ImmutableList.copyOf(list);
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView arg, BlockPos arg2, BlockPos arg3, Structure.StructureBlockInfo arg4, Structure.StructureBlockInfo arg5, StructurePlacementData arg6) {
        if (this.blocks.contains((Object)arg5.state.getBlock())) {
            return null;
        }
        return arg5;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_IGNORE;
    }
}

