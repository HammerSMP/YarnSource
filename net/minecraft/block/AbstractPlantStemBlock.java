/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantPartBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractPlantStemBlock
extends AbstractPlantPartBlock
implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_25;
    private final double growthChance;

    protected AbstractPlantStemBlock(AbstractBlock.Settings settings, Direction growthDirection, VoxelShape outlineShape, boolean tickWater, double growthChance) {
        super(settings, growthDirection, outlineShape, tickWater);
        this.growthChance = growthChance;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public BlockState getRandomGrowthState(WorldAccess arg) {
        return (BlockState)this.getDefaultState().with(AGE, arg.getRandom().nextInt(25));
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < 25;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos lv;
        if (state.get(AGE) < 25 && random.nextDouble() < this.growthChance && this.chooseStemState(world.getBlockState(lv = pos.offset(this.growthDirection)))) {
            world.setBlockState(lv, (BlockState)state.cycle(AGE));
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == this.growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        if (direction == this.growthDirection && (newState.isOf(this) || newState.isOf(this.getPlant()))) {
            return this.getPlant().getDefaultState();
        }
        if (this.tickWater) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return this.chooseStemState(world.getBlockState(pos.offset(this.growthDirection)));
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos lv = pos.offset(this.growthDirection);
        int i = Math.min(state.get(AGE) + 1, 25);
        int j = this.method_26376(random);
        for (int k = 0; k < j && this.chooseStemState(world.getBlockState(lv)); ++k) {
            world.setBlockState(lv, (BlockState)state.with(AGE, i));
            lv = lv.offset(this.growthDirection);
            i = Math.min(i + 1, 25);
        }
    }

    protected abstract int method_26376(Random var1);

    protected abstract boolean chooseStemState(BlockState var1);

    @Override
    protected AbstractPlantStemBlock getStem() {
        return this;
    }
}

