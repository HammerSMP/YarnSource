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
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class ExperienceOrbEntityRenderer
extends EntityRenderer<ExperienceOrbEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/experience_orb.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityTranslucent(TEXTURE);

    public ExperienceOrbEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    @Override
    protected int getBlockLight(ExperienceOrbEntity arg, float f) {
        return MathHelper.clamp(super.getBlockLight(arg, f) + 7, 0, 15);
    }

    @Override
    public void render(ExperienceOrbEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        int j = arg.getOrbSize();
        float h = (float)(j % 4 * 16 + 0) / 64.0f;
        float k = (float)(j % 4 * 16 + 16) / 64.0f;
        float l = (float)(j / 4 * 16 + 0) / 64.0f;
        float m = (float)(j / 4 * 16 + 16) / 64.0f;
        float n = 1.0f;
        float o = 0.5f;
        float p = 0.25f;
        float q = 255.0f;
        float r = ((float)arg.renderTicks + g) / 2.0f;
        int s = (int)((MathHelper.sin(r + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int t = 255;
        int u = (int)((MathHelper.sin(r + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        arg2.translate(0.0, 0.1f, 0.0);
        arg2.multiply(this.dispatcher.getRotation());
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        float v = 0.3f;
        arg2.scale(0.3f, 0.3f, 0.3f);
        VertexConsumer lv = arg3.getBuffer(LAYER);
        MatrixStack.Entry lv2 = arg2.peek();
        Matrix4f lv3 = lv2.getModel();
        Matrix3f lv4 = lv2.getNormal();
        ExperienceOrbEntityRenderer.method_23171(lv, lv3, lv4, -0.5f, -0.25f, s, 255, u, h, m, i);
        ExperienceOrbEntityRenderer.method_23171(lv, lv3, lv4, 0.5f, -0.25f, s, 255, u, k, m, i);
        ExperienceOrbEntityRenderer.method_23171(lv, lv3, lv4, 0.5f, 0.75f, s, 255, u, k, l, i);
        ExperienceOrbEntityRenderer.method_23171(lv, lv3, lv4, -0.5f, 0.75f, s, 255, u, h, l, i);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    private static void method_23171(VertexConsumer arg, Matrix4f arg2, Matrix3f arg3, float f, float g, int i, int j, int k, float h, float l, int m) {
        arg.vertex(arg2, f, g, 0.0f).color(i, j, k, 128).texture(h, l).overlay(OverlayTexture.DEFAULT_UV).light(m).normal(arg3, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(ExperienceOrbEntity arg) {
        return TEXTURE;
    }
}

