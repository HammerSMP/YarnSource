/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class WallBlock
extends Block
implements Waterloggable {
    public static final BooleanProperty UP = Properties.UP;
    public static final EnumProperty<WallShape> EAST_SHAPE = Properties.EAST_WALL_SHAPE;
    public static final EnumProperty<WallShape> NORTH_SHAPE = Properties.NORTH_WALL_SHAPE;
    public static final EnumProperty<WallShape> SOUTH_SHAPE = Properties.SOUTH_WALL_SHAPE;
    public static final EnumProperty<WallShape> WEST_SHAPE = Properties.WEST_WALL_SHAPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private final Map<BlockState, VoxelShape> shapeMap;
    private final Map<BlockState, VoxelShape> collisionShapeMap;
    private static final VoxelShape field_22163 = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape field_22164 = Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 16.0, 9.0);
    private static final VoxelShape field_22165 = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 16.0);
    private static final VoxelShape field_22166 = Block.createCuboidShape(0.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape field_22167 = Block.createCuboidShape(7.0, 0.0, 7.0, 16.0, 16.0, 9.0);

    public WallBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(UP, true)).with(NORTH_SHAPE, WallShape.NONE)).with(EAST_SHAPE, WallShape.NONE)).with(SOUTH_SHAPE, WallShape.NONE)).with(WEST_SHAPE, WallShape.NONE)).with(WATERLOGGED, false));
        this.shapeMap = this.getShapeMap(4.0f, 3.0f, 16.0f, 0.0f, 14.0f, 16.0f);
        this.collisionShapeMap = this.getShapeMap(4.0f, 3.0f, 24.0f, 0.0f, 24.0f, 24.0f);
    }

    private static VoxelShape method_24426(VoxelShape arg, WallShape arg2, VoxelShape arg3, VoxelShape arg4) {
        if (arg2 == WallShape.TALL) {
            return VoxelShapes.union(arg, arg4);
        }
        if (arg2 == WallShape.LOW) {
            return VoxelShapes.union(arg, arg3);
        }
        return arg;
    }

    private Map<BlockState, VoxelShape> getShapeMap(float f, float g, float h, float i, float j, float k) {
        float l = 8.0f - f;
        float m = 8.0f + f;
        float n = 8.0f - g;
        float o = 8.0f + g;
        VoxelShape lv = Block.createCuboidShape(l, 0.0, l, m, h, m);
        VoxelShape lv2 = Block.createCuboidShape(n, i, 0.0, o, j, o);
        VoxelShape lv3 = Block.createCuboidShape(n, i, n, o, j, 16.0);
        VoxelShape lv4 = Block.createCuboidShape(0.0, i, n, o, j, o);
        VoxelShape lv5 = Block.createCuboidShape(n, i, n, 16.0, j, o);
        VoxelShape lv6 = Block.createCuboidShape(n, i, 0.0, o, k, o);
        VoxelShape lv7 = Block.createCuboidShape(n, i, n, o, k, 16.0);
        VoxelShape lv8 = Block.createCuboidShape(0.0, i, n, o, k, o);
        VoxelShape lv9 = Block.createCuboidShape(n, i, n, 16.0, k, o);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Boolean lv10 : UP.getValues()) {
            for (WallShape lv11 : EAST_SHAPE.getValues()) {
                for (WallShape lv12 : NORTH_SHAPE.getValues()) {
                    for (WallShape lv13 : WEST_SHAPE.getValues()) {
                        for (WallShape lv14 : SOUTH_SHAPE.getValues()) {
                            VoxelShape lv15 = VoxelShapes.empty();
                            lv15 = WallBlock.method_24426(lv15, lv11, lv5, lv9);
                            lv15 = WallBlock.method_24426(lv15, lv13, lv4, lv8);
                            lv15 = WallBlock.method_24426(lv15, lv12, lv2, lv6);
                            lv15 = WallBlock.method_24426(lv15, lv14, lv3, lv7);
                            if (lv10.booleanValue()) {
                                lv15 = VoxelShapes.union(lv15, lv);
                            }
                            BlockState lv16 = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(UP, lv10)).with(EAST_SHAPE, lv11)).with(WEST_SHAPE, lv13)).with(NORTH_SHAPE, lv12)).with(SOUTH_SHAPE, lv14);
                            builder.put(lv16.with(WATERLOGGED, false), (Object)lv15);
                            builder.put(lv16.with(WATERLOGGED, true), (Object)lv15);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.shapeMap.get(arg);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.collisionShapeMap.get(arg);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }

    private boolean shouldConnectTo(BlockState arg, boolean bl, Direction arg2) {
        Block lv = arg.getBlock();
        boolean bl2 = lv instanceof FenceGateBlock && FenceGateBlock.canWallConnect(arg, arg2);
        return arg.isIn(BlockTags.WALLS) || !WallBlock.cannotConnect(lv) && bl || lv instanceof PaneBlock || bl2;
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
        BlockPos lv8 = lv2.up();
        BlockState lv9 = lv.getBlockState(lv4);
        BlockState lv10 = lv.getBlockState(lv5);
        BlockState lv11 = lv.getBlockState(lv6);
        BlockState lv12 = lv.getBlockState(lv7);
        BlockState lv13 = lv.getBlockState(lv8);
        boolean bl = this.shouldConnectTo(lv9, lv9.isSideSolidFullSquare(lv, lv4, Direction.SOUTH), Direction.SOUTH);
        boolean bl2 = this.shouldConnectTo(lv10, lv10.isSideSolidFullSquare(lv, lv5, Direction.WEST), Direction.WEST);
        boolean bl3 = this.shouldConnectTo(lv11, lv11.isSideSolidFullSquare(lv, lv6, Direction.NORTH), Direction.NORTH);
        boolean bl4 = this.shouldConnectTo(lv12, lv12.isSideSolidFullSquare(lv, lv7, Direction.EAST), Direction.EAST);
        BlockState lv14 = (BlockState)this.getDefaultState().with(WATERLOGGED, lv3.getFluid() == Fluids.WATER);
        return this.method_24422(lv, lv14, lv8, lv13, bl, bl2, bl3, bl4);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (arg2 == Direction.DOWN) {
            return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
        }
        if (arg2 == Direction.UP) {
            return this.method_24421(arg4, arg, arg6, arg3);
        }
        return this.method_24423(arg4, arg5, arg, arg6, arg3, arg2);
    }

    private static boolean method_24424(BlockState arg, Property<WallShape> arg2) {
        return arg.get(arg2) != WallShape.NONE;
    }

    private static boolean method_24427(VoxelShape arg, VoxelShape arg2) {
        return !VoxelShapes.matchesAnywhere(arg2, arg, BooleanBiFunction.ONLY_FIRST);
    }

    private BlockState method_24421(WorldView arg, BlockState arg2, BlockPos arg3, BlockState arg4) {
        boolean bl = WallBlock.method_24424(arg2, NORTH_SHAPE);
        boolean bl2 = WallBlock.method_24424(arg2, EAST_SHAPE);
        boolean bl3 = WallBlock.method_24424(arg2, SOUTH_SHAPE);
        boolean bl4 = WallBlock.method_24424(arg2, WEST_SHAPE);
        return this.method_24422(arg, arg2, arg3, arg4, bl, bl2, bl3, bl4);
    }

    private BlockState method_24423(WorldView arg, BlockPos arg2, BlockState arg3, BlockPos arg4, BlockState arg5, Direction arg6) {
        Direction lv = arg6.getOpposite();
        boolean bl = arg6 == Direction.NORTH ? this.shouldConnectTo(arg5, arg5.isSideSolidFullSquare(arg, arg4, lv), lv) : WallBlock.method_24424(arg3, NORTH_SHAPE);
        boolean bl2 = arg6 == Direction.EAST ? this.shouldConnectTo(arg5, arg5.isSideSolidFullSquare(arg, arg4, lv), lv) : WallBlock.method_24424(arg3, EAST_SHAPE);
        boolean bl3 = arg6 == Direction.SOUTH ? this.shouldConnectTo(arg5, arg5.isSideSolidFullSquare(arg, arg4, lv), lv) : WallBlock.method_24424(arg3, SOUTH_SHAPE);
        boolean bl4 = arg6 == Direction.WEST ? this.shouldConnectTo(arg5, arg5.isSideSolidFullSquare(arg, arg4, lv), lv) : WallBlock.method_24424(arg3, WEST_SHAPE);
        BlockPos lv2 = arg2.up();
        BlockState lv3 = arg.getBlockState(lv2);
        return this.method_24422(arg, arg3, lv2, lv3, bl, bl2, bl3, bl4);
    }

    private BlockState method_24422(WorldView arg, BlockState arg2, BlockPos arg3, BlockState arg4, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        VoxelShape lv = arg4.getCollisionShape(arg, arg3).getFace(Direction.DOWN);
        BlockState lv2 = this.method_24425(arg2, bl, bl2, bl3, bl4, lv);
        return (BlockState)lv2.with(UP, this.method_27092(lv2, arg4, lv));
    }

    private boolean method_27092(BlockState arg, BlockState arg2, VoxelShape arg3) {
        boolean bl7;
        boolean bl6;
        boolean bl;
        boolean bl2 = bl = arg2.getBlock() instanceof WallBlock && arg2.get(UP) != false;
        if (bl) {
            return true;
        }
        WallShape lv = arg.get(NORTH_SHAPE);
        WallShape lv2 = arg.get(SOUTH_SHAPE);
        WallShape lv3 = arg.get(EAST_SHAPE);
        WallShape lv4 = arg.get(WEST_SHAPE);
        boolean bl22 = lv2 == WallShape.NONE;
        boolean bl3 = lv4 == WallShape.NONE;
        boolean bl4 = lv3 == WallShape.NONE;
        boolean bl5 = lv == WallShape.NONE;
        boolean bl8 = bl6 = bl5 && bl22 && bl3 && bl4 || bl5 != bl22 || bl3 != bl4;
        if (bl6) {
            return true;
        }
        boolean bl9 = bl7 = lv == WallShape.TALL && lv2 == WallShape.TALL || lv3 == WallShape.TALL && lv4 == WallShape.TALL;
        if (bl7) {
            return false;
        }
        return arg2.getBlock().isIn(BlockTags.WALL_POST_OVERRIDE) || WallBlock.method_24427(arg3, field_22163);
    }

    private BlockState method_24425(BlockState arg, boolean bl, boolean bl2, boolean bl3, boolean bl4, VoxelShape arg2) {
        return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH_SHAPE, this.method_24428(bl, arg2, field_22164))).with(EAST_SHAPE, this.method_24428(bl2, arg2, field_22167))).with(SOUTH_SHAPE, this.method_24428(bl3, arg2, field_22165))).with(WEST_SHAPE, this.method_24428(bl4, arg2, field_22166));
    }

    private WallShape method_24428(boolean bl, VoxelShape arg, VoxelShape arg2) {
        if (bl) {
            if (WallBlock.method_24427(arg, arg2)) {
                return WallShape.TALL;
            }
            return WallShape.LOW;
        }
        return WallShape.NONE;
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Override
    public boolean isTranslucent(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.get(WATERLOGGED) == false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(UP, NORTH_SHAPE, EAST_SHAPE, WEST_SHAPE, SOUTH_SHAPE, WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH_SHAPE, arg.get(SOUTH_SHAPE))).with(EAST_SHAPE, arg.get(WEST_SHAPE))).with(SOUTH_SHAPE, arg.get(NORTH_SHAPE))).with(WEST_SHAPE, arg.get(EAST_SHAPE));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH_SHAPE, arg.get(EAST_SHAPE))).with(EAST_SHAPE, arg.get(SOUTH_SHAPE))).with(SOUTH_SHAPE, arg.get(WEST_SHAPE))).with(WEST_SHAPE, arg.get(NORTH_SHAPE));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH_SHAPE, arg.get(WEST_SHAPE))).with(EAST_SHAPE, arg.get(NORTH_SHAPE))).with(SOUTH_SHAPE, arg.get(EAST_SHAPE))).with(WEST_SHAPE, arg.get(SOUTH_SHAPE));
            }
        }
        return arg;
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        switch (arg2) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)arg.with(NORTH_SHAPE, arg.get(SOUTH_SHAPE))).with(SOUTH_SHAPE, arg.get(NORTH_SHAPE));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)arg.with(EAST_SHAPE, arg.get(WEST_SHAPE))).with(WEST_SHAPE, arg.get(EAST_SHAPE));
            }
        }
        return super.mirror(arg, arg2);
    }
}

