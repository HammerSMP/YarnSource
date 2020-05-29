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

public class HeightmapRangeDecoratorConfig
implements DecoratorConfig {
    public static final Codec<HeightmapRangeDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("min").forGetter(arg -> arg.min), (App)Codec.INT.fieldOf("max").forGetter(arg -> arg.max)).apply((Applicative)instance, HeightmapRangeDecoratorConfig::new));
    public final int min;
    public final int max;

    public HeightmapRangeDecoratorConfig(int i, int j) {
        this.min = i;
        this.max = j;
    }
}

