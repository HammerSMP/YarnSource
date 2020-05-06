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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends PersistentProjectileEntity>
extends EntityRenderer<T> {
    public ProjectileEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(T arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, ((PersistentProjectileEntity)arg).prevYaw, ((PersistentProjectileEntity)arg).yaw) - 90.0f));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, ((PersistentProjectileEntity)arg).prevPitch, ((PersistentProjectileEntity)arg).pitch)));
        boolean j = false;
        float h = 0.0f;
        float k = 0.5f;
        float l = 0.0f;
        float m = 0.15625f;
        float n = 0.0f;
        float o = 0.15625f;
        float p = 0.15625f;
        float q = 0.3125f;
        float r = 0.05625f;
        float s = (float)((PersistentProjectileEntity)arg).shake - g;
        if (s > 0.0f) {
            float t = -MathHelper.sin(s * 3.0f) * s;
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(t));
        }
        arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        arg2.scale(0.05625f, 0.05625f, 0.05625f);
        arg2.translate(-4.0, 0.0, 0.0);
        VertexConsumer lv = arg3.getBuffer(RenderLayer.getEntityCutout(this.getTexture(arg)));
        MatrixStack.Entry lv2 = arg2.peek();
        Matrix4f lv3 = lv2.getModel();
        Matrix3f lv4 = lv2.getNormal();
        this.method_23153(lv3, lv4, lv, -7, -2, -2, 0.0f, 0.15625f, -1, 0, 0, i);
        this.method_23153(lv3, lv4, lv, -7, -2, 2, 0.15625f, 0.15625f, -1, 0, 0, i);
        this.method_23153(lv3, lv4, lv, -7, 2, 2, 0.15625f, 0.3125f, -1, 0, 0, i);
        this.method_23153(lv3, lv4, lv, -7, 2, -2, 0.0f, 0.3125f, -1, 0, 0, i);
        this.method_23153(lv3, lv4, lv, -7, 2, -2, 0.0f, 0.15625f, 1, 0, 0, i);
        this.method_23153(lv3, lv4, lv, -7, 2, 2, 0.15625f, 0.15625f, 1, 0, 0, i);
        this.method_23153(lv3, lv4, lv, -7, -2, 2, 0.15625f, 0.3125f, 1, 0, 0, i);
        this.method_23153(lv3, lv4, lv, -7, -2, -2, 0.0f, 0.3125f, 1, 0, 0, i);
        for (int u = 0; u < 4; ++u) {
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            this.method_23153(lv3, lv4, lv, -8, -2, 0, 0.0f, 0.0f, 0, 1, 0, i);
            this.method_23153(lv3, lv4, lv, 8, -2, 0, 0.5f, 0.0f, 0, 1, 0, i);
            this.method_23153(lv3, lv4, lv, 8, 2, 0, 0.5f, 0.15625f, 0, 1, 0, i);
            this.method_23153(lv3, lv4, lv, -8, 2, 0, 0.0f, 0.15625f, 0, 1, 0, i);
        }
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    public void method_23153(Matrix4f arg, Matrix3f arg2, VertexConsumer arg3, int i, int j, int k, float f, float g, int l, int m, int n, int o) {
        arg3.vertex(arg, i, j, k).color(255, 255, 255, 255).texture(f, g).overlay(OverlayTexture.DEFAULT_UV).light(o).normal(arg2, l, n, m).next();
    }
}

