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

public class CountDepthDecoratorConfig
implements DecoratorConfig {
    public final int count;
    public final int baseline;
    public final int spread;

    public CountDepthDecoratorConfig(int i, int j, int k) {
        this.count = i;
        this.baseline = j;
        this.spread = k;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("count"), (Object)dynamicOps.createInt(this.count), (Object)dynamicOps.createString("baseline"), (Object)dynamicOps.createInt(this.baseline), (Object)dynamicOps.createString("spread"), (Object)dynamicOps.createInt(this.spread))));
    }

    public static CountDepthDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("count").asInt(0);
        int j = dynamic.get("baseline").asInt(0);
        int k = dynamic.get("spread").asInt(0);
        return new CountDepthDecoratorConfig(i, j, k);
    }
}

