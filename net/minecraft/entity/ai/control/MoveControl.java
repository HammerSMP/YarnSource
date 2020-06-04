/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.control;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class MoveControl {
    protected final MobEntity entity;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected double speed;
    protected float forwardMovement;
    protected float sidewaysMovement;
    protected State state = State.WAIT;

    public MoveControl(MobEntity arg) {
        this.entity = arg;
    }

    public boolean isMoving() {
        return this.state == State.MOVE_TO;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void moveTo(double d, double e, double f, double g) {
        this.targetX = d;
        this.targetY = e;
        this.targetZ = f;
        this.speed = g;
        if (this.state != State.JUMPING) {
            this.state = State.MOVE_TO;
        }
    }

    public void strafeTo(float f, float g) {
        this.state = State.STRAFE;
        this.forwardMovement = f;
        this.sidewaysMovement = g;
        this.speed = 0.25;
    }

    public void tick() {
        if (this.state == State.STRAFE) {
            float n;
            float f = (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            float g = (float)this.speed * f;
            float h = this.forwardMovement;
            float i = this.sidewaysMovement;
            float j = MathHelper.sqrt(h * h + i * i);
            if (j < 1.0f) {
                j = 1.0f;
            }
            j = g / j;
            float k = MathHelper.sin(this.entity.yaw * ((float)Math.PI / 180));
            float l = MathHelper.cos(this.entity.yaw * ((float)Math.PI / 180));
            float m = (h *= j) * l - (i *= j) * k;
            if (!this.method_25946(m, n = i * l + h * k)) {
                this.forwardMovement = 1.0f;
                this.sidewaysMovement = 0.0f;
            }
            this.entity.setMovementSpeed(g);
            this.entity.setForwardSpeed(this.forwardMovement);
            this.entity.setSidewaysSpeed(this.sidewaysMovement);
            this.state = State.WAIT;
        } else if (this.state == State.MOVE_TO) {
            this.state = State.WAIT;
            double d = this.targetX - this.entity.getX();
            double e = this.targetZ - this.entity.getZ();
            double o = this.targetY - this.entity.getY();
            double p = d * d + o * o + e * e;
            if (p < 2.500000277905201E-7) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            float q = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
            this.entity.yaw = this.changeAngle(this.entity.yaw, q, 90.0f);
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            BlockPos lv = this.entity.getBlockPos();
            BlockState lv2 = this.entity.world.getBlockState(lv);
            Block lv3 = lv2.getBlock();
            VoxelShape lv4 = lv2.getCollisionShape(this.entity.world, lv);
            if (o > (double)this.entity.stepHeight && d * d + e * e < (double)Math.max(1.0f, this.entity.getWidth()) || !lv4.isEmpty() && this.entity.getY() < lv4.getMax(Direction.Axis.Y) + (double)lv.getY() && !lv3.isIn(BlockTags.DOORS) && !lv3.isIn(BlockTags.FENCES)) {
                this.entity.getJumpControl().setActive();
                this.state = State.JUMPING;
            }
        } else if (this.state == State.JUMPING) {
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            if (this.entity.isOnGround()) {
                this.state = State.WAIT;
            }
        } else {
            this.entity.setForwardSpeed(0.0f);
        }
    }

    private boolean method_25946(float f, float g) {
        PathNodeMaker lv2;
        EntityNavigation lv = this.entity.getNavigation();
        return lv == null || (lv2 = lv.getNodeMaker()) == null || lv2.getDefaultNodeType(this.entity.world, MathHelper.floor(this.entity.getX() + (double)f), MathHelper.floor(this.entity.getY()), MathHelper.floor(this.entity.getZ() + (double)g)) == PathNodeType.WALKABLE;
    }

    protected float changeAngle(float f, float g, float h) {
        float j;
        float i = MathHelper.wrapDegrees(g - f);
        if (i > h) {
            i = h;
        }
        if (i < -h) {
            i = -h;
        }
        if ((j = f + i) < 0.0f) {
            j += 360.0f;
        } else if (j > 360.0f) {
            j -= 360.0f;
        }
        return j;
    }

    public double getTargetX() {
        return this.targetX;
    }

    public double getTargetY() {
        return this.targetY;
    }

    public double getTargetZ() {
        return this.targetZ;
    }

    public static enum State {
        WAIT,
        MOVE_TO,
        STRAFE,
        JUMPING;

    }
}

