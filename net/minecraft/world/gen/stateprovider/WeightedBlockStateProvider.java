/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class WeightedBlockStateProvider
extends BlockStateProvider {
    public static final Codec<WeightedBlockStateProvider> field_24946 = WeightedList.method_28338(BlockState.field_24734).comapFlatMap(WeightedBlockStateProvider::method_28868, arg -> arg.states).fieldOf("entries").codec();
    private final WeightedList<BlockState> states;

    private static DataResult<WeightedBlockStateProvider> method_28868(WeightedList<BlockState> arg) {
        if (arg.method_28339()) {
            return DataResult.error((String)"WeightedStateProvider with no states");
        }
        return DataResult.success((Object)new WeightedBlockStateProvider(arg));
    }

    private WeightedBlockStateProvider(WeightedList<BlockState> arg) {
        this.states = arg;
    }

    @Override
    protected BlockStateProviderType<?> method_28862() {
        return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
    }

    public WeightedBlockStateProvider() {
        this(new WeightedList<BlockState>());
    }

    public WeightedBlockStateProvider addState(BlockState arg, int i) {
        this.states.add(arg, i);
        return this;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos arg) {
        return this.states.pickRandom(random);
    }
}

