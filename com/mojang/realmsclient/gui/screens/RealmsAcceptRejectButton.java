/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsObjectSelectionList;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsAcceptRejectButton {
    public final int width;
    public final int height;
    public final int x;
    public final int y;

    public RealmsAcceptRejectButton(int i, int j, int k, int l) {
        this.width = i;
        this.height = j;
        this.x = k;
        this.y = l;
    }

    public void render(MatrixStack arg, int i, int j, int k, int l) {
        int m = i + this.x;
        int n = j + this.y;
        boolean bl = false;
        if (k >= m && k <= m + this.width && l >= n && l <= n + this.height) {
            bl = true;
        }
        this.render(arg, m, n, bl);
    }

    protected abstract void render(MatrixStack var1, int var2, int var3, boolean var4);

    public int getRight() {
        return this.x + this.width;
    }

    public int getBottom() {
        return this.y + this.height;
    }

    public abstract void handleClick(int var1);

    public static void render(MatrixStack arg, List<RealmsAcceptRejectButton> list, RealmsObjectSelectionList<?> arg2, int i, int j, int k, int l) {
        for (RealmsAcceptRejectButton lv : list) {
            if (arg2.getRowWidth() <= lv.getRight()) continue;
            lv.render(arg, i, j, k, l);
        }
    }

    public static void handleClick(RealmsObjectSelectionList<?> arg, AlwaysSelectedEntryListWidget.Entry<?> arg2, List<RealmsAcceptRejectButton> list, int i, double d, double e) {
        int j;
        if (i == 0 && (j = arg.children().indexOf(arg2)) > -1) {
            arg.setSelected(j);
            int k = arg.getRowLeft();
            int l = arg.getRowTop(j);
            int m = (int)(d - (double)k);
            int n = (int)(e - (double)l);
            for (RealmsAcceptRejectButton lv : list) {
                if (m < lv.x || m > lv.getRight() || n < lv.y || n > lv.getBottom()) continue;
                lv.handleClick(j);
            }
        }
    }
}

