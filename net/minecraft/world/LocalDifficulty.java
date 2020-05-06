/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;

@Immutable
public class LocalDifficulty {
    private final Difficulty globalDifficulty;
    private final float localDifficulty;

    public LocalDifficulty(Difficulty arg, long l, long m, float f) {
        this.globalDifficulty = arg;
        this.localDifficulty = this.setLocalDifficulty(arg, l, m, f);
    }

    public Difficulty getGlobalDifficulty() {
        return this.globalDifficulty;
    }

    public float getLocalDifficulty() {
        return this.localDifficulty;
    }

    public boolean isHarderThan(float f) {
        return this.localDifficulty > f;
    }

    public float getClampedLocalDifficulty() {
        if (this.localDifficulty < 2.0f) {
            return 0.0f;
        }
        if (this.localDifficulty > 4.0f) {
            return 1.0f;
        }
        return (this.localDifficulty - 2.0f) / 2.0f;
    }

    private float setLocalDifficulty(Difficulty arg, long l, long m, float f) {
        if (arg == Difficulty.PEACEFUL) {
            return 0.0f;
        }
        boolean bl = arg == Difficulty.HARD;
        float g = 0.75f;
        float h = MathHelper.clamp(((float)l + -72000.0f) / 1440000.0f, 0.0f, 1.0f) * 0.25f;
        g += h;
        float i = 0.0f;
        i += MathHelper.clamp((float)m / 3600000.0f, 0.0f, 1.0f) * (bl ? 1.0f : 0.75f);
        i += MathHelper.clamp(f * 0.25f, 0.0f, h);
        if (arg == Difficulty.EASY) {
            i *= 0.5f;
        }
        return (float)arg.getId() * (g += i);
    }
}

