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

public class RangeDecoratorConfig
implements DecoratorConfig {
    public final int count;
    public final int bottomOffset;
    public final int topOffset;
    public final int maximum;

    public RangeDecoratorConfig(int i, int j, int k, int l) {
        this.count = i;
        this.bottomOffset = j;
        this.topOffset = k;
        this.maximum = l;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("count"), (Object)dynamicOps.createInt(this.count), (Object)dynamicOps.createString("bottom_offset"), (Object)dynamicOps.createInt(this.bottomOffset), (Object)dynamicOps.createString("top_offset"), (Object)dynamicOps.createInt(this.topOffset), (Object)dynamicOps.createString("maximum"), (Object)dynamicOps.createInt(this.maximum))));
    }

    public static RangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("count").asInt(0);
        int j = dynamic.get("bottom_offset").asInt(0);
        int k = dynamic.get("top_offset").asInt(0);
        int l = dynamic.get("maximum").asInt(0);
        return new RangeDecoratorConfig(i, j, k, l);
    }
}

