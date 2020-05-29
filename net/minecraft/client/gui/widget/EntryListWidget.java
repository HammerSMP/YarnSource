/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
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
public abstract class EntryListWidget<E extends Entry<E>>
extends AbstractParentElement
implements Drawable {
    protected final MinecraftClient client;
    protected final int itemHeight;
    private final List<E> children = new Entries();
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected boolean centerListVertically = true;
    private double scrollAmount;
    private boolean renderSelection = true;
    private boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    private E selected;

    public EntryListWidget(MinecraftClient arg, int i, int j, int k, int l, int m) {
        this.client = arg;
        this.width = i;
        this.height = j;
        this.top = k;
        this.bottom = l;
        this.itemHeight = m;
        this.left = 0;
        this.right = i;
    }

    public void method_29344(boolean bl) {
        this.renderSelection = bl;
    }

    protected void setRenderHeader(boolean bl, int i) {
        this.renderHeader = bl;
        this.headerHeight = i;
        if (!bl) {
            this.headerHeight = 0;
        }
    }

    public int getRowWidth() {
        return 220;
    }

    @Nullable
    public E getSelected() {
        return this.selected;
    }

    public void setSelected(@Nullable E arg) {
        this.selected = arg;
    }

    @Nullable
    public E getFocused() {
        return (E)((Entry)super.getFocused());
    }

    public final List<E> children() {
        return this.children;
    }

    protected final void clearEntries() {
        this.children.clear();
    }

    protected void replaceEntries(Collection<E> collection) {
        this.children.clear();
        this.children.addAll(collection);
    }

    protected E getEntry(int i) {
        return (E)((Entry)this.children().get(i));
    }

    protected int addEntry(E arg) {
        this.children.add(arg);
        return this.children.size() - 1;
    }

    protected int getItemCount() {
        return this.children().size();
    }

    protected boolean isSelectedItem(int i) {
        return Objects.equals(this.getSelected(), this.children().get(i));
    }

    @Nullable
    protected final E getEntryAtPosition(double d, double e) {
        int i = this.getRowWidth() / 2;
        int j = this.left + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = MathHelper.floor(e - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int n = m / this.itemHeight;
        if (d < (double)this.getScrollbarPositionX() && d >= (double)k && d <= (double)l && n >= 0 && m >= 0 && n < this.getItemCount()) {
            return (E)((Entry)this.children().get(n));
        }
        return null;
    }

    public void updateSize(int i, int j, int k, int l) {
        this.width = i;
        this.height = j;
        this.top = k;
        this.bottom = l;
        this.left = 0;
        this.right = i;
    }

    public void setLeftPos(int i) {
        this.left = i;
        this.right = i + this.width;
    }

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    protected void clickedHeader(int i, int j) {
    }

    protected void renderHeader(MatrixStack arg, int i, int j, Tessellator arg2) {
    }

    protected void renderBackground(MatrixStack arg) {
    }

    protected void renderDecorations(MatrixStack arg, int i, int j) {
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        int k = this.getScrollbarPositionX();
        int l = k + 6;
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float g = 32.0f;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(this.left, this.bottom, 0.0).texture((float)this.left / 32.0f, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        lv2.vertex(this.right, this.bottom, 0.0).texture((float)this.right / 32.0f, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        lv2.vertex(this.right, this.top, 0.0).texture((float)this.right / 32.0f, (float)(this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        lv2.vertex(this.left, this.top, 0.0).texture((float)this.left / 32.0f, (float)(this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        lv.draw();
        int m = this.getRowLeft();
        int n = this.top + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(arg, m, n, lv);
        }
        this.renderList(arg, m, n, i, j, f);
        this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);
        float h = 32.0f;
        int o = -100;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(this.left, this.top, -100.0).texture(0.0f, (float)this.top / 32.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.left + this.width, this.top, -100.0).texture((float)this.width / 32.0f, (float)this.top / 32.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.left + this.width, 0.0, -100.0).texture((float)this.width / 32.0f, 0.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.left, 0.0, -100.0).texture(0.0f, 0.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.left, this.height, -100.0).texture(0.0f, (float)this.height / 32.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.left + this.width, this.height, -100.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.left + this.width, this.bottom, -100.0).texture((float)this.width / 32.0f, (float)this.bottom / 32.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.left, this.bottom, -100.0).texture(0.0f, (float)this.bottom / 32.0f).color(64, 64, 64, 255).next();
        lv.draw();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        int p = 4;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(this.left, this.top + 4, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 0).next();
        lv2.vertex(this.right, this.top + 4, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 0).next();
        lv2.vertex(this.right, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
        lv2.vertex(this.left, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
        lv2.vertex(this.left, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
        lv2.vertex(this.right, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
        lv2.vertex(this.right, this.bottom - 4, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 0).next();
        lv2.vertex(this.left, this.bottom - 4, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 0).next();
        lv.draw();
        int q = this.getMaxScroll();
        if (q > 0) {
            int r = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            r = MathHelper.clamp(r, 32, this.bottom - this.top - 8);
            int s = (int)this.getScrollAmount() * (this.bottom - this.top - r) / q + this.top;
            if (s < this.top) {
                s = this.top;
            }
            lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            lv2.vertex(k, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
            lv2.vertex(l, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
            lv2.vertex(l, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
            lv2.vertex(k, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
            lv2.vertex(k, s + r, 0.0).texture(0.0f, 1.0f).color(128, 128, 128, 255).next();
            lv2.vertex(l, s + r, 0.0).texture(1.0f, 1.0f).color(128, 128, 128, 255).next();
            lv2.vertex(l, s, 0.0).texture(1.0f, 0.0f).color(128, 128, 128, 255).next();
            lv2.vertex(k, s, 0.0).texture(0.0f, 0.0f).color(128, 128, 128, 255).next();
            lv2.vertex(k, s + r - 1, 0.0).texture(0.0f, 1.0f).color(192, 192, 192, 255).next();
            lv2.vertex(l - 1, s + r - 1, 0.0).texture(1.0f, 1.0f).color(192, 192, 192, 255).next();
            lv2.vertex(l - 1, s, 0.0).texture(1.0f, 0.0f).color(192, 192, 192, 255).next();
            lv2.vertex(k, s, 0.0).texture(0.0f, 0.0f).color(192, 192, 192, 255).next();
            lv.draw();
        }
        this.renderDecorations(arg, i, j);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    protected void centerScrollOn(E arg) {
        this.setScrollAmount(this.children().indexOf(arg) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2);
    }

    protected void ensureVisible(E arg) {
        int k;
        int i = this.getRowTop(this.children().indexOf(arg));
        int j = i - this.top - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }
        if ((k = this.bottom - i - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-k);
        }
    }

    private void scroll(int i) {
        this.setScrollAmount(this.getScrollAmount() + (double)i);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double d) {
        this.scrollAmount = MathHelper.clamp(d, 0.0, (double)this.getMaxScroll());
    }

    private int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    protected void updateScrollingState(double d, double e, int i) {
        this.scrolling = i == 0 && d >= (double)this.getScrollbarPositionX() && d < (double)(this.getScrollbarPositionX() + 6);
    }

    protected int getScrollbarPositionX() {
        return this.width / 2 + 124;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        this.updateScrollingState(d, e, i);
        if (!this.isMouseOver(d, e)) {
            return false;
        }
        E lv = this.getEntryAtPosition(d, e);
        if (lv != null) {
            if (lv.mouseClicked(d, e, i)) {
                this.setFocused((Element)lv);
                this.setDragging(true);
                return true;
            }
        } else if (i == 0) {
            this.clickedHeader((int)(d - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.top) + (int)this.getScrollAmount() - 4);
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
        if (i != 0 || !this.scrolling) {
            return false;
        }
        if (e < (double)this.top) {
            this.setScrollAmount(0.0);
        } else if (e > (double)this.bottom) {
            this.setScrollAmount(this.getMaxScroll());
        } else {
            double h = Math.max(1, this.getMaxScroll());
            int j = this.bottom - this.top;
            int k = MathHelper.clamp((int)((float)(j * j) / (float)this.getMaxPosition()), 32, j - 8);
            double l = Math.max(1.0, h / (double)(j - k));
            this.setScrollAmount(this.getScrollAmount() + g * l);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        this.setScrollAmount(this.getScrollAmount() - f * (double)this.itemHeight / 2.0);
        return true;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
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
        if (!this.children().isEmpty()) {
            int j = this.children().indexOf(this.getSelected());
            int k = MathHelper.clamp(j + i, 0, this.getItemCount() - 1);
            Entry lv = (Entry)this.children().get(k);
            this.setSelected(lv);
            this.ensureVisible(lv);
        }
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return e >= (double)this.top && e <= (double)this.bottom && d >= (double)this.left && d <= (double)this.right;
    }

    protected void renderList(MatrixStack arg, int i, int j, int k, int l, float f) {
        int m = this.getItemCount();
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        for (int n = 0; n < m; ++n) {
            int o = this.getRowTop(n);
            int p = this.getRowBottom(n);
            if (p < this.top || o > this.bottom) continue;
            int q = j + n * this.itemHeight + this.headerHeight;
            int r = this.itemHeight - 4;
            E lv3 = this.getEntry(n);
            int s = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(n)) {
                int t = this.left + this.width / 2 - s / 2;
                int u = this.left + this.width / 2 + s / 2;
                RenderSystem.disableTexture();
                float g = this.isFocused() ? 1.0f : 0.5f;
                RenderSystem.color4f(g, g, g, 1.0f);
                lv2.begin(7, VertexFormats.POSITION);
                lv2.vertex(t, q + r + 2, 0.0).next();
                lv2.vertex(u, q + r + 2, 0.0).next();
                lv2.vertex(u, q - 2, 0.0).next();
                lv2.vertex(t, q - 2, 0.0).next();
                lv.draw();
                RenderSystem.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                lv2.begin(7, VertexFormats.POSITION);
                lv2.vertex(t + 1, q + r + 1, 0.0).next();
                lv2.vertex(u - 1, q + r + 1, 0.0).next();
                lv2.vertex(u - 1, q - 1, 0.0).next();
                lv2.vertex(t + 1, q - 1, 0.0).next();
                lv.draw();
                RenderSystem.enableTexture();
            }
            int v = this.getRowLeft();
            ((Entry)lv3).render(arg, n, o, v, s, r, k, l, this.isMouseOver(k, l) && Objects.equals(this.getEntryAtPosition(k, l), lv3), f);
        }
    }

    protected int getRowLeft() {
        return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    protected int getRowTop(int i) {
        return this.top + 4 - (int)this.getScrollAmount() + i * this.itemHeight + this.headerHeight;
    }

    private int getRowBottom(int i) {
        return this.getRowTop(i) + this.itemHeight;
    }

    protected boolean isFocused() {
        return false;
    }

    protected E remove(int i) {
        Entry lv = (Entry)this.children.get(i);
        if (this.removeEntry((Entry)this.children.get(i))) {
            return (E)lv;
        }
        return null;
    }

    protected boolean removeEntry(E arg) {
        boolean bl = this.children.remove(arg);
        if (bl && arg == this.getSelected()) {
            this.setSelected(null);
        }
        return bl;
    }

    @Override
    @Nullable
    public /* synthetic */ Element getFocused() {
        return this.getFocused();
    }

    @Environment(value=EnvType.CLIENT)
    class Entries
    extends AbstractList<E> {
        private final List<E> entries = Lists.newArrayList();

        private Entries() {
        }

        @Override
        public E get(int i) {
            return (Entry)this.entries.get(i);
        }

        @Override
        public int size() {
            return this.entries.size();
        }

        @Override
        public E set(int i, E arg) {
            Entry lv = (Entry)this.entries.set(i, arg);
            ((Entry)arg).list = EntryListWidget.this;
            return lv;
        }

        @Override
        public void add(int i, E arg) {
            this.entries.add(i, arg);
            ((Entry)arg).list = EntryListWidget.this;
        }

        @Override
        public E remove(int i) {
            return (Entry)this.entries.remove(i);
        }

        @Override
        public /* synthetic */ Object remove(int i) {
            return this.remove(i);
        }

        @Override
        public /* synthetic */ void add(int i, Object object) {
            this.add(i, (E)((Entry)object));
        }

        @Override
        public /* synthetic */ Object set(int i, Object object) {
            return this.set(i, (E)((Entry)object));
        }

        @Override
        public /* synthetic */ Object get(int i) {
            return this.get(i);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry<E extends Entry<E>>
    implements Element {
        @Deprecated
        EntryListWidget<E> list;

        public abstract void render(MatrixStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        @Override
        public boolean isMouseOver(double d, double e) {
            return Objects.equals(this.list.getEntryAtPosition(d, e), this);
        }
    }
}

