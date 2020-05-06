/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class TopSolidHeightmapNoiseBiasedDecoratorConfig
implements DecoratorConfig {
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;
    public final Heightmap.Type heightmap;

    public TopSolidHeightmapNoiseBiasedDecoratorConfig(int i, double d, double e, Heightmap.Type arg) {
        this.noiseToCountRatio = i;
        this.noiseFactor = d;
        this.noiseOffset = e;
        this.heightmap = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("noise_to_count_ratio"), (Object)dynamicOps.createInt(this.noiseToCountRatio), (Object)dynamicOps.createString("noise_factor"), (Object)dynamicOps.createDouble(this.noiseFactor), (Object)dynamicOps.createString("noise_offset"), (Object)dynamicOps.createDouble(this.noiseOffset), (Object)dynamicOps.createString("heightmap"), (Object)dynamicOps.createString(this.heightmap.getName()))));
    }

    public static TopSolidHeightmapNoiseBiasedDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("noise_to_count_ratio").asInt(10);
        double d = dynamic.get("noise_factor").asDouble(80.0);
        double e = dynamic.get("noise_offset").asDouble(0.0);
        Heightmap.Type lv = Heightmap.Type.byName(dynamic.get("heightmap").asString("OCEAN_FLOOR_WG"));
        return new TopSolidHeightmapNoiseBiasedDecoratorConfig(i, d, e, lv);
    }
}

