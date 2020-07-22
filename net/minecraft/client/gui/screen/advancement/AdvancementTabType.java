/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
enum AdvancementTabType {
    ABOVE(0, 0, 28, 32, 8),
    BELOW(84, 0, 28, 32, 8),
    LEFT(0, 64, 32, 28, 5),
    RIGHT(96, 64, 32, 28, 5);

    private final int u;
    private final int v;
    private final int width;
    private final int height;
    private final int tabCount;

    private AdvancementTabType(int u, int v, int width, int height, int tabCount) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.tabCount = tabCount;
    }

    public int getTabCount() {
        return this.tabCount;
    }

    public void drawBackground(MatrixStack arg, DrawableHelper arg2, int i, int j, boolean bl, int k) {
        int l = this.u;
        if (k > 0) {
            l += this.width;
        }
        if (k == this.tabCount - 1) {
            l += this.width;
        }
        int m = bl ? this.v + this.height : this.v;
        arg2.drawTexture(arg, i + this.getTabX(k), j + this.getTabY(k), l, m, this.width, this.height);
    }

    public void drawIcon(int x, int y, int index, ItemRenderer itemRenderer, ItemStack icon) {
        int l = x + this.getTabX(index);
        int m = y + this.getTabY(index);
        switch (this) {
            case ABOVE: {
                l += 6;
                m += 9;
                break;
            }
            case BELOW: {
                l += 6;
                m += 6;
                break;
            }
            case LEFT: {
                l += 10;
                m += 5;
                break;
            }
            case RIGHT: {
                l += 6;
                m += 5;
            }
        }
        itemRenderer.renderInGui(icon, l, m);
    }

    public int getTabX(int index) {
        switch (this) {
            case ABOVE: {
                return (this.width + 4) * index;
            }
            case BELOW: {
                return (this.width + 4) * index;
            }
            case LEFT: {
                return -this.width + 4;
            }
            case RIGHT: {
                return 248;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + (Object)((Object)this));
    }

    public int getTabY(int index) {
        switch (this) {
            case ABOVE: {
                return -this.height + 4;
            }
            case BELOW: {
                return 136;
            }
            case LEFT: {
                return this.height * index;
            }
            case RIGHT: {
                return this.height * index;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + (Object)((Object)this));
    }

    public boolean isClickOnTab(int screenX, int screenY, int index, double mouseX, double mouseY) {
        int l = screenX + this.getTabX(index);
        int m = screenY + this.getTabY(index);
        return mouseX > (double)l && mouseX < (double)(l + this.width) && mouseY > (double)m && mouseY < (double)(m + this.height);
    }
}

