/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.class_5428;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;

public interface class_5432<R> {
    public R method_30374(ConfiguredDecorator<?> var1);

    default public R method_30372(int i) {
        return this.method_30374(Decorator.CHANCE.configure(new ChanceDecoratorConfig(i)));
    }

    default public R method_30373(class_5428 arg) {
        return this.method_30374(Decorator.COUNT.configure(new CountConfig(arg)));
    }

    default public R method_30375(int i) {
        return this.method_30373(class_5428.method_30314(i));
    }

    default public R method_30376(int i) {
        return this.method_30373(class_5428.method_30315(0, i));
    }

    default public R method_30377(int i) {
        return this.method_30374(Decorator.RANGE.configure(new RangeDecoratorConfig(0, 0, i)));
    }

    default public R method_30371() {
        return this.method_30374(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
    }
}

