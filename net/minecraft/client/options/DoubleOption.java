/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DoubleOption
extends Option {
    protected final float step;
    protected final double min;
    protected double max;
    private final Function<GameOptions, Double> getter;
    private final BiConsumer<GameOptions, Double> setter;
    private final BiFunction<GameOptions, DoubleOption, Text> displayStringGetter;

    public DoubleOption(String string, double d, double e, float f, Function<GameOptions, Double> function, BiConsumer<GameOptions, Double> biConsumer, BiFunction<GameOptions, DoubleOption, Text> biFunction) {
        super(string);
        this.min = d;
        this.max = e;
        this.step = f;
        this.getter = function;
        this.setter = biConsumer;
        this.displayStringGetter = biFunction;
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions arg, int i, int j, int k) {
        return new DoubleOptionSliderWidget(arg, i, j, k, 20, this);
    }

    public double getRatio(double d) {
        return MathHelper.clamp((this.adjust(d) - this.min) / (this.max - this.min), 0.0, 1.0);
    }

    public double getValue(double d) {
        return this.adjust(MathHelper.lerp(MathHelper.clamp(d, 0.0, 1.0), this.min, this.max));
    }

    private double adjust(double d) {
        if (this.step > 0.0f) {
            d = this.step * (float)Math.round(d / (double)this.step);
        }
        return MathHelper.clamp(d, this.min, this.max);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public void setMax(float f) {
        this.max = f;
    }

    public void set(GameOptions arg, double d) {
        this.setter.accept(arg, d);
    }

    public double get(GameOptions arg) {
        return this.getter.apply(arg);
    }

    public Text getDisplayString(GameOptions arg) {
        return this.displayStringGetter.apply(arg, this);
    }
}

