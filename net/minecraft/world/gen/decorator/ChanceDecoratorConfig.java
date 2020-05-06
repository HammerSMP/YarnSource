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

public class ChanceDecoratorConfig
implements DecoratorConfig {
    public final int chance;

    public ChanceDecoratorConfig(int i) {
        this.chance = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("chance"), (Object)dynamicOps.createInt(this.chance))));
    }

    public static ChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("chance").asInt(0);
        return new ChanceDecoratorConfig(i);
    }
}

