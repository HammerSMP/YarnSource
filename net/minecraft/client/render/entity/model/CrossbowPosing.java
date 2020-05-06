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
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CrossbowPosing {
    public static void hold(ModelPart arg, ModelPart arg2, ModelPart arg3, boolean bl) {
        ModelPart lv = bl ? arg : arg2;
        ModelPart lv2 = bl ? arg2 : arg;
        lv.yaw = (bl ? -0.3f : 0.3f) + arg3.yaw;
        lv2.yaw = (bl ? 0.6f : -0.6f) + arg3.yaw;
        lv.pitch = -1.5707964f + arg3.pitch + 0.1f;
        lv2.pitch = -1.5f + arg3.pitch;
    }

    public static void charge(ModelPart arg, ModelPart arg2, LivingEntity arg3, boolean bl) {
        ModelPart lv = bl ? arg : arg2;
        ModelPart lv2 = bl ? arg2 : arg;
        lv.yaw = bl ? -0.8f : 0.8f;
        lv2.pitch = lv.pitch = -0.97079635f;
        float f = CrossbowItem.getPullTime(arg3.getActiveItem());
        float g = MathHelper.clamp((float)arg3.getItemUseTime(), 0.0f, f);
        float h = g / f;
        lv2.yaw = MathHelper.lerp(h, 0.4f, 0.85f) * (float)(bl ? 1 : -1);
        lv2.pitch = MathHelper.lerp(h, lv2.pitch, -1.5707964f);
    }
}

