/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherArmorFeatureRenderer
extends EnergySwirlOverlayFeatureRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/wither/wither_armor.png");
    private final WitherEntityModel<WitherEntity> model = new WitherEntityModel(0.5f);

    public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntity, WitherEntityModel<WitherEntity>> arg) {
        super(arg);
    }

    @Override
    protected float getEnergySwirlX(float f) {
        return MathHelper.cos(f * 0.02f) * 3.0f;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    @Override
    protected EntityModel<WitherEntity> getEnergySwirlModel() {
        return this.model;
    }
}

