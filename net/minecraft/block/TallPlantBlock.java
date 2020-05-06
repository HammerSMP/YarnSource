/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class TallPlantBlock
extends PlantBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public TallPlantBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        DoubleBlockHalf lv = arg.get(HALF);
        if (!(arg2.getAxis() != Direction.Axis.Y || lv == DoubleBlockHalf.LOWER != (arg2 == Direction.UP) || arg3.isOf(this) && arg3.get(HALF) != lv)) {
            return Blocks.AIR.getDefaultState();
        }
        if (lv == DoubleBlockHalf.LOWER && arg2 == Direction.DOWN && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockPos lv = arg.getBlockPos();
        if (lv.getY() < 255 && arg.getWorld().getBlockState(lv.up()).canReplace(arg)) {
            return super.getPlacementState(arg);
        }
        return null;
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        arg.setBlockState(arg2.up(), (BlockState)this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        if (arg.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState lv = arg2.getBlockState(arg3.down());
            return lv.isOf(this) && lv.get(HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canPlaceAt(arg, arg2, arg3);
    }

    public void placeAt(IWorld arg, BlockPos arg2, int i) {
        arg.setBlockState(arg2, (BlockState)this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER), i);
        arg.setBlockState(arg2.up(), (BlockState)this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), i);
    }

    @Override
    public void afterBreak(World arg, PlayerEntity arg2, BlockPos arg3, BlockState arg4, @Nullable BlockEntity arg5, ItemStack arg6) {
        super.afterBreak(arg, arg2, arg3, Blocks.AIR.getDefaultState(), arg5, arg6);
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        DoubleBlockHalf lv = arg3.get(HALF);
        BlockPos lv2 = lv == DoubleBlockHalf.LOWER ? arg2.up() : arg2.down();
        BlockState lv3 = arg.getBlockState(lv2);
        if (lv3.isOf(this) && lv3.get(HALF) != lv) {
            arg.setBlockState(lv2, Blocks.AIR.getDefaultState(), 35);
            arg.syncWorldEvent(arg4, 2001, lv2, Block.getRawIdFromState(lv3));
            if (!arg.isClient && !arg4.isCreative()) {
                TallPlantBlock.dropStacks(arg3, arg, arg2, null, arg4, arg4.getMainHandStack());
                TallPlantBlock.dropStacks(lv3, arg, lv2, null, arg4, arg4.getMainHandStack());
            }
        }
        super.onBreak(arg, arg2, arg3, arg4);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(HALF);
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType() {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockState arg, BlockPos arg2) {
        return MathHelper.hashCode(arg2.getX(), arg2.down(arg.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), arg2.getZ());
    }
}

