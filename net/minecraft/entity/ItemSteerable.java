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

    default public boolean travel(MobEntity entity, SaddledComponent saddledEntity, Vec3d movementInput) {
        Entity lv;
        if (!entity.isAlive()) {
            return false;
        }
        Entity entity2 = lv = entity.getPassengerList().isEmpty() ? null : entity.getPassengerList().get(0);
        if (!(entity.hasPassengers() && entity.canBeControlledByRider() && lv instanceof PlayerEntity)) {
            entity.stepHeight = 0.5f;
            entity.flyingSpeed = 0.02f;
            this.setMovementInput(movementInput);
            return false;
        }
        entity.prevYaw = entity.yaw = lv.yaw;
        entity.pitch = lv.pitch * 0.5f;
        entity.setRotation(entity.yaw, entity.pitch);
        entity.bodyYaw = entity.yaw;
        entity.headYaw = entity.yaw;
        entity.stepHeight = 1.0f;
        entity.flyingSpeed = entity.getMovementSpeed() * 0.1f;
        if (saddledEntity.boosted && saddledEntity.field_23216++ > saddledEntity.currentBoostTime) {
            saddledEntity.boosted = false;
        }
        if (entity.isLogicalSideForUpdatingMovement()) {
            float f = this.getSaddledSpeed();
            if (saddledEntity.boosted) {
                f += f * 1.15f * MathHelper.sin((float)saddledEntity.field_23216 / (float)saddledEntity.currentBoostTime * (float)Math.PI);
            }
            entity.setMovementSpeed(f);
            this.setMovementInput(new Vec3d(0.0, 0.0, 1.0));
            entity.bodyTrackingIncrements = 0;
        } else {
            entity.method_29242(entity, false);
            entity.setVelocity(Vec3d.ZERO);
        }
        return true;
    }
}

