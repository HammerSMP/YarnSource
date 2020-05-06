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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class NoteParticle
extends SpriteBillboardParticle {
    private NoteParticle(ClientWorld arg, double d, double e, double f, double g) {
        super(arg, d, e, f, 0.0, 0.0, 0.0);
        this.velocityX *= (double)0.01f;
        this.velocityY *= (double)0.01f;
        this.velocityZ *= (double)0.01f;
        this.velocityY += 0.2;
        this.colorRed = Math.max(0.0f, MathHelper.sin(((float)g + 0.0f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.colorGreen = Math.max(0.0f, MathHelper.sin(((float)g + 0.33333334f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.colorBlue = Math.max(0.0f, MathHelper.sin(((float)g + 0.6666667f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.scale *= 1.5f;
        this.maxAge = 6;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float f) {
        return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
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
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= (double)0.66f;
        this.velocityY *= (double)0.66f;
        this.velocityZ *= (double)0.66f;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
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
            NoteParticle lv = new NoteParticle(arg2, d, e, f, g);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }
}

