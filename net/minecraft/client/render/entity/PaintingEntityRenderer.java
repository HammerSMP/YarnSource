/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class PaintingEntityRenderer
extends EntityRenderer<PaintingEntity> {
    public PaintingEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(PaintingEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - f));
        PaintingMotive lv = arg.motive;
        float h = 0.0625f;
        arg2.scale(0.0625f, 0.0625f, 0.0625f);
        VertexConsumer lv2 = arg3.getBuffer(RenderLayer.getEntitySolid(this.getTexture(arg)));
        PaintingManager lv3 = MinecraftClient.getInstance().getPaintingManager();
        this.method_4074(arg2, lv2, arg, lv.getWidth(), lv.getHeight(), lv3.getPaintingSprite(lv), lv3.getBackSprite());
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(PaintingEntity arg) {
        return MinecraftClient.getInstance().getPaintingManager().getBackSprite().getAtlas().getId();
    }

    private void method_4074(MatrixStack arg, VertexConsumer arg2, PaintingEntity arg3, int i, int j, Sprite arg4, Sprite arg5) {
        MatrixStack.Entry lv = arg.peek();
        Matrix4f lv2 = lv.getModel();
        Matrix3f lv3 = lv.getNormal();
        float f = (float)(-i) / 2.0f;
        float g = (float)(-j) / 2.0f;
        float h = 0.5f;
        float k = arg5.getMinU();
        float l = arg5.getMaxU();
        float m = arg5.getMinV();
        float n = arg5.getMaxV();
        float o = arg5.getMinU();
        float p = arg5.getMaxU();
        float q = arg5.getMinV();
        float r = arg5.getFrameV(1.0);
        float s = arg5.getMinU();
        float t = arg5.getFrameU(1.0);
        float u = arg5.getMinV();
        float v = arg5.getMaxV();
        int w = i / 16;
        int x = j / 16;
        double d = 16.0 / (double)w;
        double e = 16.0 / (double)x;
        for (int y = 0; y < w; ++y) {
            for (int z = 0; z < x; ++z) {
                float aa = f + (float)((y + 1) * 16);
                float ab = f + (float)(y * 16);
                float ac = g + (float)((z + 1) * 16);
                float ad = g + (float)(z * 16);
                int ae = MathHelper.floor(arg3.getX());
                int af = MathHelper.floor(arg3.getY() + (double)((ac + ad) / 2.0f / 16.0f));
                int ag = MathHelper.floor(arg3.getZ());
                Direction lv4 = arg3.getHorizontalFacing();
                if (lv4 == Direction.NORTH) {
                    ae = MathHelper.floor(arg3.getX() + (double)((aa + ab) / 2.0f / 16.0f));
                }
                if (lv4 == Direction.WEST) {
                    ag = MathHelper.floor(arg3.getZ() - (double)((aa + ab) / 2.0f / 16.0f));
                }
                if (lv4 == Direction.SOUTH) {
                    ae = MathHelper.floor(arg3.getX() - (double)((aa + ab) / 2.0f / 16.0f));
                }
                if (lv4 == Direction.EAST) {
                    ag = MathHelper.floor(arg3.getZ() + (double)((aa + ab) / 2.0f / 16.0f));
                }
                int ah = WorldRenderer.getLightmapCoordinates(arg3.world, new BlockPos(ae, af, ag));
                float ai = arg4.getFrameU(d * (double)(w - y));
                float aj = arg4.getFrameU(d * (double)(w - (y + 1)));
                float ak = arg4.getFrameV(e * (double)(x - z));
                float al = arg4.getFrameV(e * (double)(x - (z + 1)));
                this.method_23188(lv2, lv3, arg2, aa, ad, aj, ak, -0.5f, 0, 0, -1, ah);
                this.method_23188(lv2, lv3, arg2, ab, ad, ai, ak, -0.5f, 0, 0, -1, ah);
                this.method_23188(lv2, lv3, arg2, ab, ac, ai, al, -0.5f, 0, 0, -1, ah);
                this.method_23188(lv2, lv3, arg2, aa, ac, aj, al, -0.5f, 0, 0, -1, ah);
                this.method_23188(lv2, lv3, arg2, aa, ac, k, m, 0.5f, 0, 0, 1, ah);
                this.method_23188(lv2, lv3, arg2, ab, ac, l, m, 0.5f, 0, 0, 1, ah);
                this.method_23188(lv2, lv3, arg2, ab, ad, l, n, 0.5f, 0, 0, 1, ah);
                this.method_23188(lv2, lv3, arg2, aa, ad, k, n, 0.5f, 0, 0, 1, ah);
                this.method_23188(lv2, lv3, arg2, aa, ac, o, q, -0.5f, 0, 1, 0, ah);
                this.method_23188(lv2, lv3, arg2, ab, ac, p, q, -0.5f, 0, 1, 0, ah);
                this.method_23188(lv2, lv3, arg2, ab, ac, p, r, 0.5f, 0, 1, 0, ah);
                this.method_23188(lv2, lv3, arg2, aa, ac, o, r, 0.5f, 0, 1, 0, ah);
                this.method_23188(lv2, lv3, arg2, aa, ad, o, q, 0.5f, 0, -1, 0, ah);
                this.method_23188(lv2, lv3, arg2, ab, ad, p, q, 0.5f, 0, -1, 0, ah);
                this.method_23188(lv2, lv3, arg2, ab, ad, p, r, -0.5f, 0, -1, 0, ah);
                this.method_23188(lv2, lv3, arg2, aa, ad, o, r, -0.5f, 0, -1, 0, ah);
                this.method_23188(lv2, lv3, arg2, aa, ac, t, u, 0.5f, -1, 0, 0, ah);
                this.method_23188(lv2, lv3, arg2, aa, ad, t, v, 0.5f, -1, 0, 0, ah);
                this.method_23188(lv2, lv3, arg2, aa, ad, s, v, -0.5f, -1, 0, 0, ah);
                this.method_23188(lv2, lv3, arg2, aa, ac, s, u, -0.5f, -1, 0, 0, ah);
                this.method_23188(lv2, lv3, arg2, ab, ac, t, u, -0.5f, 1, 0, 0, ah);
                this.method_23188(lv2, lv3, arg2, ab, ad, t, v, -0.5f, 1, 0, 0, ah);
                this.method_23188(lv2, lv3, arg2, ab, ad, s, v, 0.5f, 1, 0, 0, ah);
                this.method_23188(lv2, lv3, arg2, ab, ac, s, u, 0.5f, 1, 0, 0, ah);
            }
        }
    }

    private void method_23188(Matrix4f arg, Matrix3f arg2, VertexConsumer arg3, float f, float g, float h, float i, float j, int k, int l, int m, int n) {
        arg3.vertex(arg, f, g, j).color(255, 255, 255, 255).texture(h, i).overlay(OverlayTexture.DEFAULT_UV).light(n).normal(arg2, k, l, m).next();
    }
}

