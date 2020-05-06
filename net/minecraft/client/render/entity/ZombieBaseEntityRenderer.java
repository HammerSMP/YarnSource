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
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>>
extends BipedEntityRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");

    protected ZombieBaseEntityRenderer(EntityRenderDispatcher arg, M arg2, M arg3, M arg4) {
        super(arg, arg2, 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, arg3, arg4));
    }

    @Override
    public Identifier getTexture(ZombieEntity arg) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(T arg) {
        return ((ZombieEntity)arg).isConvertingInWater();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity arg) {
        return this.isShaking((T)((ZombieEntity)arg));
    }
}

