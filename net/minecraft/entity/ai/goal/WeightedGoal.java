/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.goal.Goal;

public class WeightedGoal
extends Goal {
    private final Goal goal;
    private final int weight;
    private boolean running;

    public WeightedGoal(int i, Goal arg) {
        this.weight = i;
        this.goal = arg;
    }

    public boolean canBeReplacedBy(WeightedGoal arg) {
        return this.canStop() && arg.getWeight() < this.getWeight();
    }

    @Override
    public boolean canStart() {
        return this.goal.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return this.goal.shouldContinue();
    }

    @Override
    public boolean canStop() {
        return this.goal.canStop();
    }

    @Override
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.goal.start();
    }

    @Override
    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.goal.stop();
    }

    @Override
    public void tick() {
        this.goal.tick();
    }

    @Override
    public void setControls(EnumSet<Goal.Control> enumSet) {
        this.goal.setControls(enumSet);
    }

    @Override
    public EnumSet<Goal.Control> getControls() {
        return this.goal.getControls();
    }

    public boolean isRunning() {
        return this.running;
    }

    public int getWeight() {
        return this.weight;
    }

    public Goal getGoal() {
        return this.goal;
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        return this.goal.equals(((WeightedGoal)object).goal);
    }

    public int hashCode() {
        return this.goal.hashCode();
    }
}

