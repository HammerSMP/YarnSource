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
import java.util.function.Predicate;
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

    public EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.itemHeight = itemHeight;
        this.left = 0;
        this.right = width;
    }

    public void setRenderSelection(boolean renderSelection) {
        this.renderSelection = renderSelection;
    }

    protected void setRenderHeader(boolean renderHeader, int headerHeight) {
        this.renderHeader = renderHeader;
        this.headerHeight = headerHeight;
        if (!renderHeader) {
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

    public void setSelected(@Nullable E entry) {
        this.selected = entry;
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

    protected void replaceEntries(Collection<E> newEntries) {
        this.children.clear();
        this.children.addAll(newEntries);
    }

    protected E getEntry(int index) {
        return (E)((Entry)this.children().get(index));
    }

    protected int addEntry(E entry) {
        this.children.add(entry);
        return this.children.size() - 1;
    }

    protected int getItemCount() {
        return this.children().size();
    }

    protected boolean isSelectedItem(int index) {
        return Objects.equals(this.getSelected(), this.children().get(index));
    }

    @Nullable
    protected final E getEntryAtPosition(double x, double y) {
        int i = this.getRowWidth() / 2;
        int j = this.left + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int n = m / this.itemHeight;
        if (x < (double)this.getScrollbarPositionX() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getItemCount()) {
            return (E)((Entry)this.children().get(n));
        }
        return null;
    }

    public void updateSize(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
    }

    public void setLeftPos(int left) {
        this.left = left;
        this.right = left + this.width;
    }

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    protected void clickedHeader(int x, int y) {
    }

    protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator arg2) {
    }

    protected void renderBackground(MatrixStack matrices) {
    }

    protected void renderDecorations(MatrixStack arg, int i, int j) {
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
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
            this.renderHeader(matrices, m, n, lv);
        }
        this.renderList(matrices, m, n, mouseX, mouseY, delta);
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
        this.renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    protected void centerScrollOn(E entry) {
        this.setScrollAmount(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2);
    }

    protected void ensureVisible(E entry) {
        int k;
        int i = this.getRowTop(this.children().indexOf(entry));
        int j = i - this.top - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }
        if ((k = this.bottom - i - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-k);
        }
    }

    private void scroll(int amount) {
        this.setScrollAmount(this.getScrollAmount() + (double)amount);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = MathHelper.clamp(amount, 0.0, (double)this.getMaxScroll());
    }

    private int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
    }

    protected int getScrollbarPositionX() {
        return this.width / 2 + 124;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        E lv = this.getEntryAtPosition(mouseX, mouseY);
        if (lv != null) {
            if (lv.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused((Element)lv);
                this.setDragging(true);
                return true;
            }
        } else if (button == 0) {
            this.clickedHeader((int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.getScrollAmount() - 4);
            return true;
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (button != 0 || !this.scrolling) {
            return false;
        }
        if (mouseY < (double)this.top) {
            this.setScrollAmount(0.0);
        } else if (mouseY > (double)this.bottom) {
            this.setScrollAmount(this.getMaxScroll());
        } else {
            double h = Math.max(1, this.getMaxScroll());
            int j = this.bottom - this.top;
            int k = MathHelper.clamp((int)((float)(j * j) / (float)this.getMaxPosition()), 32, j - 8);
            double l = Math.max(1.0, h / (double)(j - k));
            this.setScrollAmount(this.getScrollAmount() + deltaY * l);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.setScrollAmount(this.getScrollAmount() - amount * (double)this.itemHeight / 2.0);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 264) {
            this.moveSelection(MoveDirection.DOWN);
            return true;
        }
        if (keyCode == 265) {
            this.moveSelection(MoveDirection.UP);
            return true;
        }
        return false;
    }

    protected void moveSelection(MoveDirection direction) {
        this.moveSelectionIf(direction, entry -> true);
    }

    protected void method_30015() {
        E lv = this.getSelected();
        if (lv != null) {
            this.setSelected(lv);
            this.ensureVisible(lv);
        }
    }

    protected void moveSelectionIf(MoveDirection direction, Predicate<E> predicate) {
        int i;
        int n = i = direction == MoveDirection.UP ? -1 : 1;
        if (!this.children().isEmpty()) {
            int k;
            int j = this.children().indexOf(this.getSelected());
            while (j != (k = MathHelper.clamp(j + i, 0, this.getItemCount() - 1))) {
                Entry lv = (Entry)this.children().get(k);
                if (predicate.test(lv)) {
                    this.setSelected(lv);
                    this.ensureVisible(lv);
                    break;
                }
                j = k;
            }
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
    }

    protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        int m = this.getItemCount();
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        for (int n = 0; n < m; ++n) {
            int o = this.getRowTop(n);
            int p = this.getRowBottom(n);
            if (p < this.top || o > this.bottom) continue;
            int q = y + n * this.itemHeight + this.headerHeight;
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
            ((Entry)lv3).render(matrices, n, o, v, s, r, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPosition(mouseX, mouseY), lv3), delta);
        }
    }

    protected int getRowLeft() {
        return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    protected int getRowTop(int index) {
        return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    private int getRowBottom(int index) {
        return this.getRowTop(index) + this.itemHeight;
    }

    protected boolean isFocused() {
        return false;
    }

    protected E remove(int index) {
        Entry lv = (Entry)this.children.get(index);
        if (this.removeEntry((Entry)this.children.get(index))) {
            return (E)lv;
        }
        return null;
    }

    protected boolean removeEntry(E entry) {
        boolean bl = this.children.remove(entry);
        if (bl && entry == this.getSelected()) {
            this.setSelected(null);
        }
        return bl;
    }

    private void method_29621(Entry<E> arg) {
        ((Entry)arg).list = this;
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
            EntryListWidget.this.method_29621(arg);
            return lv;
        }

        @Override
        public void add(int i, E arg) {
            this.entries.add(i, arg);
            EntryListWidget.this.method_29621(arg);
        }

        @Override
        public E remove(int i) {
            return (Entry)this.entries.remove(i);
        }

        @Override
        public /* synthetic */ Object remove(int index) {
            return this.remove(index);
        }

        @Override
        public /* synthetic */ void add(int index, Object entry) {
            this.add(index, (E)((Entry)entry));
        }

        @Override
        public /* synthetic */ Object set(int index, Object entry) {
            return this.set(index, (E)((Entry)entry));
        }

        @Override
        public /* synthetic */ Object get(int index) {
            return this.get(index);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry<E extends Entry<E>>
    implements Element {
        @Deprecated
        private EntryListWidget<E> list;

        public abstract void render(MatrixStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return Objects.equals(this.list.getEntryAtPosition(mouseX, mouseY), this);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum MoveDirection {
        UP,
        DOWN;

    }
}

