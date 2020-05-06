/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrafePlayerPhase
extends AbstractPhase {
    private static final Logger LOGGER = LogManager.getLogger();
    private int field_7060;
    private Path field_7059;
    private Vec3d field_7057;
    private LivingEntity field_7062;
    private boolean field_7058;

    public StrafePlayerPhase(EnderDragonEntity arg) {
        super(arg);
    }

    @Override
    public void serverTick() {
        double j;
        if (this.field_7062 == null) {
            LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            return;
        }
        if (this.field_7059 != null && this.field_7059.isFinished()) {
            double d = this.field_7062.getX();
            double e = this.field_7062.getZ();
            double f = d - this.dragon.getX();
            double g = e - this.dragon.getZ();
            double h = MathHelper.sqrt(f * f + g * g);
            double i = Math.min((double)0.4f + h / 80.0 - 1.0, 10.0);
            this.field_7057 = new Vec3d(d, this.field_7062.getY() + i, e);
        }
        double d = j = this.field_7057 == null ? 0.0 : this.field_7057.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (j < 100.0 || j > 22500.0) {
            this.method_6860();
        }
        double k = 64.0;
        if (this.field_7062.squaredDistanceTo(this.dragon) < 4096.0) {
            if (this.dragon.canSee(this.field_7062)) {
                ++this.field_7060;
                Vec3d lv = new Vec3d(this.field_7062.getX() - this.dragon.getX(), 0.0, this.field_7062.getZ() - this.dragon.getZ()).normalize();
                Vec3d lv2 = new Vec3d(MathHelper.sin(this.dragon.yaw * ((float)Math.PI / 180)), 0.0, -MathHelper.cos(this.dragon.yaw * ((float)Math.PI / 180))).normalize();
                float l = (float)lv2.dotProduct(lv);
                float m = (float)(Math.acos(l) * 57.2957763671875);
                m += 0.5f;
                if (this.field_7060 >= 5 && m >= 0.0f && m < 10.0f) {
                    double n = 1.0;
                    Vec3d lv3 = this.dragon.getRotationVec(1.0f);
                    double o = this.dragon.partHead.getX() - lv3.x * 1.0;
                    double p = this.dragon.partHead.getBodyY(0.5) + 0.5;
                    double q = this.dragon.partHead.getZ() - lv3.z * 1.0;
                    double r = this.field_7062.getX() - o;
                    double s = this.field_7062.getBodyY(0.5) - p;
                    double t = this.field_7062.getZ() - q;
                    if (!this.dragon.isSilent()) {
                        this.dragon.world.syncWorldEvent(null, 1017, this.dragon.getBlockPos(), 0);
                    }
                    DragonFireballEntity lv4 = new DragonFireballEntity(this.dragon.world, this.dragon, r, s, t);
                    lv4.refreshPositionAndAngles(o, p, q, 0.0f, 0.0f);
                    this.dragon.world.spawnEntity(lv4);
                    this.field_7060 = 0;
                    if (this.field_7059 != null) {
                        while (!this.field_7059.isFinished()) {
                            this.field_7059.next();
                        }
                    }
                    this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
                }
            } else if (this.field_7060 > 0) {
                --this.field_7060;
            }
        } else if (this.field_7060 > 0) {
            --this.field_7060;
        }
    }

    private void method_6860() {
        if (this.field_7059 == null || this.field_7059.isFinished()) {
            int i;
            int j = i = this.dragon.getNearestPathNodeIndex();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.field_7058 = !this.field_7058;
                j += 6;
            }
            j = this.field_7058 ? ++j : --j;
            if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() <= 0) {
                j -= 12;
                j &= 7;
                j += 12;
            } else if ((j %= 12) < 0) {
                j += 12;
            }
            this.field_7059 = this.dragon.findPath(i, j, null);
            if (this.field_7059 != null) {
                this.field_7059.next();
            }
        }
        this.method_6861();
    }

    private void method_6861() {
        if (this.field_7059 != null && !this.field_7059.isFinished()) {
            double f;
            Vec3i lv = this.field_7059.getCurrentPosition();
            this.field_7059.next();
            double d = lv.getX();
            double e = lv.getZ();
            while ((f = (double)((float)lv.getY() + this.dragon.getRandom().nextFloat() * 20.0f)) < (double)lv.getY()) {
            }
            this.field_7057 = new Vec3d(d, f, e);
        }
    }

    @Override
    public void beginPhase() {
        this.field_7060 = 0;
        this.field_7057 = null;
        this.field_7059 = null;
        this.field_7062 = null;
    }

    public void method_6862(LivingEntity arg) {
        this.field_7062 = arg;
        int i = this.dragon.getNearestPathNodeIndex();
        int j = this.dragon.getNearestPathNodeIndex(this.field_7062.getX(), this.field_7062.getY(), this.field_7062.getZ());
        int k = MathHelper.floor(this.field_7062.getX());
        int l = MathHelper.floor(this.field_7062.getZ());
        double d = (double)k - this.dragon.getX();
        double e = (double)l - this.dragon.getZ();
        double f = MathHelper.sqrt(d * d + e * e);
        double g = Math.min((double)0.4f + f / 80.0 - 1.0, 10.0);
        int m = MathHelper.floor(this.field_7062.getY() + g);
        PathNode lv = new PathNode(k, m, l);
        this.field_7059 = this.dragon.findPath(i, j, lv);
        if (this.field_7059 != null) {
            this.field_7059.next();
            this.method_6861();
        }
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7057;
    }

    public PhaseType<StrafePlayerPhase> getType() {
        return PhaseType.STRAFE_PLAYER;
    }
}

