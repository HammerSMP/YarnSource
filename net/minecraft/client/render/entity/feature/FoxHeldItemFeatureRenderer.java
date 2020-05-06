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
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer
extends FeatureRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
    public FoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, FoxEntity arg3, float f, float g, float h, float j, float k, float l) {
        boolean bl = arg3.isSleeping();
        boolean bl2 = arg3.isBaby();
        arg.push();
        if (bl2) {
            float m = 0.75f;
            arg.scale(0.75f, 0.75f, 0.75f);
            arg.translate(0.0, 0.5, 0.209375f);
        }
        arg.translate(((FoxEntityModel)this.getContextModel()).head.pivotX / 16.0f, ((FoxEntityModel)this.getContextModel()).head.pivotY / 16.0f, ((FoxEntityModel)this.getContextModel()).head.pivotZ / 16.0f);
        float n = arg3.getHeadRoll(h);
        arg.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(n));
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(k));
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(l));
        if (arg3.isBaby()) {
            if (bl) {
                arg.translate(0.4f, 0.26f, 0.15f);
            } else {
                arg.translate(0.06f, 0.26f, -0.5);
            }
        } else if (bl) {
            arg.translate(0.46f, 0.26f, 0.22f);
        } else {
            arg.translate(0.06f, 0.27f, -0.5);
        }
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        if (bl) {
            arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
        }
        ItemStack lv = arg3.getEquippedStack(EquipmentSlot.MAINHAND);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(arg3, lv, ModelTransformation.Mode.GROUND, false, arg, arg2, i);
        arg.pop();
    }
}

