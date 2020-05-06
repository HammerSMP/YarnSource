/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class TakeoffPhase
extends AbstractPhase {
    private boolean field_7056;
    private Path field_7054;
    private Vec3d field_7055;

    public TakeoffPhase(EnderDragonEntity arg) {
        super(arg);
    }

    @Override
    public void serverTick() {
        if (this.field_7056 || this.field_7054 == null) {
            this.field_7056 = false;
            this.method_6858();
        } else {
            BlockPos lv = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            if (!lv.isWithinDistance(this.dragon.getPos(), 10.0)) {
                this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            }
        }
    }

    @Override
    public void beginPhase() {
        this.field_7056 = true;
        this.field_7054 = null;
        this.field_7055 = null;
    }

    private void method_6858() {
        int i = this.dragon.getNearestPathNodeIndex();
        Vec3d lv = this.dragon.method_6834(1.0f);
        int j = this.dragon.getNearestPathNodeIndex(-lv.x * 40.0, 105.0, -lv.z * 40.0);
        if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() <= 0) {
            j -= 12;
            j &= 7;
            j += 12;
        } else if ((j %= 12) < 0) {
            j += 12;
        }
        this.field_7054 = this.dragon.findPath(i, j, null);
        this.method_6859();
    }

    private void method_6859() {
        if (this.field_7054 != null) {
            this.field_7054.next();
            if (!this.field_7054.isFinished()) {
                double d;
                Vec3i lv = this.field_7054.getCurrentPosition();
                this.field_7054.next();
                while ((d = (double)((float)lv.getY() + this.dragon.getRandom().nextFloat() * 20.0f)) < (double)lv.getY()) {
                }
                this.field_7055 = new Vec3d(lv.getX(), d, lv.getZ());
            }
        }
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7055;
    }

    public PhaseType<TakeoffPhase> getType() {
        return PhaseType.TAKEOFF;
    }
}

