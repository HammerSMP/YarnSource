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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class CyclingOption
extends Option {
    private final BiConsumer<GameOptions, Integer> setter;
    private final BiFunction<GameOptions, CyclingOption, Text> messageProvider;

    public CyclingOption(String string, BiConsumer<GameOptions, Integer> biConsumer, BiFunction<GameOptions, CyclingOption, Text> biFunction) {
        super(string);
        this.setter = biConsumer;
        this.messageProvider = biFunction;
    }

    public void cycle(GameOptions arg, int i) {
        this.setter.accept(arg, i);
        arg.write();
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions arg, int i, int j, int k) {
        return new OptionButtonWidget(i, j, k, 20, this, this.getMessage(arg), arg2 -> {
            this.cycle(arg, 1);
            arg2.setMessage(this.getMessage(arg));
        });
    }

    public Text getMessage(GameOptions arg) {
        return this.messageProvider.apply(arg, this);
    }
}

