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
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class DecoratedDecoratorConfig
implements DecoratorConfig {
    public static final Codec<DecoratedDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredDecorator.CODEC.fieldOf("outer").forGetter(DecoratedDecoratorConfig::method_30455), (App)ConfiguredDecorator.CODEC.fieldOf("inner").forGetter(DecoratedDecoratorConfig::method_30457)).apply((Applicative)instance, DecoratedDecoratorConfig::new));
    private final ConfiguredDecorator<?> field_25855;
    private final ConfiguredDecorator<?> field_25856;

    public DecoratedDecoratorConfig(ConfiguredDecorator<?> arg, ConfiguredDecorator<?> arg2) {
        this.field_25855 = arg;
        this.field_25856 = arg2;
    }

    public ConfiguredDecorator<?> method_30455() {
        return this.field_25855;
    }

    public ConfiguredDecorator<?> method_30457() {
        return this.field_25856;
    }
}

