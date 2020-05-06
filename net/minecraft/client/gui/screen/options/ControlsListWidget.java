/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.client.gui.screen.options;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

@Environment(value=EnvType.CLIENT)
public class ControlsListWidget
extends ElementListWidget<Entry> {
    private final ControlsOptionsScreen gui;
    private int maxKeyNameLength;

    public ControlsListWidget(ControlsOptionsScreen arg, MinecraftClient arg2) {
        super(arg2, arg.width + 45, arg.height, 43, arg.height - 32, 20);
        this.gui = arg;
        Object[] lvs = (KeyBinding[])ArrayUtils.clone((Object[])arg2.options.keysAll);
        Arrays.sort(lvs);
        String string = null;
        for (Object lv : lvs) {
            TranslatableText lv2;
            int i;
            String string2 = ((KeyBinding)lv).getCategory();
            if (!string2.equals(string)) {
                string = string2;
                this.addEntry(new CategoryEntry(new TranslatableText(string2)));
            }
            if ((i = arg2.textRenderer.getStringWidth(lv2 = new TranslatableText(((KeyBinding)lv).getId()))) > this.maxKeyNameLength) {
                this.maxKeyNameLength = i;
            }
            this.addEntry(new KeyBindingEntry((KeyBinding)lv, lv2));
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 15;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    @Environment(value=EnvType.CLIENT)
    public class KeyBindingEntry
    extends Entry {
        private final KeyBinding binding;
        private final Text bindingName;
        private final ButtonWidget editButton;
        private final ButtonWidget resetButton;

        private KeyBindingEntry(final KeyBinding arg22, final Text arg3) {
            this.binding = arg22;
            this.bindingName = arg3;
            this.editButton = new ButtonWidget(0, 0, 75, 20, arg3, arg2 -> {
                ((ControlsListWidget)ControlsListWidget.this).gui.focusedBinding = arg22;
            }){

                @Override
                protected MutableText getNarrationMessage() {
                    if (arg22.isNotBound()) {
                        return new TranslatableText("narrator.controls.unbound", arg3);
                    }
                    return new TranslatableText("narrator.controls.bound", arg3, super.getNarrationMessage());
                }
            };
            this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), arg2 -> {
                ((ControlsListWidget)ControlsListWidget.this).client.options.setKeyCode(arg22, arg22.getDefaultKeyCode());
                KeyBinding.updateKeysByCode();
            }){

                @Override
                protected MutableText getNarrationMessage() {
                    return new TranslatableText("narrator.controls.reset", arg3);
                }
            };
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            boolean bl2 = ((ControlsListWidget)ControlsListWidget.this).gui.focusedBinding == this.binding;
            ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.getClass();
            ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.draw(arg, this.bindingName, (float)(k + 90 - ControlsListWidget.this.maxKeyNameLength), (float)(j + m / 2 - 9 / 2), 0xFFFFFF);
            this.resetButton.x = k + 190;
            this.resetButton.y = j;
            this.resetButton.active = !this.binding.isDefault();
            this.resetButton.render(arg, n, o, f);
            this.editButton.x = k + 105;
            this.editButton.y = j;
            this.editButton.setMessage(this.binding.getLocalizedName());
            boolean bl3 = false;
            if (!this.binding.isNotBound()) {
                for (KeyBinding lv : ((ControlsListWidget)ControlsListWidget.this).client.options.keysAll) {
                    if (lv == this.binding || !this.binding.equals(lv)) continue;
                    bl3 = true;
                    break;
                }
            }
            if (bl2) {
                this.editButton.setMessage(new LiteralText("> ").append(this.editButton.getMessage().shallowCopy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW));
            } else if (bl3) {
                this.editButton.setMessage(this.editButton.getMessage().shallowCopy().formatted(Formatting.RED));
            }
            this.editButton.render(arg, n, o, f);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of((Object)this.editButton, (Object)this.resetButton);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (this.editButton.mouseClicked(d, e, i)) {
                return true;
            }
            return this.resetButton.mouseClicked(d, e, i);
        }

        @Override
        public boolean mouseReleased(double d, double e, int i) {
            return this.editButton.mouseReleased(d, e, i) || this.resetButton.mouseReleased(d, e, i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class CategoryEntry
    extends Entry {
        private final Text name;
        private final int nameWidth;

        public CategoryEntry(Text arg2) {
            this.name = arg2;
            this.nameWidth = ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.getStringWidth(this.name);
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.getClass();
            ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.draw(arg, this.name, (float)(((ControlsListWidget)ControlsListWidget.this).client.currentScreen.width / 2 - this.nameWidth / 2), (float)(j + m - 9 - 1), 0xFFFFFF);
        }

        @Override
        public boolean changeFocus(boolean bl) {
            return false;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends ElementListWidget.Entry<Entry> {
    }
}

