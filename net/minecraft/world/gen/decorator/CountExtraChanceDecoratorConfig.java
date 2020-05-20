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
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountExtraChanceDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountExtraChanceDecoratorConfig> field_24986 = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("count").forGetter(arg -> arg.count), (App)Codec.FLOAT.fieldOf("extra_chance").forGetter(arg -> Float.valueOf(arg.extraChance)), (App)Codec.INT.fieldOf("extra_count").forGetter(arg -> arg.extraCount)).apply((Applicative)instance, CountExtraChanceDecoratorConfig::new));
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public CountExtraChanceDecoratorConfig(int i, float f, int j) {
        this.count = i;
        this.extraChance = f;
        this.extraCount = j;
    }
}

