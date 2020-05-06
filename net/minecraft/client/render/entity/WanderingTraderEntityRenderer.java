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
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WanderingTraderEntityRenderer
extends MobEntityRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/wandering_trader.png");

    public WanderingTraderEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new VillagerResemblingModel(0.0f), 0.5f);
        this.addFeature(new HeadFeatureRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>>(this));
        this.addFeature(new VillagerHeldItemFeatureRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>>(this));
    }

    @Override
    public Identifier getTexture(WanderingTraderEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void scale(WanderingTraderEntity arg, MatrixStack arg2, float f) {
        float g = 0.9375f;
        arg2.scale(0.9375f, 0.9375f, 0.9375f);
    }
}

