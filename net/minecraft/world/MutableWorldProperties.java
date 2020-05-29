/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProperties;

public interface MutableWorldProperties
extends WorldProperties {
    public void setSpawnX(int var1);

    public void setSpawnY(int var1);

    public void setSpawnZ(int var1);

    default public void setSpawnPos(BlockPos arg) {
        this.setSpawnX(arg.getX());
        this.setSpawnY(arg.getY());
        this.setSpawnZ(arg.getZ());
    }
}

