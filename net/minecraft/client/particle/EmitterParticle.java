/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class EmitterParticle
extends NoRenderParticle {
    private final Entity entity;
    private int emitterAge;
    private final int maxEmitterAge;
    private final ParticleEffect parameters;

    public EmitterParticle(ClientWorld arg, Entity arg2, ParticleEffect arg3) {
        this(arg, arg2, arg3, 3);
    }

    public EmitterParticle(ClientWorld arg, Entity arg2, ParticleEffect arg3, int i) {
        this(arg, arg2, arg3, i, arg2.getVelocity());
    }

    private EmitterParticle(ClientWorld arg, Entity arg2, ParticleEffect arg3, int i, Vec3d arg4) {
        super(arg, arg2.getX(), arg2.getBodyY(0.5), arg2.getZ(), arg4.x, arg4.y, arg4.z);
        this.entity = arg2;
        this.maxEmitterAge = i;
        this.parameters = arg3;
        this.tick();
    }

    @Override
    public void tick() {
        for (int i = 0; i < 16; ++i) {
            double f;
            double e;
            double d = this.random.nextFloat() * 2.0f - 1.0f;
            if (d * d + (e = (double)(this.random.nextFloat() * 2.0f - 1.0f)) * e + (f = (double)(this.random.nextFloat() * 2.0f - 1.0f)) * f > 1.0) continue;
            double g = this.entity.offsetX(d / 4.0);
            double h = this.entity.getBodyY(0.5 + e / 4.0);
            double j = this.entity.offsetZ(f / 4.0);
            this.world.addParticle(this.parameters, false, g, h, j, d, e + 0.2, f);
        }
        ++this.emitterAge;
        if (this.emitterAge >= this.maxEmitterAge) {
            this.markDead();
        }
    }
}

