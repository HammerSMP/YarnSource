/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntMinecartEntity
extends AbstractMinecartEntity {
    private int fuseTicks = -1;

    public TntMinecartEntity(EntityType<? extends TntMinecartEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public TntMinecartEntity(World world, double x, double y, double z) {
        super(EntityType.TNT_MINECART, world, x, y, z);
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.TNT;
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.TNT.getDefaultState();
    }

    @Override
    public void tick() {
        double d;
        super.tick();
        if (this.fuseTicks > 0) {
            --this.fuseTicks;
            this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        } else if (this.fuseTicks == 0) {
            this.explode(TntMinecartEntity.squaredHorizontalLength(this.getVelocity()));
        }
        if (this.horizontalCollision && (d = TntMinecartEntity.squaredHorizontalLength(this.getVelocity())) >= (double)0.01f) {
            this.explode(d);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        PersistentProjectileEntity lv2;
        Entity lv = source.getSource();
        if (lv instanceof PersistentProjectileEntity && (lv2 = (PersistentProjectileEntity)lv).isOnFire()) {
            this.explode(lv2.getVelocity().lengthSquared());
        }
        return super.damage(source, amount);
    }

    @Override
    public void dropItems(DamageSource damageSource) {
        double d = TntMinecartEntity.squaredHorizontalLength(this.getVelocity());
        if (damageSource.isFire() || damageSource.isExplosive() || d >= (double)0.01f) {
            if (this.fuseTicks < 0) {
                this.prime();
                this.fuseTicks = this.random.nextInt(20) + this.random.nextInt(20);
            }
            return;
        }
        super.dropItems(damageSource);
        if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(Blocks.TNT);
        }
    }

    protected void explode(double d) {
        if (!this.world.isClient) {
            double e = Math.sqrt(d);
            if (e > 5.0) {
                e = 5.0;
            }
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)(4.0 + this.random.nextDouble() * 1.5 * e), Explosion.DestructionType.BREAK);
            this.remove();
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        if (fallDistance >= 3.0f) {
            float h = fallDistance / 10.0f;
            this.explode(h * h);
        }
        return super.handleFallDamage(fallDistance, damageMultiplier);
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        if (powered && this.fuseTicks < 0) {
            this.prime();
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 10) {
            this.prime();
        } else {
            super.handleStatus(status);
        }
    }

    public void prime() {
        this.fuseTicks = 80;
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)10);
            if (!this.isSilent()) {
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public int getFuseTicks() {
        return this.fuseTicks;
    }

    public boolean isPrimed() {
        return this.fuseTicks > -1;
    }

    @Override
    public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        if (this.isPrimed() && (blockState.isIn(BlockTags.RAILS) || world.getBlockState(pos.up()).isIn(BlockTags.RAILS))) {
            return 0.0f;
        }
        return super.getEffectiveExplosionResistance(explosion, world, pos, blockState, fluidState, max);
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
        if (this.isPrimed() && (state.isIn(BlockTags.RAILS) || world.getBlockState(pos.up()).isIn(BlockTags.RAILS))) {
            return false;
        }
        return super.canExplosionDestroyBlock(explosion, world, pos, state, explosionPower);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("TNTFuse", 99)) {
            this.fuseTicks = tag.getInt("TNTFuse");
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("TNTFuse", this.fuseTicks);
    }
}

