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

    default public void angerToTag(CompoundTag tag) {
        tag.putInt("AngerTime", this.getAngerTime());
        if (this.getAngryAt() != null) {
            tag.putUuid("AngryAt", this.getAngryAt());
        }
    }

    default public void angerFromTag(ServerWorld world, CompoundTag tag) {
        this.setAngerTime(tag.getInt("AngerTime"));
        if (!tag.containsUuid("AngryAt")) {
            this.setAngryAt(null);
            return;
        }
        UUID uUID = tag.getUuid("AngryAt");
        this.setAngryAt(uUID);
        Entity lv = world.getEntity(uUID);
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

    default public void tickAngerLogic(ServerWorld world, boolean bl) {
        LivingEntity lv = this.getTarget();
        UUID uUID = this.getAngryAt();
        if ((lv == null || lv.isDead()) && uUID != null && world.getEntity(uUID) instanceof MobEntity) {
            this.stopAnger();
            return;
        }
        if (lv != null && !Objects.equals(uUID, lv.getUuid())) {
            this.setAngryAt(lv.getUuid());
            this.chooseRandomAngerTime();
        }
        if (!(this.getAngerTime() <= 0 || lv != null && lv.getType() == EntityType.PLAYER && bl)) {
            this.setAngerTime(this.getAngerTime() - 1);
            if (this.getAngerTime() == 0) {
                this.stopAnger();
            }
        }
    }

    default public boolean shouldAngerAt(LivingEntity entity) {
        if (!EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(entity)) {
            return false;
        }
        if (entity.getType() == EntityType.PLAYER && this.isUniversallyAngry(entity.world)) {
            return true;
        }
        return entity.getUuid().equals(this.getAngryAt());
    }

    default public boolean isUniversallyAngry(World world) {
        return world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.hasAngerTime() && this.getAngryAt() == null;
    }

    default public boolean hasAngerTime() {
        return this.getAngerTime() > 0;
    }

    default public void forgive(PlayerEntity player) {
        if (!player.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            return;
        }
        if (!player.getUuid().equals(this.getAngryAt())) {
            return;
        }
        this.stopAnger();
    }

    default public void universallyAnger() {
        this.stopAnger();
        this.chooseRandomAngerTime();
    }

    default public void stopAnger() {
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

