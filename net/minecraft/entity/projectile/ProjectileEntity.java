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
    private int field_23739;
    private boolean field_23740;

    ProjectileEntity(EntityType<? extends ProjectileEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public void setOwner(@Nullable Entity arg) {
        if (arg != null) {
            this.ownerUuid = arg.getUuid();
            this.field_23739 = arg.getEntityId();
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUuid != null && this.world instanceof ServerWorld) {
            return ((ServerWorld)this.world).getEntity(this.ownerUuid);
        }
        if (this.field_23739 != 0) {
            return this.world.getEntityById(this.field_23739);
        }
        return null;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
        if (this.ownerUuid != null) {
            arg.putUuid("Owner", this.ownerUuid);
        }
        if (this.field_23740) {
            arg.putBoolean("LeftOwner", true);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
        if (arg.containsUuid("Owner")) {
            this.ownerUuid = arg.getUuid("Owner");
        }
        this.field_23740 = arg.getBoolean("LeftOwner");
    }

    @Override
    public void tick() {
        if (!this.field_23740) {
            this.field_23740 = this.method_26961();
        }
        super.tick();
    }

    private boolean method_26961() {
        Entity lv = this.getOwner();
        if (lv != null) {
            for (Entity lv2 : this.world.getEntities(this, this.getBoundingBox().expand(1.0), arg -> !arg.isSpectator() && arg.collides())) {
                if (lv2.getRootVehicle() != lv.getRootVehicle()) continue;
                return false;
            }
        }
        return true;
    }

    public void setVelocity(double d, double e, double f, float g, float h) {
        Vec3d lv = new Vec3d(d, e, f).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)h, this.random.nextGaussian() * (double)0.0075f * (double)h, this.random.nextGaussian() * (double)0.0075f * (double)h).multiply(g);
        this.setVelocity(lv);
        float i = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(lv));
        this.yaw = (float)(MathHelper.atan2(lv.x, lv.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(lv.y, i) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public void setProperties(Entity arg, float f, float g, float h, float i, float j) {
        float k = -MathHelper.sin(g * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
        float l = -MathHelper.sin((f + h) * ((float)Math.PI / 180));
        float m = MathHelper.cos(g * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
        this.setVelocity(k, l, m, i, j);
        Vec3d lv = arg.getVelocity();
        this.setVelocity(this.getVelocity().add(lv.x, arg.isOnGround() ? 0.0 : lv.y, lv.z));
    }

    protected void onCollision(HitResult arg) {
        HitResult.Type lv = arg.getType();
        if (lv == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)arg);
        } else if (lv == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult)arg);
        }
    }

    protected void onEntityHit(EntityHitResult arg) {
    }

    protected void onBlockHit(BlockHitResult arg) {
        BlockHitResult lv = arg;
        BlockState lv2 = this.world.getBlockState(lv.getBlockPos());
        lv2.onProjectileHit(this.world, lv2, lv, this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double d, double e, double f) {
        this.setVelocity(d, e, f);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float g = MathHelper.sqrt(d * d + f * f);
            this.pitch = (float)(MathHelper.atan2(e, g) * 57.2957763671875);
            this.yaw = (float)(MathHelper.atan2(d, f) * 57.2957763671875);
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
        return lv == null || this.field_23740 || !lv.isConnectedThroughVehicle(arg);
    }

    protected void method_26962() {
        Vec3d lv = this.getVelocity();
        float f = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(lv));
        this.pitch = ProjectileEntity.method_26960(this.prevPitch, (float)(MathHelper.atan2(lv.y, f) * 57.2957763671875));
        this.yaw = ProjectileEntity.method_26960(this.prevYaw, (float)(MathHelper.atan2(lv.x, lv.z) * 57.2957763671875));
    }

    protected static float method_26960(float f, float g) {
        while (g - f < -180.0f) {
            f -= 360.0f;
        }
        while (g - f >= 180.0f) {
            f += 360.0f;
        }
        return MathHelper.lerp(0.2f, f, g);
    }
}

