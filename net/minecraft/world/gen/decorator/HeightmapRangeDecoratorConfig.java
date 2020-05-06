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

public class HeightmapRangeDecoratorConfig
implements DecoratorConfig {
    public final int min;
    public final int max;

    public HeightmapRangeDecoratorConfig(int i, int j) {
        this.min = i;
        this.max = j;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("min"), (Object)dynamicOps.createInt(this.min), (Object)dynamicOps.createString("max"), (Object)dynamicOps.createInt(this.max))));
    }

    public static HeightmapRangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("min").asInt(0);
        int j = dynamic.get("max").asInt(0);
        return new HeightmapRangeDecoratorConfig(i, j);
    }
}

