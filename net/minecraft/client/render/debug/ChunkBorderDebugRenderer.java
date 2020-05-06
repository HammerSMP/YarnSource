/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class ChunkBorderDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public ChunkBorderDebugRenderer(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        RenderSystem.enableDepthTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        Entity lv = this.client.gameRenderer.getCamera().getFocusedEntity();
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        double g = 0.0 - e;
        double h = 256.0 - e;
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        double i = (double)(lv.chunkX << 4) - d;
        double j = (double)(lv.chunkZ << 4) - f;
        RenderSystem.lineWidth(1.0f);
        lv3.begin(3, VertexFormats.POSITION_COLOR);
        for (int k = -16; k <= 32; k += 16) {
            for (int l = -16; l <= 32; l += 16) {
                lv3.vertex(i + (double)k, g, j + (double)l).color(1.0f, 0.0f, 0.0f, 0.0f).next();
                lv3.vertex(i + (double)k, g, j + (double)l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                lv3.vertex(i + (double)k, h, j + (double)l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                lv3.vertex(i + (double)k, h, j + (double)l).color(1.0f, 0.0f, 0.0f, 0.0f).next();
            }
        }
        for (int m = 2; m < 16; m += 2) {
            lv3.vertex(i + (double)m, g, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            lv3.vertex(i + (double)m, g, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i + (double)m, h, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i + (double)m, h, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            lv3.vertex(i + (double)m, g, j + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            lv3.vertex(i + (double)m, g, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i + (double)m, h, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i + (double)m, h, j + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (int n = 2; n < 16; n += 2) {
            lv3.vertex(i, g, j + (double)n).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            lv3.vertex(i, g, j + (double)n).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i, h, j + (double)n).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i, h, j + (double)n).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            lv3.vertex(i + 16.0, g, j + (double)n).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            lv3.vertex(i + 16.0, g, j + (double)n).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i + 16.0, h, j + (double)n).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i + 16.0, h, j + (double)n).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (int o = 0; o <= 256; o += 2) {
            double p = (double)o - e;
            lv3.vertex(i, p, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            lv3.vertex(i, p, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i, p, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i + 16.0, p, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i + 16.0, p, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i, p, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            lv3.vertex(i, p, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        lv2.draw();
        RenderSystem.lineWidth(2.0f);
        lv3.begin(3, VertexFormats.POSITION_COLOR);
        for (int q = 0; q <= 16; q += 16) {
            for (int r = 0; r <= 16; r += 16) {
                lv3.vertex(i + (double)q, g, j + (double)r).color(0.25f, 0.25f, 1.0f, 0.0f).next();
                lv3.vertex(i + (double)q, g, j + (double)r).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                lv3.vertex(i + (double)q, h, j + (double)r).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                lv3.vertex(i + (double)q, h, j + (double)r).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            }
        }
        for (int s = 0; s <= 256; s += 16) {
            double t = (double)s - e;
            lv3.vertex(i, t, j).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            lv3.vertex(i, t, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            lv3.vertex(i, t, j + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            lv3.vertex(i + 16.0, t, j + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            lv3.vertex(i + 16.0, t, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            lv3.vertex(i, t, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            lv3.vertex(i, t, j).color(0.25f, 0.25f, 1.0f, 0.0f).next();
        }
        lv2.draw();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
    }
}

