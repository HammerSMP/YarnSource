/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.EntryListWidget;

@Environment(value=EnvType.CLIENT)
public abstract class ElementListWidget<E extends Entry<E>>
extends EntryListWidget<E> {
    public ElementListWidget(MinecraftClient arg, int i, int j, int k, int l, int m) {
        super(arg, i, j, k, l, m);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        boolean bl2 = super.changeFocus(lookForwards);
        if (bl2) {
            this.ensureVisible(this.getFocused());
        }
        return bl2;
    }

    @Override
    protected boolean isSelectedItem(int index) {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry<E extends Entry<E>>
    extends EntryListWidget.Entry<E>
    implements ParentElement {
        @Nullable
        private Element focused;
        private boolean dragging;

        @Override
        public boolean isDragging() {
            return this.dragging;
        }

        @Override
        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }

        @Override
        public void setFocused(@Nullable Element focused) {
            this.focused = focused;
        }

        @Override
        @Nullable
        public Element getFocused() {
            return this.focused;
        }
    }
}

