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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public HeldItemFeatureRenderer(FeatureRendererContext<T, M> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        ItemStack lv2;
        boolean bl = ((LivingEntity)arg3).getMainArm() == Arm.RIGHT;
        ItemStack lv = bl ? ((LivingEntity)arg3).getOffHandStack() : ((LivingEntity)arg3).getMainHandStack();
        ItemStack itemStack = lv2 = bl ? ((LivingEntity)arg3).getMainHandStack() : ((LivingEntity)arg3).getOffHandStack();
        if (lv.isEmpty() && lv2.isEmpty()) {
            return;
        }
        arg.push();
        if (((EntityModel)this.getContextModel()).child) {
            float m = 0.5f;
            arg.translate(0.0, 0.75, 0.0);
            arg.scale(0.5f, 0.5f, 0.5f);
        }
        this.renderItem((LivingEntity)arg3, lv2, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, arg, arg2, i);
        this.renderItem((LivingEntity)arg3, lv, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, arg, arg2, i);
        arg.pop();
    }

    private void renderItem(LivingEntity arg, ItemStack arg2, ModelTransformation.Mode arg3, Arm arg4, MatrixStack arg5, VertexConsumerProvider arg6, int i) {
        if (arg2.isEmpty()) {
            return;
        }
        arg5.push();
        ((ModelWithArms)this.getContextModel()).setArmAngle(arg4, arg5);
        arg5.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
        arg5.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        boolean bl = arg4 == Arm.LEFT;
        arg5.translate((float)(bl ? -1 : 1) / 16.0f, 0.125, -0.625);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(arg, arg2, arg3, bl, arg5, arg6, i);
        arg5.pop();
    }
}

