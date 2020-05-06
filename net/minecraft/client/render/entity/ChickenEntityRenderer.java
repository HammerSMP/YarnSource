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
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityRenderer
extends MobEntityRenderer<ChickenEntity, ChickenEntityModel<ChickenEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/chicken.png");

    public ChickenEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new ChickenEntityModel(), 0.3f);
    }

    @Override
    public Identifier getTexture(ChickenEntity arg) {
        return TEXTURE;
    }

    @Override
    protected float getAnimationProgress(ChickenEntity arg, float f) {
        float g = MathHelper.lerp(f, arg.field_6736, arg.field_6741);
        float h = MathHelper.lerp(f, arg.field_6738, arg.field_6743);
        return (MathHelper.sin(g) + 1.0f) * h;
    }
}

