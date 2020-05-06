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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomBooleanFeatureConfig
implements FeatureConfig {
    public final ConfiguredFeature<?, ?> featureTrue;
    public final ConfiguredFeature<?, ?> featureFalse;

    public RandomBooleanFeatureConfig(ConfiguredFeature<?, ?> arg, ConfiguredFeature<?, ?> arg2) {
        this.featureTrue = arg;
        this.featureFalse = arg2;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("feature_true"), (Object)this.featureTrue.serialize(dynamicOps).getValue(), (Object)dynamicOps.createString("feature_false"), (Object)this.featureFalse.serialize(dynamicOps).getValue())));
    }

    public static <T> RandomBooleanFeatureConfig deserialize(Dynamic<T> dynamic) {
        ConfiguredFeature<?, ?> lv = ConfiguredFeature.deserialize(dynamic.get("feature_true").orElseEmptyMap());
        ConfiguredFeature<?, ?> lv2 = ConfiguredFeature.deserialize(dynamic.get("feature_false").orElseEmptyMap());
        return new RandomBooleanFeatureConfig(lv, lv2);
    }
}

