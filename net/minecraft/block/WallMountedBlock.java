/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class WallMountedBlock
extends HorizontalFacingBlock {
    public static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;

    protected WallMountedBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return WallMountedBlock.canPlaceAt(arg2, arg3, WallMountedBlock.getDirection(arg).getOpposite());
    }

    public static boolean canPlaceAt(WorldView arg, BlockPos arg2, Direction arg3) {
        BlockPos lv = arg2.offset(arg3);
        return arg.getBlockState(lv).isSideSolidFullSquare(arg, lv, arg3.getOpposite());
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        for (Direction lv : arg.getPlacementDirections()) {
            BlockState lv3;
            if (lv.getAxis() == Direction.Axis.Y) {
                BlockState lv2 = (BlockState)((BlockState)this.getDefaultState().with(FACE, lv == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)).with(FACING, arg.getPlayerFacing());
            } else {
                lv3 = (BlockState)((BlockState)this.getDefaultState().with(FACE, WallMountLocation.WALL)).with(FACING, lv.getOpposite());
            }
            if (!lv3.canPlaceAt(arg.getWorld(), arg.getBlockPos())) continue;
            return lv3;
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (WallMountedBlock.getDirection(arg).getOpposite() == arg2 && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    protected static Direction getDirection(BlockState arg) {
        switch (arg.get(FACE)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
        }
        return arg.get(FACING);
    }
}

