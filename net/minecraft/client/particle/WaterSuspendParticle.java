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
public class WaterSuspendParticle
extends SpriteBillboardParticle {
    private WaterSuspendParticle(ClientWorld arg, double d, double e, double f) {
        super(arg, d, e - 0.125, f);
        this.colorRed = 0.4f;
        this.colorGreen = 0.4f;
        this.colorBlue = 0.7f;
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
    }

    private WaterSuspendParticle(ClientWorld arg, double d, double e, double f, double g, double h, double i) {
        super(arg, d, e - 0.125, f, g, h, i);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.6f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Environment(value=EnvType.CLIENT)
    public static class WarpedSporeFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public WarpedSporeFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            WaterSuspendParticle lv = new WaterSuspendParticle(arg2, d, e, f, g, h, i);
            lv.setSprite(this.spriteProvider);
            lv.setColor(0.1f, 0.1f, 0.3f);
            lv.setBoundingBoxSpacing(0.001f, 0.001f);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CrimsonSporeFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public CrimsonSporeFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            WaterSuspendParticle lv = new WaterSuspendParticle(arg2, d, e, f, g, h, i);
            lv.setSprite(this.spriteProvider);
            lv.setColor(0.9f, 0.4f, 0.5f);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class UnderwaterFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public UnderwaterFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            WaterSuspendParticle lv = new WaterSuspendParticle(arg2, d, e, f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }
}

