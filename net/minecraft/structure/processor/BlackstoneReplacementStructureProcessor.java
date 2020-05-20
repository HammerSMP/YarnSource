/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.processor;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlackstoneReplacementStructureProcessor
extends StructureProcessor {
    public static final Codec<BlackstoneReplacementStructureProcessor> field_24996 = Codec.unit(() -> INSTANCE);
    public static final BlackstoneReplacementStructureProcessor INSTANCE = new BlackstoneReplacementStructureProcessor();
    private final Map<Block, Block> replacementMap = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(Blocks.COBBLESTONE, Blocks.BLACKSTONE);
        hashMap.put(Blocks.MOSSY_COBBLESTONE, Blocks.BLACKSTONE);
        hashMap.put(Blocks.STONE, Blocks.POLISHED_BLACKSTONE);
        hashMap.put(Blocks.STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        hashMap.put(Blocks.MOSSY_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        hashMap.put(Blocks.COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
        hashMap.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
        hashMap.put(Blocks.STONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
        hashMap.put(Blocks.STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        hashMap.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        hashMap.put(Blocks.COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
        hashMap.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
        hashMap.put(Blocks.SMOOTH_STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        hashMap.put(Blocks.STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        hashMap.put(Blocks.STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        hashMap.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        hashMap.put(Blocks.STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        hashMap.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        hashMap.put(Blocks.COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
        hashMap.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
        hashMap.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE);
        hashMap.put(Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        hashMap.put(Blocks.IRON_BARS, Blocks.CHAIN);
    });

    private BlackstoneReplacementStructureProcessor() {
    }

    @Override
    public Structure.StructureBlockInfo process(WorldView arg, BlockPos arg2, BlockPos arg3, Structure.StructureBlockInfo arg4, Structure.StructureBlockInfo arg5, StructurePlacementData arg6) {
        Block lv = this.replacementMap.get(arg5.state.getBlock());
        if (lv == null) {
            return arg5;
        }
        BlockState lv2 = arg5.state;
        BlockState lv3 = lv.getDefaultState();
        if (lv2.method_28498(StairsBlock.FACING)) {
            lv3 = (BlockState)lv3.with(StairsBlock.FACING, lv2.get(StairsBlock.FACING));
        }
        if (lv2.method_28498(StairsBlock.HALF)) {
            lv3 = (BlockState)lv3.with(StairsBlock.HALF, lv2.get(StairsBlock.HALF));
        }
        if (lv2.method_28498(SlabBlock.TYPE)) {
            lv3 = (BlockState)lv3.with(SlabBlock.TYPE, lv2.get(SlabBlock.TYPE));
        }
        return new Structure.StructureBlockInfo(arg5.pos, lv3, arg5.tag);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLACKSTONE_REPLACE;
    }
}

