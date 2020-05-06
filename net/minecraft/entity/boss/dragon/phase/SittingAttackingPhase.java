/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractSittingPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.sound.SoundEvents;

public class SittingAttackingPhase
extends AbstractSittingPhase {
    private int ticks;

    public SittingAttackingPhase(EnderDragonEntity arg) {
        super(arg);
    }

    @Override
    public void clientTick() {
        this.dragon.world.playSound(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.dragon.getSoundCategory(), 2.5f, 0.8f + this.dragon.getRandom().nextFloat() * 0.3f, false);
    }

    @Override
    public void serverTick() {
        if (this.ticks++ >= 40) {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_FLAMING);
        }
    }

    @Override
    public void beginPhase() {
        this.ticks = 0;
    }

    public PhaseType<SittingAttackingPhase> getType() {
        return PhaseType.SITTING_ATTACKING;
    }
}

