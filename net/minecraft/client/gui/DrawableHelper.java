/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public abstract class DrawableHelper {
    public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/options_background.png");
    public static final Identifier STATS_ICON_TEXTURE = new Identifier("textures/gui/container/stats_icons.png");
    public static final Identifier GUI_ICONS_TEXTURE = new Identifier("textures/gui/icons.png");
    private int zOffset;

    protected void drawHorizontalLine(MatrixStack arg, int i, int j, int k, int l) {
        if (j < i) {
            int m = i;
            i = j;
            j = m;
        }
        DrawableHelper.fill(arg, i, k, j + 1, k + 1, l);
    }

    protected void drawVerticalLine(MatrixStack arg, int i, int j, int k, int l) {
        if (k < j) {
            int m = j;
            j = k;
            k = m;
        }
        DrawableHelper.fill(arg, i, j + 1, i + 1, k, l);
    }

    public static void fill(MatrixStack arg, int i, int j, int k, int l, int m) {
        DrawableHelper.fill(arg.peek().getModel(), i, j, k, l, m);
    }

    private static void fill(Matrix4f arg, int i, int j, int k, int l, int m) {
        if (i < k) {
            int n = i;
            i = k;
            k = n;
        }
        if (j < l) {
            int o = j;
            j = l;
            l = o;
        }
        float f = (float)(m >> 24 & 0xFF) / 255.0f;
        float g = (float)(m >> 16 & 0xFF) / 255.0f;
        float h = (float)(m >> 8 & 0xFF) / 255.0f;
        float p = (float)(m & 0xFF) / 255.0f;
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        lv.begin(7, VertexFormats.POSITION_COLOR);
        lv.vertex(arg, i, l, 0.0f).color(g, h, p, f).next();
        lv.vertex(arg, k, l, 0.0f).color(g, h, p, f).next();
        lv.vertex(arg, k, j, 0.0f).color(g, h, p, f).next();
        lv.vertex(arg, i, j, 0.0f).color(g, h, p, f).next();
        lv.end();
        BufferRenderer.draw(lv);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void fillGradient(MatrixStack arg, int i, int j, int k, int l, int m, int n) {
        this.fillGradient(arg.peek().getModel(), i, j, k, l, m, n);
    }

    private void fillGradient(Matrix4f arg, int i, int j, int k, int l, int m, int n) {
        float f = (float)(m >> 24 & 0xFF) / 255.0f;
        float g = (float)(m >> 16 & 0xFF) / 255.0f;
        float h = (float)(m >> 8 & 0xFF) / 255.0f;
        float o = (float)(m & 0xFF) / 255.0f;
        float p = (float)(n >> 24 & 0xFF) / 255.0f;
        float q = (float)(n >> 16 & 0xFF) / 255.0f;
        float r = (float)(n >> 8 & 0xFF) / 255.0f;
        float s = (float)(n & 0xFF) / 255.0f;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(7, VertexFormats.POSITION_COLOR);
        lv2.vertex(arg, k, j, this.zOffset).color(g, h, o, f).next();
        lv2.vertex(arg, i, j, this.zOffset).color(g, h, o, f).next();
        lv2.vertex(arg, i, l, this.zOffset).color(q, r, s, p).next();
        lv2.vertex(arg, k, l, this.zOffset).color(q, r, s, p).next();
        lv.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public void drawCenteredString(MatrixStack arg, TextRenderer arg2, String string, int i, int j, int k) {
        arg2.drawWithShadow(arg, string, (float)(i - arg2.getWidth(string) / 2), (float)j, k);
    }

    public void drawCenteredText(MatrixStack arg, TextRenderer arg2, Text arg3, int i, int j, int k) {
        arg2.drawWithShadow(arg, arg3, (float)(i - arg2.getWidth(arg3) / 2), (float)j, k);
    }

    public void drawStringWithShadow(MatrixStack arg, TextRenderer arg2, String string, int i, int j, int k) {
        arg2.drawWithShadow(arg, string, (float)i, (float)j, k);
    }

    public void drawTextWithShadow(MatrixStack arg, TextRenderer arg2, Text arg3, int i, int j, int k) {
        arg2.drawWithShadow(arg, arg3, (float)i, (float)j, k);
    }

    public static void drawSprite(MatrixStack arg, int i, int j, int k, int l, int m, Sprite arg2) {
        DrawableHelper.drawTexturedQuad(arg.peek().getModel(), i, i + l, j, j + m, k, arg2.getMinU(), arg2.getMaxU(), arg2.getMinV(), arg2.getMaxV());
    }

    public void drawTexture(MatrixStack arg, int i, int j, int k, int l, int m, int n) {
        DrawableHelper.drawTexture(arg, i, j, this.zOffset, k, l, m, n, 256, 256);
    }

    public static void drawTexture(MatrixStack arg, int i, int j, int k, float f, float g, int l, int m, int n, int o) {
        DrawableHelper.drawTexture(arg, i, i + l, j, j + m, k, l, m, f, g, o, n);
    }

    public static void drawTexture(MatrixStack arg, int i, int j, int k, int l, float f, float g, int m, int n, int o, int p) {
        DrawableHelper.drawTexture(arg, i, i + k, j, j + l, 0, m, n, f, g, o, p);
    }

    public static void drawTexture(MatrixStack arg, int i, int j, float f, float g, int k, int l, int m, int n) {
        DrawableHelper.drawTexture(arg, i, j, k, l, f, g, k, l, m, n);
    }

    private static void drawTexture(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, float f, float g, int p, int q) {
        DrawableHelper.drawTexturedQuad(arg.peek().getModel(), i, j, k, l, m, (f + 0.0f) / (float)p, (f + (float)n) / (float)p, (g + 0.0f) / (float)q, (g + (float)o) / (float)q);
    }

    private static void drawTexturedQuad(Matrix4f arg, int i, int j, int k, int l, int m, float f, float g, float h, float n) {
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        lv.begin(7, VertexFormats.POSITION_TEXTURE);
        lv.vertex(arg, i, l, m).texture(f, n).next();
        lv.vertex(arg, j, l, m).texture(g, n).next();
        lv.vertex(arg, j, k, m).texture(g, h).next();
        lv.vertex(arg, i, k, m).texture(f, h).next();
        lv.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(lv);
    }

    public int getZOffset() {
        return this.zOffset;
    }

    public void setZOffset(int i) {
        this.zOffset = i;
    }
}

