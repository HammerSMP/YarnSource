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
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GiantEntityRenderer
extends MobEntityRenderer<GiantEntity, BipedEntityModel<GiantEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
    private final float scale;

    public GiantEntityRenderer(EntityRenderDispatcher dispatcher, float f) {
        super(dispatcher, new GiantEntityModel(), 0.5f * f);
        this.scale = f;
        this.addFeature(new HeldItemFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>>(this));
        this.addFeature(new ArmorFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>, GiantEntityModel>(this, new GiantEntityModel(0.5f, true), new GiantEntityModel(1.0f, true)));
    }

    @Override
    protected void scale(GiantEntity arg, MatrixStack arg2, float f) {
        arg2.scale(this.scale, this.scale, this.scale);
    }

    @Override
    public Identifier getTexture(GiantEntity arg) {
        return TEXTURE;
    }
}

