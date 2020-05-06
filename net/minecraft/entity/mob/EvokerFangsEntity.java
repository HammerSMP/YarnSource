/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class EvokerFangsEntity
extends Entity {
    private int warmup;
    private boolean field_7610;
    private int ticksLeft = 22;
    private boolean hasAttacked;
    private LivingEntity owner;
    private UUID ownerUuid;

    public EvokerFangsEntity(EntityType<? extends EvokerFangsEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public EvokerFangsEntity(World arg, double d, double e, double f, float g, int i, LivingEntity arg2) {
        this((EntityType<? extends EvokerFangsEntity>)EntityType.EVOKER_FANGS, arg);
        this.warmup = i;
        this.setOwner(arg2);
        this.yaw = g * 57.295776f;
        this.updatePosition(d, e, f);
    }

    @Override
    protected void initDataTracker() {
    }

    public void setOwner(@Nullable LivingEntity arg) {
        this.owner = arg;
        this.ownerUuid = arg == null ? null : arg.getUuid();
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity lv;
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld && (lv = ((ServerWorld)this.world).getEntity(this.ownerUuid)) instanceof LivingEntity) {
            this.owner = (LivingEntity)lv;
        }
        return this.owner;
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
        this.warmup = arg.getInt("Warmup");
        if (arg.containsUuidNew("Owner")) {
            this.ownerUuid = arg.getUuidNew("Owner");
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
        arg.putInt("Warmup", this.warmup);
        if (this.ownerUuid != null) {
            arg.putUuidNew("Owner", this.ownerUuid);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.hasAttacked) {
                --this.ticksLeft;
                if (this.ticksLeft == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                        double e = this.getY() + 0.05 + this.random.nextDouble();
                        double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                        double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        double h = 0.3 + this.random.nextDouble() * 0.3;
                        double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        this.world.addParticle(ParticleTypes.CRIT, d, e + 1.0, f, g, h, j);
                    }
                }
            }
        } else if (--this.warmup < 0) {
            if (this.warmup == -8) {
                List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(0.2, 0.0, 0.2));
                for (LivingEntity lv : list) {
                    this.damage(lv);
                }
            }
            if (!this.field_7610) {
                this.world.sendEntityStatus(this, (byte)4);
                this.field_7610 = true;
            }
            if (--this.ticksLeft < 0) {
                this.remove();
            }
        }
    }

    private void damage(LivingEntity arg) {
        LivingEntity lv = this.getOwner();
        if (!arg.isAlive() || arg.isInvulnerable() || arg == lv) {
            return;
        }
        if (lv == null) {
            arg.damage(DamageSource.MAGIC, 6.0f);
        } else {
            if (lv.isTeammate(arg)) {
                return;
            }
            arg.damage(DamageSource.magic(this, lv), 6.0f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        super.handleStatus(b);
        if (b == 4) {
            this.hasAttacked = true;
            if (!this.isSilent()) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0f, this.random.nextFloat() * 0.2f + 0.85f, false);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getAnimationProgress(float f) {
        if (!this.hasAttacked) {
            return 0.0f;
        }
        int i = this.ticksLeft - 2;
        if (i <= 0) {
            return 1.0f;
        }
        return 1.0f - ((float)i - f) / 20.0f;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

