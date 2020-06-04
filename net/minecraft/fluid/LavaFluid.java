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
    public void randomDisplayTick(World arg, BlockPos arg2, FluidState arg3, Random random) {
        BlockPos lv = arg2.up();
        if (arg.getBlockState(lv).isAir() && !arg.getBlockState(lv).isOpaqueFullCube(arg, lv)) {
            if (random.nextInt(100) == 0) {
                double d = (double)arg2.getX() + random.nextDouble();
                double e = (double)arg2.getY() + 1.0;
                double f = (double)arg2.getZ() + random.nextDouble();
                arg.addParticle(ParticleTypes.LAVA, d, e, f, 0.0, 0.0, 0.0);
                arg.playSound(d, e, f, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
            if (random.nextInt(200) == 0) {
                arg.playSound(arg2.getX(), (double)arg2.getY(), (double)arg2.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }

    @Override
    public void onRandomTick(World arg, BlockPos arg2, FluidState arg3, Random random) {
        if (!arg.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }
        int i = random.nextInt(3);
        if (i > 0) {
            BlockPos lv = arg2;
            for (int j = 0; j < i; ++j) {
                if (!arg.canSetBlock(lv = lv.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1))) {
                    return;
                }
                BlockState lv2 = arg.getBlockState(lv);
                if (lv2.isAir()) {
                    if (!this.canLightFire(arg, lv)) continue;
                    arg.setBlockState(lv, AbstractFireBlock.getState(arg, lv));
                    return;
                }
                if (!lv2.getMaterial().blocksMovement()) continue;
                return;
            }
        } else {
            for (int k = 0; k < 3; ++k) {
                BlockPos lv3 = arg2.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                if (!arg.canSetBlock(lv3)) {
                    return;
                }
                if (!arg.isAir(lv3.up()) || !this.hasBurnableBlock(arg, lv3)) continue;
                arg.setBlockState(lv3.up(), AbstractFireBlock.getState(arg, lv3));
            }
        }
    }

    private boolean canLightFire(WorldView arg, BlockPos arg2) {
        for (Direction lv : Direction.values()) {
            if (!this.hasBurnableBlock(arg, arg2.offset(lv))) continue;
            return true;
        }
        return false;
    }

    private boolean hasBurnableBlock(WorldView arg, BlockPos arg2) {
        if (arg2.getY() >= 0 && arg2.getY() < 256 && !arg.isChunkLoaded(arg2)) {
            return false;
        }
        return arg.getBlockState(arg2).getMaterial().isBurnable();
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_LAVA;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess arg, BlockPos arg2, BlockState arg3) {
        this.playExtinguishEvent(arg, arg2);
    }

    @Override
    public int getFlowSpeed(WorldView arg) {
        return arg.getDimension().isUltrawarm() ? 4 : 2;
    }

    @Override
    public BlockState toBlockState(FluidState arg) {
        return (BlockState)Blocks.LAVA.getDefaultState().with(FluidBlock.LEVEL, LavaFluid.method_15741(arg));
    }

    @Override
    public boolean matchesType(Fluid arg) {
        return arg == Fluids.LAVA || arg == Fluids.FLOWING_LAVA;
    }

    @Override
    public int getLevelDecreasePerBlock(WorldView arg) {
        return arg.getDimension().isUltrawarm() ? 1 : 2;
    }

    @Override
    public boolean canBeReplacedWith(FluidState arg, BlockView arg2, BlockPos arg3, Fluid arg4, Direction arg5) {
        return arg.getHeight(arg2, arg3) >= 0.44444445f && arg4.isIn(FluidTags.WATER);
    }

    @Override
    public int getTickRate(WorldView arg) {
        return arg.getDimension().hasCeiling() ? 10 : 30;
    }

    @Override
    public int getNextTickDelay(World arg, BlockPos arg2, FluidState arg3, FluidState arg4) {
        int i = this.getTickRate(arg);
        if (!(arg3.isEmpty() || arg4.isEmpty() || arg3.get(FALLING).booleanValue() || arg4.get(FALLING).booleanValue() || !(arg4.getHeight(arg, arg2) > arg3.getHeight(arg, arg2)) || arg.getRandom().nextInt(4) == 0)) {
            i *= 4;
        }
        return i;
    }

    private void playExtinguishEvent(WorldAccess arg, BlockPos arg2) {
        arg.syncWorldEvent(1501, arg2, 0);
    }

    @Override
    protected boolean isInfinite() {
        return false;
    }

    @Override
    protected void flow(WorldAccess arg, BlockPos arg2, BlockState arg3, Direction arg4, FluidState arg5) {
        if (arg4 == Direction.DOWN) {
            FluidState lv = arg.getFluidState(arg2);
            if (this.isIn(FluidTags.LAVA) && lv.matches(FluidTags.WATER)) {
                if (arg3.getBlock() instanceof FluidBlock) {
                    arg.setBlockState(arg2, Blocks.STONE.getDefaultState(), 3);
                }
                this.playExtinguishEvent(arg, arg2);
                return;
            }
        }
        super.flow(arg, arg2, arg3, arg4, arg5);
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
        public int getLevel(FluidState arg) {
            return arg.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState arg) {
            return false;
        }
    }

    public static class Still
    extends LavaFluid {
        @Override
        public int getLevel(FluidState arg) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState arg) {
            return true;
        }
    }
}

