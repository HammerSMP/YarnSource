/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.util.Random;
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
public class SpellParticle
extends SpriteBillboardParticle {
    private static final Random RANDOM = new Random();
    private final SpriteProvider spriteProvider;

    private SpellParticle(ClientWorld arg, double d, double e, double f, double g, double h, double i, SpriteProvider arg2) {
        super(arg, d, e, f, 0.5 - RANDOM.nextDouble(), h, 0.5 - RANDOM.nextDouble());
        this.spriteProvider = arg2;
        this.velocityY *= (double)0.2f;
        if (g == 0.0 && i == 0.0) {
            this.velocityX *= (double)0.1f;
            this.velocityZ *= (double)0.1f;
        }
        this.scale *= 0.75f;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.setSpriteForAge(arg2);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
        this.velocityY += 0.004;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= (double)0.96f;
        this.velocityY *= (double)0.96f;
        this.velocityZ *= (double)0.96f;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class InstantFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17872;

        public InstantFactory(SpriteProvider arg) {
            this.field_17872 = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            return new SpellParticle(arg2, d, e, f, g, h, i, this.field_17872);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class WitchFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17875;

        public WitchFactory(SpriteProvider arg) {
            this.field_17875 = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            SpellParticle lv = new SpellParticle(arg2, d, e, f, g, h, i, this.field_17875);
            float j = arg2.random.nextFloat() * 0.5f + 0.35f;
            lv.setColor(1.0f * j, 0.0f * j, 1.0f * j);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class EntityAmbientFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public EntityAmbientFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            SpellParticle lv = new SpellParticle(arg2, d, e, f, g, h, i, this.spriteProvider);
            lv.setColorAlpha(0.15f);
            lv.setColor((float)g, (float)h, (float)i);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class EntityFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17873;

        public EntityFactory(SpriteProvider arg) {
            this.field_17873 = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            SpellParticle lv = new SpellParticle(arg2, d, e, f, g, h, i, this.field_17873);
            lv.setColor((float)g, (float)h, (float)i);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DefaultFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            return new SpellParticle(arg2, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}

