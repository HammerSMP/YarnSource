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
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LeverBlock
extends WallMountedBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    protected static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
    protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
    protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
    protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

    protected LeverBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, WallMountLocation.WALL));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch ((WallMountLocation)state.get(FACE)) {
            case FLOOR: {
                switch (state.get(FACING).getAxis()) {
                    case X: {
                        return FLOOR_X_AXIS_SHAPE;
                    }
                }
                return FLOOR_Z_AXIS_SHAPE;
            }
            case WALL: {
                switch (state.get(FACING)) {
                    case EAST: {
                        return EAST_WALL_SHAPE;
                    }
                    case WEST: {
                        return WEST_WALL_SHAPE;
                    }
                    case SOUTH: {
                        return SOUTH_WALL_SHAPE;
                    }
                }
                return NORTH_WALL_SHAPE;
            }
        }
        switch (state.get(FACING).getAxis()) {
            case X: {
                return CEILING_X_AXIS_SHAPE;
            }
        }
        return CEILING_Z_AXIS_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            BlockState lv = (BlockState)state.cycle(POWERED);
            if (lv.get(POWERED).booleanValue()) {
                LeverBlock.spawnParticles(lv, world, pos, 1.0f);
            }
            return ActionResult.SUCCESS;
        }
        BlockState lv2 = this.method_21846(state, world, pos);
        float f = lv2.get(POWERED) != false ? 0.6f : 0.5f;
        world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        return ActionResult.CONSUME;
    }

    public BlockState method_21846(BlockState arg, World arg2, BlockPos arg3) {
        arg = (BlockState)arg.cycle(POWERED);
        arg2.setBlockState(arg3, arg, 3);
        this.updateNeighbors(arg, arg2, arg3);
        return arg;
    }

    private static void spawnParticles(BlockState state, WorldAccess world, BlockPos pos, float alpha) {
        Direction lv = state.get(FACING).getOpposite();
        Direction lv2 = LeverBlock.getDirection(state).getOpposite();
        double d = (double)pos.getX() + 0.5 + 0.1 * (double)lv.getOffsetX() + 0.2 * (double)lv2.getOffsetX();
        double e = (double)pos.getY() + 0.5 + 0.1 * (double)lv.getOffsetY() + 0.2 * (double)lv2.getOffsetY();
        double g = (double)pos.getZ() + 0.5 + 0.1 * (double)lv.getOffsetZ() + 0.2 * (double)lv2.getOffsetZ();
        world.addParticle(new DustParticleEffect(1.0f, 0.0f, 0.0f, alpha), d, e, g, 0.0, 0.0, 0.0);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(POWERED).booleanValue() && random.nextFloat() < 0.25f) {
            LeverBlock.spawnParticles(state, world, pos, 0.5f);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        if (state.get(POWERED).booleanValue()) {
            this.updateNeighbors(state, world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED).booleanValue() && LeverBlock.getDirection(state) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(LeverBlock.getDirection(state).getOpposite()), this);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, POWERED);
    }
}

