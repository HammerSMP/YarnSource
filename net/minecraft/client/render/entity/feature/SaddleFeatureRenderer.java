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
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Saddleable;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SaddleFeatureRenderer<T extends Entity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private final Identifier TEXTURE;
    private final M model;

    public SaddleFeatureRenderer(FeatureRendererContext<T, M> arg, M arg2, Identifier arg3) {
        super(arg);
        this.model = arg2;
        this.TEXTURE = arg3;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        if (!((Saddleable)arg3).isSaddled()) {
            return;
        }
        ((EntityModel)this.getContextModel()).copyStateTo(this.model);
        ((EntityModel)this.model).animateModel(arg3, f, g, h);
        ((EntityModel)this.model).setAngles(arg3, f, g, j, k, l);
        VertexConsumer lv = arg2.getBuffer(RenderLayer.getEntityCutoutNoCull(this.TEXTURE));
        ((Model)this.model).render(arg, lv, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}

