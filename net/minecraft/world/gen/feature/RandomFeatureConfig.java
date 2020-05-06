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
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public class RandomFeatureConfig
implements FeatureConfig {
    public final List<RandomFeatureEntry<?>> features;
    public final ConfiguredFeature<?, ?> defaultFeature;

    public RandomFeatureConfig(List<RandomFeatureEntry<?>> list, ConfiguredFeature<?, ?> arg) {
        this.features = list;
        this.defaultFeature = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        Object object = dynamicOps.createList(this.features.stream().map(arg -> arg.serialize(dynamicOps).getValue()));
        Object object2 = this.defaultFeature.serialize(dynamicOps).getValue();
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("features"), (Object)object, (Object)dynamicOps.createString("default"), (Object)object2)));
    }

    public static <T> RandomFeatureConfig deserialize(Dynamic<T> dynamic) {
        List list = dynamic.get("features").asList(RandomFeatureEntry::deserialize);
        ConfiguredFeature<?, ?> lv = ConfiguredFeature.deserialize(dynamic.get("default").orElseEmptyMap());
        return new RandomFeatureConfig(list, lv);
    }
}

