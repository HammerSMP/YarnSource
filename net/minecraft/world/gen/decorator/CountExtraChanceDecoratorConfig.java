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

public class CountExtraChanceDecoratorConfig
implements DecoratorConfig {
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public CountExtraChanceDecoratorConfig(int i, float f, int j) {
        this.count = i;
        this.extraChance = f;
        this.extraCount = j;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("count"), (Object)dynamicOps.createInt(this.count), (Object)dynamicOps.createString("extra_chance"), (Object)dynamicOps.createFloat(this.extraChance), (Object)dynamicOps.createString("extra_count"), (Object)dynamicOps.createInt(this.extraCount))));
    }

    public static CountExtraChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("count").asInt(0);
        float f = dynamic.get("extra_chance").asFloat(0.0f);
        int j = dynamic.get("extra_count").asInt(0);
        return new CountExtraChanceDecoratorConfig(i, f, j);
    }
}

