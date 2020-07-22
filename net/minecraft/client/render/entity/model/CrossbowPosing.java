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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CrossbowPosing {
    public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
        ModelPart lv = rightArmed ? holdingArm : otherArm;
        ModelPart lv2 = rightArmed ? otherArm : holdingArm;
        lv.yaw = (rightArmed ? -0.3f : 0.3f) + head.yaw;
        lv2.yaw = (rightArmed ? 0.6f : -0.6f) + head.yaw;
        lv.pitch = -1.5707964f + head.pitch + 0.1f;
        lv2.pitch = -1.5f + head.pitch;
    }

    public static void charge(ModelPart holdingArm, ModelPart pullingArm, LivingEntity actor, boolean rightArmed) {
        ModelPart lv = rightArmed ? holdingArm : pullingArm;
        ModelPart lv2 = rightArmed ? pullingArm : holdingArm;
        lv.yaw = rightArmed ? -0.8f : 0.8f;
        lv2.pitch = lv.pitch = -0.97079635f;
        float f = CrossbowItem.getPullTime(actor.getActiveItem());
        float g = MathHelper.clamp((float)actor.getItemUseTime(), 0.0f, f);
        float h = g / f;
        lv2.yaw = MathHelper.lerp(h, 0.4f, 0.85f) * (float)(rightArmed ? 1 : -1);
        lv2.pitch = MathHelper.lerp(h, lv2.pitch, -1.5707964f);
    }

    public static <T extends MobEntity> void method_29351(ModelPart arg, ModelPart arg2, T arg3, float f, float g) {
        float h = MathHelper.sin(f * (float)Math.PI);
        float i = MathHelper.sin((1.0f - (1.0f - f) * (1.0f - f)) * (float)Math.PI);
        arg.roll = 0.0f;
        arg2.roll = 0.0f;
        arg.yaw = 0.15707964f;
        arg2.yaw = -0.15707964f;
        if (arg3.getMainArm() == Arm.RIGHT) {
            arg.pitch = -1.8849558f + MathHelper.cos(g * 0.09f) * 0.15f;
            arg2.pitch = -0.0f + MathHelper.cos(g * 0.19f) * 0.5f;
            arg.pitch += h * 2.2f - i * 0.4f;
            arg2.pitch += h * 1.2f - i * 0.4f;
        } else {
            arg.pitch = -0.0f + MathHelper.cos(g * 0.19f) * 0.5f;
            arg2.pitch = -1.8849558f + MathHelper.cos(g * 0.09f) * 0.15f;
            arg.pitch += h * 1.2f - i * 0.4f;
            arg2.pitch += h * 2.2f - i * 0.4f;
        }
        CrossbowPosing.method_29350(arg, arg2, g);
    }

    public static void method_29350(ModelPart arg, ModelPart arg2, float f) {
        arg.roll += MathHelper.cos(f * 0.09f) * 0.05f + 0.05f;
        arg2.roll -= MathHelper.cos(f * 0.09f) * 0.05f + 0.05f;
        arg.pitch += MathHelper.sin(f * 0.067f) * 0.05f;
        arg2.pitch -= MathHelper.sin(f * 0.067f) * 0.05f;
    }

    public static void method_29352(ModelPart arg, ModelPart arg2, boolean bl, float f, float g) {
        float j;
        float h = MathHelper.sin(f * (float)Math.PI);
        float i = MathHelper.sin((1.0f - (1.0f - f) * (1.0f - f)) * (float)Math.PI);
        arg2.roll = 0.0f;
        arg.roll = 0.0f;
        arg2.yaw = -(0.1f - h * 0.6f);
        arg.yaw = 0.1f - h * 0.6f;
        arg2.pitch = j = (float)(-Math.PI) / (bl ? 1.5f : 2.25f);
        arg.pitch = j;
        arg2.pitch += h * 1.2f - i * 0.4f;
        arg.pitch += h * 1.2f - i * 0.4f;
        CrossbowPosing.method_29350(arg2, arg, g);
    }
}

