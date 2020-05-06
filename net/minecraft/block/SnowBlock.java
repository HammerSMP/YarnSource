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
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;

public class SnowBlock
extends Block {
    public static final IntProperty LAYERS = Properties.LAYERS;
    protected static final VoxelShape[] LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    protected SnowBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LAYERS, 1));
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        switch (arg4) {
            case LAND: {
                return arg.get(LAYERS) < 5;
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return false;
            }
        }
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return LAYERS_TO_SHAPE[arg.get(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return LAYERS_TO_SHAPE[arg.get(LAYERS) - 1];
    }

    @Override
    public VoxelShape getSidesShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return LAYERS_TO_SHAPE[arg.get(LAYERS)];
    }

    @Override
    public VoxelShape getVisualShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return LAYERS_TO_SHAPE[arg.get(LAYERS)];
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return true;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockState lv = arg2.getBlockState(arg3.down());
        if (lv.isOf(Blocks.ICE) || lv.isOf(Blocks.PACKED_ICE) || lv.isOf(Blocks.BARRIER)) {
            return false;
        }
        if (lv.isOf(Blocks.HONEY_BLOCK) || lv.isOf(Blocks.SOUL_SAND)) {
            return true;
        }
        return Block.isFaceFullSquare(lv.getCollisionShape(arg2, arg3.down()), Direction.UP) || lv.getBlock() == this && lv.get(LAYERS) == 8;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg2.getLightLevel(LightType.BLOCK, arg3) > 11) {
            SnowBlock.dropStacks(arg, arg2, arg3);
            arg2.removeBlock(arg3, false);
        }
    }

    @Override
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        int i = arg.get(LAYERS);
        if (arg2.getStack().getItem() == this.asItem() && i < 8) {
            if (arg2.canReplaceExisting()) {
                return arg2.getSide() == Direction.UP;
            }
            return true;
        }
        return i == 1;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = arg.getWorld().getBlockState(arg.getBlockPos());
        if (lv.isOf(this)) {
            int i = lv.get(LAYERS);
            return (BlockState)lv.with(LAYERS, Math.min(8, i + 1));
        }
        return super.getPlacementState(arg);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(LAYERS);
    }
}

