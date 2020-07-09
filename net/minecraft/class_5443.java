/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class class_5443
implements DecoratorConfig {
    public static final Codec<class_5443> field_25854 = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredDecorator.field_24981.fieldOf("outer").forGetter(class_5443::method_30455), (App)ConfiguredDecorator.field_24981.fieldOf("inner").forGetter(class_5443::method_30457)).apply((Applicative)instance, class_5443::new));
    private final ConfiguredDecorator<?> field_25855;
    private final ConfiguredDecorator<?> field_25856;

    public class_5443(ConfiguredDecorator<?> arg, ConfiguredDecorator<?> arg2) {
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

