/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.player.PlayerEntity;

public class StopFollowingCustomerGoal
extends Goal {
    private final AbstractTraderEntity trader;

    public StopFollowingCustomerGoal(AbstractTraderEntity arg) {
        this.trader = arg;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.trader.isAlive()) {
            return false;
        }
        if (this.trader.isTouchingWater()) {
            return false;
        }
        if (!this.trader.isOnGround()) {
            return false;
        }
        if (this.trader.velocityModified) {
            return false;
        }
        PlayerEntity lv = this.trader.getCurrentCustomer();
        if (lv == null) {
            return false;
        }
        if (this.trader.squaredDistanceTo(lv) > 16.0) {
            return false;
        }
        return lv.currentScreenHandler != null;
    }

    @Override
    public void start() {
        this.trader.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.trader.setCurrentCustomer(null);
    }
}

