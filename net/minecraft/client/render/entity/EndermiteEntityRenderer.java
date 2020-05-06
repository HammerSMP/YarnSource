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
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EndermiteEntityRenderer
extends MobEntityRenderer<EndermiteEntity, EndermiteEntityModel<EndermiteEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/endermite.png");

    public EndermiteEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new EndermiteEntityModel(), 0.3f);
    }

    @Override
    protected float getLyingAngle(EndermiteEntity arg) {
        return 180.0f;
    }

    @Override
    public Identifier getTexture(EndermiteEntity arg) {
        return TEXTURE;
    }

    @Override
    protected /* synthetic */ float getLyingAngle(LivingEntity arg) {
        return this.getLyingAngle((EndermiteEntity)arg);
    }

    @Override
    public /* synthetic */ Identifier getTexture(Entity arg) {
        return this.getTexture((EndermiteEntity)arg);
    }
}

