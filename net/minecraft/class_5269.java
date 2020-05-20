/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.class_5217;
import net.minecraft.util.math.BlockPos;

public interface class_5269
extends class_5217 {
    public void setSpawnX(int var1);

    public void setSpawnY(int var1);

    public void setSpawnZ(int var1);

    default public void setSpawnPos(BlockPos arg) {
        this.setSpawnX(arg.getX());
        this.setSpawnY(arg.getY());
        this.setSpawnZ(arg.getZ());
    }
}

