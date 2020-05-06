/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class PillarBlock
extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

    public PillarBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)this.getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch (arg.get(AXIS)) {
                    case X: {
                        return (BlockState)arg.with(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)arg.with(AXIS, Direction.Axis.X);
                    }
                }
                return arg;
            }
        }
        return arg;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AXIS);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(AXIS, arg.getSide().getAxis());
    }
}

