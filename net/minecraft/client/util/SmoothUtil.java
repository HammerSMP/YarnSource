/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SmoothUtil {
    private double actualSum;
    private double smoothedSum;
    private double movementLatency;

    public double smooth(double original, double smoother) {
        this.actualSum += original;
        double f = this.actualSum - this.smoothedSum;
        double g = MathHelper.lerp(0.5, this.movementLatency, f);
        double h = Math.signum(f);
        if (h * f > h * this.movementLatency) {
            f = g;
        }
        this.movementLatency = g;
        this.smoothedSum += f * smoother;
        return f * smoother;
    }

    public void clear() {
        this.actualSum = 0.0;
        this.smoothedSum = 0.0;
        this.movementLatency = 0.0;
    }
}

