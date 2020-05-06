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
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BlazeEntityRenderer
extends MobEntityRenderer<BlazeEntity, BlazeEntityModel<BlazeEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/blaze.png");

    public BlazeEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new BlazeEntityModel(), 0.5f);
    }

    @Override
    protected int getBlockLight(BlazeEntity arg, float f) {
        return 15;
    }

    @Override
    public Identifier getTexture(BlazeEntity arg) {
        return TEXTURE;
    }
}

