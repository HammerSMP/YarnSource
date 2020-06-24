/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class PillarBlockStateProvider
extends BlockStateProvider {
    public static final Codec<PillarBlockStateProvider> CODEC = BlockState.CODEC.fieldOf("state").xmap(AbstractBlock.AbstractBlockState::getBlock, Block::getDefaultState).xmap(PillarBlockStateProvider::new, arg -> arg.block).codec();
    private final Block block;

    public PillarBlockStateProvider(Block arg) {
        this.block = arg;
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.ROTATED_BLOCK_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos arg) {
        Direction.Axis lv = Direction.Axis.pickRandomAxis(random);
        return (BlockState)this.block.getDefaultState().with(PillarBlock.AXIS, lv);
    }
}

