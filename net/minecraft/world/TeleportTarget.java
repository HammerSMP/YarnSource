/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import net.minecraft.util.math.Vec3d;

public class TeleportTarget {
    public final Vec3d position;
    public final Vec3d velocity;
    public final float yaw;
    public final float pitch;

    public TeleportTarget(Vec3d arg, Vec3d arg2, float f, float g) {
        this.position = arg;
        this.velocity = arg2;
        this.yaw = f;
        this.pitch = g;
    }
}

