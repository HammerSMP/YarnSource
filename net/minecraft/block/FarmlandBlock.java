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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState lv = world.getBlockState(pos.up());
        return !lv.getMaterial().isSolid() || lv.getBlock() instanceof FenceGateBlock || lv.getBlock() instanceof PistonExtensionBlock;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (!this.getDefaultState().canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
            return Blocks.DIRT.getDefaultState();
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            FarmlandBlock.setToDirt(state, world, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(MOISTURE);
        if (FarmlandBlock.isWaterNearby(world, pos) || world.hasRain(pos.up())) {
            if (i < 7) {
                world.setBlockState(pos, (BlockState)state.with(MOISTURE, 7), 2);
            }
        } else if (i > 0) {
            world.setBlockState(pos, (BlockState)state.with(MOISTURE, i - 1), 2);
        } else if (!FarmlandBlock.hasCrop(world, pos)) {
            FarmlandBlock.setToDirt(state, world, pos);
        }
    }

    @Override
    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        if (!world.isClient && world.random.nextFloat() < distance - 0.5f && entity instanceof LivingEntity && (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512f) {
            FarmlandBlock.setToDirt(world.getBlockState(pos), world, pos);
        }
        super.onLandedUpon(world, pos, entity, distance);
    }

    public static void setToDirt(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, FarmlandBlock.pushEntitiesUpBeforeBlockChange(state, Blocks.DIRT.getDefaultState(), world, pos));
    }

    private static boolean hasCrop(BlockView world, BlockPos pos) {
        Block lv = world.getBlockState(pos.up()).getBlock();
        return lv instanceof CropBlock || lv instanceof StemBlock || lv instanceof AttachedStemBlock;
    }

    private static boolean isWaterNearby(WorldView world, BlockPos pos) {
        for (BlockPos lv : BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (!world.getFluidState(lv).isIn(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MOISTURE);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

