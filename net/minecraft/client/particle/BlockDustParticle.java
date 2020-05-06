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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class BlockDustParticle
extends SpriteBillboardParticle {
    private final BlockState blockState;
    private BlockPos blockPos;
    private final float sampleU;
    private final float sampleV;

    public BlockDustParticle(ClientWorld arg, double d, double e, double f, double g, double h, double i, BlockState arg2) {
        super(arg, d, e, f, g, h, i);
        this.blockState = arg2;
        this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(arg2));
        this.gravityStrength = 1.0f;
        this.colorRed = 0.6f;
        this.colorGreen = 0.6f;
        this.colorBlue = 0.6f;
        this.scale /= 2.0f;
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    public BlockDustParticle setBlockPos(BlockPos arg) {
        this.blockPos = arg;
        if (this.blockState.isOf(Blocks.GRASS_BLOCK)) {
            return this;
        }
        this.updateColor(arg);
        return this;
    }

    public BlockDustParticle setBlockPosFromPosition() {
        this.blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.blockState.isOf(Blocks.GRASS_BLOCK)) {
            return this;
        }
        this.updateColor(this.blockPos);
        return this;
    }

    protected void updateColor(@Nullable BlockPos arg) {
        int i = MinecraftClient.getInstance().getBlockColorMap().getColor(this.blockState, this.world, arg, 0);
        this.colorRed *= (float)(i >> 16 & 0xFF) / 255.0f;
        this.colorGreen *= (float)(i >> 8 & 0xFF) / 255.0f;
        this.colorBlue *= (float)(i & 0xFF) / 255.0f;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0f * 16.0f);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    public int getColorMultiplier(float f) {
        int i = super.getColorMultiplier(f);
        int j = 0;
        if (this.world.isChunkLoaded(this.blockPos)) {
            j = WorldRenderer.getLightmapCoordinates(this.world, this.blockPos);
        }
        return i == 0 ? j : i;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<BlockStateParticleEffect> {
        @Override
        public Particle createParticle(BlockStateParticleEffect arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            BlockState lv = arg.getBlockState();
            if (lv.isAir() || lv.isOf(Blocks.MOVING_PISTON)) {
                return null;
            }
            return new BlockDustParticle(arg2, d, e, f, g, h, i, lv).setBlockPosFromPosition();
        }
    }
}

