/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.class_5217;
import net.minecraft.util.math.BlockPos;

public interface class_5269
extends class_5217 {
    public void method_27416(int var1);

    public void method_27417(int var1);

    public void method_27419(int var1);

    default public void setSpawnPos(BlockPos arg) {
        this.method_27416(arg.getX());
        this.method_27417(arg.getY());
        this.method_27419(arg.getZ());
    }

    public void setTime(long var1);

    public void setTimeOfDay(long var1);
}

