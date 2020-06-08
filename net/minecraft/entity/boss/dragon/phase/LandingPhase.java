/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class LandingPhase
extends AbstractPhase {
    private Vec3d target;

    public LandingPhase(EnderDragonEntity arg) {
        super(arg);
    }

    @Override
    public void clientTick() {
        Vec3d lv = this.dragon.method_6834(1.0f).normalize();
        lv.rotateY(-0.7853982f);
        double d = this.dragon.partHead.getX();
        double e = this.dragon.partHead.getBodyY(0.5);
        double f = this.dragon.partHead.getZ();
        for (int i = 0; i < 8; ++i) {
            Random random = this.dragon.getRandom();
            double g = d + random.nextGaussian() / 2.0;
            double h = e + random.nextGaussian() / 2.0;
            double j = f + random.nextGaussian() / 2.0;
            Vec3d lv2 = this.dragon.getVelocity();
            this.dragon.world.addParticle(ParticleTypes.DRAGON_BREATH, g, h, j, -lv.x * (double)0.08f + lv2.x, -lv.y * (double)0.3f + lv2.y, -lv.z * (double)0.08f + lv2.z);
            lv.rotateY(0.19634955f);
        }
    }

    @Override
    public void serverTick() {
        if (this.target == null) {
            this.target = Vec3d.ofBottomCenter(this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN));
        }
        if (this.target.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0) {
            this.dragon.getPhaseManager().create(PhaseType.SITTING_FLAMING).method_6857();
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
        }
    }

    @Override
    public float getMaxYAcceleration() {
        return 1.5f;
    }

    @Override
    public float method_6847() {
        float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.dragon.getVelocity())) + 1.0f;
        float g = Math.min(f, 40.0f);
        return g / f;
    }

    @Override
    public void beginPhase() {
        this.target = null;
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.target;
    }

    public PhaseType<LandingPhase> getType() {
        return PhaseType.LANDING;
    }
}

