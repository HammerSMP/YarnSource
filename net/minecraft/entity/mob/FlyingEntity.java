/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class FlyingEntity
extends MobEntity {
    protected FlyingEntity(EntityType<? extends FlyingEntity> arg, World arg2) {
        super((EntityType<? extends MobEntity>)arg, arg2);
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    protected void fall(double d, boolean bl, BlockState arg, BlockPos arg2) {
    }

    @Override
    public void travel(Vec3d arg) {
        if (this.isTouchingWater()) {
            this.updateVelocity(0.02f, arg);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.8f));
        } else if (this.isInLava()) {
            this.updateVelocity(0.02f, arg);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.5));
        } else {
            float f = 0.91f;
            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.91f;
            }
            float g = 0.16277137f / (f * f * f);
            f = 0.91f;
            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.91f;
            }
            this.updateVelocity(this.onGround ? 0.1f * g : 0.02f, arg);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(f));
        }
        this.lastLimbDistance = this.limbDistance;
        double d = this.getX() - this.prevX;
        double e = this.getZ() - this.prevZ;
        float h = MathHelper.sqrt(d * d + e * e) * 4.0f;
        if (h > 1.0f) {
            h = 1.0f;
        }
        this.limbDistance += (h - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }

    @Override
    public boolean isClimbing() {
        return false;
    }
}

