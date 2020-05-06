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
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AreaEffectCloudEntityRenderer
extends EntityRenderer<AreaEffectCloudEntity> {
    public AreaEffectCloudEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public Identifier getTexture(AreaEffectCloudEntity arg) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

