/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface Phase {
    public boolean isSittingOrHovering();

    public void clientTick();

    public void serverTick();

    public void crystalDestroyed(EndCrystalEntity var1, BlockPos var2, DamageSource var3, @Nullable PlayerEntity var4);

    public void beginPhase();

    public void endPhase();

    public float getMaxYAcceleration();

    public float method_6847();

    public PhaseType<? extends Phase> getType();

    @Nullable
    public Vec3d getTarget();

    public float modifyDamageTaken(DamageSource var1, float var2);
}

