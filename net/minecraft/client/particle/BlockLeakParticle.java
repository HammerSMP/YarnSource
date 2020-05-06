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
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class BlockLeakParticle
extends SpriteBillboardParticle {
    private final Fluid fluid;
    protected boolean obsidianTear;

    private BlockLeakParticle(ClientWorld arg, double d, double e, double f, Fluid arg2) {
        super(arg, d, e, f);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.gravityStrength = 0.06f;
        this.fluid = arg2;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getColorMultiplier(float f) {
        if (this.obsidianTear) {
            return 240;
        }
        return super.getColorMultiplier(f);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.updateAge();
        if (this.dead) {
            return;
        }
        this.velocityY -= (double)this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.updateVelocity();
        if (this.dead) {
            return;
        }
        this.velocityX *= (double)0.98f;
        this.velocityY *= (double)0.98f;
        this.velocityZ *= (double)0.98f;
        BlockPos lv = new BlockPos(this.x, this.y, this.z);
        FluidState lv2 = this.world.getFluidState(lv);
        if (lv2.getFluid() == this.fluid && this.y < (double)((float)lv.getY() + lv2.getHeight(this.world, lv))) {
            this.markDead();
        }
    }

    protected void updateAge() {
        if (this.maxAge-- <= 0) {
            this.markDead();
        }
    }

    protected void updateVelocity() {
    }

    @Environment(value=EnvType.CLIENT)
    public static class LandingObsidianTearFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public LandingObsidianTearFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            Landing lv = new Landing(arg2, d, e, f, Fluids.EMPTY);
            lv.obsidianTear = true;
            lv.maxAge = (int)(28.0 / (Math.random() * 0.8 + 0.2));
            lv.setColor(0.51171875f, 0.03125f, 0.890625f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingObsidianTearFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingObsidianTearFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            ContinuousFalling lv = new ContinuousFalling(arg2, d, e, f, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
            lv.obsidianTear = true;
            lv.gravityStrength = 0.01f;
            lv.setColor(0.51171875f, 0.03125f, 0.890625f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DrippingObsidianTearFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingObsidianTearFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            Dripping lv = new Dripping(arg2, d, e, f, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
            lv.obsidianTear = true;
            lv.gravityStrength *= 0.01f;
            lv.maxAge = 100;
            lv.setColor(0.51171875f, 0.03125f, 0.890625f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingNectarFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingNectarFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            Falling lv = new Falling(arg2, d, e, f, Fluids.EMPTY);
            lv.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
            lv.gravityStrength = 0.007f;
            lv.setColor(0.92f, 0.782f, 0.72f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LandingHoneyFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public LandingHoneyFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            Landing lv = new Landing(arg2, d, e, f, Fluids.EMPTY);
            lv.maxAge = (int)(128.0 / (Math.random() * 0.8 + 0.2));
            lv.setColor(0.522f, 0.408f, 0.082f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingHoneyFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingHoneyFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            FallingHoney lv = new FallingHoney(arg2, d, e, f, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
            lv.gravityStrength = 0.01f;
            lv.setColor(0.582f, 0.448f, 0.082f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DrippingHoneyFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingHoneyFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            Dripping lv = new Dripping(arg2, d, e, f, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
            lv.gravityStrength *= 0.01f;
            lv.maxAge = 100;
            lv.setColor(0.622f, 0.508f, 0.082f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LandingLavaFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public LandingLavaFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            Landing lv = new Landing(arg2, d, e, f, Fluids.LAVA);
            lv.setColor(1.0f, 0.2857143f, 0.083333336f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingLavaFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingLavaFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            ContinuousFalling lv = new ContinuousFalling(arg2, d, e, f, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
            lv.setColor(1.0f, 0.2857143f, 0.083333336f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DrippingLavaFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingLavaFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            DrippingLava lv = new DrippingLava(arg2, d, e, f, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingWaterFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingWaterFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            ContinuousFalling lv = new ContinuousFalling(arg2, d, e, f, Fluids.WATER, ParticleTypes.SPLASH);
            lv.setColor(0.2f, 0.3f, 1.0f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DrippingWaterFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingWaterFactory(SpriteProvider arg) {
            this.spriteProvider = arg;
        }

        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            Dripping lv = new Dripping(arg2, d, e, f, Fluids.WATER, ParticleTypes.FALLING_WATER);
            lv.setColor(0.2f, 0.3f, 1.0f);
            lv.setSprite(this.spriteProvider);
            return lv;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Landing
    extends BlockLeakParticle {
        private Landing(ClientWorld arg, double d, double e, double f, Fluid arg2) {
            super(arg, d, e, f, arg2);
            this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Falling
    extends BlockLeakParticle {
        private Falling(ClientWorld arg, double d, double e, double f, Fluid arg2) {
            super(arg, d, e, f, arg2);
            this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        }

        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class FallingHoney
    extends ContinuousFalling {
        private FallingHoney(ClientWorld arg, double d, double e, double f, Fluid arg2, ParticleEffect arg3) {
            super(arg, d, e, f, arg2, arg3);
        }

        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                this.world.playSound(this.x + 0.5, this.y, this.z + 0.5, SoundEvents.BLOCK_BEEHIVE_DRIP, SoundCategory.BLOCKS, 0.3f + this.world.random.nextFloat() * 2.0f / 3.0f, 1.0f, false);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ContinuousFalling
    extends Falling {
        protected final ParticleEffect nextParticle;

        private ContinuousFalling(ClientWorld arg, double d, double e, double f, Fluid arg2, ParticleEffect arg3) {
            super(arg, d, e, f, arg2);
            this.nextParticle = arg3;
        }

        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DrippingLava
    extends Dripping {
        private DrippingLava(ClientWorld arg, double d, double e, double f, Fluid arg2, ParticleEffect arg3) {
            super(arg, d, e, f, arg2, arg3);
        }

        @Override
        protected void updateAge() {
            this.colorRed = 1.0f;
            this.colorGreen = 16.0f / (float)(40 - this.maxAge + 16);
            this.colorBlue = 4.0f / (float)(40 - this.maxAge + 8);
            super.updateAge();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Dripping
    extends BlockLeakParticle {
        private final ParticleEffect nextParticle;

        private Dripping(ClientWorld arg, double d, double e, double f, Fluid arg2, ParticleEffect arg3) {
            super(arg, d, e, f, arg2);
            this.nextParticle = arg3;
            this.gravityStrength *= 0.02f;
            this.maxAge = 40;
        }

        @Override
        protected void updateAge() {
            if (this.maxAge-- <= 0) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
            }
        }

        @Override
        protected void updateVelocity() {
            this.velocityX *= 0.02;
            this.velocityY *= 0.02;
            this.velocityZ *= 0.02;
        }
    }
}

