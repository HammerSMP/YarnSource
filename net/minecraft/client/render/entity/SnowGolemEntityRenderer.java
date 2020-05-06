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
import net.minecraft.client.render.entity.feature.SnowmanPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityRenderer
extends MobEntityRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/snow_golem.png");

    public SnowGolemEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new SnowmanEntityModel(), 0.5f);
        this.addFeature(new SnowmanPumpkinFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(SnowGolemEntity arg) {
        return TEXTURE;
    }
}

