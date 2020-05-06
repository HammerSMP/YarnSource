/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EmeraldOreFeatureConfig
implements FeatureConfig {
    public final BlockState target;
    public final BlockState state;

    public EmeraldOreFeatureConfig(BlockState arg, BlockState arg2) {
        this.target = arg;
        this.state = arg2;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("target"), (Object)BlockState.serialize(dynamicOps, this.target).getValue(), (Object)dynamicOps.createString("state"), (Object)BlockState.serialize(dynamicOps, this.state).getValue())));
    }

    public static <T> EmeraldOreFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState lv = dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState lv2 = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new EmeraldOreFeatureConfig(lv, lv2);
    }
}

