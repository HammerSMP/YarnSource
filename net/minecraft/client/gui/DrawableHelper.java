/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public abstract class DrawableHelper {
    public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/options_background.png");
    public static final Identifier STATS_ICON_TEXTURE = new Identifier("textures/gui/container/stats_icons.png");
    public static final Identifier GUI_ICONS_TEXTURE = new Identifier("textures/gui/icons.png");
    private int zOffset;

    protected void drawHorizontalLine(MatrixStack matrices, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int m = x1;
            x1 = x2;
            x2 = m;
        }
        DrawableHelper.fill(matrices, x1, y, x2 + 1, y + 1, color);
    }

    protected void drawVerticalLine(MatrixStack matrices, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int m = y1;
            y1 = y2;
            y2 = m;
        }
        DrawableHelper.fill(matrices, x, y1 + 1, x + 1, y2, color);
    }

    public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        DrawableHelper.fill(matrices.peek().getModel(), x1, y1, x2, y2, color);
    }

    private static void fill(Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int n = x1;
            x1 = x2;
            x2 = n;
        }
        if (y1 < y2) {
            int o = y1;
            y1 = y2;
            y2 = o;
        }
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float g = (float)(color >> 16 & 0xFF) / 255.0f;
        float h = (float)(color >> 8 & 0xFF) / 255.0f;
        float p = (float)(color & 0xFF) / 255.0f;
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        lv.begin(7, VertexFormats.POSITION_COLOR);
        lv.vertex(matrix, x1, y2, 0.0f).color(g, h, p, f).next();
        lv.vertex(matrix, x2, y2, 0.0f).color(g, h, p, f).next();
        lv.vertex(matrix, x2, y1, 0.0f).color(g, h, p, f).next();
        lv.vertex(matrix, x1, y1, 0.0f).color(g, h, p, f).next();
        lv.end();
        BufferRenderer.draw(lv);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void fillGradient(MatrixStack matrices, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(7, VertexFormats.POSITION_COLOR);
        DrawableHelper.fillGradient(matrices.peek().getModel(), lv2, xStart, yStart, xEnd, yEnd, this.zOffset, colorStart, colorEnd);
        lv.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    protected static void fillGradient(Matrix4f arg, BufferBuilder arg2, int xStart, int yStart, int xEnd, int yEnd, int m, int n, int o) {
        float f = (float)(n >> 24 & 0xFF) / 255.0f;
        float g = (float)(n >> 16 & 0xFF) / 255.0f;
        float h = (float)(n >> 8 & 0xFF) / 255.0f;
        float p = (float)(n & 0xFF) / 255.0f;
        float q = (float)(o >> 24 & 0xFF) / 255.0f;
        float r = (float)(o >> 16 & 0xFF) / 255.0f;
        float s = (float)(o >> 8 & 0xFF) / 255.0f;
        float t = (float)(o & 0xFF) / 255.0f;
        arg2.vertex(arg, xEnd, yStart, m).color(g, h, p, f).next();
        arg2.vertex(arg, xStart, yStart, m).color(g, h, p, f).next();
        arg2.vertex(arg, xStart, yEnd, m).color(r, s, t, q).next();
        arg2.vertex(arg, xEnd, yEnd, m).color(r, s, t, q).next();
    }

    public void drawCenteredString(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int color) {
        textRenderer.drawWithShadow(matrices, text, (float)(x - textRenderer.getWidth(text) / 2), (float)y, color);
    }

    public void drawCenteredText(MatrixStack matrices, TextRenderer textRenderer, StringRenderable arg3, int x, int y, int color) {
        textRenderer.drawWithShadow(matrices, arg3, (float)(x - textRenderer.getWidth(arg3) / 2), (float)y, color);
    }

    public void drawStringWithShadow(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int color) {
        textRenderer.drawWithShadow(matrices, text, (float)x, (float)y, color);
    }

    public void drawTextWithShadow(MatrixStack matrices, TextRenderer textRenderer, StringRenderable arg3, int x, int y, int color) {
        textRenderer.drawWithShadow(matrices, arg3, (float)x, (float)y, color);
    }

    public void method_29343(int i, int j, BiConsumer<Integer, Integer> biConsumer) {
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        biConsumer.accept(i + 1, j);
        biConsumer.accept(i - 1, j);
        biConsumer.accept(i, j + 1);
        biConsumer.accept(i, j - 1);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        biConsumer.accept(i, j);
    }

    public static void drawSprite(MatrixStack matrices, int x, int y, int z, int width, int height, Sprite sprite) {
        DrawableHelper.drawTexturedQuad(matrices.peek().getModel(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        DrawableHelper.drawTexture(matrices, x, y, this.zOffset, u, v, width, height, 256, 256);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureHeight, int textureWidth) {
        DrawableHelper.drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        DrawableHelper.drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    private static void drawTexture(MatrixStack matrices, int x0, int y0, int x1, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight) {
        DrawableHelper.drawTexturedQuad(matrices.peek().getModel(), x0, y0, x1, y1, z, (u + 0.0f) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0f) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
    }

    private static void drawTexturedQuad(Matrix4f matrices, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        lv.begin(7, VertexFormats.POSITION_TEXTURE);
        lv.vertex(matrices, x0, y1, z).texture(u0, v1).next();
        lv.vertex(matrices, x1, y1, z).texture(u1, v1).next();
        lv.vertex(matrices, x1, y0, z).texture(u1, v0).next();
        lv.vertex(matrices, x0, y0, z).texture(u0, v0).next();
        lv.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(lv);
    }

    public int getZOffset() {
        return this.zOffset;
    }

    public void setZOffset(int zOffset) {
        this.zOffset = zOffset;
    }
}

