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
import net.minecraft.world.gen.feature.FeatureConfig;

public class SeaPickleFeatureConfig
implements FeatureConfig {
    public final int count;

    public SeaPickleFeatureConfig(int i) {
        this.count = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("count"), (Object)dynamicOps.createInt(this.count))));
    }

    public static <T> SeaPickleFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("count").asInt(0);
        return new SeaPickleFeatureConfig(i);
    }
}

