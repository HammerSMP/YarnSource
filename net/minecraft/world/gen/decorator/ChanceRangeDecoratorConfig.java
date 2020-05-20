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

public class ChanceRangeDecoratorConfig
implements DecoratorConfig {
    public static final Codec<ChanceRangeDecoratorConfig> field_24876 = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("chance").forGetter(arg -> Float.valueOf(arg.chance)), (App)Codec.INT.fieldOf("bottom_offset").withDefault((Object)0).forGetter(arg -> arg.bottomOffset), (App)Codec.INT.fieldOf("top_offset").withDefault((Object)0).forGetter(arg -> arg.topOffset), (App)Codec.INT.fieldOf("top").withDefault((Object)0).forGetter(arg -> arg.top)).apply((Applicative)instance, ChanceRangeDecoratorConfig::new));
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
}

