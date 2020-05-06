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
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer
extends FeatureRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
    public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, SnowGolemEntity arg3, float f, float g, float h, float j, float k, float l) {
        if (arg3.isInvisible() || !arg3.hasPumpkin()) {
            return;
        }
        arg.push();
        ((SnowmanEntityModel)this.getContextModel()).getTopSnowball().rotate(arg);
        float m = 0.625f;
        arg.translate(0.0, -0.34375, 0.0);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        arg.scale(0.625f, -0.625f, -0.625f);
        ItemStack lv = new ItemStack(Blocks.CARVED_PUMPKIN);
        MinecraftClient.getInstance().getItemRenderer().renderItem(arg3, lv, ModelTransformation.Mode.HEAD, false, arg, arg2, arg3.world, i, LivingEntityRenderer.getOverlay(arg3, 0.0f));
        arg.pop();
    }
}

