/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class BannerBlock
extends AbstractBannerBlock {
    public static final IntProperty ROTATION = Properties.ROTATION;
    private static final Map<DyeColor, Block> COLORED_BANNERS = Maps.newHashMap();
    private static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public BannerBlock(DyeColor arg, AbstractBlock.Settings arg2) {
        super(arg, arg2);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ROTATION, 0));
        COLORED_BANNERS.put(arg, this);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return arg2.getBlockState(arg3.down()).getMaterial().isSolid();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(ROTATION, MathHelper.floor((double)((180.0f + arg.getPlayerYaw()) * 16.0f / 360.0f) + 0.5) & 0xF);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == Direction.DOWN && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(ROTATION, arg2.rotate(arg.get(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return (BlockState)arg.with(ROTATION, arg2.mirror(arg.get(ROTATION), 16));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(ROTATION);
    }

    @Environment(value=EnvType.CLIENT)
    public static Block getForColor(DyeColor arg) {
        return COLORED_BANNERS.getOrDefault(arg, Blocks.WHITE_BANNER);
    }
}

