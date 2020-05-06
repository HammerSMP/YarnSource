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
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityRenderer
extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerEntityRenderer(EntityRenderDispatcher arg, ReloadableResourceManager arg2) {
        super(arg, new ZombieVillagerEntityModel(0.0f, false), 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, new ZombieVillagerEntityModel(0.5f, true), new ZombieVillagerEntityModel(1.0f, true)));
        this.addFeature(new VillagerClothingFeatureRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>>(this, arg2, "zombie_villager"));
    }

    @Override
    public Identifier getTexture(ZombieVillagerEntity arg) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(ZombieVillagerEntity arg) {
        return arg.isConverting();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity arg) {
        return this.isShaking((ZombieVillagerEntity)arg);
    }
}

