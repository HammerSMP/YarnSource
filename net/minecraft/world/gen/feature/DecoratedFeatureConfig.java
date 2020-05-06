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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DecoratedFeatureConfig
implements FeatureConfig {
    public final ConfiguredFeature<?, ?> feature;
    public final ConfiguredDecorator<?> decorator;

    public DecoratedFeatureConfig(ConfiguredFeature<?, ?> arg, ConfiguredDecorator<?> arg2) {
        this.feature = arg;
        this.decorator = arg2;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("feature"), (Object)this.feature.serialize(dynamicOps).getValue(), (Object)dynamicOps.createString("decorator"), (Object)this.decorator.serialize(dynamicOps).getValue())));
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId((Feature<?>)this.feature.feature), Registry.DECORATOR.getId(this.decorator.decorator));
    }

    public static <T> DecoratedFeatureConfig deserialize(Dynamic<T> dynamic) {
        ConfiguredFeature<?, ?> lv = ConfiguredFeature.deserialize(dynamic.get("feature").orElseEmptyMap());
        ConfiguredDecorator<?> lv2 = ConfiguredDecorator.deserialize(dynamic.get("decorator").orElseEmptyMap());
        return new DecoratedFeatureConfig(lv, lv2);
    }
}

