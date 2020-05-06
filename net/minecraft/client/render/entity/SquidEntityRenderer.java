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
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer
extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/squid.png");

    public SquidEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new SquidEntityModel(), 0.7f);
    }

    @Override
    public Identifier getTexture(SquidEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(SquidEntity arg, MatrixStack arg2, float f, float g, float h) {
        float i = MathHelper.lerp(h, arg.field_6905, arg.field_6907);
        float j = MathHelper.lerp(h, arg.field_6906, arg.field_6903);
        arg2.translate(0.0, 0.5, 0.0);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - g));
        arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i));
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
        arg2.translate(0.0, -1.2f, 0.0);
    }

    @Override
    protected float getAnimationProgress(SquidEntity arg, float f) {
        return MathHelper.lerp(f, arg.field_6900, arg.field_6904);
    }
}

