/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class WeightedBlockStateProvider
extends BlockStateProvider {
    private final WeightedList<BlockState> states;

    private WeightedBlockStateProvider(WeightedList<BlockState> arg) {
        super(BlockStateProviderType.WEIGHTED_STATE_PROVIDER);
        this.states = arg;
    }

    public WeightedBlockStateProvider() {
        this(new WeightedList<BlockState>());
    }

    public <T> WeightedBlockStateProvider(Dynamic<T> dynamic) {
        this(new WeightedList<BlockState>(dynamic.get("entries").orElseEmptyList(), BlockState::deserialize));
    }

    public WeightedBlockStateProvider addState(BlockState arg, int i) {
        this.states.add(arg, i);
        return this;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos arg) {
        return this.states.pickRandom(random);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString())).put(dynamicOps.createString("entries"), this.states.serialize(dynamicOps, arg -> BlockState.serialize(dynamicOps, arg)));
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build())).getValue();
    }
}

