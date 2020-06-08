/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
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
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FluidBlock
extends Block
implements FluidDrainable {
    public static final IntProperty LEVEL = Properties.LEVEL_15;
    protected final FlowableFluid fluid;
    private final List<FluidState> statesByLevel;
    public static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    protected FluidBlock(FlowableFluid arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.fluid = arg;
        this.statesByLevel = Lists.newArrayList();
        this.statesByLevel.add(arg.getStill(false));
        for (int i = 1; i < 8; ++i) {
            this.statesByLevel.add(arg.getFlowing(8 - i, false));
        }
        this.statesByLevel.add(arg.getFlowing(8, true));
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL, 0));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (arg4.isAbove(COLLISION_SHAPE, arg3, true) && arg.get(LEVEL) == 0 && arg4.method_27866(arg2.getFluidState(arg3.up()), this.fluid)) {
            return COLLISION_SHAPE;
        }
        return VoxelShapes.empty();
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return arg.getFluidState().hasRandomTicks();
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        arg.getFluidState().onRandomTick(arg2, arg3, random);
    }

    @Override
    public boolean isTranslucent(BlockState arg, BlockView arg2, BlockPos arg3) {
        return false;
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return !this.fluid.isIn(FluidTags.LAVA);
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        int i = arg.get(LEVEL);
        return this.statesByLevel.get(Math.min(i, 8));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState arg, BlockState arg2, Direction arg3) {
        return arg2.getFluidState().getFluid().matchesType(this.fluid);
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState arg, LootContext.Builder arg2) {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return VoxelShapes.empty();
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (this.receiveNeighborFluids(arg2, arg3, arg)) {
            arg2.getFluidTickScheduler().schedule(arg3, arg.getFluidState().getFluid(), this.fluid.getTickRate(arg2));
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.getFluidState().isStill() || arg3.getFluidState().isStill()) {
            arg4.getFluidTickScheduler().schedule(arg5, arg.getFluidState().getFluid(), this.fluid.getTickRate(arg4));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (this.receiveNeighborFluids(arg2, arg3, arg)) {
            arg2.getFluidTickScheduler().schedule(arg3, arg.getFluidState().getFluid(), this.fluid.getTickRate(arg2));
        }
    }

    private boolean receiveNeighborFluids(World arg, BlockPos arg2, BlockState arg3) {
        if (this.fluid.isIn(FluidTags.LAVA)) {
            boolean bl = arg.getBlockState(arg2.down()).isOf(Blocks.SOUL_SOIL);
            for (Direction lv : Direction.values()) {
                if (lv == Direction.DOWN) continue;
                BlockPos lv2 = arg2.offset(lv);
                if (arg.getFluidState(lv2).matches(FluidTags.WATER)) {
                    Block lv3 = arg.getFluidState(arg2).isStill() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                    arg.setBlockState(arg2, lv3.getDefaultState());
                    this.playExtinguishSound(arg, arg2);
                    return false;
                }
                if (!bl || !arg.getBlockState(lv2).isOf(Blocks.BLUE_ICE)) continue;
                arg.setBlockState(arg2, Blocks.BASALT.getDefaultState());
                this.playExtinguishSound(arg, arg2);
                return false;
            }
        }
        return true;
    }

    private void playExtinguishSound(WorldAccess arg, BlockPos arg2) {
        arg.syncWorldEvent(1501, arg2, 0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(LEVEL);
    }

    @Override
    public Fluid tryDrainFluid(WorldAccess arg, BlockPos arg2, BlockState arg3) {
        if (arg3.get(LEVEL) == 0) {
            arg.setBlockState(arg2, Blocks.AIR.getDefaultState(), 11);
            return this.fluid;
        }
        return Fluids.EMPTY;
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (this.fluid.isIn(FluidTags.LAVA)) {
            float f = (float)arg3.getY() + arg.getFluidState().getHeight(arg2, arg3);
            Box lv = arg4.getBoundingBox();
            if (lv.minY < (double)f || (double)f > lv.maxY) {
                arg4.setInLava();
            }
        }
    }
}

