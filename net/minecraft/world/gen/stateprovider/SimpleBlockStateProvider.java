/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class SimpleBlockStateProvider
extends BlockStateProvider {
    public static final Codec<SimpleBlockStateProvider> field_24945 = BlockState.field_24734.fieldOf("state").xmap(SimpleBlockStateProvider::new, arg -> arg.state).codec();
    private final BlockState state;

    public SimpleBlockStateProvider(BlockState arg) {
        this.state = arg;
    }

    @Override
    protected BlockStateProviderType<?> method_28862() {
        return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos arg) {
        return this.state;
    }
}

