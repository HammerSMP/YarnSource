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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return BASE_SHAPE;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        CompoundTag lv5;
        World lv = ctx.getWorld();
        ItemStack lv2 = ctx.getStack();
        CompoundTag lv3 = lv2.getTag();
        PlayerEntity lv4 = ctx.getPlayer();
        boolean bl = false;
        if (!lv.isClient && lv4 != null && lv3 != null && lv4.isCreativeLevelTwoOp() && lv3.contains("BlockEntityTag") && (lv5 = lv3.getCompound("BlockEntityTag")).contains("Book")) {
            bl = true;
        }
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite())).with(HAS_BOOK, bl);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
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
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, HAS_BOOK);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView world) {
        return new LecternBlockEntity();
    }

    public static boolean putBookIfAbsent(World world, BlockPos pos, BlockState state, ItemStack book) {
        if (!state.get(HAS_BOOK).booleanValue()) {
            if (!world.isClient) {
                LecternBlock.putBook(world, pos, state, book);
            }
            return true;
        }
        return false;
    }

    private static void putBook(World world, BlockPos pos, BlockState state, ItemStack book) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof LecternBlockEntity) {
            LecternBlockEntity lv2 = (LecternBlockEntity)lv;
            lv2.setBook(book.split(1));
            LecternBlock.setHasBook(world, pos, state, true);
            world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    public static void setHasBook(World world, BlockPos pos, BlockState state, boolean hasBook) {
        world.setBlockState(pos, (BlockState)((BlockState)state.with(POWERED, false)).with(HAS_BOOK, hasBook), 3);
        LecternBlock.updateNeighborAlways(world, pos, state);
    }

    public static void setPowered(World world, BlockPos pos, BlockState state) {
        LecternBlock.setPowered(world, pos, state, true);
        world.getBlockTickScheduler().schedule(pos, state.getBlock(), 2);
        world.syncWorldEvent(1043, pos, 0);
    }

    private static void setPowered(World world, BlockPos pos, BlockState state, boolean powered) {
        world.setBlockState(pos, (BlockState)state.with(POWERED, powered), 3);
        LecternBlock.updateNeighborAlways(world, pos, state);
    }

    private static void updateNeighborAlways(World world, BlockPos pos, BlockState state) {
        world.updateNeighborsAlways(pos.down(), state.getBlock());
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        LecternBlock.setPowered(world, pos, state, false);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        if (state.get(HAS_BOOK).booleanValue()) {
            this.dropBook(state, world, pos);
        }
        if (state.get(POWERED).booleanValue()) {
            world.updateNeighborsAlways(pos.down(), this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void dropBook(BlockState state, World world, BlockPos pos) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof LecternBlockEntity) {
            LecternBlockEntity lv2 = (LecternBlockEntity)lv;
            Direction lv3 = state.get(FACING);
            ItemStack lv4 = lv2.getBook().copy();
            float f = 0.25f * (float)lv3.getOffsetX();
            float g = 0.25f * (float)lv3.getOffsetZ();
            ItemEntity lv5 = new ItemEntity(world, (double)pos.getX() + 0.5 + (double)f, pos.getY() + 1, (double)pos.getZ() + 0.5 + (double)g, lv4);
            lv5.setToDefaultPickupDelay();
            world.spawnEntity(lv5);
            lv2.clear();
        }
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return direction == Direction.UP && state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity lv;
        if (state.get(HAS_BOOK).booleanValue() && (lv = world.getBlockEntity(pos)) instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)lv).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(HAS_BOOK).booleanValue()) {
            if (!world.isClient) {
                this.openScreen(world, pos, player);
            }
            return ActionResult.success(world.isClient);
        }
        ItemStack lv = player.getStackInHand(hand);
        if (lv.isEmpty() || lv.getItem().isIn(ItemTags.LECTERN_BOOKS)) {
            return ActionResult.PASS;
        }
        return ActionResult.CONSUME;
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        if (!state.get(HAS_BOOK).booleanValue()) {
            return null;
        }
        return super.createScreenHandlerFactory(state, world, pos);
    }

    private void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof LecternBlockEntity) {
            player.openHandledScreen((LecternBlockEntity)lv);
            player.incrementStat(Stats.INTERACT_WITH_LECTERN);
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

