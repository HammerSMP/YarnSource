/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class InGameOverlayRenderer {
    private static final Identifier UNDERWATER_TEX = new Identifier("textures/misc/underwater.png");

    public static void renderOverlays(MinecraftClient arg, MatrixStack arg2) {
        BlockState lv2;
        RenderSystem.disableAlphaTest();
        ClientPlayerEntity lv = arg.player;
        if (!lv.noClip && (lv2 = InGameOverlayRenderer.getInWallBlockState(lv)) != null) {
            InGameOverlayRenderer.renderInWallOverlay(arg, arg.getBlockRenderManager().getModels().getSprite(lv2), arg2);
        }
        if (!arg.player.isSpectator()) {
            if (arg.player.isSubmergedIn(FluidTags.WATER)) {
                InGameOverlayRenderer.renderUnderwaterOverlay(arg, arg2);
            }
            if (arg.player.isOnFire()) {
                InGameOverlayRenderer.renderFireOverlay(arg, arg2);
            }
        }
        RenderSystem.enableAlphaTest();
    }

    @Nullable
    private static BlockState getInWallBlockState(PlayerEntity arg) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int i = 0; i < 8; ++i) {
            double d = arg.getX() + (double)(((float)((i >> 0) % 2) - 0.5f) * arg.getWidth() * 0.8f);
            double e = arg.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
            double f = arg.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * arg.getWidth() * 0.8f);
            lv.set(d, e, f);
            BlockState lv2 = arg.world.getBlockState(lv);
            if (lv2.getRenderType() == BlockRenderType.INVISIBLE || !lv2.shouldBlockVision(arg.world, lv)) continue;
            return lv2;
        }
        return null;
    }

    private static void renderInWallOverlay(MinecraftClient arg, Sprite arg2, MatrixStack arg3) {
        arg.getTextureManager().bindTexture(arg2.getAtlas().getId());
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        float f = 0.1f;
        float g = -1.0f;
        float h = 1.0f;
        float i = -1.0f;
        float j = 1.0f;
        float k = -0.5f;
        float l = arg2.getMinU();
        float m = arg2.getMaxU();
        float n = arg2.getMinV();
        float o = arg2.getMaxV();
        Matrix4f lv2 = arg3.peek().getModel();
        lv.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        lv.vertex(lv2, -1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, o).next();
        lv.vertex(lv2, 1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, o).next();
        lv.vertex(lv2, 1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, n).next();
        lv.vertex(lv2, -1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, n).next();
        lv.end();
        BufferRenderer.draw(lv);
    }

    private static void renderUnderwaterOverlay(MinecraftClient arg, MatrixStack arg2) {
        arg.getTextureManager().bindTexture(UNDERWATER_TEX);
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        float f = arg.player.getBrightnessAtEyes();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float g = 4.0f;
        float h = -1.0f;
        float i = 1.0f;
        float j = -1.0f;
        float k = 1.0f;
        float l = -0.5f;
        float m = -arg.player.yaw / 64.0f;
        float n = arg.player.pitch / 64.0f;
        Matrix4f lv2 = arg2.peek().getModel();
        lv.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        lv.vertex(lv2, -1.0f, -1.0f, -0.5f).color(f, f, f, 0.1f).texture(4.0f + m, 4.0f + n).next();
        lv.vertex(lv2, 1.0f, -1.0f, -0.5f).color(f, f, f, 0.1f).texture(0.0f + m, 4.0f + n).next();
        lv.vertex(lv2, 1.0f, 1.0f, -0.5f).color(f, f, f, 0.1f).texture(0.0f + m, 0.0f + n).next();
        lv.vertex(lv2, -1.0f, 1.0f, -0.5f).color(f, f, f, 0.1f).texture(4.0f + m, 0.0f + n).next();
        lv.end();
        BufferRenderer.draw(lv);
        RenderSystem.disableBlend();
    }

    private static void renderFireOverlay(MinecraftClient arg, MatrixStack arg2) {
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Sprite lv2 = ModelLoader.FIRE_1.getSprite();
        arg.getTextureManager().bindTexture(lv2.getAtlas().getId());
        float f = lv2.getMinU();
        float g = lv2.getMaxU();
        float h = (f + g) / 2.0f;
        float i = lv2.getMinV();
        float j = lv2.getMaxV();
        float k = (i + j) / 2.0f;
        float l = lv2.getAnimationFrameDelta();
        float m = MathHelper.lerp(l, f, h);
        float n = MathHelper.lerp(l, g, h);
        float o = MathHelper.lerp(l, i, k);
        float p = MathHelper.lerp(l, j, k);
        float q = 1.0f;
        for (int r = 0; r < 2; ++r) {
            arg2.push();
            float s = -0.5f;
            float t = 0.5f;
            float u = -0.5f;
            float v = 0.5f;
            float w = -0.5f;
            arg2.translate((float)(-(r * 2 - 1)) * 0.24f, -0.3f, 0.0);
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)(r * 2 - 1) * 10.0f));
            Matrix4f lv3 = arg2.peek().getModel();
            lv.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
            lv.vertex(lv3, -0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(n, p).next();
            lv.vertex(lv3, 0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(m, p).next();
            lv.vertex(lv3, 0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(m, o).next();
            lv.vertex(lv3, -0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(n, o).next();
            lv.end();
            BufferRenderer.draw(lv);
            arg2.pop();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
}

