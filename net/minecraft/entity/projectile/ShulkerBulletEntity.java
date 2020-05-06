/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class ShulkerBulletEntity
extends ProjectileEntity {
    private Entity target;
    @Nullable
    private Direction direction;
    private int stepCount;
    private double targetX;
    private double targetY;
    private double targetZ;
    @Nullable
    private UUID targetUuid;

    public ShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
        this.noClip = true;
    }

    @Environment(value=EnvType.CLIENT)
    public ShulkerBulletEntity(World arg, double d, double e, double f, double g, double h, double i) {
        this((EntityType<? extends ShulkerBulletEntity>)EntityType.SHULKER_BULLET, arg);
        this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
        this.setVelocity(g, h, i);
    }

    public ShulkerBulletEntity(World arg, LivingEntity arg2, Entity arg3, Direction.Axis arg4) {
        this((EntityType<? extends ShulkerBulletEntity>)EntityType.SHULKER_BULLET, arg);
        this.setOwner(arg2);
        BlockPos lv = arg2.getBlockPos();
        double d = (double)lv.getX() + 0.5;
        double e = (double)lv.getY() + 0.5;
        double f = (double)lv.getZ() + 0.5;
        this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
        this.target = arg3;
        this.direction = Direction.UP;
        this.method_7486(arg4);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.target != null) {
            arg.putUuidNew("Target", this.target.getUuid());
        }
        if (this.direction != null) {
            arg.putInt("Dir", this.direction.getId());
        }
        arg.putInt("Steps", this.stepCount);
        arg.putDouble("TXD", this.targetX);
        arg.putDouble("TYD", this.targetY);
        arg.putDouble("TZD", this.targetZ);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.stepCount = arg.getInt("Steps");
        this.targetX = arg.getDouble("TXD");
        this.targetY = arg.getDouble("TYD");
        this.targetZ = arg.getDouble("TZD");
        if (arg.contains("Dir", 99)) {
            this.direction = Direction.byId(arg.getInt("Dir"));
        }
        if (arg.containsUuidNew("Target")) {
            this.targetUuid = arg.getUuidNew("Target");
        }
    }

    @Override
    protected void initDataTracker() {
    }

    private void setDirection(@Nullable Direction arg) {
        this.direction = arg;
    }

    private void method_7486(@Nullable Direction.Axis arg) {
        BlockPos lv2;
        double d = 0.5;
        if (this.target == null) {
            BlockPos lv = this.getBlockPos().down();
        } else {
            d = (double)this.target.getHeight() * 0.5;
            lv2 = new BlockPos(this.target.getX(), this.target.getY() + d, this.target.getZ());
        }
        double e = (double)lv2.getX() + 0.5;
        double f = (double)lv2.getY() + d;
        double g = (double)lv2.getZ() + 0.5;
        Direction lv3 = null;
        if (!lv2.isWithinDistance(this.getPos(), 2.0)) {
            BlockPos lv4 = this.getBlockPos();
            ArrayList list = Lists.newArrayList();
            if (arg != Direction.Axis.X) {
                if (lv4.getX() < lv2.getX() && this.world.isAir(lv4.east())) {
                    list.add(Direction.EAST);
                } else if (lv4.getX() > lv2.getX() && this.world.isAir(lv4.west())) {
                    list.add(Direction.WEST);
                }
            }
            if (arg != Direction.Axis.Y) {
                if (lv4.getY() < lv2.getY() && this.world.isAir(lv4.up())) {
                    list.add(Direction.UP);
                } else if (lv4.getY() > lv2.getY() && this.world.isAir(lv4.down())) {
                    list.add(Direction.DOWN);
                }
            }
            if (arg != Direction.Axis.Z) {
                if (lv4.getZ() < lv2.getZ() && this.world.isAir(lv4.south())) {
                    list.add(Direction.SOUTH);
                } else if (lv4.getZ() > lv2.getZ() && this.world.isAir(lv4.north())) {
                    list.add(Direction.NORTH);
                }
            }
            lv3 = Direction.random(this.random);
            if (list.isEmpty()) {
                for (int i = 5; !this.world.isAir(lv4.offset(lv3)) && i > 0; --i) {
                    lv3 = Direction.random(this.random);
                }
            } else {
                lv3 = (Direction)list.get(this.random.nextInt(list.size()));
            }
            e = this.getX() + (double)lv3.getOffsetX();
            f = this.getY() + (double)lv3.getOffsetY();
            g = this.getZ() + (double)lv3.getOffsetZ();
        }
        this.setDirection(lv3);
        double h = e - this.getX();
        double j = f - this.getY();
        double k = g - this.getZ();
        double l = MathHelper.sqrt(h * h + j * j + k * k);
        if (l == 0.0) {
            this.targetX = 0.0;
            this.targetY = 0.0;
            this.targetZ = 0.0;
        } else {
            this.targetX = h / l * 0.15;
            this.targetY = j / l * 0.15;
            this.targetZ = k / l * 0.15;
        }
        this.velocityDirty = true;
        this.stepCount = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            if (this.target == null && this.targetUuid != null) {
                this.target = ((ServerWorld)this.world).getEntity(this.targetUuid);
                if (this.target == null) {
                    this.targetUuid = null;
                }
            }
            if (!(this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && ((PlayerEntity)this.target).isSpectator())) {
                this.targetX = MathHelper.clamp(this.targetX * 1.025, -1.0, 1.0);
                this.targetY = MathHelper.clamp(this.targetY * 1.025, -1.0, 1.0);
                this.targetZ = MathHelper.clamp(this.targetZ * 1.025, -1.0, 1.0);
                Vec3d lv = this.getVelocity();
                this.setVelocity(lv.add((this.targetX - lv.x) * 0.2, (this.targetY - lv.y) * 0.2, (this.targetZ - lv.z) * 0.2));
            } else if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            }
            HitResult lv2 = ProjectileUtil.getCollision(this, this::method_26958, RayTraceContext.ShapeType.COLLIDER);
            if (lv2.getType() != HitResult.Type.MISS) {
                this.onCollision(lv2);
            }
        }
        Vec3d lv3 = this.getVelocity();
        this.updatePosition(this.getX() + lv3.x, this.getY() + lv3.y, this.getZ() + lv3.z);
        ProjectileUtil.method_7484(this, 0.5f);
        if (this.world.isClient) {
            this.world.addParticle(ParticleTypes.END_ROD, this.getX() - lv3.x, this.getY() - lv3.y + 0.15, this.getZ() - lv3.z, 0.0, 0.0, 0.0);
        } else if (this.target != null && !this.target.removed) {
            if (this.stepCount > 0) {
                --this.stepCount;
                if (this.stepCount == 0) {
                    this.method_7486(this.direction == null ? null : this.direction.getAxis());
                }
            }
            if (this.direction != null) {
                BlockPos lv4 = this.getBlockPos();
                Direction.Axis lv5 = this.direction.getAxis();
                if (this.world.isTopSolid(lv4.offset(this.direction), this)) {
                    this.method_7486(lv5);
                } else {
                    BlockPos lv6 = this.target.getBlockPos();
                    if (lv5 == Direction.Axis.X && lv4.getX() == lv6.getX() || lv5 == Direction.Axis.Z && lv4.getZ() == lv6.getZ() || lv5 == Direction.Axis.Y && lv4.getY() == lv6.getY()) {
                        this.method_7486(lv5);
                    }
                }
            }
        }
    }

    @Override
    protected boolean method_26958(Entity arg) {
        return super.method_26958(arg) && !arg.noClip;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        return d < 16384.0;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    protected void onEntityHit(EntityHitResult arg) {
        super.onEntityHit(arg);
        Entity lv = arg.getEntity();
        Entity lv2 = this.getOwner();
        LivingEntity lv3 = lv2 instanceof LivingEntity ? (LivingEntity)lv2 : null;
        boolean bl = lv.damage(DamageSource.mobProjectile(this, lv3).setProjectile(), 4.0f);
        if (bl) {
            this.dealDamage(lv3, lv);
            if (lv instanceof LivingEntity) {
                ((LivingEntity)lv).addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 200));
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult arg) {
        super.onBlockHit(arg);
        ((ServerWorld)this.world).spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0f, 1.0f);
    }

    @Override
    protected void onCollision(HitResult arg) {
        super.onCollision(arg);
        this.remove();
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (!this.world.isClient) {
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0f, 1.0f);
            ((ServerWorld)this.world).spawnParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
            this.remove();
        }
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

