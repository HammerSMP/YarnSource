/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ModelUtil {
    public static float interpolateAngle(float angle1, float angle2, float progress) {
        float i;
        for (i = angle2 - angle1; i < (float)(-Math.PI); i += (float)Math.PI * 2) {
        }
        while (i >= (float)Math.PI) {
            i -= (float)Math.PI * 2;
        }
        return angle1 + progress * i;
    }
}

