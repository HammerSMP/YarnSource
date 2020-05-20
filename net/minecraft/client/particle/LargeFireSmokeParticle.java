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
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(value=EnvType.CLIENT)
public class LargeFireSmokeParticle
extends FireSmokeParticle {
    protected LargeFireSmokeParticle(ClientWorld arg, double d, double e, double f, double g, double h, double i, SpriteProvider arg2) {
        super(arg, d, e, f, g, h, i, 2.5f, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            return new LargeFireSmokeParticle(arg2, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
