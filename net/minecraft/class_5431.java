/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public enum class_5431 {
    FULL{

        @Override
        public boolean method_30367(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
            return Block.isFaceFullSquare(arg.getSidesShape(arg2, arg3), arg4);
        }
    }
    ,
    CENTER{
        private final int field_25826 = 1;
        private final VoxelShape field_25827 = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);

        @Override
        public boolean method_30367(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
            return !VoxelShapes.matchesAnywhere(arg.getSidesShape(arg2, arg3).getFace(arg4), this.field_25827, BooleanBiFunction.ONLY_SECOND);
        }
    }
    ,
    RIGID{
        private final int field_25828 = 2;
        private final VoxelShape field_25829 = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST);

        @Override
        public boolean method_30367(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
            return !VoxelShapes.matchesAnywhere(arg.getSidesShape(arg2, arg3).getFace(arg4), this.field_25829, BooleanBiFunction.ONLY_SECOND);
        }
    };


    public abstract boolean method_30367(BlockState var1, BlockView var2, BlockPos var3, Direction var4);
}

