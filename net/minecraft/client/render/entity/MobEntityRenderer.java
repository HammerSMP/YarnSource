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
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.LightType;

@Environment(value=EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, M extends EntityModel<T>>
extends LivingEntityRenderer<T, M> {
    public MobEntityRenderer(EntityRenderDispatcher arg, M arg2, float f) {
        super(arg, arg2, f);
    }

    @Override
    protected boolean hasLabel(T arg) {
        return super.hasLabel(arg) && (((LivingEntity)arg).shouldRenderName() || ((Entity)arg).hasCustomName() && arg == this.dispatcher.targetedEntity);
    }

    @Override
    public boolean shouldRender(T arg, Frustum arg2, double d, double e, double f) {
        if (super.shouldRender(arg, arg2, d, e, f)) {
            return true;
        }
        Entity lv = ((MobEntity)arg).getHoldingEntity();
        if (lv != null) {
            return arg2.isVisible(lv.getVisibilityBoundingBox());
        }
        return false;
    }

    @Override
    public void render(T arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        super.render(arg, f, g, arg2, arg3, i);
        Entity lv = ((MobEntity)arg).getHoldingEntity();
        if (lv == null) {
            return;
        }
        this.method_4073(arg, g, arg2, arg3, lv);
    }

    private <E extends Entity> void method_4073(T arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, E arg4) {
        arg2.push();
        double d = MathHelper.lerp(f * 0.5f, arg4.yaw, arg4.prevYaw) * ((float)Math.PI / 180);
        double e = MathHelper.lerp(f * 0.5f, arg4.pitch, arg4.prevPitch) * ((float)Math.PI / 180);
        double g = Math.cos(d);
        double h = Math.sin(d);
        double i = Math.sin(e);
        if (arg4 instanceof AbstractDecorationEntity) {
            g = 0.0;
            h = 0.0;
            i = -1.0;
        }
        double j = Math.cos(e);
        double k = MathHelper.lerp((double)f, arg4.prevX, arg4.getX()) - g * 0.7 - h * 0.5 * j;
        double l = MathHelper.lerp((double)f, arg4.prevY + (double)arg4.getStandingEyeHeight() * 0.7, arg4.getY() + (double)arg4.getStandingEyeHeight() * 0.7) - i * 0.5 - 0.25;
        double m = MathHelper.lerp((double)f, arg4.prevZ, arg4.getZ()) - h * 0.7 + g * 0.5 * j;
        double n = (double)(MathHelper.lerp(f, ((MobEntity)arg).bodyYaw, ((MobEntity)arg).prevBodyYaw) * ((float)Math.PI / 180)) + 1.5707963267948966;
        g = Math.cos(n) * (double)((Entity)arg).getWidth() * 0.4;
        h = Math.sin(n) * (double)((Entity)arg).getWidth() * 0.4;
        double o = MathHelper.lerp((double)f, ((MobEntity)arg).prevX, ((Entity)arg).getX()) + g;
        double p = MathHelper.lerp((double)f, ((MobEntity)arg).prevY, ((Entity)arg).getY());
        double q = MathHelper.lerp((double)f, ((MobEntity)arg).prevZ, ((Entity)arg).getZ()) + h;
        arg2.translate(g, -(1.6 - (double)((Entity)arg).getHeight()) * 0.5, h);
        float r = (float)(k - o);
        float s = (float)(l - p);
        float t = (float)(m - q);
        float u = 0.025f;
        VertexConsumer lv = arg3.getBuffer(RenderLayer.getLeash());
        Matrix4f lv2 = arg2.peek().getModel();
        float v = MathHelper.fastInverseSqrt(r * r + t * t) * 0.025f / 2.0f;
        float w = t * v;
        float x = r * v;
        BlockPos lv3 = new BlockPos(((Entity)arg).getCameraPosVec(f));
        BlockPos lv4 = new BlockPos(arg4.getCameraPosVec(f));
        int y = this.getBlockLight(arg, lv3);
        int z = this.dispatcher.getRenderer(arg4).getBlockLight(arg4, lv4);
        int aa = ((MobEntity)arg).world.getLightLevel(LightType.SKY, lv3);
        int ab = ((MobEntity)arg).world.getLightLevel(LightType.SKY, lv4);
        MobEntityRenderer.method_23186(lv, lv2, r, s, t, y, z, aa, ab, 0.025f, 0.025f, w, x);
        MobEntityRenderer.method_23186(lv, lv2, r, s, t, y, z, aa, ab, 0.025f, 0.0f, w, x);
        arg2.pop();
    }

    public static void method_23186(VertexConsumer arg, Matrix4f arg2, float f, float g, float h, int i, int j, int k, int l, float m, float n, float o, float p) {
        int q = 24;
        for (int r = 0; r < 24; ++r) {
            float s = (float)r / 23.0f;
            int t = (int)MathHelper.lerp(s, i, j);
            int u = (int)MathHelper.lerp(s, k, l);
            int v = LightmapTextureManager.pack(t, u);
            MobEntityRenderer.method_23187(arg, arg2, v, f, g, h, m, n, 24, r, false, o, p);
            MobEntityRenderer.method_23187(arg, arg2, v, f, g, h, m, n, 24, r + 1, true, o, p);
        }
    }

    public static void method_23187(VertexConsumer arg, Matrix4f arg2, int i, float f, float g, float h, float j, float k, int l, int m, boolean bl, float n, float o) {
        float p = 0.5f;
        float q = 0.4f;
        float r = 0.3f;
        if (m % 2 == 0) {
            p *= 0.7f;
            q *= 0.7f;
            r *= 0.7f;
        }
        float s = (float)m / (float)l;
        float t = f * s;
        float u = g * (s * s + s) * 0.5f + ((float)l - (float)m) / ((float)l * 0.75f) + 0.125f;
        float v = h * s;
        if (!bl) {
            arg.vertex(arg2, t + n, u + j - k, v - o).color(p, q, r, 1.0f).light(i).next();
        }
        arg.vertex(arg2, t - n, u + k, v + o).color(p, q, r, 1.0f).light(i).next();
        if (bl) {
            arg.vertex(arg2, t + n, u + j - k, v - o).color(p, q, r, 1.0f).light(i).next();
        }
    }
}

