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
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityRenderer
extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/iron_golem/iron_golem.png");

    public IronGolemEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new IronGolemEntityModel(), 0.7f);
        this.addFeature(new IronGolemCrackFeatureRenderer(this));
        this.addFeature(new IronGolemFlowerFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(IronGolemEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(IronGolemEntity arg, MatrixStack arg2, float f, float g, float h) {
        super.setupTransforms(arg, arg2, f, g, h);
        if ((double)arg.limbDistance < 0.01) {
            return;
        }
        float i = 13.0f;
        float j = arg.limbAngle - arg.limbDistance * (1.0f - h) + 6.0f;
        float k = (Math.abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f;
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(6.5f * k));
    }
}

