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
    private final ControlsOptionsScreen parent;
    private int maxKeyNameLength;

    public ControlsListWidget(ControlsOptionsScreen parent, MinecraftClient client) {
        super(client, parent.width + 45, parent.height, 43, parent.height - 32, 20);
        this.parent = parent;
        Object[] lvs = (KeyBinding[])ArrayUtils.clone((Object[])client.options.keysAll);
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
            if ((i = client.textRenderer.getWidth(lv2 = new TranslatableText(((KeyBinding)lv).getTranslationKey()))) > this.maxKeyNameLength) {
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

        private KeyBindingEntry(final KeyBinding binding, final Text text) {
            this.binding = binding;
            this.bindingName = text;
            this.editButton = new ButtonWidget(0, 0, 75, 20, text, arg2 -> {
                ((ControlsListWidget)ControlsListWidget.this).parent.focusedBinding = binding;
            }){

                @Override
                protected MutableText getNarrationMessage() {
                    if (binding.isUnbound()) {
                        return new TranslatableText("narrator.controls.unbound", text);
                    }
                    return new TranslatableText("narrator.controls.bound", text, super.getNarrationMessage());
                }
            };
            this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), arg2 -> {
                ((ControlsListWidget)ControlsListWidget.this).client.options.setKeyCode(binding, binding.getDefaultKey());
                KeyBinding.updateKeysByCode();
            }){

                @Override
                protected MutableText getNarrationMessage() {
                    return new TranslatableText("narrator.controls.reset", text);
                }
            };
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean bl2 = ((ControlsListWidget)ControlsListWidget.this).parent.focusedBinding == this.binding;
            ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.getClass();
            ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.draw(matrices, this.bindingName, (float)(x + 90 - ControlsListWidget.this.maxKeyNameLength), (float)(y + entryHeight / 2 - 9 / 2), 0xFFFFFF);
            this.resetButton.x = x + 190;
            this.resetButton.y = y;
            this.resetButton.active = !this.binding.isDefault();
            this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
            this.editButton.x = x + 105;
            this.editButton.y = y;
            this.editButton.setMessage(this.binding.getBoundKeyLocalizedText());
            boolean bl3 = false;
            if (!this.binding.isUnbound()) {
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
            this.editButton.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of((Object)this.editButton, (Object)this.resetButton);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.editButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return this.resetButton.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class CategoryEntry
    extends Entry {
        private final Text text;
        private final int textWidth;

        public CategoryEntry(Text text) {
            this.text = text;
            this.textWidth = ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.getWidth(this.text);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.getClass();
            ((ControlsListWidget)ControlsListWidget.this).client.textRenderer.draw(matrices, this.text, (float)(((ControlsListWidget)ControlsListWidget.this).client.currentScreen.width / 2 - this.textWidth / 2), (float)(y + entryHeight - 9 - 1), 0xFFFFFF);
        }

        @Override
        public boolean changeFocus(boolean lookForwards) {
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

