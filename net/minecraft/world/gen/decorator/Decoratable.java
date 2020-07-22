/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.decorator;

import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;

public interface Decoratable<R> {
    public R decorate(ConfiguredDecorator<?> var1);

    default public R applyChance(int chance) {
        return this.decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(chance)));
    }

    default public R repeat(UniformIntDistribution count) {
        return this.decorate(Decorator.COUNT.configure(new CountConfig(count)));
    }

    default public R repeat(int count) {
        return this.repeat(UniformIntDistribution.of(count));
    }

    default public R repeatRandomly(int maxCount) {
        return this.repeat(UniformIntDistribution.of(0, maxCount));
    }

    default public R method_30377(int i) {
        return this.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(0, 0, i)));
    }

    default public R spreadHorizontally() {
        return this.decorate(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
    }
}

