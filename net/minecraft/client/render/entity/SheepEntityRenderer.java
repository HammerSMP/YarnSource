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
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SheepEntityRenderer
extends MobEntityRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/sheep/sheep.png");

    public SheepEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new SheepEntityModel(), 0.7f);
        this.addFeature(new SheepWoolFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(SheepEntity arg) {
        return TEXTURE;
    }
}

