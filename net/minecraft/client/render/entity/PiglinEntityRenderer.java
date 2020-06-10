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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityRenderer
extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {
    private static final Identifier PIGLIN_TEXTURE = new Identifier("textures/entity/piglin/piglin.png");
    private static final Identifier ZOMBIFIED_PIGLIN_TEXTURE = new Identifier("textures/entity/piglin/zombified_piglin.png");

    public PiglinEntityRenderer(EntityRenderDispatcher arg, boolean bl) {
        super(arg, PiglinEntityRenderer.getPiglinModel(bl), 0.5f, 1.0019531f, 1.0f, 1.0019531f);
        this.addFeature(new ArmorFeatureRenderer(this, new BipedEntityModel(0.5f), new BipedEntityModel(1.02f)));
    }

    private static PiglinEntityModel<MobEntity> getPiglinModel(boolean bl) {
        PiglinEntityModel<MobEntity> lv = new PiglinEntityModel<MobEntity>(0.0f, 64, 64);
        if (bl) {
            lv.leftEar.visible = false;
        }
        return lv;
    }

    @Override
    public Identifier getTexture(MobEntity arg) {
        return arg instanceof PiglinEntity ? PIGLIN_TEXTURE : ZOMBIFIED_PIGLIN_TEXTURE;
    }

    @Override
    protected boolean isShaking(MobEntity arg) {
        return arg instanceof PiglinEntity && ((PiglinEntity)arg).canConvert();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity arg) {
        return this.isShaking((MobEntity)arg);
    }
}

