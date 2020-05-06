/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderer
extends BlockEntityRenderer<BeaconBlockEntity> {
    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

    public BeaconBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(BeaconBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        long l = arg.getWorld().getTime();
        List<BeaconBlockEntity.BeamSegment> list = arg.getBeamSegments();
        int k = 0;
        for (int m = 0; m < list.size(); ++m) {
            BeaconBlockEntity.BeamSegment lv = list.get(m);
            BeaconBlockEntityRenderer.render(arg2, arg3, f, l, k, m == list.size() - 1 ? 1024 : lv.getHeight(), lv.getColor());
            k += lv.getHeight();
        }
    }

    private static void render(MatrixStack arg, VertexConsumerProvider arg2, float f, long l, int i, int j, float[] fs) {
        BeaconBlockEntityRenderer.renderLightBeam(arg, arg2, BEAM_TEXTURE, f, 1.0f, l, i, j, fs, 0.2f, 0.25f);
    }

    public static void renderLightBeam(MatrixStack arg, VertexConsumerProvider arg2, Identifier arg3, float f, float g, long l, int i, int j, float[] fs, float h, float k) {
        int m = i + j;
        arg.push();
        arg.translate(0.5, 0.0, 0.5);
        float n = (float)Math.floorMod(l, 40L) + f;
        float o = j < 0 ? n : -n;
        float p = MathHelper.fractionalPart(o * 0.2f - (float)MathHelper.floor(o * 0.1f));
        float q = fs[0];
        float r = fs[1];
        float s = fs[2];
        arg.push();
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n * 2.25f - 45.0f));
        float t = 0.0f;
        float u = h;
        float v = h;
        float w = 0.0f;
        float x = -h;
        float y = 0.0f;
        float z = 0.0f;
        float aa = -h;
        float ab = 0.0f;
        float ac = 1.0f;
        float ad = -1.0f + p;
        float ae = (float)j * g * (0.5f / h) + ad;
        BeaconBlockEntityRenderer.method_22741(arg, arg2.getBuffer(RenderLayer.getBeaconBeam(arg3, false)), q, r, s, 1.0f, i, m, 0.0f, u, v, 0.0f, x, 0.0f, 0.0f, aa, 0.0f, 1.0f, ae, ad);
        arg.pop();
        float af = -k;
        float ag = -k;
        float ah = k;
        float ai = -k;
        float aj = -k;
        float ak = k;
        float al = k;
        float am = k;
        float an = 0.0f;
        float ao = 1.0f;
        float ap = -1.0f + p;
        float aq = (float)j * g + ap;
        BeaconBlockEntityRenderer.method_22741(arg, arg2.getBuffer(RenderLayer.getBeaconBeam(arg3, true)), q, r, s, 0.125f, i, m, af, ag, ah, ai, aj, ak, al, am, 0.0f, 1.0f, aq, ap);
        arg.pop();
    }

    private static void method_22741(MatrixStack arg, VertexConsumer arg2, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s, float t, float u, float v, float w) {
        MatrixStack.Entry lv = arg.peek();
        Matrix4f lv2 = lv.getModel();
        Matrix3f lv3 = lv.getNormal();
        BeaconBlockEntityRenderer.method_22740(lv2, lv3, arg2, f, g, h, i, j, k, l, m, n, o, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(lv2, lv3, arg2, f, g, h, i, j, k, r, s, p, q, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(lv2, lv3, arg2, f, g, h, i, j, k, n, o, r, s, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(lv2, lv3, arg2, f, g, h, i, j, k, p, q, l, m, t, u, v, w);
    }

    private static void method_22740(Matrix4f arg, Matrix3f arg2, VertexConsumer arg3, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s) {
        BeaconBlockEntityRenderer.method_23076(arg, arg2, arg3, f, g, h, i, k, l, m, q, r);
        BeaconBlockEntityRenderer.method_23076(arg, arg2, arg3, f, g, h, i, j, l, m, q, s);
        BeaconBlockEntityRenderer.method_23076(arg, arg2, arg3, f, g, h, i, j, n, o, p, s);
        BeaconBlockEntityRenderer.method_23076(arg, arg2, arg3, f, g, h, i, k, n, o, p, r);
    }

    private static void method_23076(Matrix4f arg, Matrix3f arg2, VertexConsumer arg3, float f, float g, float h, float i, int j, float k, float l, float m, float n) {
        arg3.vertex(arg, k, j, l).color(f, g, h, i).texture(m, n).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(arg2, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public boolean rendersOutsideBoundingBox(BeaconBlockEntity arg) {
        return true;
    }
}

