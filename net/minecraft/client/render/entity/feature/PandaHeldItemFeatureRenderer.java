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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer
extends FeatureRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
    public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, PandaEntity arg3, float f, float g, float h, float j, float k, float l) {
        ItemStack lv = arg3.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!arg3.isScared() || arg3.isScaredByThunderstorm()) {
            return;
        }
        float m = -0.6f;
        float n = 1.4f;
        if (arg3.isEating()) {
            m -= 0.2f * MathHelper.sin(j * 0.6f) + 0.2f;
            n -= 0.09f * MathHelper.sin(j * 0.6f);
        }
        arg.push();
        arg.translate(0.1f, n, m);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(arg3, lv, ModelTransformation.Mode.GROUND, false, arg, arg2, i);
        arg.pop();
    }
}

