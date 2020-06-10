/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface Angerable {
    public int getAngerTime();

    public void setAngerTime(int var1);

    @Nullable
    public UUID getAngryAt();

    public void setAngryAt(@Nullable UUID var1);

    public void chooseRandomAngerTime();

    default public void angerToTag(CompoundTag arg) {
        arg.putInt("AngerTime", this.getAngerTime());
        if (this.getAngryAt() != null) {
            arg.putUuid("AngryAt", this.getAngryAt());
        }
    }

    default public void angerFromTag(ServerWorld arg, CompoundTag arg2) {
        this.setAngerTime(arg2.getInt("AngerTime"));
        if (!arg2.containsUuid("AngryAt")) {
            this.setAngryAt(null);
            return;
        }
        UUID uUID = arg2.getUuid("AngryAt");
        this.setAngryAt(uUID);
        Entity lv = arg.getEntity(uUID);
        if (lv == null) {
            return;
        }
        if (lv instanceof MobEntity) {
            this.setAttacker((MobEntity)lv);
        }
        if (lv.getType() == EntityType.PLAYER) {
            this.setAttacking((PlayerEntity)lv);
        }
    }

    default public void tickAngerLogic(ServerWorld arg, boolean bl) {
        LivingEntity lv = this.getTarget();
        UUID uUID = this.getAngryAt();
        if ((lv == null || lv.isDead()) && uUID != null && arg.getEntity(uUID) instanceof MobEntity) {
            this.method_29922();
            return;
        }
        if (lv != null && !Objects.equals(uUID, lv.getUuid())) {
            this.setAngryAt(lv.getUuid());
            this.chooseRandomAngerTime();
        }
        if (!(this.getAngerTime() <= 0 || lv != null && lv.getType() == EntityType.PLAYER && bl)) {
            this.setAngerTime(this.getAngerTime() - 1);
            if (this.getAngerTime() == 0) {
                this.method_29922();
            }
        }
    }

    default public boolean shouldAngerAt(LivingEntity arg) {
        if (!EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(arg)) {
            return false;
        }
        if (arg.getType() == EntityType.PLAYER && this.method_29923(arg.world)) {
            return true;
        }
        return arg.getUuid().equals(this.getAngryAt());
    }

    default public boolean method_29923(World arg) {
        return arg.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.hasAngerTime() && this.getAngryAt() == null;
    }

    default public boolean hasAngerTime() {
        return this.getAngerTime() > 0;
    }

    default public void forgive(PlayerEntity arg) {
        if (!arg.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            return;
        }
        if (!arg.getUuid().equals(this.getAngryAt())) {
            return;
        }
        this.method_29922();
    }

    default public void method_29921() {
        this.method_29922();
        this.chooseRandomAngerTime();
    }

    default public void method_29922() {
        this.setAttacker(null);
        this.setAngryAt(null);
        this.setTarget(null);
        this.setAngerTime(0);
    }

    public void setAttacker(@Nullable LivingEntity var1);

    public void setAttacking(@Nullable PlayerEntity var1);

    public void setTarget(@Nullable LivingEntity var1);

    @Nullable
    public LivingEntity getTarget();
}

