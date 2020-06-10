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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SkeletonEntityRenderer
extends BipedEntityRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");

    public SkeletonEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new SkeletonEntityModel(), 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, new SkeletonEntityModel(0.5f, true), new SkeletonEntityModel(1.0f, true)));
    }

    @Override
    public Identifier getTexture(AbstractSkeletonEntity arg) {
        return TEXTURE;
    }
}

