/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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

public class WallRedstoneTorchBlock
extends RedstoneTorchBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    protected WallRedstoneTorchBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(LIT, true));
    }

    @Override
    public String getTranslationKey() {
        return this.asItem().getTranslationKey();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return WallTorchBlock.getBoundingShape(arg);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return Blocks.WALL_TORCH.canPlaceAt(arg, arg2, arg3);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        return Blocks.WALL_TORCH.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = Blocks.WALL_TORCH.getPlacementState(arg);
        return lv == null ? null : (BlockState)this.getDefaultState().with(FACING, lv.get(FACING));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (!arg.get(LIT).booleanValue()) {
            return;
        }
        Direction lv = arg.get(FACING).getOpposite();
        double d = 0.27;
        double e = (double)arg3.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)lv.getOffsetX();
        double f = (double)arg3.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
        double g = (double)arg3.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)lv.getOffsetZ();
        arg2.addParticle(this.particle, e, f, g, 0.0, 0.0, 0.0);
    }

    @Override
    protected boolean shouldUnpower(World arg, BlockPos arg2, BlockState arg3) {
        Direction lv = arg3.get(FACING).getOpposite();
        return arg.isEmittingRedstonePower(arg2.offset(lv), lv);
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (arg.get(LIT).booleanValue() && arg.get(FACING) != arg4) {
            return 15;
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return Blocks.WALL_TORCH.rotate(arg, arg2);
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return Blocks.WALL_TORCH.mirror(arg, arg2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, LIT);
    }
}

