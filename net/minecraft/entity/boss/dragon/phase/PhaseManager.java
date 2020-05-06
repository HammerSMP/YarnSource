/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final EnderDragonEntity dragon;
    private final Phase[] phases = new Phase[PhaseType.count()];
    private Phase current;

    public PhaseManager(EnderDragonEntity arg) {
        this.dragon = arg;
        this.setPhase(PhaseType.HOVER);
    }

    public void setPhase(PhaseType<?> arg) {
        if (this.current != null && arg == this.current.getType()) {
            return;
        }
        if (this.current != null) {
            this.current.endPhase();
        }
        this.current = this.create(arg);
        if (!this.dragon.world.isClient) {
            this.dragon.getDataTracker().set(EnderDragonEntity.PHASE_TYPE, arg.getTypeId());
        }
        LOGGER.debug("Dragon is now in phase {} on the {}", arg, (Object)(this.dragon.world.isClient ? "client" : "server"));
        this.current.beginPhase();
    }

    public Phase getCurrent() {
        return this.current;
    }

    public <T extends Phase> T create(PhaseType<T> arg) {
        int i = arg.getTypeId();
        if (this.phases[i] == null) {
            this.phases[i] = arg.create(this.dragon);
        }
        return (T)this.phases[i];
    }
}

