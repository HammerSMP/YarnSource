/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile.thrown;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class EnderPearlEntity
extends ThrownItemEntity {
    private LivingEntity owner;

    public EnderPearlEntity(EntityType<? extends EnderPearlEntity> arg, World arg2) {
        super((EntityType<? extends ThrownItemEntity>)arg, arg2);
    }

    public EnderPearlEntity(World arg, LivingEntity arg2) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.ENDER_PEARL, arg2, arg);
        this.owner = arg2;
    }

    @Environment(value=EnvType.CLIENT)
    public EnderPearlEntity(World arg, double d, double e, double f) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.ENDER_PEARL, d, e, f, arg);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void onEntityHit(EntityHitResult arg) {
        super.onEntityHit(arg);
        Entity lv = arg.getEntity();
        if (lv == this.owner) {
            return;
        }
        lv.damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0f);
    }

    @Override
    protected void onBlockHit(BlockHitResult arg) {
        super.onBlockHit(arg);
        Entity lv = this.getOwner();
        BlockPos lv2 = arg.getBlockPos();
        BlockEntity lv3 = this.world.getBlockEntity(lv2);
        if (lv3 instanceof EndGatewayBlockEntity) {
            EndGatewayBlockEntity lv4 = (EndGatewayBlockEntity)lv3;
            if (lv != null) {
                if (lv instanceof ServerPlayerEntity) {
                    Criteria.ENTER_BLOCK.trigger((ServerPlayerEntity)lv, this.world.getBlockState(lv2));
                }
                lv4.tryTeleportingEntity(lv);
                this.remove();
            } else {
                lv4.tryTeleportingEntity(this);
            }
        }
    }

    @Override
    protected void onCollision(HitResult arg) {
        super.onCollision(arg);
        Entity lv = this.getOwner();
        for (int i = 0; i < 32; ++i) {
            this.world.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }
        if (!this.world.isClient && !this.removed) {
            if (lv instanceof ServerPlayerEntity) {
                ServerPlayerEntity lv2 = (ServerPlayerEntity)lv;
                if (lv2.networkHandler.getConnection().isOpen() && lv2.world == this.world && !lv2.isSleeping()) {
                    if (this.random.nextFloat() < 0.05f && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                        EndermiteEntity lv3 = EntityType.ENDERMITE.create(this.world);
                        lv3.setPlayerSpawned(true);
                        lv3.refreshPositionAndAngles(lv.getX(), lv.getY(), lv.getZ(), lv.yaw, lv.pitch);
                        this.world.spawnEntity(lv3);
                    }
                    if (lv.hasVehicle()) {
                        lv.stopRiding();
                    }
                    lv.requestTeleport(this.getX(), this.getY(), this.getZ());
                    lv.fallDistance = 0.0f;
                    lv.damage(DamageSource.FALL, 5.0f);
                }
            } else if (lv != null) {
                lv.requestTeleport(this.getX(), this.getY(), this.getZ());
                lv.fallDistance = 0.0f;
            }
            this.remove();
        }
    }

    @Override
    public void tick() {
        Entity lv = this.getOwner();
        if (lv != null && lv instanceof PlayerEntity && !lv.isAlive()) {
            this.remove();
        } else {
            super.tick();
        }
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType arg) {
        Entity lv = this.getOwner();
        if (lv.dimension != arg) {
            this.setOwner(null);
        }
        return super.changeDimension(arg);
    }
}
