/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ChestBlock
extends AbstractChestBlock<ChestBlockEntity>
implements Waterloggable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
    protected static final VoxelShape DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
    protected static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>> INVENTORY_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>>(){

        @Override
        public Optional<Inventory> getFromBoth(ChestBlockEntity arg, ChestBlockEntity arg2) {
            return Optional.of(new DoubleInventory(arg, arg2));
        }

        @Override
        public Optional<Inventory> getFrom(ChestBlockEntity arg) {
            return Optional.of(arg);
        }

        @Override
        public Optional<Inventory> getFallback() {
            return Optional.empty();
        }

        @Override
        public /* synthetic */ Object getFallback() {
            return this.getFallback();
        }
    };
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>>(){

        @Override
        public Optional<NamedScreenHandlerFactory> getFromBoth(final ChestBlockEntity arg, final ChestBlockEntity arg2) {
            final DoubleInventory lv = new DoubleInventory(arg, arg2);
            return Optional.of(new NamedScreenHandlerFactory(){

                @Override
                @Nullable
                public ScreenHandler createMenu(int i, PlayerInventory arg3, PlayerEntity arg22) {
                    if (arg.checkUnlocked(arg22) && arg2.checkUnlocked(arg22)) {
                        arg.checkLootInteraction(arg3.player);
                        arg2.checkLootInteraction(arg3.player);
                        return GenericContainerScreenHandler.createGeneric9x6(i, arg3, lv);
                    }
                    return null;
                }

                @Override
                public Text getDisplayName() {
                    if (arg.hasCustomName()) {
                        return arg.getDisplayName();
                    }
                    if (arg2.hasCustomName()) {
                        return arg2.getDisplayName();
                    }
                    return new TranslatableText("container.chestDouble");
                }
            });
        }

        @Override
        public Optional<NamedScreenHandlerFactory> getFrom(ChestBlockEntity arg) {
            return Optional.of(arg);
        }

        @Override
        public Optional<NamedScreenHandlerFactory> getFallback() {
            return Optional.empty();
        }

        @Override
        public /* synthetic */ Object getFallback() {
            return this.getFallback();
        }
    };

    protected ChestBlock(AbstractBlock.Settings arg, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
        super(arg, supplier);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(CHEST_TYPE, ChestType.SINGLE)).with(WATERLOGGED, false));
    }

    public static DoubleBlockProperties.Type getDoubleBlockType(BlockState arg) {
        ChestType lv = arg.get(CHEST_TYPE);
        if (lv == ChestType.SINGLE) {
            return DoubleBlockProperties.Type.SINGLE;
        }
        if (lv == ChestType.RIGHT) {
            return DoubleBlockProperties.Type.FIRST;
        }
        return DoubleBlockProperties.Type.SECOND;
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (arg3.isOf(this) && arg2.getAxis().isHorizontal()) {
            ChestType lv = arg3.get(CHEST_TYPE);
            if (arg.get(CHEST_TYPE) == ChestType.SINGLE && lv != ChestType.SINGLE && arg.get(FACING) == arg3.get(FACING) && ChestBlock.getFacing(arg3) == arg2.getOpposite()) {
                return (BlockState)arg.with(CHEST_TYPE, lv.getOpposite());
            }
        } else if (ChestBlock.getFacing(arg) == arg2) {
            return (BlockState)arg.with(CHEST_TYPE, ChestType.SINGLE);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (arg.get(CHEST_TYPE) == ChestType.SINGLE) {
            return SINGLE_SHAPE;
        }
        switch (ChestBlock.getFacing(arg)) {
            default: {
                return DOUBLE_NORTH_SHAPE;
            }
            case SOUTH: {
                return DOUBLE_SOUTH_SHAPE;
            }
            case WEST: {
                return DOUBLE_WEST_SHAPE;
            }
            case EAST: 
        }
        return DOUBLE_EAST_SHAPE;
    }

    public static Direction getFacing(BlockState arg) {
        Direction lv = arg.get(FACING);
        return arg.get(CHEST_TYPE) == ChestType.LEFT ? lv.rotateYClockwise() : lv.rotateYCounterclockwise();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        Direction lv5;
        ChestType lv = ChestType.SINGLE;
        Direction lv2 = arg.getPlayerFacing().getOpposite();
        FluidState lv3 = arg.getWorld().getFluidState(arg.getBlockPos());
        boolean bl = arg.shouldCancelInteraction();
        Direction lv4 = arg.getSide();
        if (lv4.getAxis().isHorizontal() && bl && (lv5 = this.getNeighborChestDirection(arg, lv4.getOpposite())) != null && lv5.getAxis() != lv4.getAxis()) {
            lv2 = lv5;
            ChestType chestType = lv = lv2.rotateYCounterclockwise() == lv4.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
        }
        if (lv == ChestType.SINGLE && !bl) {
            if (lv2 == this.getNeighborChestDirection(arg, lv2.rotateYClockwise())) {
                lv = ChestType.LEFT;
            } else if (lv2 == this.getNeighborChestDirection(arg, lv2.rotateYCounterclockwise())) {
                lv = ChestType.RIGHT;
            }
        }
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, lv2)).with(CHEST_TYPE, lv)).with(WATERLOGGED, lv3.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Nullable
    private Direction getNeighborChestDirection(ItemPlacementContext arg, Direction arg2) {
        BlockState lv = arg.getWorld().getBlockState(arg.getBlockPos().offset(arg2));
        return lv.isOf(this) && lv.get(CHEST_TYPE) == ChestType.SINGLE ? lv.get(FACING) : null;
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        BlockEntity lv;
        if (arg5.hasCustomName() && (lv = arg.getBlockEntity(arg2)) instanceof ChestBlockEntity) {
            ((ChestBlockEntity)lv).setCustomName(arg5.getName());
        }
    }

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof Inventory) {
            ItemScatterer.spawn(arg2, arg3, (Inventory)((Object)lv));
            arg2.updateComparators(arg3, this);
        }
        super.onStateReplaced(arg, arg2, arg3, arg4, bl);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg2.isClient) {
            return ActionResult.SUCCESS;
        }
        NamedScreenHandlerFactory lv = this.createScreenHandlerFactory(arg, arg2, arg3);
        if (lv != null) {
            arg4.openHandledScreen(lv);
            arg4.incrementStat(this.getOpenStat());
            PiglinBrain.onGoldBlockBroken(arg4, true);
        }
        return ActionResult.CONSUME;
    }

    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
    }

    @Nullable
    public static Inventory getInventory(ChestBlock arg, BlockState arg2, World arg3, BlockPos arg4, boolean bl) {
        return arg.getBlockEntitySource(arg2, arg3, arg4, bl).apply(INVENTORY_RETRIEVER).orElse(null);
    }

    @Override
    public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState arg3, World arg22, BlockPos arg32, boolean bl) {
        BiPredicate<WorldAccess, BlockPos> biPredicate2;
        if (bl) {
            BiPredicate<WorldAccess, BlockPos> biPredicate = (arg, arg2) -> false;
        } else {
            biPredicate2 = ChestBlock::isChestBlocked;
        }
        return DoubleBlockProperties.toPropertySource((BlockEntityType)this.entityTypeRetriever.get(), ChestBlock::getDoubleBlockType, ChestBlock::getFacing, FACING, arg3, arg22, arg32, biPredicate2);
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState arg, World arg2, BlockPos arg3) {
        return this.getBlockEntitySource(arg, arg2, arg3, false).apply(NAME_RETRIEVER).orElse(null);
    }

    @Environment(value=EnvType.CLIENT)
    public static DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(final ChestAnimationProgress arg) {
        return new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction>(){

            @Override
            public Float2FloatFunction getFromBoth(ChestBlockEntity arg3, ChestBlockEntity arg2) {
                return f -> Math.max(arg3.getAnimationProgress(f), arg2.getAnimationProgress(f));
            }

            @Override
            public Float2FloatFunction getFrom(ChestBlockEntity arg2) {
                return arg2::getAnimationProgress;
            }

            @Override
            public Float2FloatFunction getFallback() {
                return arg::getAnimationProgress;
            }

            @Override
            public /* synthetic */ Object getFallback() {
                return this.getFallback();
            }
        };
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new ChestBlockEntity();
    }

    public static boolean isChestBlocked(WorldAccess arg, BlockPos arg2) {
        return ChestBlock.hasBlockOnTop(arg, arg2) || ChestBlock.hasOcelotOnTop(arg, arg2);
    }

    private static boolean hasBlockOnTop(BlockView arg, BlockPos arg2) {
        BlockPos lv = arg2.up();
        return arg.getBlockState(lv).isSolidBlock(arg, lv);
    }

    private static boolean hasOcelotOnTop(WorldAccess arg, BlockPos arg2) {
        List<CatEntity> list = arg.getNonSpectatingEntities(CatEntity.class, new Box(arg2.getX(), arg2.getY() + 1, arg2.getZ(), arg2.getX() + 1, arg2.getY() + 2, arg2.getZ() + 1));
        if (!list.isEmpty()) {
            for (CatEntity lv : list) {
                if (!lv.isInSittingPose()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        return ScreenHandler.calculateComparatorOutput(ChestBlock.getInventory(this, arg, arg2, arg3, false));
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
        arg.add(FACING, CHEST_TYPE, WATERLOGGED);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

