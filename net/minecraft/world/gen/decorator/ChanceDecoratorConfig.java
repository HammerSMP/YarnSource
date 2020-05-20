/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class ChanceDecoratorConfig
implements DecoratorConfig {
    public static final Codec<ChanceDecoratorConfig> field_24980 = Codec.INT.fieldOf("chance").xmap(ChanceDecoratorConfig::new, arg -> arg.chance).codec();
    public final int chance;

    public ChanceDecoratorConfig(int i) {
        this.chance = i;
    }
}

