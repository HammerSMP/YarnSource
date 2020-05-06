/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.ZombieEntity;

public class ZombieAttackGoal
extends MeleeAttackGoal {
    private final ZombieEntity zombie;
    private int ticks;

    public ZombieAttackGoal(ZombieEntity arg, double d, boolean bl) {
        super(arg, d, bl);
        this.zombie = arg;
    }

    @Override
    public void start() {
        super.start();
        this.ticks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.zombie.setAttacking(false);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.ticks;
        if (this.ticks >= 5 && this.ticksUntilAttack < 10) {
            this.zombie.setAttacking(true);
        } else {
            this.zombie.setAttacking(false);
        }
    }
}

