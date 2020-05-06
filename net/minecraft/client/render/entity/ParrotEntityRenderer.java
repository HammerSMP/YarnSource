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
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityRenderer
extends MobEntityRenderer<ParrotEntity, ParrotEntityModel> {
    public static final Identifier[] TEXTURES = new Identifier[]{new Identifier("textures/entity/parrot/parrot_red_blue.png"), new Identifier("textures/entity/parrot/parrot_blue.png"), new Identifier("textures/entity/parrot/parrot_green.png"), new Identifier("textures/entity/parrot/parrot_yellow_blue.png"), new Identifier("textures/entity/parrot/parrot_grey.png")};

    public ParrotEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new ParrotEntityModel(), 0.3f);
    }

    @Override
    public Identifier getTexture(ParrotEntity arg) {
        return TEXTURES[arg.getVariant()];
    }

    @Override
    public float getAnimationProgress(ParrotEntity arg, float f) {
        float g = MathHelper.lerp(f, arg.field_6829, arg.field_6818);
        float h = MathHelper.lerp(f, arg.field_6827, arg.field_6819);
        return (MathHelper.sin(g) + 1.0f) * h;
    }
}

