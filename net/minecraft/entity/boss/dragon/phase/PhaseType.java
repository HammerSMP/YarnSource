/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.boss.dragon.phase;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.ChargingPlayerPhase;
import net.minecraft.entity.boss.dragon.phase.DyingPhase;
import net.minecraft.entity.boss.dragon.phase.HoldingPatternPhase;
import net.minecraft.entity.boss.dragon.phase.HoverPhase;
import net.minecraft.entity.boss.dragon.phase.LandingApproachPhase;
import net.minecraft.entity.boss.dragon.phase.LandingPhase;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.SittingAttackingPhase;
import net.minecraft.entity.boss.dragon.phase.SittingFlamingPhase;
import net.minecraft.entity.boss.dragon.phase.SittingScanningPhase;
import net.minecraft.entity.boss.dragon.phase.StrafePlayerPhase;
import net.minecraft.entity.boss.dragon.phase.TakeoffPhase;

public class PhaseType<T extends Phase> {
    private static PhaseType<?>[] types = new PhaseType[0];
    public static final PhaseType<HoldingPatternPhase> HOLDING_PATTERN = PhaseType.register(HoldingPatternPhase.class, "HoldingPattern");
    public static final PhaseType<StrafePlayerPhase> STRAFE_PLAYER = PhaseType.register(StrafePlayerPhase.class, "StrafePlayer");
    public static final PhaseType<LandingApproachPhase> LANDING_APPROACH = PhaseType.register(LandingApproachPhase.class, "LandingApproach");
    public static final PhaseType<LandingPhase> LANDING = PhaseType.register(LandingPhase.class, "Landing");
    public static final PhaseType<TakeoffPhase> TAKEOFF = PhaseType.register(TakeoffPhase.class, "Takeoff");
    public static final PhaseType<SittingFlamingPhase> SITTING_FLAMING = PhaseType.register(SittingFlamingPhase.class, "SittingFlaming");
    public static final PhaseType<SittingScanningPhase> SITTING_SCANNING = PhaseType.register(SittingScanningPhase.class, "SittingScanning");
    public static final PhaseType<SittingAttackingPhase> SITTING_ATTACKING = PhaseType.register(SittingAttackingPhase.class, "SittingAttacking");
    public static final PhaseType<ChargingPlayerPhase> CHARGING_PLAYER = PhaseType.register(ChargingPlayerPhase.class, "ChargingPlayer");
    public static final PhaseType<DyingPhase> DYING = PhaseType.register(DyingPhase.class, "Dying");
    public static final PhaseType<HoverPhase> HOVER = PhaseType.register(HoverPhase.class, "Hover");
    private final Class<? extends Phase> phaseClass;
    private final int id;
    private final String name;

    private PhaseType(int i, Class<? extends Phase> arg, String string) {
        this.id = i;
        this.phaseClass = arg;
        this.name = string;
    }

    public Phase create(EnderDragonEntity arg) {
        try {
            Constructor<Phase> constructor = this.getConstructor();
            return constructor.newInstance(arg);
        }
        catch (Exception exception) {
            throw new Error(exception);
        }
    }

    protected Constructor<? extends Phase> getConstructor() throws NoSuchMethodException {
        return this.phaseClass.getConstructor(EnderDragonEntity.class);
    }

    public int getTypeId() {
        return this.id;
    }

    public String toString() {
        return this.name + " (#" + this.id + ")";
    }

    public static PhaseType<?> getFromId(int i) {
        if (i < 0 || i >= types.length) {
            return HOLDING_PATTERN;
        }
        return types[i];
    }

    public static int count() {
        return types.length;
    }

    private static <T extends Phase> PhaseType<T> register(Class<T> arg, String string) {
        PhaseType<T> lv = new PhaseType<T>(types.length, arg, string);
        types = Arrays.copyOf(types, types.length + 1);
        PhaseType.types[lv.getTypeId()] = lv;
        return lv;
    }
}

