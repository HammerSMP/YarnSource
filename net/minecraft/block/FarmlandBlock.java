/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.StemBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class FarmlandBlock
extends Block {
    public static final IntProperty MOISTURE = Properties.MOISTURE;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    protected FarmlandBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(MOISTURE, 0));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == Direction.UP && !arg.canPlaceAt(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockState lv = arg2.getBlockState(arg3.up());
        return !lv.getMaterial().isSolid() || lv.getBlock() instanceof FenceGateBlock || lv.getBlock() instanceof PistonExtensionBlock;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        if (!this.getDefaultState().canPlaceAt(arg.getWorld(), arg.getBlockPos())) {
            return Blocks.DIRT.getDefaultState();
        }
        return super.getPlacementState(arg);
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.canPlaceAt(arg2, arg3)) {
            FarmlandBlock.setToDirt(arg, arg2, arg3);
        }
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        int i = arg.get(MOISTURE);
        if (FarmlandBlock.isWaterNearby(arg2, arg3) || arg2.hasRain(arg3.up())) {
            if (i < 7) {
                arg2.setBlockState(arg3, (BlockState)arg.with(MOISTURE, 7), 2);
            }
        } else if (i > 0) {
            arg2.setBlockState(arg3, (BlockState)arg.with(MOISTURE, i - 1), 2);
        } else if (!FarmlandBlock.hasCrop(arg2, arg3)) {
            FarmlandBlock.setToDirt(arg, arg2, arg3);
        }
    }

    @Override
    public void onLandedUpon(World arg, BlockPos arg2, Entity arg3, float f) {
        if (!arg.isClient && arg.random.nextFloat() < f - 0.5f && arg3 instanceof LivingEntity && (arg3 instanceof PlayerEntity || arg.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) && arg3.getWidth() * arg3.getWidth() * arg3.getHeight() > 0.512f) {
            FarmlandBlock.setToDirt(arg.getBlockState(arg2), arg, arg2);
        }
        super.onLandedUpon(arg, arg2, arg3, f);
    }

    public static void setToDirt(BlockState arg, World arg2, BlockPos arg3) {
        arg2.setBlockState(arg3, FarmlandBlock.pushEntitiesUpBeforeBlockChange(arg, Blocks.DIRT.getDefaultState(), arg2, arg3));
    }

    private static boolean hasCrop(BlockView arg, BlockPos arg2) {
        Block lv = arg.getBlockState(arg2.up()).getBlock();
        return lv instanceof CropBlock || lv instanceof StemBlock || lv instanceof AttachedStemBlock;
    }

    private static boolean isWaterNearby(WorldView arg, BlockPos arg2) {
        for (BlockPos lv : BlockPos.iterate(arg2.add(-4, 0, -4), arg2.add(4, 1, 4))) {
            if (!arg.getFluidState(lv).isIn(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(MOISTURE);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

