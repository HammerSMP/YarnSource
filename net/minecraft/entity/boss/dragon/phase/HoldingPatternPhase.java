/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class HoldingPatternPhase
extends AbstractPhase {
    private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
    private Path field_7043;
    private Vec3d field_7045;
    private boolean field_7044;

    public HoldingPatternPhase(EnderDragonEntity arg) {
        super(arg);
    }

    public PhaseType<HoldingPatternPhase> getType() {
        return PhaseType.HOLDING_PATTERN;
    }

    @Override
    public void serverTick() {
        double d;
        double d2 = d = this.field_7045 == null ? 0.0 : this.field_7045.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.method_6841();
        }
    }

    @Override
    public void beginPhase() {
        this.field_7043 = null;
        this.field_7045 = null;
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7045;
    }

    private void method_6841() {
        if (this.field_7043 != null && this.field_7043.isFinished()) {
            int i;
            BlockPos lv = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPortalFeature.ORIGIN));
            int n = i = this.dragon.getFight() == null ? 0 : this.dragon.getFight().getAliveEndCrystals();
            if (this.dragon.getRandom().nextInt(i + 3) == 0) {
                this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
                return;
            }
            double d = 64.0;
            PlayerEntity lv2 = this.dragon.world.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, lv.getX(), lv.getY(), lv.getZ());
            if (lv2 != null) {
                d = lv.getSquaredDistance(lv2.getPos(), true) / 512.0;
            }
            if (!(lv2 == null || lv2.abilities.invulnerable || this.dragon.getRandom().nextInt(MathHelper.abs((int)d) + 2) != 0 && this.dragon.getRandom().nextInt(i + 2) != 0)) {
                this.method_6843(lv2);
                return;
            }
        }
        if (this.field_7043 == null || this.field_7043.isFinished()) {
            int j;
            int k = j = this.dragon.getNearestPathNodeIndex();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.field_7044 = !this.field_7044;
                k += 6;
            }
            k = this.field_7044 ? ++k : --k;
            if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() < 0) {
                k -= 12;
                k &= 7;
                k += 12;
            } else if ((k %= 12) < 0) {
                k += 12;
            }
            this.field_7043 = this.dragon.findPath(j, k, null);
            if (this.field_7043 != null) {
                this.field_7043.next();
            }
        }
        this.method_6842();
    }

    private void method_6843(PlayerEntity arg) {
        this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
        this.dragon.getPhaseManager().create(PhaseType.STRAFE_PLAYER).method_6862(arg);
    }

    private void method_6842() {
        if (this.field_7043 != null && !this.field_7043.isFinished()) {
            double f;
            Vec3i lv = this.field_7043.getCurrentPosition();
            this.field_7043.next();
            double d = lv.getX();
            double e = lv.getZ();
            while ((f = (double)((float)lv.getY() + this.dragon.getRandom().nextFloat() * 20.0f)) < (double)lv.getY()) {
            }
            this.field_7045 = new Vec3d(d, f, e);
        }
    }

    @Override
    public void crystalDestroyed(EndCrystalEntity arg, BlockPos arg2, DamageSource arg3, @Nullable PlayerEntity arg4) {
        if (arg4 != null && !arg4.abilities.invulnerable) {
            this.method_6843(arg4);
        }
    }
}

