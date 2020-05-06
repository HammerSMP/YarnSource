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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class PillarBlockStateProvider
extends BlockStateProvider {
    private final Block block;

    public PillarBlockStateProvider(Block arg) {
        super(BlockStateProviderType.SIMPLE_STATE_PROVIDER);
        this.block = arg;
    }

    public <T> PillarBlockStateProvider(Dynamic<T> dynamic) {
        this(BlockState.deserialize(dynamic.get("state").orElseEmptyMap()).getBlock());
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos arg) {
        Direction.Axis lv = Direction.Axis.pickRandomAxis(random);
        return (BlockState)this.block.getDefaultState().with(PillarBlock.AXIS, lv);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString())).put(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.block.getDefaultState()).getValue());
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build())).getValue();
    }
}

