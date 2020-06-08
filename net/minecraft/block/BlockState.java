/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;

public class BlockState
extends AbstractBlock.AbstractBlockState {
    public static final Codec<BlockState> CODEC = BlockState.createCodec(Registry.BLOCK, Block::getDefaultState).stable();

    public BlockState(Block arg, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
        super(arg, immutableMap, mapCodec);
    }

    @Override
    protected BlockState asBlockState() {
        return this;
    }
}

