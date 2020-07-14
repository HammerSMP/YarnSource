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
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AscendingParticle
extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final double ascendingAcceleration;

    protected AscendingParticle(ClientWorld world, double x, double y, double z, float randomVelocityXMultiplier, float randomVelocityYMultiplier, float randomVelocityZMultiplier, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider, float colorMultiplier, int baseMaxAge, double ascendingAcceleration, boolean collidesWithWorld) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        float q;
        this.ascendingAcceleration = ascendingAcceleration;
        this.spriteProvider = spriteProvider;
        this.velocityX *= (double)randomVelocityXMultiplier;
        this.velocityY *= (double)randomVelocityYMultiplier;
        this.velocityZ *= (double)randomVelocityZMultiplier;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        this.colorRed = q = world.random.nextFloat() * colorMultiplier;
        this.colorGreen = q;
        this.colorBlue = q;
        this.scale *= 0.75f * scaleMultiplier;
        this.maxAge = (int)((double)baseMaxAge / ((double)world.random.nextFloat() * 0.8 + 0.2));
        this.maxAge = (int)((float)this.maxAge * scaleMultiplier);
        this.maxAge = Math.max(this.maxAge, 1);
        this.setSpriteForAge(spriteProvider);
        this.collidesWithWorld = collidesWithWorld;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
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
        this.velocityY += this.ascendingAcceleration;
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
}

