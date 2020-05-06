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
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CarvingMaskDecoratorConfig
implements DecoratorConfig {
    protected final GenerationStep.Carver step;
    protected final float probability;

    public CarvingMaskDecoratorConfig(GenerationStep.Carver arg, float f) {
        this.step = arg;
        this.probability = f;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("step"), (Object)dynamicOps.createString(this.step.toString()), (Object)dynamicOps.createString("probability"), (Object)dynamicOps.createFloat(this.probability))));
    }

    public static CarvingMaskDecoratorConfig deserialize(Dynamic<?> dynamic) {
        GenerationStep.Carver lv = GenerationStep.Carver.valueOf(dynamic.get("step").asString(""));
        float f = dynamic.get("probability").asFloat(0.0f);
        return new CarvingMaskDecoratorConfig(lv, f);
    }
}

