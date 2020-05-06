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
import net.minecraft.world.gen.feature.OceanRuinFeature;

public class OceanRuinFeatureConfig
implements FeatureConfig {
    public final OceanRuinFeature.BiomeType biomeType;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinFeatureConfig(OceanRuinFeature.BiomeType arg, float f, float g) {
        this.biomeType = arg;
        this.largeProbability = f;
        this.clusterProbability = g;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("biome_temp"), (Object)dynamicOps.createString(this.biomeType.getName()), (Object)dynamicOps.createString("large_probability"), (Object)dynamicOps.createFloat(this.largeProbability), (Object)dynamicOps.createString("cluster_probability"), (Object)dynamicOps.createFloat(this.clusterProbability))));
    }

    public static <T> OceanRuinFeatureConfig deserialize(Dynamic<T> dynamic) {
        OceanRuinFeature.BiomeType lv = OceanRuinFeature.BiomeType.byName(dynamic.get("biome_temp").asString(""));
        float f = dynamic.get("large_probability").asFloat(0.0f);
        float g = dynamic.get("cluster_probability").asFloat(0.0f);
        return new OceanRuinFeatureConfig(lv, f, g);
    }
}

