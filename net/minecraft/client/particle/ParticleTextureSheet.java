/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

@Environment(value=EnvType.CLIENT)
public interface ParticleTextureSheet {
    public static final ParticleTextureSheet TERRAIN_SHEET = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder arg, TextureManager arg2) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            arg2.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            arg.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator arg) {
            arg.draw();
        }

        public String toString() {
            return "TERRAIN_SHEET";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_OPAQUE = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder arg, TextureManager arg2) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            arg2.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            arg.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator arg) {
            arg.draw();
        }

        public String toString() {
            return "PARTICLE_SHEET_OPAQUE";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_TRANSLUCENT = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder arg, TextureManager arg2) {
            RenderSystem.depthMask(true);
            arg2.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.alphaFunc(516, 0.003921569f);
            arg.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator arg) {
            arg.draw();
        }

        public String toString() {
            return "PARTICLE_SHEET_TRANSLUCENT";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_LIT = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder arg, TextureManager arg2) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            arg2.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            arg.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator arg) {
            arg.draw();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT";
        }
    };
    public static final ParticleTextureSheet CUSTOM = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder arg, TextureManager arg2) {
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }

        @Override
        public void draw(Tessellator arg) {
        }

        public String toString() {
            return "CUSTOM";
        }
    };
    public static final ParticleTextureSheet NO_RENDER = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder arg, TextureManager arg2) {
        }

        @Override
        public void draw(Tessellator arg) {
        }

        public String toString() {
            return "NO_RENDER";
        }
    };

    public void begin(BufferBuilder var1, TextureManager var2);

    public void draw(Tessellator var1);
}

