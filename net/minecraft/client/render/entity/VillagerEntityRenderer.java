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
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VillagerEntityRenderer
extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/villager/villager.png");

    public VillagerEntityRenderer(EntityRenderDispatcher dispatcher, ReloadableResourceManager arg2) {
        super(dispatcher, new VillagerResemblingModel(0.0f), 0.5f);
        this.addFeature(new HeadFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this));
        this.addFeature(new VillagerClothingFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this, arg2, "villager"));
        this.addFeature(new VillagerHeldItemFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this));
    }

    @Override
    public Identifier getTexture(VillagerEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void scale(VillagerEntity arg, MatrixStack arg2, float f) {
        float g = 0.9375f;
        if (arg.isBaby()) {
            g = (float)((double)g * 0.5);
            this.shadowRadius = 0.25f;
        } else {
            this.shadowRadius = 0.5f;
        }
        arg2.scale(g, g, g);
    }
}

