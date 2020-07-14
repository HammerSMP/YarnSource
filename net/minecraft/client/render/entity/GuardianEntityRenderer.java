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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class GuardianEntityRenderer
extends MobEntityRenderer<GuardianEntity, GuardianEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/guardian.png");
    private static final Identifier EXPLOSION_BEAM_TEXTURE = new Identifier("textures/entity/guardian_beam.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

    public GuardianEntityRenderer(EntityRenderDispatcher arg) {
        this(arg, 0.5f);
    }

    protected GuardianEntityRenderer(EntityRenderDispatcher dispatcher, float f) {
        super(dispatcher, new GuardianEntityModel(), f);
    }

    @Override
    public boolean shouldRender(GuardianEntity arg, Frustum arg2, double d, double e, double f) {
        LivingEntity lv;
        if (super.shouldRender(arg, arg2, d, e, f)) {
            return true;
        }
        if (arg.hasBeamTarget() && (lv = arg.getBeamTarget()) != null) {
            Vec3d lv2 = this.fromLerpedPosition(lv, (double)lv.getHeight() * 0.5, 1.0f);
            Vec3d lv3 = this.fromLerpedPosition(arg, arg.getStandingEyeHeight(), 1.0f);
            return arg2.isVisible(new Box(lv3.x, lv3.y, lv3.z, lv2.x, lv2.y, lv2.z));
        }
        return false;
    }

    private Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double e = MathHelper.lerp((double)delta, entity.lastRenderX, entity.getX());
        double g = MathHelper.lerp((double)delta, entity.lastRenderY, entity.getY()) + yOffset;
        double h = MathHelper.lerp((double)delta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(e, g, h);
    }

    @Override
    public void render(GuardianEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        super.render(arg, f, g, arg2, arg3, i);
        LivingEntity lv = arg.getBeamTarget();
        if (lv != null) {
            float h = arg.getBeamProgress(g);
            float j = (float)arg.world.getTime() + g;
            float k = j * 0.5f % 1.0f;
            float l = arg.getStandingEyeHeight();
            arg2.push();
            arg2.translate(0.0, l, 0.0);
            Vec3d lv2 = this.fromLerpedPosition(lv, (double)lv.getHeight() * 0.5, g);
            Vec3d lv3 = this.fromLerpedPosition(arg, l, g);
            Vec3d lv4 = lv2.subtract(lv3);
            float m = (float)(lv4.length() + 1.0);
            lv4 = lv4.normalize();
            float n = (float)Math.acos(lv4.y);
            float o = (float)Math.atan2(lv4.z, lv4.x);
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((1.5707964f - o) * 57.295776f));
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n * 57.295776f));
            boolean p = true;
            float q = j * 0.05f * -1.5f;
            float r = h * h;
            int s = 64 + (int)(r * 191.0f);
            int t = 32 + (int)(r * 191.0f);
            int u = 128 - (int)(r * 64.0f);
            float v = 0.2f;
            float w = 0.282f;
            float x = MathHelper.cos(q + 2.3561945f) * 0.282f;
            float y = MathHelper.sin(q + 2.3561945f) * 0.282f;
            float z = MathHelper.cos(q + 0.7853982f) * 0.282f;
            float aa = MathHelper.sin(q + 0.7853982f) * 0.282f;
            float ab = MathHelper.cos(q + 3.926991f) * 0.282f;
            float ac = MathHelper.sin(q + 3.926991f) * 0.282f;
            float ad = MathHelper.cos(q + 5.4977875f) * 0.282f;
            float ae = MathHelper.sin(q + 5.4977875f) * 0.282f;
            float af = MathHelper.cos(q + (float)Math.PI) * 0.2f;
            float ag = MathHelper.sin(q + (float)Math.PI) * 0.2f;
            float ah = MathHelper.cos(q + 0.0f) * 0.2f;
            float ai = MathHelper.sin(q + 0.0f) * 0.2f;
            float aj = MathHelper.cos(q + 1.5707964f) * 0.2f;
            float ak = MathHelper.sin(q + 1.5707964f) * 0.2f;
            float al = MathHelper.cos(q + 4.712389f) * 0.2f;
            float am = MathHelper.sin(q + 4.712389f) * 0.2f;
            float an = m;
            float ao = 0.0f;
            float ap = 0.4999f;
            float aq = -1.0f + k;
            float ar = m * 2.5f + aq;
            VertexConsumer lv5 = arg3.getBuffer(LAYER);
            MatrixStack.Entry lv6 = arg2.peek();
            Matrix4f lv7 = lv6.getModel();
            Matrix3f lv8 = lv6.getNormal();
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, af, an, ag, s, t, u, 0.4999f, ar);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, af, 0.0f, ag, s, t, u, 0.4999f, aq);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, ah, 0.0f, ai, s, t, u, 0.0f, aq);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, ah, an, ai, s, t, u, 0.0f, ar);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, aj, an, ak, s, t, u, 0.4999f, ar);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, aj, 0.0f, ak, s, t, u, 0.4999f, aq);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, al, 0.0f, am, s, t, u, 0.0f, aq);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, al, an, am, s, t, u, 0.0f, ar);
            float as = 0.0f;
            if (arg.age % 2 == 0) {
                as = 0.5f;
            }
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, x, an, y, s, t, u, 0.5f, as + 0.5f);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, z, an, aa, s, t, u, 1.0f, as + 0.5f);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, ad, an, ae, s, t, u, 1.0f, as);
            GuardianEntityRenderer.method_23173(lv5, lv7, lv8, ab, an, ac, s, t, u, 0.5f, as);
            arg2.pop();
        }
    }

    private static void method_23173(VertexConsumer arg, Matrix4f arg2, Matrix3f arg3, float f, float g, float h, int i, int j, int k, float l, float m) {
        arg.vertex(arg2, f, g, h).color(i, j, k, 255).texture(l, m).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(arg3, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(GuardianEntity arg) {
        return TEXTURE;
    }
}

