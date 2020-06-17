/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface ItemSteerable {
    public boolean consumeOnAStickItem();

    public void setMovementInput(Vec3d var1);

    public float getSaddledSpeed();

    default public boolean travel(MobEntity arg, SaddledComponent arg2, Vec3d arg3) {
        Entity lv;
        if (!arg.isAlive()) {
            return false;
        }
        Entity entity = lv = arg.getPassengerList().isEmpty() ? null : arg.getPassengerList().get(0);
        if (!(arg.hasPassengers() && arg.canBeControlledByRider() && lv instanceof PlayerEntity)) {
            arg.stepHeight = 0.5f;
            arg.flyingSpeed = 0.02f;
            this.setMovementInput(arg3);
            return false;
        }
        arg.prevYaw = arg.yaw = lv.yaw;
        arg.pitch = lv.pitch * 0.5f;
        arg.setRotation(arg.yaw, arg.pitch);
        arg.bodyYaw = arg.yaw;
        arg.headYaw = arg.yaw;
        arg.stepHeight = 1.0f;
        arg.flyingSpeed = arg.getMovementSpeed() * 0.1f;
        if (arg2.boosted && arg2.field_23216++ > arg2.currentBoostTime) {
            arg2.boosted = false;
        }
        if (arg.isLogicalSideForUpdatingMovement()) {
            float f = this.getSaddledSpeed();
            if (arg2.boosted) {
                f += f * 1.15f * MathHelper.sin((float)arg2.field_23216 / (float)arg2.currentBoostTime * (float)Math.PI);
            }
            arg.setMovementSpeed(f);
            this.setMovementInput(new Vec3d(0.0, 0.0, 1.0));
            arg.bodyTrackingIncrements = 0;
        } else {
            arg.method_29242(arg, false);
            arg.setVelocity(Vec3d.ZERO);
        }
        return true;
    }
}

