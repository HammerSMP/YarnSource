/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.hit;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class EntityHitResult
extends HitResult {
    private final Entity entity;

    public EntityHitResult(Entity arg) {
        this(arg, arg.getPos());
    }

    public EntityHitResult(Entity arg, Vec3d arg2) {
        super(arg2);
        this.entity = arg;
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public HitResult.Type getType() {
        return HitResult.Type.ENTITY;
    }
}

