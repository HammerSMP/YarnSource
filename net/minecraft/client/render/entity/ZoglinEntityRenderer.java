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
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZoglinEntityRenderer
extends MobEntityRenderer<ZoglinEntity, HoglinEntityModel<ZoglinEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/hoglin/zoglin.png");

    public ZoglinEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new HoglinEntityModel(), 0.7f);
    }

    @Override
    public Identifier getTexture(ZoglinEntity arg) {
        return TEXTURE;
    }
}

