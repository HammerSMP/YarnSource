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
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftFeatureConfig
implements FeatureConfig {
    public final double probability;
    public final MineshaftFeature.Type type;

    public MineshaftFeatureConfig(double d, MineshaftFeature.Type arg) {
        this.probability = d;
        this.type = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("probability"), (Object)dynamicOps.createDouble(this.probability), (Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(this.type.getName()))));
    }

    public static <T> MineshaftFeatureConfig deserialize(Dynamic<T> dynamic) {
        float f = dynamic.get("probability").asFloat(0.0f);
        MineshaftFeature.Type lv = MineshaftFeature.Type.byName(dynamic.get("type").asString(""));
        return new MineshaftFeatureConfig(f, lv);
    }
}

