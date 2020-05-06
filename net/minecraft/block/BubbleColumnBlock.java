/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BubbleColumnBlock
extends Block
implements FluidDrainable {
    public static final BooleanProperty DRAG = Properties.DRAG;

    public BubbleColumnBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(DRAG, true));
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        BlockState lv = arg2.getBlockState(arg3.up());
        if (lv.isAir()) {
            arg4.onBubbleColumnSurfaceCollision(arg.get(DRAG));
            if (!arg2.isClient) {
                ServerWorld lv2 = (ServerWorld)arg2;
                for (int i = 0; i < 2; ++i) {
                    lv2.spawnParticles(ParticleTypes.SPLASH, (float)arg3.getX() + arg2.random.nextFloat(), arg3.getY() + 1, (float)arg3.getZ() + arg2.random.nextFloat(), 1, 0.0, 0.0, 0.0, 1.0);
                    lv2.spawnParticles(ParticleTypes.BUBBLE, (float)arg3.getX() + arg2.random.nextFloat(), arg3.getY() + 1, (float)arg3.getZ() + arg2.random.nextFloat(), 1, 0.0, 0.01, 0.0, 0.2);
                }
            }
        } else {
            arg4.onBubbleColumnCollision(arg.get(DRAG));
        }
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        BubbleColumnBlock.update(arg2, arg3.up(), BubbleColumnBlock.calculateDrag(arg2, arg3.down()));
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        BubbleColumnBlock.update(arg2, arg3.up(), BubbleColumnBlock.calculateDrag(arg2, arg3));
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        return Fluids.WATER.getStill(false);
    }

    public static void update(IWorld arg, BlockPos arg2, boolean bl) {
        if (BubbleColumnBlock.isStillWater(arg, arg2)) {
            arg.setBlockState(arg2, (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, bl), 2);
        }
    }

    public static boolean isStillWater(IWorld arg, BlockPos arg2) {
        FluidState lv = arg.getFluidState(arg2);
        return arg.getBlockState(arg2).isOf(Blocks.WATER) && lv.getLevel() >= 8 && lv.isStill();
    }

    private static boolean calculateDrag(BlockView arg, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        if (lv.isOf(Blocks.BUBBLE_COLUMN)) {
            return lv.get(DRAG);
        }
        return !lv.isOf(Blocks.SOUL_SAND);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        double d = arg3.getX();
        double e = arg3.getY();
        double f = arg3.getZ();
        if (arg.get(DRAG).booleanValue()) {
            arg2.addImportantParticle(ParticleTypes.CURRENT_DOWN, d + 0.5, e + 0.8, f, 0.0, 0.0, 0.0);
            if (random.nextInt(200) == 0) {
                arg2.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        } else {
            arg2.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + 0.5, e, f + 0.5, 0.0, 0.04, 0.0);
            arg2.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0, 0.04, 0.0);
            if (random.nextInt(200) == 0) {
                arg2.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg.canPlaceAt(arg4, arg5)) {
            return Blocks.WATER.getDefaultState();
        }
        if (arg2 == Direction.DOWN) {
            arg4.setBlockState(arg5, (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, BubbleColumnBlock.calculateDrag(arg4, arg6)), 2);
        } else if (arg2 == Direction.UP && !arg3.isOf(Blocks.BUBBLE_COLUMN) && BubbleColumnBlock.isStillWater(arg4, arg6)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 5);
        }
        arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockState lv = arg2.getBlockState(arg3.down());
        return lv.isOf(Blocks.BUBBLE_COLUMN) || lv.isOf(Blocks.MAGMA_BLOCK) || lv.isOf(Blocks.SOUL_SAND);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(DRAG);
    }

    @Override
    public Fluid tryDrainFluid(IWorld arg, BlockPos arg2, BlockState arg3) {
        arg.setBlockState(arg2, Blocks.AIR.getDefaultState(), 11);
        return Fluids.WATER;
    }
}

