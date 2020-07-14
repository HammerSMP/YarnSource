/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class LavaFluid
extends FlowableFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_LAVA;
    }

    @Override
    public Fluid getStill() {
        return Fluids.LAVA;
    }

    @Override
    public Item getBucketItem() {
        return Items.LAVA_BUCKET;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
        BlockPos lv = pos.up();
        if (world.getBlockState(lv).isAir() && !world.getBlockState(lv).isOpaqueFullCube(world, lv)) {
            if (random.nextInt(100) == 0) {
                double d = (double)pos.getX() + random.nextDouble();
                double e = (double)pos.getY() + 1.0;
                double f = (double)pos.getZ() + random.nextDouble();
                world.addParticle(ParticleTypes.LAVA, d, e, f, 0.0, 0.0, 0.0);
                world.playSound(d, e, f, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
            if (random.nextInt(200) == 0) {
                world.playSound(pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }

    @Override
    public void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
        if (!world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }
        int i = random.nextInt(3);
        if (i > 0) {
            BlockPos lv = pos;
            for (int j = 0; j < i; ++j) {
                if (!world.canSetBlock(lv = lv.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1))) {
                    return;
                }
                BlockState lv2 = world.getBlockState(lv);
                if (lv2.isAir()) {
                    if (!this.canLightFire(world, lv)) continue;
                    world.setBlockState(lv, AbstractFireBlock.getState(world, lv));
                    return;
                }
                if (!lv2.getMaterial().blocksMovement()) continue;
                return;
            }
        } else {
            for (int k = 0; k < 3; ++k) {
                BlockPos lv3 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                if (!world.canSetBlock(lv3)) {
                    return;
                }
                if (!world.isAir(lv3.up()) || !this.hasBurnableBlock(world, lv3)) continue;
                world.setBlockState(lv3.up(), AbstractFireBlock.getState(world, lv3));
            }
        }
    }

    private boolean canLightFire(WorldView world, BlockPos pos) {
        for (Direction lv : Direction.values()) {
            if (!this.hasBurnableBlock(world, pos.offset(lv))) continue;
            return true;
        }
        return false;
    }

    private boolean hasBurnableBlock(WorldView world, BlockPos pos) {
        if (pos.getY() >= 0 && pos.getY() < 256 && !world.isChunkLoaded(pos)) {
            return false;
        }
        return world.getBlockState(pos).getMaterial().isBurnable();
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_LAVA;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        this.playExtinguishEvent(world, pos);
    }

    @Override
    public int getFlowSpeed(WorldView world) {
        return world.getDimension().isUltrawarm() ? 4 : 2;
    }

    @Override
    public BlockState toBlockState(FluidState state) {
        return (BlockState)Blocks.LAVA.getDefaultState().with(FluidBlock.LEVEL, LavaFluid.method_15741(state));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
    }

    @Override
    public int getLevelDecreasePerBlock(WorldView world) {
        return world.getDimension().isUltrawarm() ? 1 : 2;
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return state.getHeight(world, pos) >= 0.44444445f && fluid.isIn(FluidTags.WATER);
    }

    @Override
    public int getTickRate(WorldView world) {
        return world.getDimension().isUltrawarm() ? 10 : 30;
    }

    @Override
    public int getNextTickDelay(World world, BlockPos pos, FluidState oldState, FluidState newState) {
        int i = this.getTickRate(world);
        if (!(oldState.isEmpty() || newState.isEmpty() || oldState.get(FALLING).booleanValue() || newState.get(FALLING).booleanValue() || !(newState.getHeight(world, pos) > oldState.getHeight(world, pos)) || world.getRandom().nextInt(4) == 0)) {
            i *= 4;
        }
        return i;
    }

    private void playExtinguishEvent(WorldAccess world, BlockPos pos) {
        world.syncWorldEvent(1501, pos, 0);
    }

    @Override
    protected boolean isInfinite() {
        return false;
    }

    @Override
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState lv = world.getFluidState(pos);
            if (this.isIn(FluidTags.LAVA) && lv.isIn(FluidTags.WATER)) {
                if (state.getBlock() instanceof FluidBlock) {
                    world.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
                }
                this.playExtinguishEvent(world, pos);
                return;
            }
        }
        super.flow(world, pos, state, direction, fluidState);
    }

    @Override
    protected boolean hasRandomTicks() {
        return true;
    }

    @Override
    protected float getBlastResistance() {
        return 100.0f;
    }

    public static class Flowing
    extends LavaFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> arg) {
            super.appendProperties(arg);
            arg.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still
    extends LavaFluid {
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}

