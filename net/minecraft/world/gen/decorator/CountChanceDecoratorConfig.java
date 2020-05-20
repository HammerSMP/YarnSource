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

public class CountChanceDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountChanceDecoratorConfig> field_24984 = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("count").forGetter(arg -> arg.count), (App)Codec.FLOAT.fieldOf("chance").forGetter(arg -> Float.valueOf(arg.chance))).apply((Applicative)instance, CountChanceDecoratorConfig::new));
    public final int count;
    public final float chance;

    public CountChanceDecoratorConfig(int i, float f) {
        this.count = i;
        this.chance = f;
    }
}

