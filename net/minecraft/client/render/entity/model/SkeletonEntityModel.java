/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SkeletonEntityModel<T extends MobEntity>
extends BipedEntityModel<T> {
    public SkeletonEntityModel() {
        this(0.0f, false);
    }

    public SkeletonEntityModel(float f, boolean bl) {
        super(f);
        if (!bl) {
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
            this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
            this.leftArm = new ModelPart(this, 40, 16);
            this.leftArm.mirror = true;
            this.leftArm.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
            this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
            this.rightLeg = new ModelPart(this, 0, 16);
            this.rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
            this.rightLeg.setPivot(-2.0f, 12.0f, 0.0f);
            this.leftLeg = new ModelPart(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
            this.leftLeg.setPivot(2.0f, 12.0f, 0.0f);
        }
    }

    @Override
    public void animateModel(T arg, float f, float g, float h) {
        this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
        this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack lv = ((LivingEntity)arg).getStackInHand(Hand.MAIN_HAND);
        if (lv.getItem() == Items.BOW && ((MobEntity)arg).isAttacking()) {
            if (((MobEntity)arg).getMainArm() == Arm.RIGHT) {
                this.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
        }
        super.animateModel(arg, f, g, h);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        super.setAngles(arg, f, g, h, i, j);
        ItemStack lv = ((LivingEntity)arg).getMainHandStack();
        if (((MobEntity)arg).isAttacking() && (lv.isEmpty() || lv.getItem() != Items.BOW)) {
            float k = MathHelper.sin(this.handSwingProgress * (float)Math.PI);
            float l = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * (float)Math.PI);
            this.rightArm.roll = 0.0f;
            this.leftArm.roll = 0.0f;
            this.rightArm.yaw = -(0.1f - k * 0.6f);
            this.leftArm.yaw = 0.1f - k * 0.6f;
            this.rightArm.pitch = -1.5707964f;
            this.leftArm.pitch = -1.5707964f;
            this.rightArm.pitch -= k * 1.2f - l * 0.4f;
            this.leftArm.pitch -= k * 1.2f - l * 0.4f;
            this.rightArm.roll += MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
            this.leftArm.roll -= MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
            this.rightArm.pitch += MathHelper.sin(h * 0.067f) * 0.05f;
            this.leftArm.pitch -= MathHelper.sin(h * 0.067f) * 0.05f;
        }
    }

    @Override
    public void setArmAngle(Arm arg, MatrixStack arg2) {
        float f = arg == Arm.RIGHT ? 1.0f : -1.0f;
        ModelPart lv = this.getArm(arg);
        lv.pivotX += f;
        lv.rotate(arg2);
        lv.pivotX -= f;
    }
}

