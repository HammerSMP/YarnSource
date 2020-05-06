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

    protected AscendingParticle(ClientWorld arg, double d, double e, double f, float g, float h, float i, double j, double k, double l, float m, SpriteProvider arg2, float n, int o, double p, boolean bl) {
        super(arg, d, e, f, 0.0, 0.0, 0.0);
        float q;
        this.ascendingAcceleration = p;
        this.spriteProvider = arg2;
        this.velocityX *= (double)g;
        this.velocityY *= (double)h;
        this.velocityZ *= (double)i;
        this.velocityX += j;
        this.velocityY += k;
        this.velocityZ += l;
        this.colorRed = q = arg.random.nextFloat() * n;
        this.colorGreen = q;
        this.colorBlue = q;
        this.scale *= 0.75f * m;
        this.maxAge = (int)((double)o / ((double)arg.random.nextFloat() * 0.8 + 0.2));
        this.maxAge = (int)((float)this.maxAge * m);
        this.maxAge = Math.max(this.maxAge, 1);
        this.setSpriteForAge(arg2);
        this.collidesWithWorld = bl;
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

