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

    public BooleanOption(String key, Predicate<GameOptions> getter, BiConsumer<GameOptions, Boolean> setter) {
        super(key);
        this.getter = getter;
        this.setter = setter;
    }

    public void set(GameOptions options, String value) {
        this.set(options, "true".equals(value));
    }

    public void set(GameOptions options) {
        this.set(options, !this.get(options));
        options.write();
    }

    private void set(GameOptions options, boolean value) {
        this.setter.accept(options, value);
    }

    public boolean get(GameOptions options) {
        return this.getter.test(options);
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width) {
        return new OptionButtonWidget(x, y, width, 20, this, this.getDisplayString(options), button -> {
            this.set(options);
            button.setMessage(this.getDisplayString(options));
        });
    }

    public Text getDisplayString(GameOptions options) {
        return ScreenTexts.method_30619(this.getDisplayPrefix(), this.get(options));
    }
}

