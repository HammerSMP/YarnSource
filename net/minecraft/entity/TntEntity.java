/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntEntity
extends Entity {
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Nullable
    private LivingEntity causingEntity;
    private int fuseTimer = 80;

    public TntEntity(EntityType<? extends TntEntity> arg, World arg2) {
        super(arg, arg2);
        this.inanimate = true;
    }

    public TntEntity(World arg, double d, double e, double f, @Nullable LivingEntity arg2) {
        this((EntityType<? extends TntEntity>)EntityType.TNT, arg);
        this.updatePosition(d, e, f);
        double g = arg.random.nextDouble() * 6.2831854820251465;
        this.setVelocity(-Math.sin(g) * 0.02, 0.2f, -Math.cos(g) * 0.02);
        this.setFuse(80);
        this.prevX = d;
        this.prevY = e;
        this.prevZ = f;
        this.causingEntity = arg2;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, 80);
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    public void tick() {
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }
        --this.fuseTimer;
        if (this.fuseTimer <= 0) {
            this.remove();
            if (!this.world.isClient) {
                this.explode();
            }
        } else {
            this.updateWaterState();
            if (this.world.isClient) {
                this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    private void explode() {
        float f = 4.0f;
        this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625), this.getZ(), 4.0f, Explosion.DestructionType.BREAK);
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
        arg.putShort("Fuse", (short)this.getFuseTimer());
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
        this.setFuse(arg.getShort("Fuse"));
    }

    @Nullable
    public LivingEntity getCausingEntity() {
        return this.causingEntity;
    }

    @Override
    protected float getEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return 0.15f;
    }

    public void setFuse(int i) {
        this.dataTracker.set(FUSE, i);
        this.fuseTimer = i;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (FUSE.equals(arg)) {
            this.fuseTimer = this.getFuse();
        }
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public int getFuseTimer() {
        return this.fuseTimer;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

