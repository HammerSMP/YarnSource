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
import net.minecraft.class_5432;
import net.minecraft.class_5443;
import net.minecraft.class_5444;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class ConfiguredDecorator<DC extends DecoratorConfig>
implements class_5432<ConfiguredDecorator<?>> {
    public static final Codec<ConfiguredDecorator<?>> field_24981 = Registry.DECORATOR.dispatch("name", arg -> arg.decorator, Decorator::getCodec);
    private final Decorator<DC> decorator;
    private final DC config;

    public ConfiguredDecorator(Decorator<DC> arg, DC arg2) {
        this.decorator = arg;
        this.config = arg2;
    }

    public Stream<BlockPos> method_30444(class_5444 arg, Random random, BlockPos arg2) {
        return this.decorator.getPositions(arg, random, this.config, arg2);
    }

    public String toString() {
        return String.format("[%s %s]", Registry.DECORATOR.getId(this.decorator), this.config);
    }

    @Override
    public ConfiguredDecorator<?> method_30374(ConfiguredDecorator<?> arg) {
        return new ConfiguredDecorator<class_5443>(Decorator.DECORATED, new class_5443(arg, this));
    }

    public DC method_30445() {
        return this.config;
    }

    @Override
    public /* synthetic */ Object method_30374(ConfiguredDecorator arg) {
        return this.method_30374(arg);
    }
}

