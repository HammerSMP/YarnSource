/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;

@Environment(value=EnvType.CLIENT)
public abstract class AlwaysSelectedEntryListWidget<E extends EntryListWidget.Entry<E>>
extends EntryListWidget<E> {
    private boolean inFocus;

    public AlwaysSelectedEntryListWidget(MinecraftClient arg, int i, int j, int k, int l, int m) {
        super(arg, i, j, k, l, m);
    }

    @Override
    public boolean changeFocus(boolean bl) {
        if (!this.inFocus && this.getItemCount() == 0) {
            return false;
        }
        boolean bl2 = this.inFocus = !this.inFocus;
        if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
            this.moveSelection(EntryListWidget.MoveDirection.DOWN);
        } else if (this.inFocus && this.getSelected() != null) {
            this.method_30015();
        }
        return this.inFocus;
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry<E extends Entry<E>>
    extends EntryListWidget.Entry<E> {
        @Override
        public boolean changeFocus(boolean bl) {
            return false;
        }
    }
}

