/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CarvingMaskDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CarvingMaskDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)GenerationStep.Carver.field_24770.fieldOf("step").forGetter(arg -> arg.step), (App)Codec.FLOAT.fieldOf("probability").forGetter(arg -> Float.valueOf(arg.probability))).apply((Applicative)instance, CarvingMaskDecoratorConfig::new));
    protected final GenerationStep.Carver step;
    protected final float probability;

    public CarvingMaskDecoratorConfig(GenerationStep.Carver step, float probability) {
        this.step = step;
        this.probability = probability;
    }
}

