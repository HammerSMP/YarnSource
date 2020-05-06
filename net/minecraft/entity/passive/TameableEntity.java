/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class TameableEntity
extends AnimalEntity {
    protected static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private boolean sitting;

    protected TameableEntity(EntityType<? extends TameableEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.onTamedChanged();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TAMEABLE_FLAGS, (byte)0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.getOwnerUuid() != null) {
            arg.putUuidNew("Owner", this.getOwnerUuid());
        }
        arg.putBoolean("Sitting", this.sitting);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        UUID uUID2;
        super.readCustomDataFromTag(arg);
        if (arg.containsUuidNew("Owner")) {
            UUID uUID = arg.getUuidNew("Owner");
        } else {
            String string = arg.getString("Owner");
            uUID2 = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID2 != null) {
            try {
                this.setOwnerUuid(uUID2);
                this.setTamed(true);
            }
            catch (Throwable throwable) {
                this.setTamed(false);
            }
        }
        this.sitting = arg.getBoolean("Sitting");
        this.setSitting(this.sitting);
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity arg) {
        return !this.isLeashed();
    }

    @Environment(value=EnvType.CLIENT)
    protected void showEmoteParticle(boolean bl) {
        DefaultParticleType lv = ParticleTypes.HEART;
        if (!bl) {
            lv = ParticleTypes.SMOKE;
        }
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(lv, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 7) {
            this.showEmoteParticle(true);
        } else if (b == 6) {
            this.showEmoteParticle(false);
        } else {
            super.handleStatus(b);
        }
    }

    public boolean isTamed() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
    }

    public void setTamed(boolean bl) {
        byte b = this.dataTracker.get(TAMEABLE_FLAGS);
        if (bl) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 4));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & 0xFFFFFFFB));
        }
        this.onTamedChanged();
    }

    protected void onTamedChanged() {
    }

    public boolean isSitting() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
    }

    public void setSitting(boolean bl) {
        byte b = this.dataTracker.get(TAMEABLE_FLAGS);
        if (bl) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 1));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & 0xFFFFFFFE));
        }
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uUID) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uUID));
    }

    public void setOwner(PlayerEntity arg) {
        this.setTamed(true);
        this.setOwnerUuid(arg.getUuid());
        if (arg instanceof ServerPlayerEntity) {
            Criteria.TAME_ANIMAL.trigger((ServerPlayerEntity)arg, this);
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uUID = this.getOwnerUuid();
            if (uUID == null) {
                return null;
            }
            return this.world.getPlayerByUuid(uUID);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    @Override
    public boolean canTarget(LivingEntity arg) {
        if (this.isOwner(arg)) {
            return false;
        }
        return super.canTarget(arg);
    }

    public boolean isOwner(LivingEntity arg) {
        return arg == this.getOwner();
    }

    public boolean canAttackWithOwner(LivingEntity arg, LivingEntity arg2) {
        return true;
    }

    @Override
    public AbstractTeam getScoreboardTeam() {
        LivingEntity lv;
        if (this.isTamed() && (lv = this.getOwner()) != null) {
            return lv.getScoreboardTeam();
        }
        return super.getScoreboardTeam();
    }

    @Override
    public boolean isTeammate(Entity arg) {
        if (this.isTamed()) {
            LivingEntity lv = this.getOwner();
            if (arg == lv) {
                return true;
            }
            if (lv != null) {
                return lv.isTeammate(arg);
            }
        }
        return super.isTeammate(arg);
    }

    @Override
    public void onDeath(DamageSource arg) {
        if (!this.world.isClient && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
            this.getOwner().sendSystemMessage(this.getDamageTracker().getDeathMessage());
        }
        super.onDeath(arg);
    }

    public boolean method_24345() {
        return this.sitting;
    }

    public void method_24346(boolean bl) {
        this.sitting = bl;
    }
}

