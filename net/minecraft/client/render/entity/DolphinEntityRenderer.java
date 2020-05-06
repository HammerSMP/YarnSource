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
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityRenderer
extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/dolphin.png");

    public DolphinEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new DolphinEntityModel(), 0.7f);
        this.addFeature(new DolphinHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(DolphinEntity arg) {
        return TEXTURE;
    }
}

