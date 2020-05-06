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
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FoxEntityRenderer
extends MobEntityRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/fox/fox.png");
    private static final Identifier SLEEPING_TEXTURE = new Identifier("textures/entity/fox/fox_sleep.png");
    private static final Identifier SNOW_TEXTURE = new Identifier("textures/entity/fox/snow_fox.png");
    private static final Identifier SLEEPING_SNOW_TEXTURE = new Identifier("textures/entity/fox/snow_fox_sleep.png");

    public FoxEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new FoxEntityModel(), 0.4f);
        this.addFeature(new FoxHeldItemFeatureRenderer(this));
    }

    @Override
    protected void setupTransforms(FoxEntity arg, MatrixStack arg2, float f, float g, float h) {
        super.setupTransforms(arg, arg2, f, g, h);
        if (arg.isChasing() || arg.isWalking()) {
            float i = -MathHelper.lerp(h, arg.prevPitch, arg.pitch);
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i));
        }
    }

    @Override
    public Identifier getTexture(FoxEntity arg) {
        if (arg.getFoxType() == FoxEntity.Type.RED) {
            return arg.isSleeping() ? SLEEPING_TEXTURE : TEXTURE;
        }
        return arg.isSleeping() ? SLEEPING_SNOW_TEXTURE : SNOW_TEXTURE;
    }
}

