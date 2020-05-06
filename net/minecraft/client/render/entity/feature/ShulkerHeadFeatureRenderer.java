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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class ShulkerHeadFeatureRenderer
extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
    public ShulkerHeadFeatureRenderer(FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, ShulkerEntity arg3, float f, float g, float h, float j, float k, float l) {
        Identifier lv5;
        arg.push();
        arg.translate(0.0, 1.0, 0.0);
        arg.scale(-1.0f, -1.0f, 1.0f);
        Quaternion lv = arg3.getAttachedFace().getOpposite().getRotationQuaternion();
        lv.conjugate();
        arg.multiply(lv);
        arg.scale(-1.0f, -1.0f, 1.0f);
        arg.translate(0.0, -1.0, 0.0);
        ModelPart lv2 = ((ShulkerEntityModel)this.getContextModel()).getHead();
        lv2.yaw = k * ((float)Math.PI / 180);
        lv2.pitch = l * ((float)Math.PI / 180);
        DyeColor lv3 = arg3.getColor();
        if (lv3 == null) {
            Identifier lv4 = ShulkerEntityRenderer.TEXTURE;
        } else {
            lv5 = ShulkerEntityRenderer.COLORED_TEXTURES[lv3.getId()];
        }
        VertexConsumer lv6 = arg2.getBuffer(RenderLayer.getEntitySolid(lv5));
        lv2.render(arg, lv6, i, LivingEntityRenderer.getOverlay(arg3, 0.0f));
        arg.pop();
    }
}

