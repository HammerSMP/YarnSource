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
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StriderEntityRenderer
extends MobEntityRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/strider/strider.png");
    private static final Identifier COLD_TEXTURE = new Identifier("textures/entity/strider/strider_cold.png");

    public StriderEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new StriderEntityModel(), 0.5f);
        this.addFeature(new SaddleFeatureRenderer(this, new StriderEntityModel(), new Identifier("textures/entity/strider/strider_saddle.png")));
    }

    @Override
    public Identifier getTexture(StriderEntity arg) {
        return arg.isCold() ? COLD_TEXTURE : TEXTURE;
    }

    @Override
    protected void scale(StriderEntity arg, MatrixStack arg2, float f) {
        float g = 0.9375f;
        if (arg.isBaby()) {
            g *= 0.5f;
            this.shadowRadius = 0.25f;
        } else {
            this.shadowRadius = 0.5f;
        }
        arg2.scale(g, g, g);
    }

    @Override
    protected boolean isShaking(StriderEntity arg) {
        return arg.isCold();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity entity) {
        return this.isShaking((StriderEntity)entity);
    }
}

