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
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class Deadmau5FeatureRenderer
extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public Deadmau5FeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, AbstractClientPlayerEntity arg3, float f, float g, float h, float j, float k, float l) {
        if (!"deadmau5".equals(arg3.getName().getString()) || !arg3.hasSkinTexture() || arg3.isInvisible()) {
            return;
        }
        VertexConsumer lv = arg2.getBuffer(RenderLayer.getEntitySolid(arg3.getSkinTexture()));
        int m = LivingEntityRenderer.getOverlay(arg3, 0.0f);
        for (int n = 0; n < 2; ++n) {
            float o = MathHelper.lerp(h, arg3.prevYaw, arg3.yaw) - MathHelper.lerp(h, arg3.prevBodyYaw, arg3.bodyYaw);
            float p = MathHelper.lerp(h, arg3.prevPitch, arg3.pitch);
            arg.push();
            arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(o));
            arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(p));
            arg.translate(0.375f * (float)(n * 2 - 1), 0.0, 0.0);
            arg.translate(0.0, -0.375, 0.0);
            arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-p));
            arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-o));
            float q = 1.3333334f;
            arg.scale(1.3333334f, 1.3333334f, 1.3333334f);
            ((PlayerEntityModel)this.getContextModel()).renderEars(arg, lv, i, m);
            arg.pop();
        }
    }
}

