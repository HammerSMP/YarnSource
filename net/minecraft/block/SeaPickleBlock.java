/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SeaPickleBlock
extends PlantBlock
implements Fertilizable,
Waterloggable {
    public static final IntProperty PICKLES = Properties.PICKLES;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape ONE_PICKLE_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
    protected static final VoxelShape TWO_PICKLES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
    protected static final VoxelShape THREE_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    protected static final VoxelShape FOUR_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

    protected SeaPickleBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(PICKLES, 1)).with(WATERLOGGED, true));
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = arg.getWorld().getBlockState(arg.getBlockPos());
        if (lv.isOf(this)) {
            return (BlockState)lv.with(PICKLES, Math.min(4, lv.get(PICKLES) + 1));
        }
        FluidState lv2 = arg.getWorld().getFluidState(arg.getBlockPos());
        boolean bl = lv2.matches(FluidTags.WATER) && lv2.getLevel() == 8;
        return (BlockState)super.getPlacementState(arg).with(WATERLOGGED, bl);
    }

    public static boolean isDry(BlockState arg) {
        return arg.get(WATERLOGGED) == false;
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return !arg.getCollisionShape(arg2, arg3).getFace(Direction.UP).isEmpty();
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.down();
        return this.canPlantOnTop(arg2.getBlockState(lv), arg2, lv);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        if (arg2.getStack().getItem() == this.asItem() && arg.get(PICKLES) < 4) {
            return true;
        }
        return super.canReplace(arg, arg2);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        switch (arg.get(PICKLES)) {
            default: {
                return ONE_PICKLE_SHAPE;
            }
            case 2: {
                return TWO_PICKLES_SHAPE;
            }
            case 3: {
                return THREE_PICKLES_SHAPE;
            }
            case 4: 
        }
        return FOUR_PICKLES_SHAPE;
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(PICKLES, WATERLOGGED);
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        return true;
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        if (!SeaPickleBlock.isDry(arg3) && arg.getBlockState(arg2.down()).isIn(BlockTags.CORAL_BLOCKS)) {
            int i = 5;
            int j = 1;
            int k = 2;
            int l = 0;
            int m = arg2.getX() - 2;
            int n = 0;
            for (int o = 0; o < 5; ++o) {
                for (int p = 0; p < j; ++p) {
                    int q = 2 + arg2.getY() - 1;
                    for (int r = q - 2; r < q; ++r) {
                        BlockState lv2;
                        BlockPos lv = new BlockPos(m + o, r, arg2.getZ() - n + p);
                        if (lv == arg2 || random.nextInt(6) != 0 || !arg.getBlockState(lv).isOf(Blocks.WATER) || !(lv2 = arg.getBlockState(lv.down())).isIn(BlockTags.CORAL_BLOCKS)) continue;
                        arg.setBlockState(lv, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, random.nextInt(4) + 1), 3);
                    }
                }
                if (l < 2) {
                    j += 2;
                    ++n;
                } else {
                    j -= 2;
                    --n;
                }
                ++l;
            }
            arg.setBlockState(arg2, (BlockState)arg3.with(PICKLES, 4), 2);
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

