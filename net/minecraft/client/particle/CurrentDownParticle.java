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
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CurrentDownParticle
extends SpriteBillboardParticle {
    private float accelerationAngle;

    private CurrentDownParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
        this.maxAge = (int)(Math.random() * 60.0) + 30;
        this.collidesWithWorld = false;
        this.velocityX = 0.0;
        this.velocityY = -0.05;
        this.velocityZ = 0.0;
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.gravityStrength = 0.002f;
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
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        float f = 0.6f;
        this.velocityX += (double)(0.6f * MathHelper.cos(this.accelerationAngle));
        this.velocityZ += (double)(0.6f * MathHelper.sin(this.accelerationAngle));
        this.velocityX *= 0.07;
        this.velocityZ *= 0.07;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (!this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER) || this.onGround) {
            this.markDead();
        }
        this.accelerationAngle = (float)((double)this.accelerationAngle + 0.08);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            CurrentDownParticle lv = new CurrentDownParticle(arg2, d, e, f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }
}

