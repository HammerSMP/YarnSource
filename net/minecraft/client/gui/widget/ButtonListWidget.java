/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class ButtonListWidget
extends ElementListWidget<ButtonEntry> {
    public ButtonListWidget(MinecraftClient arg, int i, int j, int k, int l, int m) {
        super(arg, i, j, k, l, m);
        this.centerListVertically = false;
    }

    public int addSingleOptionEntry(Option arg) {
        return this.addEntry(ButtonEntry.create(this.client.options, this.width, arg));
    }

    public void addOptionEntry(Option arg, @Nullable Option arg2) {
        this.addEntry(ButtonEntry.create(this.client.options, this.width, arg, arg2));
    }

    public void addAll(Option[] args) {
        for (int i = 0; i < args.length; i += 2) {
            this.addOptionEntry(args[i], i < args.length - 1 ? args[i + 1] : null);
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }

    public Optional<AbstractButtonWidget> method_29624(double d, double e) {
        for (ButtonEntry lv : this.children()) {
            for (AbstractButtonWidget lv2 : lv.buttons) {
                if (!lv2.isMouseOver(d, e)) continue;
                return Optional.of(lv2);
            }
        }
        return Optional.empty();
    }

    @Environment(value=EnvType.CLIENT)
    public static class ButtonEntry
    extends ElementListWidget.Entry<ButtonEntry> {
        private final List<AbstractButtonWidget> buttons;

        private ButtonEntry(List<AbstractButtonWidget> list) {
            this.buttons = list;
        }

        public static ButtonEntry create(GameOptions arg, int i, Option arg2) {
            return new ButtonEntry((List<AbstractButtonWidget>)ImmutableList.of((Object)arg2.createButton(arg, i / 2 - 155, 0, 310)));
        }

        public static ButtonEntry create(GameOptions arg, int i, Option arg2, @Nullable Option arg3) {
            AbstractButtonWidget lv = arg2.createButton(arg, i / 2 - 155, 0, 150);
            if (arg3 == null) {
                return new ButtonEntry((List<AbstractButtonWidget>)ImmutableList.of((Object)lv));
            }
            return new ButtonEntry((List<AbstractButtonWidget>)ImmutableList.of((Object)lv, (Object)arg3.createButton(arg, i / 2 - 155 + 160, 0, 150)));
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.buttons.forEach(arg2 -> {
                arg2.y = j;
                arg2.render(arg, n, o, f);
            });
        }

        @Override
        public List<? extends Element> children() {
            return this.buttons;
        }
    }
}

