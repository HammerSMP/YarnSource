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
import java.util.List;
import java.util.Map;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomRandomFeatureConfig
implements FeatureConfig {
    public final List<ConfiguredFeature<?, ?>> features;
    public final int count;

    public RandomRandomFeatureConfig(List<ConfiguredFeature<?, ?>> list, int i) {
        this.features = list;
        this.count = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("features"), (Object)dynamicOps.createList(this.features.stream().map(arg -> arg.serialize(dynamicOps).getValue())), (Object)dynamicOps.createString("count"), (Object)dynamicOps.createInt(this.count))));
    }

    public static <T> RandomRandomFeatureConfig deserialize(Dynamic<T> dynamic) {
        List list = dynamic.get("features").asList(ConfiguredFeature::deserialize);
        int i = dynamic.get("count").asInt(0);
        return new RandomRandomFeatureConfig(list, i);
    }
}

