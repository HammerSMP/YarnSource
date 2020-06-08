/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BlockFallingDustParticle
extends SpriteBillboardParticle {
    private final float field_3809;
    private final SpriteProvider spriteProvider;

    private BlockFallingDustParticle(ClientWorld arg, double d, double e, double f, float g, float h, float i, SpriteProvider arg2) {
        super(arg, d, e, f);
        this.spriteProvider = arg2;
        this.colorRed = g;
        this.colorGreen = h;
        this.colorBlue = i;
        float j = 0.9f;
        this.scale *= 0.67499995f;
        int k = (int)(32.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)k * 0.9f, 1.0f);
        this.setSpriteForAge(arg2);
        this.field_3809 = ((float)Math.random() - 0.5f) * 0.1f;
        this.angle = (float)Math.random() * ((float)Math.PI * 2);
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
        this.prevAngle = this.angle;
        this.angle += (float)Math.PI * this.field_3809 * 2.0f;
        if (this.onGround) {
            this.angle = 0.0f;
            this.prevAngle = 0.0f;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityY -= (double)0.003f;
        this.velocityY = Math.max(this.velocityY, (double)-0.14f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<BlockStateParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        @Nullable
        public Particle createParticle(BlockStateParticleEffect arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            BlockState lv = arg.getBlockState();
            if (!lv.isAir() && lv.getRenderType() == BlockRenderType.INVISIBLE) {
                return null;
            }
            BlockPos lv2 = new BlockPos(d, e, f);
            int j = MinecraftClient.getInstance().getBlockColors().getColor(lv, arg2, lv2);
            if (lv.getBlock() instanceof FallingBlock) {
                j = ((FallingBlock)lv.getBlock()).getColor(lv, arg2, lv2);
            }
            float k = (float)(j >> 16 & 0xFF) / 255.0f;
            float l = (float)(j >> 8 & 0xFF) / 255.0f;
            float m = (float)(j & 0xFF) / 255.0f;
            return new BlockFallingDustParticle(arg2, d, e, f, k, l, m, this.spriteProvider);
        }
    }
}

