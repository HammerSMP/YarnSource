/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class WallTorchBlock
extends TorchBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Block.createCuboidShape(5.5, 3.0, 11.0, 10.5, 13.0, 16.0), (Object)Direction.SOUTH, (Object)Block.createCuboidShape(5.5, 3.0, 0.0, 10.5, 13.0, 5.0), (Object)Direction.WEST, (Object)Block.createCuboidShape(11.0, 3.0, 5.5, 16.0, 13.0, 10.5), (Object)Direction.EAST, (Object)Block.createCuboidShape(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)));

    protected WallTorchBlock(AbstractBlock.Settings arg, ParticleEffect arg2) {
        super(arg, arg2);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
    }

    @Override
    public String getTranslationKey() {
        return this.asItem().getTranslationKey();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return WallTorchBlock.getBoundingShape(state);
    }

    public static VoxelShape getBoundingShape(BlockState state) {
        return BOUNDING_SHAPES.get(state.get(FACING));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction lv = state.get(FACING);
        BlockPos lv2 = pos.offset(lv.getOpposite());
        BlockState lv3 = world.getBlockState(lv2);
        return lv3.isSideSolidFullSquare(world, lv2, lv);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] lvs;
        BlockState lv = this.getDefaultState();
        World lv2 = ctx.getWorld();
        BlockPos lv3 = ctx.getBlockPos();
        for (Direction lv4 : lvs = ctx.getPlacementDirections()) {
            Direction lv5;
            if (!lv4.getAxis().isHorizontal() || !(lv = (BlockState)lv.with(FACING, lv5 = lv4.getOpposite())).canPlaceAt(lv2, lv3)) continue;
            return lv;
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction lv = state.get(FACING);
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 0.7;
        double f = (double)pos.getZ() + 0.5;
        double g = 0.22;
        double h = 0.27;
        Direction lv2 = lv.getOpposite();
        world.addParticle(ParticleTypes.SMOKE, d + 0.27 * (double)lv2.getOffsetX(), e + 0.22, f + 0.27 * (double)lv2.getOffsetZ(), 0.0, 0.0, 0.0);
        world.addParticle(this.particle, d + 0.27 * (double)lv2.getOffsetX(), e + 0.22, f + 0.27 * (double)lv2.getOffsetZ(), 0.0, 0.0, 0.0);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

