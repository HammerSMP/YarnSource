/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class HorizontalConnectingBlock
extends Block
implements Waterloggable {
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
    protected final VoxelShape[] collisionShapes;
    protected final VoxelShape[] boundingShapes;
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap();

    protected HorizontalConnectingBlock(float f, float g, float h, float i, float j, AbstractBlock.Settings arg) {
        super(arg);
        this.collisionShapes = this.createShapes(f, g, j, 0.0f, j);
        this.boundingShapes = this.createShapes(f, g, h, 0.0f, i);
        for (BlockState lv : this.stateManager.getStates()) {
            this.getShapeIndex(lv);
        }
    }

    protected VoxelShape[] createShapes(float f, float g, float h, float i, float j) {
        float k = 8.0f - f;
        float l = 8.0f + f;
        float m = 8.0f - g;
        float n = 8.0f + g;
        VoxelShape lv = Block.createCuboidShape(k, 0.0, k, l, h, l);
        VoxelShape lv2 = Block.createCuboidShape(m, i, 0.0, n, j, n);
        VoxelShape lv3 = Block.createCuboidShape(m, i, m, n, j, 16.0);
        VoxelShape lv4 = Block.createCuboidShape(0.0, i, m, n, j, n);
        VoxelShape lv5 = Block.createCuboidShape(m, i, m, 16.0, j, n);
        VoxelShape lv6 = VoxelShapes.union(lv2, lv5);
        VoxelShape lv7 = VoxelShapes.union(lv3, lv4);
        VoxelShape[] lvs = new VoxelShape[]{VoxelShapes.empty(), lv3, lv4, lv7, lv2, VoxelShapes.union(lv3, lv2), VoxelShapes.union(lv4, lv2), VoxelShapes.union(lv7, lv2), lv5, VoxelShapes.union(lv3, lv5), VoxelShapes.union(lv4, lv5), VoxelShapes.union(lv7, lv5), lv6, VoxelShapes.union(lv3, lv6), VoxelShapes.union(lv4, lv6), VoxelShapes.union(lv7, lv6)};
        for (int o = 0; o < 16; ++o) {
            lvs[o] = VoxelShapes.union(lv, lvs[o]);
        }
        return lvs;
    }

    @Override
    public boolean isTranslucent(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.get(WATERLOGGED) == false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.boundingShapes[this.getShapeIndex(arg)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.collisionShapes[this.getShapeIndex(arg)];
    }

    private static int getDirectionMask(Direction arg) {
        return 1 << arg.getHorizontal();
    }

    protected int getShapeIndex(BlockState arg2) {
        return this.SHAPE_INDEX_CACHE.computeIntIfAbsent((Object)arg2, arg -> {
            int i = 0;
            if (arg.get(NORTH).booleanValue()) {
                i |= HorizontalConnectingBlock.getDirectionMask(Direction.NORTH);
            }
            if (arg.get(EAST).booleanValue()) {
                i |= HorizontalConnectingBlock.getDirectionMask(Direction.EAST);
            }
            if (arg.get(SOUTH).booleanValue()) {
                i |= HorizontalConnectingBlock.getDirectionMask(Direction.SOUTH);
            }
            if (arg.get(WEST).booleanValue()) {
                i |= HorizontalConnectingBlock.getDirectionMask(Direction.WEST);
            }
            return i;
        });
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(SOUTH))).with(EAST, arg.get(WEST))).with(SOUTH, arg.get(NORTH))).with(WEST, arg.get(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(EAST))).with(EAST, arg.get(SOUTH))).with(SOUTH, arg.get(WEST))).with(WEST, arg.get(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(WEST))).with(EAST, arg.get(NORTH))).with(SOUTH, arg.get(EAST))).with(WEST, arg.get(SOUTH));
            }
        }
        return arg;
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        switch (arg2) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)arg.with(NORTH, arg.get(SOUTH))).with(SOUTH, arg.get(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)arg.with(EAST, arg.get(WEST))).with(WEST, arg.get(EAST));
            }
        }
        return super.mirror(arg, arg2);
    }
}

