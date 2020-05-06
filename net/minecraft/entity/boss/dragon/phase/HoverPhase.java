/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.util.math.Vec3d;

public class HoverPhase
extends AbstractPhase {
    private Vec3d field_7042;

    public HoverPhase(EnderDragonEntity arg) {
        super(arg);
    }

    @Override
    public void serverTick() {
        if (this.field_7042 == null) {
            this.field_7042 = this.dragon.getPos();
        }
    }

    @Override
    public boolean isSittingOrHovering() {
        return true;
    }

    @Override
    public void beginPhase() {
        this.field_7042 = null;
    }

    @Override
    public float getMaxYAcceleration() {
        return 1.0f;
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7042;
    }

    public PhaseType<HoverPhase> getType() {
        return PhaseType.HOVER;
    }
}

