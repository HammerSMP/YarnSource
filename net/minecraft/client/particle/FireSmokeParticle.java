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
import net.minecraft.client.particle.AscendingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(value=EnvType.CLIENT)
public class FireSmokeParticle
extends AscendingParticle {
    protected FireSmokeParticle(ClientWorld arg, double d, double e, double f, double g, double h, double i, float j, SpriteProvider arg2) {
        super(arg, d, e, f, 0.1f, 0.1f, 0.1f, g, h, i, j, arg2, 0.3f, 8, 0.004, true);
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
            return new FireSmokeParticle(arg2, d, e, f, g, h, i, 1.0f, this.spriteProvider);
        }
    }
}

