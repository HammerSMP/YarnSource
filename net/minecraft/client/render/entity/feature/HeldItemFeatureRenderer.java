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

    private void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (stack.isEmpty()) {
            return;
        }
        matrices.push();
        ((ModelWithArms)this.getContextModel()).setArmAngle(arm, matrices);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        boolean bl = arm == Arm.LEFT;
        matrices.translate((float)(bl ? -1 : 1) / 16.0f, 0.125, -0.625);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
        matrices.pop();
    }
}

