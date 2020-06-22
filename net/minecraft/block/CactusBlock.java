/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CactusBlock
extends Block {
    public static final IntProperty AGE = Properties.AGE_15;
    protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    protected static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    protected CactusBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.breakBlock(arg3, true);
        }
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        BlockPos lv = arg3.up();
        if (!arg2.isAir(lv)) {
            return;
        }
        int i = 1;
        while (arg2.getBlockState(arg3.down(i)).isOf(this)) {
            ++i;
        }
        if (i >= 3) {
            return;
        }
        int j = arg.get(AGE);
        if (j == 15) {
            arg2.setBlockState(lv, this.getDefaultState());
            BlockState lv2 = (BlockState)arg.with(AGE, 0);
            arg2.setBlockState(arg3, lv2, 4);
            lv2.neighborUpdate(arg2, lv, this, arg3, false);
        } else {
            arg2.setBlockState(arg3, (BlockState)arg.with(AGE, j + 1), 4);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return OUTLINE_SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg.canPlaceAt(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BlockState lv2 = arg2.getBlockState(arg3.offset(lv));
            Material lv3 = lv2.getMaterial();
            if (!lv3.isSolid() && !arg2.getFluidState(arg3.offset(lv)).isIn(FluidTags.LAVA)) continue;
            return false;
        }
        BlockState lv4 = arg2.getBlockState(arg3.down());
        return (lv4.isOf(Blocks.CACTUS) || lv4.isOf(Blocks.SAND) || lv4.isOf(Blocks.RED_SAND)) && !arg2.getBlockState(arg3.up()).getMaterial().isLiquid();
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        arg4.damage(DamageSource.CACTUS, 1.0f);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

