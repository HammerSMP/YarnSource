/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decoratable;
import net.minecraft.world.gen.decorator.DecoratedDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class ConfiguredDecorator<DC extends DecoratorConfig>
implements Decoratable<ConfiguredDecorator<?>> {
    public static final Codec<ConfiguredDecorator<?>> CODEC = Registry.DECORATOR.dispatch("name", arg -> arg.decorator, Decorator::getCodec);
    private final Decorator<DC> decorator;
    private final DC config;

    public ConfiguredDecorator(Decorator<DC> decorator, DC config) {
        this.decorator = decorator;
        this.config = config;
    }

    public Stream<BlockPos> method_30444(DecoratorContext arg, Random random, BlockPos arg2) {
        return this.decorator.getPositions(arg, random, this.config, arg2);
    }

    public String toString() {
        return String.format("[%s %s]", Registry.DECORATOR.getId(this.decorator), this.config);
    }

    @Override
    public ConfiguredDecorator<?> decorate(ConfiguredDecorator<?> arg) {
        return new ConfiguredDecorator<DecoratedDecoratorConfig>(Decorator.DECORATED, new DecoratedDecoratorConfig(arg, this));
    }

    public DC getConfig() {
        return this.config;
    }

    @Override
    public /* synthetic */ Object decorate(ConfiguredDecorator decorator) {
        return this.decorate(decorator);
    }
}

