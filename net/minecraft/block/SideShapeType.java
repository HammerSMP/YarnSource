/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public enum SideShapeType {
    FULL{

        @Override
        public boolean matches(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
            return Block.isFaceFullSquare(arg.getSidesShape(arg2, arg3), arg4);
        }
    }
    ,
    CENTER{
        private final int radius = 1;
        private final VoxelShape squareCuboid = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);

        @Override
        public boolean matches(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
            return !VoxelShapes.matchesAnywhere(arg.getSidesShape(arg2, arg3).getFace(arg4), this.squareCuboid, BooleanBiFunction.ONLY_SECOND);
        }
    }
    ,
    RIGID{
        private final int ringWidth = 2;
        private final VoxelShape hollowSquareCuboid = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST);

        @Override
        public boolean matches(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
            return !VoxelShapes.matchesAnywhere(arg.getSidesShape(arg2, arg3).getFace(arg4), this.hollowSquareCuboid, BooleanBiFunction.ONLY_SECOND);
        }
    };


    public abstract boolean matches(BlockState var1, BlockView var2, BlockPos var3, Direction var4);
}

