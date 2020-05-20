/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.world.Difficulty;

public final class EntityPredicates {
    public static final Predicate<Entity> VALID_ENTITY = Entity::isAlive;
    public static final Predicate<LivingEntity> VALID_LIVING_ENTITY = LivingEntity::isAlive;
    public static final Predicate<Entity> NOT_MOUNTED = arg -> arg.isAlive() && !arg.hasPassengers() && !arg.hasVehicle();
    public static final Predicate<Entity> VALID_INVENTORIES = arg -> arg instanceof Inventory && arg.isAlive();
    public static final Predicate<Entity> EXCEPT_CREATIVE_OR_SPECTATOR = arg -> !(arg instanceof PlayerEntity) || !arg.isSpectator() && !((PlayerEntity)arg).isCreative();
    public static final Predicate<Entity> EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL = arg -> !(arg instanceof PlayerEntity) || !arg.isSpectator() && !((PlayerEntity)arg).isCreative() && arg.world.getDifficulty() != Difficulty.PEACEFUL;
    public static final Predicate<Entity> EXCEPT_SPECTATOR = arg -> !arg.isSpectator();

    public static Predicate<Entity> maximumDistance(double d, double e, double f, double g) {
        double h = g * g;
        return arg -> arg != null && arg.squaredDistanceTo(d, e, f) <= h;
    }

    public static Predicate<Entity> canBePushedBy(Entity arg) {
        AbstractTeam.CollisionRule lv2;
        AbstractTeam lv = arg.getScoreboardTeam();
        AbstractTeam.CollisionRule collisionRule = lv2 = lv == null ? AbstractTeam.CollisionRule.ALWAYS : lv.getCollisionRule();
        if (lv2 == AbstractTeam.CollisionRule.NEVER) {
            return Predicates.alwaysFalse();
        }
        return EXCEPT_SPECTATOR.and(arg4 -> {
            boolean bl;
            AbstractTeam.CollisionRule lv2;
            if (!arg4.isPushable()) {
                return false;
            }
            if (!(!arg.world.isClient || arg4 instanceof PlayerEntity && ((PlayerEntity)arg4).isMainPlayer())) {
                return false;
            }
            AbstractTeam lv = arg4.getScoreboardTeam();
            AbstractTeam.CollisionRule collisionRule = lv2 = lv == null ? AbstractTeam.CollisionRule.ALWAYS : lv.getCollisionRule();
            if (lv2 == AbstractTeam.CollisionRule.NEVER) {
                return false;
            }
            boolean bl2 = bl = lv != null && lv.isEqual(lv);
            if ((lv2 == AbstractTeam.CollisionRule.PUSH_OWN_TEAM || lv2 == AbstractTeam.CollisionRule.PUSH_OWN_TEAM) && bl) {
                return false;
            }
            return lv2 != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS && lv2 != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS || bl;
        });
    }

    public static Predicate<Entity> rides(Entity arg) {
        return arg2 -> {
            while (arg2.hasVehicle()) {
                if ((arg2 = arg2.getVehicle()) != arg) continue;
                return false;
            }
            return true;
        };
    }

    public static class CanPickup
    implements Predicate<Entity> {
        private final ItemStack stack;

        public CanPickup(ItemStack arg) {
            this.stack = arg;
        }

        @Override
        public boolean test(@Nullable Entity arg) {
            if (!arg.isAlive()) {
                return false;
            }
            if (!(arg instanceof LivingEntity)) {
                return false;
            }
            LivingEntity lv = (LivingEntity)arg;
            return lv.canPickUp(this.stack);
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((Entity)object);
        }
    }
}

