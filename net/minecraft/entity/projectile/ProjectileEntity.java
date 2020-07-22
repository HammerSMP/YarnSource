/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ProjectileEntity
extends Entity {
    private UUID ownerUuid;
    private int ownerEntityId;
    private boolean leftOwner;

    ProjectileEntity(EntityType<? extends ProjectileEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getUuid();
            this.ownerEntityId = entity.getEntityId();
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUuid != null && this.world instanceof ServerWorld) {
            return ((ServerWorld)this.world).getEntity(this.ownerUuid);
        }
        if (this.ownerEntityId != 0) {
            return this.world.getEntityById(this.ownerEntityId);
        }
        return null;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        if (this.ownerUuid != null) {
            tag.putUuid("Owner", this.ownerUuid);
        }
        if (this.leftOwner) {
            tag.putBoolean("LeftOwner", true);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.containsUuid("Owner")) {
            this.ownerUuid = tag.getUuid("Owner");
        }
        this.leftOwner = tag.getBoolean("LeftOwner");
    }

    @Override
    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.method_26961();
        }
        super.tick();
    }

    private boolean method_26961() {
        Entity lv = this.getOwner();
        if (lv != null) {
            for (Entity lv2 : this.world.getOtherEntities(this, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), arg -> !arg.isSpectator() && arg.collides())) {
                if (lv2.getRootVehicle() != lv.getRootVehicle()) continue;
                return false;
            }
        }
        return true;
    }

    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        Vec3d lv = new Vec3d(x, y, z).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence).multiply(speed);
        this.setVelocity(lv);
        float i = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(lv));
        this.yaw = (float)(MathHelper.atan2(lv.x, lv.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(lv.y, i) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        float k = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float l = -MathHelper.sin((pitch + roll) * ((float)Math.PI / 180));
        float m = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        this.setVelocity(k, l, m, modifierZ, modifierXYZ);
        Vec3d lv = user.getVelocity();
        this.setVelocity(this.getVelocity().add(lv.x, user.isOnGround() ? 0.0 : lv.y, lv.z));
    }

    protected void onCollision(HitResult hitResult) {
        HitResult.Type lv = hitResult.getType();
        if (lv == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)hitResult);
        } else if (lv == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult)hitResult);
        }
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
    }

    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockHitResult lv = blockHitResult;
        BlockState lv2 = this.world.getBlockState(lv.getBlockPos());
        lv2.onProjectileHit(this.world, lv2, lv, this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float g = MathHelper.sqrt(x * x + z * z);
            this.pitch = (float)(MathHelper.atan2(y, g) * 57.2957763671875);
            this.yaw = (float)(MathHelper.atan2(x, z) * 57.2957763671875);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
        }
    }

    protected boolean method_26958(Entity arg) {
        if (arg.isSpectator() || !arg.isAlive() || !arg.collides()) {
            return false;
        }
        Entity lv = this.getOwner();
        return lv == null || this.leftOwner || !lv.isConnectedThroughVehicle(arg);
    }

    protected void method_26962() {
        Vec3d lv = this.getVelocity();
        float f = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(lv));
        this.pitch = ProjectileEntity.updateRotation(this.prevPitch, (float)(MathHelper.atan2(lv.y, f) * 57.2957763671875));
        this.yaw = ProjectileEntity.updateRotation(this.prevYaw, (float)(MathHelper.atan2(lv.x, lv.z) * 57.2957763671875));
    }

    protected static float updateRotation(float f, float g) {
        while (g - f < -180.0f) {
            f -= 360.0f;
        }
        while (g - f >= 180.0f) {
            f += 360.0f;
        }
        return MathHelper.lerp(0.2f, f, g);
    }
}

