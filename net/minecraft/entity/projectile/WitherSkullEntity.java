/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class WitherSkullEntity
extends ExplosiveProjectileEntity {
    private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(WitherSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public WitherSkullEntity(EntityType<? extends WitherSkullEntity> arg, World arg2) {
        super((EntityType<? extends ExplosiveProjectileEntity>)arg, arg2);
    }

    public WitherSkullEntity(World world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(EntityType.WITHER_SKULL, owner, directionX, directionY, directionZ, world);
    }

    @Environment(value=EnvType.CLIENT)
    public WitherSkullEntity(World world, double x, double y, double z, double directionX, double directionY, double directionZ) {
        super(EntityType.WITHER_SKULL, x, y, z, directionX, directionY, directionZ, world);
    }

    @Override
    protected float getDrag() {
        return this.isCharged() ? 0.73f : super.getDrag();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        if (this.isCharged() && WitherEntity.canDestroy(blockState)) {
            return Math.min(0.8f, max);
        }
        return max;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        boolean bl2;
        super.onEntityHit(entityHitResult);
        if (this.world.isClient) {
            return;
        }
        Entity lv = entityHitResult.getEntity();
        Entity lv2 = this.getOwner();
        if (lv2 instanceof LivingEntity) {
            LivingEntity lv3 = (LivingEntity)lv2;
            boolean bl = lv.damage(DamageSource.witherSkull(this, lv3), 8.0f);
            if (bl) {
                if (lv.isAlive()) {
                    this.dealDamage(lv3, lv);
                } else {
                    lv3.heal(5.0f);
                }
            }
        } else {
            bl2 = lv.damage(DamageSource.MAGIC, 5.0f);
        }
        if (bl2 && lv instanceof LivingEntity) {
            int i = 0;
            if (this.world.getDifficulty() == Difficulty.NORMAL) {
                i = 10;
            } else if (this.world.getDifficulty() == Difficulty.HARD) {
                i = 40;
            }
            if (i > 0) {
                ((LivingEntity)lv).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * i, 1));
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            Explosion.DestructionType lv = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 1.0f, false, lv);
            this.remove();
        }
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(CHARGED, false);
    }

    public boolean isCharged() {
        return this.dataTracker.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        this.dataTracker.set(CHARGED, charged);
    }

    @Override
    protected boolean isBurning() {
        return false;
    }
}

