/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.StemBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class AttachedStemBlock
extends PlantBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private final GourdBlock gourdBlock;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.SOUTH, (Object)Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 16.0), (Object)Direction.WEST, (Object)Block.createCuboidShape(0.0, 0.0, 6.0, 10.0, 10.0, 10.0), (Object)Direction.NORTH, (Object)Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 10.0, 10.0), (Object)Direction.EAST, (Object)Block.createCuboidShape(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)));

    protected AttachedStemBlock(GourdBlock arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
        this.gourdBlock = arg;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return FACING_TO_SHAPE.get(arg.get(FACING));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg3.isOf(this.gourdBlock) && arg2 == arg.get(FACING)) {
            return (BlockState)this.gourdBlock.getStem().getDefaultState().with(StemBlock.AGE, 7);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isOf(Blocks.FARMLAND);
    }

    @Environment(value=EnvType.CLIENT)
    protected Item getSeeds() {
        if (this.gourdBlock == Blocks.PUMPKIN) {
            return Items.PUMPKIN_SEEDS;
        }
        if (this.gourdBlock == Blocks.MELON) {
            return Items.MELON_SEEDS;
        }
        return Items.AIR;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return new ItemStack(this.getSeeds());
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(FACING, arg2.rotate(arg.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return arg.rotate(arg2.getRotation(arg.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING);
    }
}

