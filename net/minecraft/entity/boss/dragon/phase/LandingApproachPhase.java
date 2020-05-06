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
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class LandingApproachPhase
extends AbstractPhase {
    private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(128.0);
    private Path field_7047;
    private Vec3d field_7048;

    public LandingApproachPhase(EnderDragonEntity arg) {
        super(arg);
    }

    public PhaseType<LandingApproachPhase> getType() {
        return PhaseType.LANDING_APPROACH;
    }

    @Override
    public void beginPhase() {
        this.field_7047 = null;
        this.field_7048 = null;
    }

    @Override
    public void serverTick() {
        double d;
        double d2 = d = this.field_7048 == null ? 0.0 : this.field_7048.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.method_6844();
        }
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7048;
    }

    private void method_6844() {
        if (this.field_7047 == null || this.field_7047.isFinished()) {
            int k;
            int i = this.dragon.getNearestPathNodeIndex();
            BlockPos lv = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            PlayerEntity lv2 = this.dragon.world.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, lv.getX(), lv.getY(), lv.getZ());
            if (lv2 != null) {
                Vec3d lv3 = new Vec3d(lv2.getX(), 0.0, lv2.getZ()).normalize();
                int j = this.dragon.getNearestPathNodeIndex(-lv3.x * 40.0, 105.0, -lv3.z * 40.0);
            } else {
                k = this.dragon.getNearestPathNodeIndex(40.0, lv.getY(), 0.0);
            }
            PathNode lv4 = new PathNode(lv.getX(), lv.getY(), lv.getZ());
            this.field_7047 = this.dragon.findPath(i, k, lv4);
            if (this.field_7047 != null) {
                this.field_7047.next();
            }
        }
        this.method_6845();
        if (this.field_7047 != null && this.field_7047.isFinished()) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
        }
    }

    private void method_6845() {
        if (this.field_7047 != null && !this.field_7047.isFinished()) {
            double f;
            Vec3i lv = this.field_7047.getCurrentPosition();
            this.field_7047.next();
            double d = lv.getX();
            double e = lv.getZ();
            while ((f = (double)((float)lv.getY() + this.dragon.getRandom().nextFloat() * 20.0f)) < (double)lv.getY()) {
            }
            this.field_7048 = new Vec3d(d, f, e);
        }
    }
}

