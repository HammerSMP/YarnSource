/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountDecoratorConfig> field_24985 = Codec.INT.fieldOf("count").xmap(CountDecoratorConfig::new, arg -> arg.count).codec();
    public final int count;

    public CountDecoratorConfig(int i) {
        this.count = i;
    }
}

