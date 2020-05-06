/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.entity.decoration;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class AbstractDecorationEntity
extends Entity {
    protected static final Predicate<Entity> PREDICATE = arg -> arg instanceof AbstractDecorationEntity;
    private int obstructionCheckCounter;
    protected BlockPos attachmentPos;
    protected Direction facing = Direction.SOUTH;

    protected AbstractDecorationEntity(EntityType<? extends AbstractDecorationEntity> arg, World arg2) {
        super(arg, arg2);
    }

    protected AbstractDecorationEntity(EntityType<? extends AbstractDecorationEntity> arg, World arg2, BlockPos arg3) {
        this(arg, arg2);
        this.attachmentPos = arg3;
    }

    @Override
    protected void initDataTracker() {
    }

    protected void setFacing(Direction arg) {
        Validate.notNull((Object)arg);
        Validate.isTrue((boolean)arg.getAxis().isHorizontal());
        this.facing = arg;
        this.prevYaw = this.yaw = (float)(this.facing.getHorizontal() * 90);
        this.updateAttachmentPosition();
    }

    protected void updateAttachmentPosition() {
        if (this.facing == null) {
            return;
        }
        double d = (double)this.attachmentPos.getX() + 0.5;
        double e = (double)this.attachmentPos.getY() + 0.5;
        double f = (double)this.attachmentPos.getZ() + 0.5;
        double g = 0.46875;
        double h = this.method_6893(this.getWidthPixels());
        double i = this.method_6893(this.getHeightPixels());
        d -= (double)this.facing.getOffsetX() * 0.46875;
        f -= (double)this.facing.getOffsetZ() * 0.46875;
        Direction lv = this.facing.rotateYCounterclockwise();
        this.setPos(d += h * (double)lv.getOffsetX(), e += i, f += h * (double)lv.getOffsetZ());
        double j = this.getWidthPixels();
        double k = this.getHeightPixels();
        double l = this.getWidthPixels();
        if (this.facing.getAxis() == Direction.Axis.Z) {
            l = 1.0;
        } else {
            j = 1.0;
        }
        this.setBoundingBox(new Box(d - (j /= 32.0), e - (k /= 32.0), f - (l /= 32.0), d + j, e + k, f + l));
    }

    private double method_6893(int i) {
        return i % 32 == 0 ? 0.5 : 0.0;
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            if (this.getY() < -64.0) {
                this.destroy();
            }
            if (this.obstructionCheckCounter++ == 100) {
                this.obstructionCheckCounter = 0;
                if (!this.removed && !this.canStayAttached()) {
                    this.remove();
                    this.onBreak(null);
                }
            }
        }
    }

    public boolean canStayAttached() {
        if (!this.world.doesNotCollide(this)) {
            return false;
        }
        int i = Math.max(1, this.getWidthPixels() / 16);
        int j = Math.max(1, this.getHeightPixels() / 16);
        BlockPos lv = this.attachmentPos.offset(this.facing.getOpposite());
        Direction lv2 = this.facing.rotateYCounterclockwise();
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int k = 0; k < i; ++k) {
            for (int l = 0; l < j; ++l) {
                int m = (i - 1) / -2;
                int n = (j - 1) / -2;
                lv3.set(lv).move(lv2, k + m).move(Direction.UP, l + n);
                BlockState lv4 = this.world.getBlockState(lv3);
                if (lv4.getMaterial().isSolid() || AbstractRedstoneGateBlock.isRedstoneGate(lv4)) continue;
                return false;
            }
        }
        return this.world.getEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean handleAttack(Entity arg) {
        if (arg instanceof PlayerEntity) {
            PlayerEntity lv = (PlayerEntity)arg;
            if (!this.world.canPlayerModifyAt(lv, this.attachmentPos)) {
                return true;
            }
            return this.damage(DamageSource.player(lv), 0.0f);
        }
        return false;
    }

    @Override
    public Direction getHorizontalFacing() {
        return this.facing;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        if (!this.removed && !this.world.isClient) {
            this.remove();
            this.scheduleVelocityUpdate();
            this.onBreak(arg.getAttacker());
        }
        return true;
    }

    @Override
    public void move(MovementType arg, Vec3d arg2) {
        if (!this.world.isClient && !this.removed && arg2.lengthSquared() > 0.0) {
            this.remove();
            this.onBreak(null);
        }
    }

    @Override
    public void addVelocity(double d, double e, double f) {
        if (!this.world.isClient && !this.removed && d * d + e * e + f * f > 0.0) {
            this.remove();
            this.onBreak(null);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        arg.putByte("Facing", (byte)this.facing.getHorizontal());
        BlockPos lv = this.getDecorationBlockPos();
        arg.putInt("TileX", lv.getX());
        arg.putInt("TileY", lv.getY());
        arg.putInt("TileZ", lv.getZ());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        this.attachmentPos = new BlockPos(arg.getInt("TileX"), arg.getInt("TileY"), arg.getInt("TileZ"));
        this.facing = Direction.fromHorizontal(arg.getByte("Facing"));
    }

    public abstract int getWidthPixels();

    public abstract int getHeightPixels();

    public abstract void onBreak(@Nullable Entity var1);

    public abstract void onPlace();

    @Override
    public ItemEntity dropStack(ItemStack arg, float f) {
        ItemEntity lv = new ItemEntity(this.world, this.getX() + (double)((float)this.facing.getOffsetX() * 0.15f), this.getY() + (double)f, this.getZ() + (double)((float)this.facing.getOffsetZ() * 0.15f), arg);
        lv.setToDefaultPickupDelay();
        this.world.spawnEntity(lv);
        return lv;
    }

    @Override
    protected boolean shouldSetPositionOnLoad() {
        return false;
    }

    @Override
    public void updatePosition(double d, double e, double f) {
        this.attachmentPos = new BlockPos(d, e, f);
        this.updateAttachmentPosition();
        this.velocityDirty = true;
    }

    public BlockPos getDecorationBlockPos() {
        return this.attachmentPos;
    }

    @Override
    public float applyRotation(BlockRotation arg) {
        if (this.facing.getAxis() != Direction.Axis.Y) {
            switch (arg) {
                case CLOCKWISE_180: {
                    this.facing = this.facing.getOpposite();
                    break;
                }
                case COUNTERCLOCKWISE_90: {
                    this.facing = this.facing.rotateYCounterclockwise();
                    break;
                }
                case CLOCKWISE_90: {
                    this.facing = this.facing.rotateYClockwise();
                    break;
                }
            }
        }
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (arg) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 90.0f;
            }
            case CLOCKWISE_90: {
                return f + 270.0f;
            }
        }
        return f;
    }

    @Override
    public float applyMirror(BlockMirror arg) {
        return this.applyRotation(arg.getRotation(this.facing));
    }

    @Override
    public void onStruckByLightning(LightningEntity arg) {
    }

    @Override
    public void calculateDimensions() {
    }
}

