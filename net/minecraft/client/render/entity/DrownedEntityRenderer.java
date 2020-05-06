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
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DrownedEntityRenderer
extends ZombieBaseEntityRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/drowned.png");

    public DrownedEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new DrownedEntityModel(0.0f, 0.0f, 64, 64), new DrownedEntityModel(0.5f, true), new DrownedEntityModel(1.0f, true));
        this.addFeature(new DrownedOverlayFeatureRenderer<DrownedEntity>(this));
    }

    @Override
    public Identifier getTexture(ZombieEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(DrownedEntity arg, MatrixStack arg2, float f, float g, float h) {
        super.setupTransforms(arg, arg2, f, g, h);
        float i = arg.getLeaningPitch(h);
        if (i > 0.0f) {
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(i, arg.pitch, -10.0f - arg.pitch)));
        }
    }
}

