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

public class ChanceRangeDecoratorConfig
implements DecoratorConfig {
    public final float chance;
    public final int bottomOffset;
    public final int topOffset;
    public final int top;

    public ChanceRangeDecoratorConfig(float f, int i, int j, int k) {
        this.chance = f;
        this.bottomOffset = i;
        this.topOffset = j;
        this.top = k;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("chance"), (Object)dynamicOps.createFloat(this.chance), (Object)dynamicOps.createString("bottom_offset"), (Object)dynamicOps.createInt(this.bottomOffset), (Object)dynamicOps.createString("top_offset"), (Object)dynamicOps.createInt(this.topOffset), (Object)dynamicOps.createString("top"), (Object)dynamicOps.createInt(this.top))));
    }

    public static ChanceRangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        float f = dynamic.get("chance").asFloat(0.0f);
        int i = dynamic.get("bottom_offset").asInt(0);
        int j = dynamic.get("top_offset").asInt(0);
        int k = dynamic.get("top").asInt(0);
        return new ChanceRangeDecoratorConfig(f, i, j, k);
    }
}

