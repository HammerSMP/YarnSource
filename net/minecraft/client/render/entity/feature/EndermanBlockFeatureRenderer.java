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
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EndermanEntity;

@Environment(value=EnvType.CLIENT)
public class EndermanBlockFeatureRenderer
extends FeatureRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
    public EndermanBlockFeatureRenderer(FeatureRendererContext<EndermanEntity, EndermanEntityModel<EndermanEntity>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, EndermanEntity arg3, float f, float g, float h, float j, float k, float l) {
        BlockState lv = arg3.getCarriedBlock();
        if (lv == null) {
            return;
        }
        arg.push();
        arg.translate(0.0, 0.6875, -0.75);
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20.0f));
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(45.0f));
        arg.translate(0.25, 0.1875, 0.25);
        float m = 0.5f;
        arg.scale(-0.5f, -0.5f, 0.5f);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(lv, arg, arg2, i, OverlayTexture.DEFAULT_UV);
        arg.pop();
    }
}

