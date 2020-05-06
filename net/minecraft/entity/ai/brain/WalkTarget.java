/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain;

import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WalkTarget {
    private final LookTarget lookTarget;
    private final float speed;
    private final int completionRange;

    public WalkTarget(BlockPos arg, float f, int i) {
        this(new BlockPosLookTarget(arg), f, i);
    }

    public WalkTarget(Vec3d arg, float f, int i) {
        this(new BlockPosLookTarget(new BlockPos(arg)), f, i);
    }

    public WalkTarget(LookTarget arg, float f, int i) {
        this.lookTarget = arg;
        this.speed = f;
        this.completionRange = i;
    }

    public LookTarget getLookTarget() {
        return this.lookTarget;
    }

    public float getSpeed() {
        return this.speed;
    }

    public int getCompletionRange() {
        return this.completionRange;
    }
}

