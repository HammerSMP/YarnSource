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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CapeFeatureRenderer
extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public CapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, AbstractClientPlayerEntity arg3, float f, float g, float h, float j, float k, float l) {
        if (!arg3.canRenderCapeTexture() || arg3.isInvisible() || !arg3.isPartVisible(PlayerModelPart.CAPE) || arg3.getCapeTexture() == null) {
            return;
        }
        ItemStack lv = arg3.getEquippedStack(EquipmentSlot.CHEST);
        if (lv.getItem() == Items.ELYTRA) {
            return;
        }
        arg.push();
        arg.translate(0.0, 0.0, 0.125);
        double d = MathHelper.lerp((double)h, arg3.prevCapeX, arg3.capeX) - MathHelper.lerp((double)h, arg3.prevX, arg3.getX());
        double e = MathHelper.lerp((double)h, arg3.prevCapeY, arg3.capeY) - MathHelper.lerp((double)h, arg3.prevY, arg3.getY());
        double m = MathHelper.lerp((double)h, arg3.prevCapeZ, arg3.capeZ) - MathHelper.lerp((double)h, arg3.prevZ, arg3.getZ());
        float n = arg3.prevBodyYaw + (arg3.bodyYaw - arg3.prevBodyYaw);
        double o = MathHelper.sin(n * ((float)Math.PI / 180));
        double p = -MathHelper.cos(n * ((float)Math.PI / 180));
        float q = (float)e * 10.0f;
        q = MathHelper.clamp(q, -6.0f, 32.0f);
        float r = (float)(d * o + m * p) * 100.0f;
        r = MathHelper.clamp(r, 0.0f, 150.0f);
        float s = (float)(d * p - m * o) * 100.0f;
        s = MathHelper.clamp(s, -20.0f, 20.0f);
        if (r < 0.0f) {
            r = 0.0f;
        }
        float t = MathHelper.lerp(h, arg3.prevStrideDistance, arg3.strideDistance);
        q += MathHelper.sin(MathHelper.lerp(h, arg3.prevHorizontalSpeed, arg3.horizontalSpeed) * 6.0f) * 32.0f * t;
        if (arg3.isInSneakingPose()) {
            q += 25.0f;
        }
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(6.0f + r / 2.0f + q));
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(s / 2.0f));
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - s / 2.0f));
        VertexConsumer lv2 = arg2.getBuffer(RenderLayer.getEntitySolid(arg3.getCapeTexture()));
        ((PlayerEntityModel)this.getContextModel()).renderCape(arg, lv2, i, OverlayTexture.DEFAULT_UV);
        arg.pop();
    }
}

