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
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LeavesBlock
extends Block {
    public static final IntProperty DISTANCE = Properties.DISTANCE_1_7;
    public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;

    public LeavesBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(DISTANCE, 7)).with(PERSISTENT, false));
    }

    @Override
    public VoxelShape getSidesShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return arg.get(DISTANCE) == 7 && arg.get(PERSISTENT) == false;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.get(PERSISTENT).booleanValue() && arg.get(DISTANCE) == 7) {
            LeavesBlock.dropStacks(arg, arg2, arg3);
            arg2.removeBlock(arg3, false);
        }
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        arg2.setBlockState(arg3, LeavesBlock.updateDistanceFromLogs(arg, arg2, arg3), 3);
    }

    @Override
    public int getOpacity(BlockState arg, BlockView arg2, BlockPos arg3) {
        return 1;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        int i = LeavesBlock.getDistanceFromLog(arg3) + 1;
        if (i != 1 || arg.get(DISTANCE) != i) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        return arg;
    }

    private static BlockState updateDistanceFromLogs(BlockState arg, WorldAccess arg2, BlockPos arg3) {
        int i = 7;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Direction lv2 : Direction.values()) {
            lv.set(arg3, lv2);
            i = Math.min(i, LeavesBlock.getDistanceFromLog(arg2.getBlockState(lv)) + 1);
            if (i == 1) break;
        }
        return (BlockState)arg.with(DISTANCE, i);
    }

    private static int getDistanceFromLog(BlockState arg) {
        if (BlockTags.LOGS.contains(arg.getBlock())) {
            return 0;
        }
        if (arg.getBlock() instanceof LeavesBlock) {
            return arg.get(DISTANCE);
        }
        return 7;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (!arg2.hasRain(arg3.up())) {
            return;
        }
        if (random.nextInt(15) != 1) {
            return;
        }
        BlockPos lv = arg3.down();
        BlockState lv2 = arg2.getBlockState(lv);
        if (lv2.isOpaque() && lv2.isSideSolidFullSquare(arg2, lv, Direction.UP)) {
            return;
        }
        double d = (float)arg3.getX() + random.nextFloat();
        double e = (double)arg3.getY() - 0.05;
        double f = (float)arg3.getZ() + random.nextFloat();
        arg2.addParticle(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(DISTANCE, PERSISTENT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return LeavesBlock.updateDistanceFromLogs((BlockState)this.getDefaultState().with(PERSISTENT, true), arg.getWorld(), arg.getBlockPos());
    }
}

