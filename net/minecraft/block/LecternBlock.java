/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LecternBlock
extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty HAS_BOOK = Properties.HAS_BOOK;
    public static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
    public static final VoxelShape BASE_SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MIDDLE_SHAPE);
    public static final VoxelShape COLLISION_SHAPE_TOP = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
    public static final VoxelShape COLLISION_SHAPE = VoxelShapes.union(BASE_SHAPE, COLLISION_SHAPE_TOP);
    public static final VoxelShape WEST_SHAPE = VoxelShapes.union(Block.createCuboidShape(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0), Block.createCuboidShape(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0), Block.createCuboidShape(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0), BASE_SHAPE);
    public static final VoxelShape NORTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333), Block.createCuboidShape(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667), Block.createCuboidShape(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0), BASE_SHAPE);
    public static final VoxelShape EAST_SHAPE = VoxelShapes.union(Block.createCuboidShape(15.0, 10.0, 0.0, 10.666667, 14.0, 16.0), Block.createCuboidShape(10.666667, 12.0, 0.0, 6.333333, 16.0, 16.0), Block.createCuboidShape(6.333333, 14.0, 0.0, 2.0, 18.0, 16.0), BASE_SHAPE);
    public static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 10.0, 15.0, 16.0, 14.0, 10.666667), Block.createCuboidShape(0.0, 12.0, 10.666667, 16.0, 16.0, 6.333333), Block.createCuboidShape(0.0, 14.0, 6.333333, 16.0, 18.0, 2.0), BASE_SHAPE);

    protected LecternBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(HAS_BOOK, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCullingShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return BASE_SHAPE;
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        CompoundTag lv5;
        World lv = arg.getWorld();
        ItemStack lv2 = arg.getStack();
        CompoundTag lv3 = lv2.getOrCreateTag();
        PlayerEntity lv4 = arg.getPlayer();
        boolean bl = false;
        if (!lv.isClient && lv4 != null && lv4.isCreativeLevelTwoOp() && lv3.contains("BlockEntityTag") && (lv5 = lv3.getCompound("BlockEntityTag")).contains("Book")) {
            bl = true;
        }
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, arg.getPlayerFacing().getOpposite())).with(HAS_BOOK, bl);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        switch (arg.get(FACING)) {
            case NORTH: {
                return NORTH_SHAPE;
            }
            case SOUTH: {
                return SOUTH_SHAPE;
            }
            case EAST: {
                return EAST_SHAPE;
            }
            case WEST: {
                return WEST_SHAPE;
            }
        }
        return BASE_SHAPE;
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
        arg.add(FACING, POWERED, HAS_BOOK);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView arg) {
        return new LecternBlockEntity();
    }

    public static boolean putBookIfAbsent(World arg, BlockPos arg2, BlockState arg3, ItemStack arg4) {
        if (!arg3.get(HAS_BOOK).booleanValue()) {
            if (!arg.isClient) {
                LecternBlock.putBook(arg, arg2, arg3, arg4);
            }
            return true;
        }
        return false;
    }

    private static void putBook(World arg, BlockPos arg2, BlockState arg3, ItemStack arg4) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof LecternBlockEntity) {
            LecternBlockEntity lv2 = (LecternBlockEntity)lv;
            lv2.setBook(arg4.split(1));
            LecternBlock.setHasBook(arg, arg2, arg3, true);
            arg.playSound(null, arg2, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    public static void setHasBook(World arg, BlockPos arg2, BlockState arg3, boolean bl) {
        arg.setBlockState(arg2, (BlockState)((BlockState)arg3.with(POWERED, false)).with(HAS_BOOK, bl), 3);
        LecternBlock.updateNeighborAlways(arg, arg2, arg3);
    }

    public static void setPowered(World arg, BlockPos arg2, BlockState arg3) {
        LecternBlock.setPowered(arg, arg2, arg3, true);
        arg.getBlockTickScheduler().schedule(arg2, arg3.getBlock(), 2);
        arg.syncWorldEvent(1043, arg2, 0);
    }

    private static void setPowered(World arg, BlockPos arg2, BlockState arg3, boolean bl) {
        arg.setBlockState(arg2, (BlockState)arg3.with(POWERED, bl), 3);
        LecternBlock.updateNeighborAlways(arg, arg2, arg3);
    }

    private static void updateNeighborAlways(World arg, BlockPos arg2, BlockState arg3) {
        arg.updateNeighborsAlways(arg2.down(), arg3.getBlock());
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        LecternBlock.setPowered(arg2, arg3, arg, false);
    }

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        if (arg.get(HAS_BOOK).booleanValue()) {
            this.dropBook(arg, arg2, arg3);
        }
        if (arg.get(POWERED).booleanValue()) {
            arg2.updateNeighborsAlways(arg3.down(), this);
        }
        super.onStateReplaced(arg, arg2, arg3, arg4, bl);
    }

    private void dropBook(BlockState arg, World arg2, BlockPos arg3) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof LecternBlockEntity) {
            LecternBlockEntity lv2 = (LecternBlockEntity)lv;
            Direction lv3 = arg.get(FACING);
            ItemStack lv4 = lv2.getBook().copy();
            float f = 0.25f * (float)lv3.getOffsetX();
            float g = 0.25f * (float)lv3.getOffsetZ();
            ItemEntity lv5 = new ItemEntity(arg2, (double)arg3.getX() + 0.5 + (double)f, arg3.getY() + 1, (double)arg3.getZ() + 0.5 + (double)g, lv4);
            lv5.setToDefaultPickupDelay();
            arg2.spawnEntity(lv5);
            lv2.clear();
        }
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return arg.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return arg4 == Direction.UP && arg.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        BlockEntity lv;
        if (arg.get(HAS_BOOK).booleanValue() && (lv = arg2.getBlockEntity(arg3)) instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)lv).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg.get(HAS_BOOK).booleanValue()) {
            if (!arg2.isClient) {
                this.openScreen(arg2, arg3, arg4);
            }
            return ActionResult.success(arg2.isClient);
        }
        ItemStack lv = arg4.getStackInHand(arg5);
        if (lv.isEmpty() || lv.getItem().isIn(ItemTags.LECTERN_BOOKS)) {
            return ActionResult.PASS;
        }
        return ActionResult.CONSUME;
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState arg, World arg2, BlockPos arg3) {
        if (!arg.get(HAS_BOOK).booleanValue()) {
            return null;
        }
        return super.createScreenHandlerFactory(arg, arg2, arg3);
    }

    private void openScreen(World arg, BlockPos arg2, PlayerEntity arg3) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof LecternBlockEntity) {
            arg3.openHandledScreen((LecternBlockEntity)lv);
            arg3.incrementStat(Stats.INTERACT_WITH_LECTERN);
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

