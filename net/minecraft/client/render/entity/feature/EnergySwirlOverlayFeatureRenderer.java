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
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class EnergySwirlOverlayFeatureRenderer<T extends Entity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public EnergySwirlOverlayFeatureRenderer(FeatureRendererContext<T, M> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!((SkinOverlayOwner)entity).shouldRenderOverlay()) {
            return;
        }
        float m = (float)((Entity)entity).age + tickDelta;
        EntityModel<T> lv = this.getEnergySwirlModel();
        lv.animateModel(entity, limbAngle, limbDistance, tickDelta);
        ((EntityModel)this.getContextModel()).copyStateTo(lv);
        VertexConsumer lv2 = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(this.getEnergySwirlTexture(), this.getEnergySwirlX(m), m * 0.01f));
        lv.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        lv.render(matrices, lv2, light, OverlayTexture.DEFAULT_UV, 0.5f, 0.5f, 0.5f, 1.0f);
    }

    protected abstract float getEnergySwirlX(float var1);

    protected abstract Identifier getEnergySwirlTexture();

    protected abstract EntityModel<T> getEnergySwirlModel();
}

