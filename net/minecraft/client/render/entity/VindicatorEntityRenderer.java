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
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VindicatorEntityRenderer
extends IllagerEntityRenderer<VindicatorEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vindicator.png");

    public VindicatorEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new IllagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<VindicatorEntity, IllagerEntityModel<VindicatorEntity>>((FeatureRendererContext)this){

            @Override
            public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, VindicatorEntity arg3, float f, float g, float h, float j, float k, float l) {
                if (arg3.isAttacking()) {
                    super.render(arg, arg2, i, arg3, f, g, h, j, k, l);
                }
            }
        });
    }

    @Override
    public Identifier getTexture(VindicatorEntity arg) {
        return TEXTURE;
    }
}

