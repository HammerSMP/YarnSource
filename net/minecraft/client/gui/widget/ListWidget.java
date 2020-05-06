/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class ListWidget
extends AbstractParentElement
implements Drawable {
    protected final MinecraftClient client;
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected final int itemHeight;
    protected boolean centerListVertically = true;
    protected int yDrag = -2;
    protected double scrollAmount;
    protected boolean visible = true;
    protected boolean renderSelection = true;
    protected boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;

    public ListWidget(MinecraftClient arg, int i, int j, int k, int l, int m) {
        this.client = arg;
        this.width = i;
        this.height = j;
        this.top = k;
        this.bottom = l;
        this.itemHeight = m;
        this.left = 0;
        this.right = i;
    }

    public boolean isVisible() {
        return this.visible;
    }

    protected abstract int getItemCount();

    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }

    protected boolean selectItem(int i, int j, double d, double e) {
        return true;
    }

    protected abstract boolean isSelectedItem(int var1);

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    protected abstract void renderBackground();

    protected void updateItemPosition(int i, int j, int k, float f) {
    }

    protected abstract void renderItem(MatrixStack var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8);

    protected void renderHeader(int i, int j, Tessellator arg) {
    }

    protected void clickedHeader(int i, int j) {
    }

    protected void renderDecorations(int i, int j) {
    }

    public int getItemAtPosition(double d, double e) {
        int i = this.left + this.width / 2 - this.getRowWidth() / 2;
        int j = this.left + this.width / 2 + this.getRowWidth() / 2;
        int k = MathHelper.floor(e - (double)this.top) - this.headerHeight + (int)this.scrollAmount - 4;
        int l = k / this.itemHeight;
        if (d < (double)this.getScrollbarPosition() && d >= (double)i && d <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
            return l;
        }
        return -1;
    }

    protected void capYPosition() {
        this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0, (double)this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    public boolean isMouseInList(double d, double e) {
        return e >= (double)this.top && e <= (double)this.bottom && d >= (double)this.left && d <= (double)this.right;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (!this.visible) {
            return;
        }
        this.renderBackground();
        int k = this.getScrollbarPosition();
        int l = k + 6;
        this.capYPosition();
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float g = 32.0f;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(this.left, this.bottom, 0.0).texture((float)this.left / 32.0f, (float)(this.bottom + (int)this.scrollAmount) / 32.0f).color(32, 32, 32, 255).next();
        lv2.vertex(this.right, this.bottom, 0.0).texture((float)this.right / 32.0f, (float)(this.bottom + (int)this.scrollAmount) / 32.0f).color(32, 32, 32, 255).next();
        lv2.vertex(this.right, this.top, 0.0).texture((float)this.right / 32.0f, (float)(this.top + (int)this.scrollAmount) / 32.0f).color(32, 32, 32, 255).next();
        lv2.vertex(this.left, this.top, 0.0).texture((float)this.left / 32.0f, (float)(this.top + (int)this.scrollAmount) / 32.0f).color(32, 32, 32, 255).next();
        lv.draw();
        int m = this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
        int n = this.top + 4 - (int)this.scrollAmount;
        if (this.renderHeader) {
            this.renderHeader(m, n, lv);
        }
        this.renderList(arg, m, n, i, j, f);
        RenderSystem.disableDepthTest();
        this.renderHoleBackground(0, this.top, 255, 255);
        this.renderHoleBackground(this.bottom, this.height, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        int o = 4;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(this.left, this.top + 4, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 0).next();
        lv2.vertex(this.right, this.top + 4, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 0).next();
        lv2.vertex(this.right, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
        lv2.vertex(this.left, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
        lv.draw();
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(this.left, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
        lv2.vertex(this.right, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
        lv2.vertex(this.right, this.bottom - 4, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 0).next();
        lv2.vertex(this.left, this.bottom - 4, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 0).next();
        lv.draw();
        int p = this.getMaxScroll();
        if (p > 0) {
            int q = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            int r = (int)this.scrollAmount * (this.bottom - this.top - (q = MathHelper.clamp(q, 32, this.bottom - this.top - 8))) / p + this.top;
            if (r < this.top) {
                r = this.top;
            }
            lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            lv2.vertex(k, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
            lv2.vertex(l, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
            lv2.vertex(l, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
            lv2.vertex(k, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
            lv.draw();
            lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            lv2.vertex(k, r + q, 0.0).texture(0.0f, 1.0f).color(128, 128, 128, 255).next();
            lv2.vertex(l, r + q, 0.0).texture(1.0f, 1.0f).color(128, 128, 128, 255).next();
            lv2.vertex(l, r, 0.0).texture(1.0f, 0.0f).color(128, 128, 128, 255).next();
            lv2.vertex(k, r, 0.0).texture(0.0f, 0.0f).color(128, 128, 128, 255).next();
            lv.draw();
            lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            lv2.vertex(k, r + q - 1, 0.0).texture(0.0f, 1.0f).color(192, 192, 192, 255).next();
            lv2.vertex(l - 1, r + q - 1, 0.0).texture(1.0f, 1.0f).color(192, 192, 192, 255).next();
            lv2.vertex(l - 1, r, 0.0).texture(1.0f, 0.0f).color(192, 192, 192, 255).next();
            lv2.vertex(k, r, 0.0).texture(0.0f, 0.0f).color(192, 192, 192, 255).next();
            lv.draw();
        }
        this.renderDecorations(i, j);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    protected void updateScrollingState(double d, double e, int i) {
        this.scrolling = i == 0 && d >= (double)this.getScrollbarPosition() && d < (double)(this.getScrollbarPosition() + 6);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        this.updateScrollingState(d, e, i);
        if (!this.isVisible() || !this.isMouseInList(d, e)) {
            return false;
        }
        int j = this.getItemAtPosition(d, e);
        if (j == -1 && i == 0) {
            this.clickedHeader((int)(d - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.top) + (int)this.scrollAmount - 4);
            return true;
        }
        if (j != -1 && this.selectItem(j, i, d, e)) {
            if (this.children().size() > j) {
                this.setFocused(this.children().get(j));
            }
            this.setDragging(true);
            return true;
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(d, e, i);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (super.mouseDragged(d, e, i, f, g)) {
            return true;
        }
        if (!this.isVisible() || i != 0 || !this.scrolling) {
            return false;
        }
        if (e < (double)this.top) {
            this.scrollAmount = 0.0;
        } else if (e > (double)this.bottom) {
            this.scrollAmount = this.getMaxScroll();
        } else {
            double h = this.getMaxScroll();
            if (h < 1.0) {
                h = 1.0;
            }
            int j = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            double k = h / (double)(this.bottom - this.top - (j = MathHelper.clamp(j, 32, this.bottom - this.top - 8)));
            if (k < 1.0) {
                k = 1.0;
            }
            this.scrollAmount += g * k;
            this.capYPosition();
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (!this.isVisible()) {
            return false;
        }
        this.scrollAmount -= f * (double)this.itemHeight / 2.0;
        return true;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (!this.isVisible()) {
            return false;
        }
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (i == 264) {
            this.moveSelection(1);
            return true;
        }
        if (i == 265) {
            this.moveSelection(-1);
            return true;
        }
        return false;
    }

    protected void moveSelection(int i) {
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (!this.isVisible()) {
            return false;
        }
        return super.charTyped(c, i);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return this.isMouseInList(d, e);
    }

    public int getRowWidth() {
        return 220;
    }

    protected void renderList(MatrixStack arg, int i, int j, int k, int l, float f) {
        int m = this.getItemCount();
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        for (int n = 0; n < m; ++n) {
            int o = j + n * this.itemHeight + this.headerHeight;
            int p = this.itemHeight - 4;
            if (o > this.bottom || o + p < this.top) {
                this.updateItemPosition(n, i, o, f);
            }
            if (this.renderSelection && this.isSelectedItem(n)) {
                int q = this.left + this.width / 2 - this.getRowWidth() / 2;
                int r = this.left + this.width / 2 + this.getRowWidth() / 2;
                RenderSystem.disableTexture();
                float g = this.isFocused() ? 1.0f : 0.5f;
                RenderSystem.color4f(g, g, g, 1.0f);
                lv2.begin(7, VertexFormats.POSITION);
                lv2.vertex(q, o + p + 2, 0.0).next();
                lv2.vertex(r, o + p + 2, 0.0).next();
                lv2.vertex(r, o - 2, 0.0).next();
                lv2.vertex(q, o - 2, 0.0).next();
                lv.draw();
                RenderSystem.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                lv2.begin(7, VertexFormats.POSITION);
                lv2.vertex(q + 1, o + p + 1, 0.0).next();
                lv2.vertex(r - 1, o + p + 1, 0.0).next();
                lv2.vertex(r - 1, o - 1, 0.0).next();
                lv2.vertex(q + 1, o - 1, 0.0).next();
                lv.draw();
                RenderSystem.enableTexture();
            }
            this.renderItem(arg, n, i, o, p, k, l, f);
        }
    }

    protected boolean isFocused() {
        return false;
    }

    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }

    protected void renderHoleBackground(int i, int j, int k, int l) {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(this.left, j, 0.0).texture(0.0f, (float)j / 32.0f).color(64, 64, 64, l).next();
        lv2.vertex(this.left + this.width, j, 0.0).texture((float)this.width / 32.0f, (float)j / 32.0f).color(64, 64, 64, l).next();
        lv2.vertex(this.left + this.width, i, 0.0).texture((float)this.width / 32.0f, (float)i / 32.0f).color(64, 64, 64, k).next();
        lv2.vertex(this.left, i, 0.0).texture(0.0f, (float)i / 32.0f).color(64, 64, 64, k).next();
        lv.draw();
    }
}

