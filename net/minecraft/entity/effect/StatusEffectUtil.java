/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.math.MathHelper;

public final class StatusEffectUtil {
    @Environment(value=EnvType.CLIENT)
    public static String durationToString(StatusEffectInstance arg, float f) {
        if (arg.isPermanent()) {
            return "**:**";
        }
        int i = MathHelper.floor((float)arg.getDuration() * f);
        return ChatUtil.ticksToString(i);
    }

    public static boolean hasHaste(LivingEntity arg) {
        return arg.hasStatusEffect(StatusEffects.HASTE) || arg.hasStatusEffect(StatusEffects.CONDUIT_POWER);
    }

    public static int getHasteAmplifier(LivingEntity arg) {
        int i = 0;
        int j = 0;
        if (arg.hasStatusEffect(StatusEffects.HASTE)) {
            i = arg.getStatusEffect(StatusEffects.HASTE).getAmplifier();
        }
        if (arg.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
            j = arg.getStatusEffect(StatusEffects.CONDUIT_POWER).getAmplifier();
        }
        return Math.max(i, j);
    }

    public static boolean hasWaterBreathing(LivingEntity arg) {
        return arg.hasStatusEffect(StatusEffects.WATER_BREATHING) || arg.hasStatusEffect(StatusEffects.CONDUIT_POWER);
    }
}

