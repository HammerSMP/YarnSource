/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FenceBlock
extends HorizontalConnectingBlock {
    private final VoxelShape[] cullingShapes;

    public FenceBlock(AbstractBlock.Settings arg) {
        super(2.0f, 2.0f, 16.0f, 16.0f, 24.0f, arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
        this.cullingShapes = this.createShapes(2.0f, 1.0f, 16.0f, 6.0f, 15.0f);
    }

    @Override
    public VoxelShape getCullingShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return this.cullingShapes[this.getShapeIndex(arg)];
    }

    @Override
    public VoxelShape getVisualShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.getOutlineShape(arg, arg2, arg3, arg4);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }

    public boolean canConnect(BlockState arg, boolean bl, Direction arg2) {
        Block lv = arg.getBlock();
        boolean bl2 = this.isFence(lv);
        boolean bl3 = lv instanceof FenceGateBlock && FenceGateBlock.canWallConnect(arg, arg2);
        return !FenceBlock.cannotConnect(lv) && bl || bl2 || bl3;
    }

    private boolean isFence(Block arg) {
        return arg.isIn(BlockTags.FENCES) && arg.isIn(BlockTags.WOODEN_FENCES) == this.getDefaultState().isIn(BlockTags.WOODEN_FENCES);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg2.isClient) {
            ItemStack lv = arg4.getStackInHand(arg5);
            if (lv.getItem() == Items.LEAD) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return LeadItem.attachHeldMobsToBlock(arg4, arg2, arg3);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getBlockPos();
        FluidState lv3 = arg.getWorld().getFluidState(arg.getBlockPos());
        BlockPos lv4 = lv2.north();
        BlockPos lv5 = lv2.east();
        BlockPos lv6 = lv2.south();
        BlockPos lv7 = lv2.west();
        BlockState lv8 = lv.getBlockState(lv4);
        BlockState lv9 = lv.getBlockState(lv5);
        BlockState lv10 = lv.getBlockState(lv6);
        BlockState lv11 = lv.getBlockState(lv7);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.getPlacementState(arg).with(NORTH, this.canConnect(lv8, lv8.isSideSolidFullSquare(lv, lv4, Direction.SOUTH), Direction.SOUTH))).with(EAST, this.canConnect(lv9, lv9.isSideSolidFullSquare(lv, lv5, Direction.WEST), Direction.WEST))).with(SOUTH, this.canConnect(lv10, lv10.isSideSolidFullSquare(lv, lv6, Direction.NORTH), Direction.NORTH))).with(WEST, this.canConnect(lv11, lv11.isSideSolidFullSquare(lv, lv7, Direction.EAST), Direction.EAST))).with(WATERLOGGED, lv3.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (arg2.getAxis().getType() == Direction.Type.HORIZONTAL) {
            return (BlockState)arg.with((Property)FACING_PROPERTIES.get(arg2), this.canConnect(arg3, arg3.isSideSolidFullSquare(arg4, arg6, arg2.getOpposite()), arg2.getOpposite()));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}

