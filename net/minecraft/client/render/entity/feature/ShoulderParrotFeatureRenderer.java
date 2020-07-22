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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

@Environment(value=EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer<T extends PlayerEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    private final ParrotEntityModel model = new ParrotEntityModel();

    public ShoulderParrotFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        this.renderShoulderParrot(arg, arg2, i, arg3, f, g, k, l, true);
        this.renderShoulderParrot(arg, arg2, i, arg3, f, g, k, l, false);
    }

    private void renderShoulderParrot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T player, float limbAngle, float limbDistance, float headYaw, float headPitch, boolean leftShoulder) {
        CompoundTag lv = leftShoulder ? ((PlayerEntity)player).getShoulderEntityLeft() : ((PlayerEntity)player).getShoulderEntityRight();
        EntityType.get(lv.getString("id")).filter(arg -> arg == EntityType.PARROT).ifPresent(arg5 -> {
            matrices.push();
            matrices.translate(leftShoulder ? (double)0.4f : (double)-0.4f, player.isInSneakingPose() ? (double)-1.3f : -1.5, 0.0);
            VertexConsumer lv = vertexConsumers.getBuffer(this.model.getLayer(ParrotEntityRenderer.TEXTURES[lv.getInt("Variant")]));
            this.model.poseOnShoulder(matrices, lv, light, OverlayTexture.DEFAULT_UV, limbAngle, limbDistance, headYaw, headPitch, arg2.age);
            matrices.pop();
        });
    }
}

