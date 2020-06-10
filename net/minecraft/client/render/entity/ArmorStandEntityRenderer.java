/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityRenderer
extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/armorstand/wood.png");

    public ArmorStandEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new ArmorStandEntityModel(), 0.0f);
        this.addFeature(new ArmorFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel, ArmorStandArmorEntityModel>(this, new ArmorStandArmorEntityModel(0.5f), new ArmorStandArmorEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new ElytraFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
        this.addFeature(new HeadFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>(this));
    }

    @Override
    public Identifier getTexture(ArmorStandEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(ArmorStandEntity arg, MatrixStack arg2, float f, float g, float h) {
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - g));
        float i = (float)(arg.world.getTime() - arg.lastHitTime) + h;
        if (i < 5.0f) {
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.sin(i / 1.5f * (float)Math.PI) * 3.0f));
        }
    }

    @Override
    protected boolean hasLabel(ArmorStandEntity arg) {
        float f;
        double d = this.dispatcher.getSquaredDistanceToCamera(arg);
        float f2 = f = arg.isInSneakingPose() ? 32.0f : 64.0f;
        if (d >= (double)(f * f)) {
            return false;
        }
        return arg.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderLayer getRenderLayer(ArmorStandEntity arg, boolean bl, boolean bl2, boolean bl3) {
        if (!arg.isMarker()) {
            return super.getRenderLayer(arg, bl, bl2, bl3);
        }
        Identifier lv = this.getTexture(arg);
        if (bl2) {
            return RenderLayer.getEntityTranslucent(lv, false);
        }
        if (bl) {
            return RenderLayer.getEntityCutoutNoCull(lv, false);
        }
        return null;
    }
}

