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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitchEntityRenderer
extends MobEntityRenderer<WitchEntity, WitchEntityModel<WitchEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/witch.png");

    public WitchEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new WitchEntityModel(0.0f), 0.5f);
        this.addFeature(new WitchHeldItemFeatureRenderer<WitchEntity>(this));
    }

    @Override
    public void render(WitchEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        ((WitchEntityModel)this.model).setLiftingNose(!arg.getMainHandStack().isEmpty());
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(WitchEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void scale(WitchEntity arg, MatrixStack arg2, float f) {
        float g = 0.9375f;
        arg2.scale(0.9375f, 0.9375f, 0.9375f);
    }
}

