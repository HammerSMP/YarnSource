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

public class RangeDecoratorConfig
implements DecoratorConfig {
    public static final Codec<RangeDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("bottom_offset").orElse((Object)0).forGetter(arg -> arg.bottomOffset), (App)Codec.INT.fieldOf("top_offset").orElse((Object)0).forGetter(arg -> arg.topOffset), (App)Codec.INT.fieldOf("maximum").orElse((Object)0).forGetter(arg -> arg.maximum)).apply((Applicative)instance, RangeDecoratorConfig::new));
    public final int bottomOffset;
    public final int topOffset;
    public final int maximum;

    public RangeDecoratorConfig(int i, int j, int k) {
        this.bottomOffset = i;
        this.topOffset = j;
        this.maximum = k;
    }
}

