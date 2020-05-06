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

public class IcePatchFeatureConfig
implements FeatureConfig {
    public final int radius;

    public IcePatchFeatureConfig(int i) {
        this.radius = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("radius"), (Object)dynamicOps.createInt(this.radius))));
    }

    public static <T> IcePatchFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("radius").asInt(0);
        return new IcePatchFeatureConfig(i);
    }
}

