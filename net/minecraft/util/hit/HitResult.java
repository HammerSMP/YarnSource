/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.hit;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public abstract class HitResult {
    protected final Vec3d pos;

    protected HitResult(Vec3d arg) {
        this.pos = arg;
    }

    public double squaredDistanceTo(Entity arg) {
        double d = this.pos.x - arg.getX();
        double e = this.pos.y - arg.getY();
        double f = this.pos.z - arg.getZ();
        return d * d + e * e + f * f;
    }

    public abstract Type getType();

    public Vec3d getPos() {
        return this.pos;
    }

    public static enum Type {
        MISS,
        BLOCK,
        ENTITY;

    }
}

