/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class CubeMapRenderer {
    private final Identifier[] faces = new Identifier[6];

    public CubeMapRenderer(Identifier arg) {
        for (int i = 0; i < 6; ++i) {
            this.faces[i] = new Identifier(arg.getNamespace(), arg.getPath() + '_' + i + ".png");
        }
    }

    public void draw(MinecraftClient arg, float f, float g, float h) {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(Matrix4f.viewboxMatrix(85.0, (float)arg.getWindow().getFramebufferWidth() / (float)arg.getWindow().getFramebufferHeight(), 0.05f, 10.0f));
        RenderSystem.matrixMode(5888);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        int i = 2;
        for (int j = 0; j < 4; ++j) {
            RenderSystem.pushMatrix();
            float k = ((float)(j % 2) / 2.0f - 0.5f) / 256.0f;
            float l = ((float)(j / 2) / 2.0f - 0.5f) / 256.0f;
            float m = 0.0f;
            RenderSystem.translatef(k, l, 0.0f);
            RenderSystem.rotatef(f, 1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(g, 0.0f, 1.0f, 0.0f);
            for (int n = 0; n < 6; ++n) {
                arg.getTextureManager().bindTexture(this.faces[n]);
                lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                int o = Math.round(255.0f * h) / (j + 1);
                if (n == 0) {
                    lv2.vertex(-1.0, -1.0, 1.0).texture(0.0f, 0.0f).color(255, 255, 255, o).next();
                    lv2.vertex(-1.0, 1.0, 1.0).texture(0.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, 1.0, 1.0).texture(1.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, -1.0, 1.0).texture(1.0f, 0.0f).color(255, 255, 255, o).next();
                }
                if (n == 1) {
                    lv2.vertex(1.0, -1.0, 1.0).texture(0.0f, 0.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, 1.0, 1.0).texture(0.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, 1.0, -1.0).texture(1.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, -1.0, -1.0).texture(1.0f, 0.0f).color(255, 255, 255, o).next();
                }
                if (n == 2) {
                    lv2.vertex(1.0, -1.0, -1.0).texture(0.0f, 0.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, 1.0, -1.0).texture(0.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(-1.0, 1.0, -1.0).texture(1.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(-1.0, -1.0, -1.0).texture(1.0f, 0.0f).color(255, 255, 255, o).next();
                }
                if (n == 3) {
                    lv2.vertex(-1.0, -1.0, -1.0).texture(0.0f, 0.0f).color(255, 255, 255, o).next();
                    lv2.vertex(-1.0, 1.0, -1.0).texture(0.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(-1.0, 1.0, 1.0).texture(1.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(-1.0, -1.0, 1.0).texture(1.0f, 0.0f).color(255, 255, 255, o).next();
                }
                if (n == 4) {
                    lv2.vertex(-1.0, -1.0, -1.0).texture(0.0f, 0.0f).color(255, 255, 255, o).next();
                    lv2.vertex(-1.0, -1.0, 1.0).texture(0.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, -1.0, 1.0).texture(1.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, -1.0, -1.0).texture(1.0f, 0.0f).color(255, 255, 255, o).next();
                }
                if (n == 5) {
                    lv2.vertex(-1.0, 1.0, 1.0).texture(0.0f, 0.0f).color(255, 255, 255, o).next();
                    lv2.vertex(-1.0, 1.0, -1.0).texture(0.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, 1.0, -1.0).texture(1.0f, 1.0f).color(255, 255, 255, o).next();
                    lv2.vertex(1.0, 1.0, 1.0).texture(1.0f, 0.0f).color(255, 255, 255, o).next();
                }
                lv.draw();
            }
            RenderSystem.popMatrix();
            RenderSystem.colorMask(true, true, true, false);
        }
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.matrixMode(5889);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public CompletableFuture<Void> loadTexturesAsync(TextureManager arg, Executor executor) {
        CompletableFuture[] completableFutures = new CompletableFuture[6];
        for (int i = 0; i < completableFutures.length; ++i) {
            completableFutures[i] = arg.loadTextureAsync(this.faces[i], executor);
        }
        return CompletableFuture.allOf(completableFutures);
    }
}

