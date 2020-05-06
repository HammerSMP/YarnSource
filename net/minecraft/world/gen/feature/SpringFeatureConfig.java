/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SpringFeatureConfig
implements FeatureConfig {
    public final FluidState state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final Set<Block> validBlocks;

    public SpringFeatureConfig(FluidState arg, boolean bl, int i, int j, Set<Block> set) {
        this.state = arg;
        this.requiresBlockBelow = bl;
        this.rockCount = i;
        this.holeCount = j;
        this.validBlocks = set;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("state"), (Object)FluidState.serialize(dynamicOps, this.state).getValue(), (Object)dynamicOps.createString("requires_block_below"), (Object)dynamicOps.createBoolean(this.requiresBlockBelow), (Object)dynamicOps.createString("rock_count"), (Object)dynamicOps.createInt(this.rockCount), (Object)dynamicOps.createString("hole_count"), (Object)dynamicOps.createInt(this.holeCount), (Object)dynamicOps.createString("valid_blocks"), (Object)dynamicOps.createList(this.validBlocks.stream().map(Registry.BLOCK::getId).map(Identifier::toString).map(dynamicOps::createString)))));
    }

    public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic2) {
        return new SpringFeatureConfig(dynamic2.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()), dynamic2.get("requires_block_below").asBoolean(true), dynamic2.get("rock_count").asInt(4), dynamic2.get("hole_count").asInt(1), (Set<Block>)ImmutableSet.copyOf((Collection)dynamic2.get("valid_blocks").asList(dynamic -> Registry.BLOCK.get(new Identifier(dynamic.asString("minecraft:air"))))));
    }
}

