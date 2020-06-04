/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface class_5354 {
    public int method_29507();

    public void method_29514(int var1);

    @Nullable
    public UUID method_29508();

    public void method_29513(@Nullable UUID var1);

    public void method_29509();

    default public void method_29517(CompoundTag arg) {
        arg.putInt("AngerTime", this.method_29507());
        if (this.method_29508() != null) {
            arg.putUuid("AngryAt", this.method_29508());
        }
    }

    default public void method_29512(World arg, CompoundTag arg2) {
        this.method_29514(arg2.getInt("AngerTime"));
        if (arg2.containsUuid("AngryAt")) {
            PlayerEntity lv;
            this.method_29513(arg2.getUuid("AngryAt"));
            UUID uUID = this.method_29508();
            PlayerEntity playerEntity = lv = uUID == null ? null : arg.getPlayerByUuid(uUID);
            if (lv != null) {
                this.setAttacker(lv);
                this.method_29505(lv);
            }
        }
    }

    default public void method_29510() {
        LivingEntity lv = this.getTarget();
        if (lv != null && lv.getType() == EntityType.PLAYER) {
            this.method_29513(lv.getUuid());
            if (this.method_29507() <= 0) {
                this.method_29509();
            }
        } else {
            int i = this.method_29507();
            if (i > 0) {
                this.method_29514(i - 1);
                if (this.method_29507() == 0) {
                    this.method_29513(null);
                }
            }
        }
    }

    default public boolean method_29515(LivingEntity arg) {
        if (arg instanceof PlayerEntity && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(arg)) {
            boolean bl = arg.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER);
            return bl ? this.method_29511() : arg.getUuid().equals(this.method_29508());
        }
        return false;
    }

    default public boolean method_29511() {
        return this.method_29507() > 0;
    }

    default public void method_29516(PlayerEntity arg) {
        if (!arg.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            // empty if block
        }
        if (!arg.getUuid().equals(this.method_29508())) {
            return;
        }
        this.setAttacker(null);
        this.method_29513(null);
        this.setTarget(null);
        this.method_29514(0);
    }

    public void setAttacker(@Nullable LivingEntity var1);

    public void method_29505(@Nullable PlayerEntity var1);

    public void setTarget(@Nullable LivingEntity var1);

    @Nullable
    public LivingEntity getTarget();
}

