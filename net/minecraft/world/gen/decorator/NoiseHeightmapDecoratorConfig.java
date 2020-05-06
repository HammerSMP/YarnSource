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
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class NoiseHeightmapDecoratorConfig
implements DecoratorConfig {
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public NoiseHeightmapDecoratorConfig(double d, int i, int j) {
        this.noiseLevel = d;
        this.belowNoise = i;
        this.aboveNoise = j;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("noise_level"), (Object)dynamicOps.createDouble(this.noiseLevel), (Object)dynamicOps.createString("below_noise"), (Object)dynamicOps.createInt(this.belowNoise), (Object)dynamicOps.createString("above_noise"), (Object)dynamicOps.createInt(this.aboveNoise))));
    }

    public static NoiseHeightmapDecoratorConfig deserialize(Dynamic<?> dynamic) {
        double d = dynamic.get("noise_level").asDouble(0.0);
        int i = dynamic.get("below_noise").asInt(0);
        int j = dynamic.get("above_noise").asInt(0);
        return new NoiseHeightmapDecoratorConfig(d, i, j);
    }
}

