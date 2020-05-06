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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class FeatureRenderer<T extends Entity, M extends EntityModel<T>> {
    private final FeatureRendererContext<T, M> context;

    public FeatureRenderer(FeatureRendererContext<T, M> arg) {
        this.context = arg;
    }

    protected static <T extends LivingEntity> void render(EntityModel<T> arg, EntityModel<T> arg2, Identifier arg3, MatrixStack arg4, VertexConsumerProvider arg5, int i, T arg6, float f, float g, float h, float j, float k, float l, float m, float n, float o) {
        if (!arg6.isInvisible()) {
            arg.copyStateTo(arg2);
            arg2.animateModel(arg6, f, g, l);
            arg2.setAngles(arg6, f, g, h, j, k);
            FeatureRenderer.renderModel(arg2, arg3, arg4, arg5, i, arg6, m, n, o);
        }
    }

    protected static <T extends LivingEntity> void renderModel(EntityModel<T> arg, Identifier arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i, T arg5, float f, float g, float h) {
        VertexConsumer lv = arg4.getBuffer(RenderLayer.getEntityCutoutNoCull(arg2));
        arg.render(arg3, lv, i, LivingEntityRenderer.getOverlay(arg5, 0.0f), f, g, h, 1.0f);
    }

    public M getContextModel() {
        return this.context.getModel();
    }

    protected Identifier getTexture(T arg) {
        return this.context.getTexture(arg);
    }

    public abstract void render(MatrixStack var1, VertexConsumerProvider var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10);
}

