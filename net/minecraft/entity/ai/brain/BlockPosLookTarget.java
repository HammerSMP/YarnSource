/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockPosLookTarget
implements LookTarget {
    private final BlockPos blockPos;
    private final Vec3d pos;

    public BlockPosLookTarget(BlockPos arg) {
        this.blockPos = arg;
        this.pos = Vec3d.method_24953(arg);
    }

    @Override
    public Vec3d getPos() {
        return this.pos;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @Override
    public boolean isSeenBy(LivingEntity arg) {
        return true;
    }

    public String toString() {
        return "BlockPosTracker{blockPos=" + this.blockPos + ", centerPosition=" + this.pos + '}';
    }
}

