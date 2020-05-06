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
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(value=EnvType.CLIENT)
public class ExplosionLargeParticle
extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    private ExplosionLargeParticle(ClientWorld arg, double d, double e, double f, double g, SpriteProvider arg2) {
        super(arg, d, e, f, 0.0, 0.0, 0.0);
        float h;
        this.maxAge = 6 + this.random.nextInt(4);
        this.colorRed = h = this.random.nextFloat() * 0.6f + 0.4f;
        this.colorGreen = h;
        this.colorBlue = h;
        this.scale = 2.0f * (1.0f - (float)g * 0.5f);
        this.spriteProvider = arg2;
        this.setSpriteForAge(arg2);
    }

    @Override
    public int getColorMultiplier(float f) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
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
            return new ExplosionLargeParticle(arg2, d, e, f, g, this.spriteProvider);
        }
    }
}

