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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class WaterFluid
extends FlowableFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_WATER;
    }

    @Override
    public Fluid getStill() {
        return Fluids.WATER;
    }

    @Override
    public Item getBucketItem() {
        return Items.WATER_BUCKET;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(World arg, BlockPos arg2, FluidState arg3, Random random) {
        if (!arg3.isStill() && !arg3.get(FALLING).booleanValue()) {
            if (random.nextInt(64) == 0) {
                arg.playSound((double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
            }
        } else if (random.nextInt(10) == 0) {
            arg.addParticle(ParticleTypes.UNDERWATER, (double)arg2.getX() + (double)random.nextFloat(), (double)arg2.getY() + (double)random.nextFloat(), (double)arg2.getZ() + (double)random.nextFloat(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    protected boolean isInfinite() {
        return true;
    }

    @Override
    protected void beforeBreakingBlock(IWorld arg, BlockPos arg2, BlockState arg3) {
        BlockEntity lv = arg3.getBlock().hasBlockEntity() ? arg.getBlockEntity(arg2) : null;
        Block.dropStacks(arg3, arg.getWorld(), arg2, lv);
    }

    @Override
    public int getFlowSpeed(WorldView arg) {
        return 4;
    }

    @Override
    public BlockState toBlockState(FluidState arg) {
        return (BlockState)Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, WaterFluid.method_15741(arg));
    }

    @Override
    public boolean matchesType(Fluid arg) {
        return arg == Fluids.WATER || arg == Fluids.FLOWING_WATER;
    }

    @Override
    public int getLevelDecreasePerBlock(WorldView arg) {
        return 1;
    }

    @Override
    public int getTickRate(WorldView arg) {
        return 5;
    }

    @Override
    public boolean canBeReplacedWith(FluidState arg, BlockView arg2, BlockPos arg3, Fluid arg4, Direction arg5) {
        return arg5 == Direction.DOWN && !arg4.isIn(FluidTags.WATER);
    }

    @Override
    protected float getBlastResistance() {
        return 100.0f;
    }

    public static class Flowing
    extends WaterFluid {
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
    extends WaterFluid {
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

