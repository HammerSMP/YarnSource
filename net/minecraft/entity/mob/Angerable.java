/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
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

    default public void angerFromTag(World arg, CompoundTag arg2) {
        this.setAngerTime(arg2.getInt("AngerTime"));
        if (arg2.containsUuid("AngryAt")) {
            PlayerEntity lv;
            this.setAngryAt(arg2.getUuid("AngryAt"));
            UUID uUID = this.getAngryAt();
            PlayerEntity playerEntity = lv = uUID == null ? null : arg.getPlayerByUuid(uUID);
            if (lv != null) {
                this.setAttacker(lv);
                this.method_29505(lv);
            }
        }
    }

    default public void tickAngerLogic() {
        LivingEntity lv = this.getTarget();
        if (lv != null && lv.getType() == EntityType.PLAYER) {
            this.setAngryAt(lv.getUuid());
            if (this.getAngerTime() <= 0) {
                this.chooseRandomAngerTime();
            }
        } else {
            int i = this.getAngerTime();
            if (i > 0) {
                this.setAngerTime(i - 1);
                if (this.getAngerTime() == 0) {
                    this.setAngryAt(null);
                }
            }
        }
    }

    default public boolean shouldAngerAt(LivingEntity arg) {
        if (arg instanceof PlayerEntity && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(arg)) {
            boolean bl = arg.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER);
            return bl ? this.hasAngerTime() : arg.getUuid().equals(this.getAngryAt());
        }
        return false;
    }

    default public boolean hasAngerTime() {
        return this.getAngerTime() > 0;
    }

    default public void forgive(PlayerEntity arg) {
        if (!arg.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            // empty if block
        }
        if (!arg.getUuid().equals(this.getAngryAt())) {
            return;
        }
        this.setAttacker(null);
        this.setAngryAt(null);
        this.setTarget(null);
        this.setAngerTime(0);
    }

    public void setAttacker(@Nullable LivingEntity var1);

    public void method_29505(@Nullable PlayerEntity var1);

    public void setTarget(@Nullable LivingEntity var1);

    @Nullable
    public LivingEntity getTarget();
}

