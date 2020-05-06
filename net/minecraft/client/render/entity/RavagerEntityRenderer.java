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
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityRenderer
extends MobEntityRenderer<RavagerEntity, RavagerEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/ravager.png");

    public RavagerEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new RavagerEntityModel(), 1.1f);
    }

    @Override
    public Identifier getTexture(RavagerEntity arg) {
        return TEXTURE;
    }
}

