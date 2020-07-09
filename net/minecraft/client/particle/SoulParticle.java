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
import net.minecraft.client.particle.AbstractSlowingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(value=EnvType.CLIENT)
public class SoulParticle
extends AbstractSlowingParticle {
    private final SpriteProvider spriteProvider;

    private SoulParticle(ClientWorld arg, double d, double e, double f, double g, double h, double i, SpriteProvider arg2) {
        super(arg, d, e, f, g, h, i);
        this.spriteProvider = arg2;
        this.scale(1.5f);
        this.setSpriteForAge(arg2);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.dead) {
            this.setSpriteForAge(this.spriteProvider);
        }
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
            SoulParticle lv = new SoulParticle(arg2, d, e, f, g, h, i, this.spriteProvider);
            lv.setColorAlpha(1.0f);
            return lv;
        }
    }
}

