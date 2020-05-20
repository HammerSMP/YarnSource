/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class NopeDecoratorConfig
implements DecoratorConfig {
    public static final Codec<NopeDecoratorConfig> field_24891 = Codec.unit(() -> field_24892);
    public static final NopeDecoratorConfig field_24892 = new NopeDecoratorConfig();
}

