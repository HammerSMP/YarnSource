/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractSittingPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SittingFlamingPhase
extends AbstractSittingPhase {
    private int ticks;
    private int field_7052;
    private AreaEffectCloudEntity field_7051;

    public SittingFlamingPhase(EnderDragonEntity arg) {
        super(arg);
    }

    @Override
    public void clientTick() {
        ++this.ticks;
        if (this.ticks % 2 == 0 && this.ticks < 10) {
            Vec3d lv = this.dragon.method_6834(1.0f).normalize();
            lv.rotateY(-0.7853982f);
            double d = this.dragon.partHead.getX();
            double e = this.dragon.partHead.getBodyY(0.5);
            double f = this.dragon.partHead.getZ();
            for (int i = 0; i < 8; ++i) {
                double g = d + this.dragon.getRandom().nextGaussian() / 2.0;
                double h = e + this.dragon.getRandom().nextGaussian() / 2.0;
                double j = f + this.dragon.getRandom().nextGaussian() / 2.0;
                for (int k = 0; k < 6; ++k) {
                    this.dragon.world.addParticle(ParticleTypes.DRAGON_BREATH, g, h, j, -lv.x * (double)0.08f * (double)k, -lv.y * (double)0.6f, -lv.z * (double)0.08f * (double)k);
                }
                lv.rotateY(0.19634955f);
            }
        }
    }

    @Override
    public void serverTick() {
        ++this.ticks;
        if (this.ticks >= 200) {
            if (this.field_7052 >= 4) {
                this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
            } else {
                this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
            }
        } else if (this.ticks == 10) {
            double g;
            Vec3d lv = new Vec3d(this.dragon.partHead.getX() - this.dragon.getX(), 0.0, this.dragon.partHead.getZ() - this.dragon.getZ()).normalize();
            float f = 5.0f;
            double d = this.dragon.partHead.getX() + lv.x * 5.0 / 2.0;
            double e = this.dragon.partHead.getZ() + lv.z * 5.0 / 2.0;
            double h = g = this.dragon.partHead.getBodyY(0.5);
            BlockPos.Mutable lv2 = new BlockPos.Mutable(d, h, e);
            while (this.dragon.world.isAir(lv2)) {
                if ((h -= 1.0) < 0.0) {
                    h = g;
                    break;
                }
                lv2.set(d, h, e);
            }
            h = MathHelper.floor(h) + 1;
            this.field_7051 = new AreaEffectCloudEntity(this.dragon.world, d, h, e);
            this.field_7051.setOwner(this.dragon);
            this.field_7051.setRadius(5.0f);
            this.field_7051.setDuration(200);
            this.field_7051.setParticleType(ParticleTypes.DRAGON_BREATH);
            this.field_7051.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE));
            this.dragon.world.spawnEntity(this.field_7051);
        }
    }

    @Override
    public void beginPhase() {
        this.ticks = 0;
        ++this.field_7052;
    }

    @Override
    public void endPhase() {
        if (this.field_7051 != null) {
            this.field_7051.remove();
            this.field_7051 = null;
        }
    }

    public PhaseType<SittingFlamingPhase> getType() {
        return PhaseType.SITTING_FLAMING;
    }

    public void method_6857() {
        this.field_7052 = 0;
    }
}

