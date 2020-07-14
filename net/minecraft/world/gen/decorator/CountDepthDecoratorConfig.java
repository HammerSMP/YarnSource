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

public class CountDepthDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountDepthDecoratorConfig> field_24982 = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("baseline").forGetter(arg -> arg.count), (App)Codec.INT.fieldOf("spread").forGetter(arg -> arg.spread)).apply((Applicative)instance, CountDepthDecoratorConfig::new));
    public final int count;
    public final int spread;

    public CountDepthDecoratorConfig(int count, int baseline) {
        this.count = count;
        this.spread = baseline;
    }
}

