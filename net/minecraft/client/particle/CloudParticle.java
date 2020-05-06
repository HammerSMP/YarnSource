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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CloudParticle
extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    private CloudParticle(ClientWorld arg, double d, double e, double f, double g, double h, double i, SpriteProvider arg2) {
        super(arg, d, e, f, 0.0, 0.0, 0.0);
        float k;
        this.spriteProvider = arg2;
        float j = 2.5f;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += g;
        this.velocityY += h;
        this.velocityZ += i;
        this.colorRed = k = 1.0f - (float)(Math.random() * (double)0.3f);
        this.colorGreen = k;
        this.colorBlue = k;
        this.scale *= 1.875f;
        int l = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.maxAge = (int)Math.max((float)l * 2.5f, 1.0f);
        this.collidesWithWorld = false;
        this.setSpriteForAge(arg2);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getSize(float f) {
        return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        double d;
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.setSpriteForAge(this.spriteProvider);
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.96f;
        this.velocityY *= (double)0.96f;
        this.velocityZ *= (double)0.96f;
        PlayerEntity lv = this.world.getClosestPlayer(this.x, this.y, this.z, 2.0, false);
        if (lv != null && this.y > (d = lv.getY())) {
            this.y += (d - this.y) * 0.2;
            this.velocityY += (lv.getVelocity().y - this.velocityY) * 0.2;
            this.setPos(this.x, this.y, this.z);
        }
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SneezeFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public SneezeFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            CloudParticle lv = new CloudParticle(arg2, d, e, f, g, h, i, this.spriteProvider);
            lv.setColor(200.0f, 50.0f, 120.0f);
            lv.setColorAlpha(0.4f);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CloudFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public CloudFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            return new CloudParticle(arg2, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}

