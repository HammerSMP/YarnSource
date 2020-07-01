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

    protected AbstractPlantStemBlock(AbstractBlock.Settings arg, Direction arg2, VoxelShape arg3, boolean bl, double d) {
        super(arg, arg2, arg3, bl);
        this.growthChance = d;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public BlockState getRandomGrowthState(WorldAccess arg) {
        return (BlockState)this.getDefaultState().with(AGE, arg.getRandom().nextInt(25));
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return arg.get(AGE) < 25;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        BlockPos lv;
        if (arg.get(AGE) < 25 && random.nextDouble() < this.growthChance && this.chooseStemState(arg2.getBlockState(lv = arg3.offset(this.growthDirection)))) {
            arg2.setBlockState(lv, (BlockState)arg.cycle(AGE));
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == this.growthDirection.getOpposite() && !arg.canPlaceAt(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        if (arg2 == this.growthDirection && (arg3.isOf(this) || arg3.isOf(this.getPlant()))) {
            return this.getPlant().getDefaultState();
        }
        if (this.tickWater) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE);
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        return this.chooseStemState(arg.getBlockState(arg2.offset(this.growthDirection)));
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        BlockPos lv = arg2.offset(this.growthDirection);
        int i = Math.min(arg3.get(AGE) + 1, 25);
        int j = this.method_26376(random);
        for (int k = 0; k < j && this.chooseStemState(arg.getBlockState(lv)); ++k) {
            arg.setBlockState(lv, (BlockState)arg3.with(AGE, i));
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

