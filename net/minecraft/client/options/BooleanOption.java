/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class BooleanOption
extends Option {
    private final Predicate<GameOptions> getter;
    private final BiConsumer<GameOptions, Boolean> setter;

    public BooleanOption(String string, Predicate<GameOptions> predicate, BiConsumer<GameOptions, Boolean> biConsumer) {
        super(string);
        this.getter = predicate;
        this.setter = biConsumer;
    }

    public void set(GameOptions arg, String string) {
        this.set(arg, "true".equals(string));
    }

    public void set(GameOptions arg) {
        this.set(arg, !this.get(arg));
        arg.write();
    }

    private void set(GameOptions arg, boolean bl) {
        this.setter.accept(arg, bl);
    }

    public boolean get(GameOptions arg) {
        return this.getter.test(arg);
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions arg, int i, int j, int k) {
        return new OptionButtonWidget(i, j, k, 20, this, this.getDisplayString(arg), arg2 -> {
            this.set(arg);
            arg2.setMessage(this.getDisplayString(arg));
        });
    }

    public Text getDisplayString(GameOptions arg) {
        return this.getDisplayPrefix().append(ScreenTexts.getToggleText(this.get(arg)));
    }
}

