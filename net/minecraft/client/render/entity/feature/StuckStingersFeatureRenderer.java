/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class StuckStingersFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends StuckObjectsFeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/bee/bee_stinger.png");

    public StuckStingersFeatureRenderer(LivingEntityRenderer<T, M> arg) {
        super(arg);
    }

    @Override
    protected int getObjectCount(T arg) {
        return ((LivingEntity)arg).getStingerCount();
    }

    @Override
    protected void renderObject(MatrixStack arg, VertexConsumerProvider arg2, int i, Entity arg3, float f, float g, float h, float j) {
        float k = MathHelper.sqrt(f * f + h * h);
        float l = (float)(Math.atan2(f, h) * 57.2957763671875);
        float m = (float)(Math.atan2(g, k) * 57.2957763671875);
        arg.translate(0.0, 0.0, 0.0);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(l - 90.0f));
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(m));
        float n = 0.0f;
        float o = 0.125f;
        float p = 0.0f;
        float q = 0.0625f;
        float r = 0.03125f;
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        arg.scale(0.03125f, 0.03125f, 0.03125f);
        arg.translate(2.5, 0.0, 0.0);
        VertexConsumer lv = arg2.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        for (int s = 0; s < 4; ++s) {
            arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            MatrixStack.Entry lv2 = arg.peek();
            Matrix4f lv3 = lv2.getModel();
            Matrix3f lv4 = lv2.getNormal();
            StuckStingersFeatureRenderer.produceVertex(lv, lv3, lv4, -4.5f, -1, 0.0f, 0.0f, i);
            StuckStingersFeatureRenderer.produceVertex(lv, lv3, lv4, 4.5f, -1, 0.125f, 0.0f, i);
            StuckStingersFeatureRenderer.produceVertex(lv, lv3, lv4, 4.5f, 1, 0.125f, 0.0625f, i);
            StuckStingersFeatureRenderer.produceVertex(lv, lv3, lv4, -4.5f, 1, 0.0f, 0.0625f, i);
        }
    }

    private static void produceVertex(VertexConsumer arg, Matrix4f arg2, Matrix3f arg3, float f, int i, float g, float h, int j) {
        arg.vertex(arg2, f, i, 0.0f).color(255, 255, 255, 255).texture(g, h).overlay(OverlayTexture.DEFAULT_UV).light(j).normal(arg3, 0.0f, 1.0f, 0.0f).next();
    }
}

