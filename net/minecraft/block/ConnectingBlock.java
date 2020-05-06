/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ConnectingBlock
extends Block {
    private static final Direction[] FACINGS = Direction.values();
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.NORTH, NORTH);
        enumMap.put(Direction.EAST, EAST);
        enumMap.put(Direction.SOUTH, SOUTH);
        enumMap.put(Direction.WEST, WEST);
        enumMap.put(Direction.UP, UP);
        enumMap.put(Direction.DOWN, DOWN);
    });
    protected final VoxelShape[] CONNECTIONS_TO_SHAPE;

    protected ConnectingBlock(float f, AbstractBlock.Settings arg) {
        super(arg);
        this.CONNECTIONS_TO_SHAPE = this.generateFacingsToShapeMap(f);
    }

    private VoxelShape[] generateFacingsToShapeMap(float f) {
        float g = 0.5f - f;
        float h = 0.5f + f;
        VoxelShape lv = Block.createCuboidShape(g * 16.0f, g * 16.0f, g * 16.0f, h * 16.0f, h * 16.0f, h * 16.0f);
        VoxelShape[] lvs = new VoxelShape[FACINGS.length];
        for (int i = 0; i < FACINGS.length; ++i) {
            Direction lv2 = FACINGS[i];
            lvs[i] = VoxelShapes.cuboid(0.5 + Math.min((double)(-f), (double)lv2.getOffsetX() * 0.5), 0.5 + Math.min((double)(-f), (double)lv2.getOffsetY() * 0.5), 0.5 + Math.min((double)(-f), (double)lv2.getOffsetZ() * 0.5), 0.5 + Math.max((double)f, (double)lv2.getOffsetX() * 0.5), 0.5 + Math.max((double)f, (double)lv2.getOffsetY() * 0.5), 0.5 + Math.max((double)f, (double)lv2.getOffsetZ() * 0.5));
        }
        VoxelShape[] lvs2 = new VoxelShape[64];
        for (int j = 0; j < 64; ++j) {
            VoxelShape lv3 = lv;
            for (int k = 0; k < FACINGS.length; ++k) {
                if ((j & 1 << k) == 0) continue;
                lv3 = VoxelShapes.union(lv3, lvs[k]);
            }
            lvs2[j] = lv3;
        }
        return lvs2;
    }

    @Override
    public boolean isTranslucent(BlockState arg, BlockView arg2, BlockPos arg3) {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.CONNECTIONS_TO_SHAPE[this.getConnectionMask(arg)];
    }

    protected int getConnectionMask(BlockState arg) {
        int i = 0;
        for (int j = 0; j < FACINGS.length; ++j) {
            if (!((Boolean)arg.get(FACING_PROPERTIES.get(FACINGS[j]))).booleanValue()) continue;
            i |= 1 << j;
        }
        return i;
    }
}

