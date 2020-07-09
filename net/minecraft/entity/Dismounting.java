/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;

public class Dismounting {
    public static int[][] getDismountOffsets(Direction arg) {
        Direction lv = arg.rotateYClockwise();
        Direction lv2 = lv.getOpposite();
        Direction lv3 = arg.getOpposite();
        return new int[][]{{lv.getOffsetX(), lv.getOffsetZ()}, {lv2.getOffsetX(), lv2.getOffsetZ()}, {lv3.getOffsetX() + lv.getOffsetX(), lv3.getOffsetZ() + lv.getOffsetZ()}, {lv3.getOffsetX() + lv2.getOffsetX(), lv3.getOffsetZ() + lv2.getOffsetZ()}, {arg.getOffsetX() + lv.getOffsetX(), arg.getOffsetZ() + lv.getOffsetZ()}, {arg.getOffsetX() + lv2.getOffsetX(), arg.getOffsetZ() + lv2.getOffsetZ()}, {lv3.getOffsetX(), lv3.getOffsetZ()}, {arg.getOffsetX(), arg.getOffsetZ()}};
    }

    public static boolean canDismountInBlock(double d) {
        return !Double.isInfinite(d) && d < 1.0;
    }

    public static boolean canPlaceEntityAt(CollisionView arg, LivingEntity arg2, Box arg3) {
        return arg.getBlockCollisions(arg2, arg3).allMatch(VoxelShape::isEmpty);
    }

    @Nullable
    public static Vec3d method_30342(CollisionView arg, double d, double e, double f, LivingEntity arg2, EntityPose arg3) {
        if (Dismounting.canDismountInBlock(e)) {
            Vec3d lv = new Vec3d(d, e, f);
            if (Dismounting.canPlaceEntityAt(arg, arg2, arg2.getBoundingBox(arg3).offset(lv))) {
                return lv;
            }
        }
        return null;
    }

    public static VoxelShape method_30341(BlockView arg, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        if (lv.isIn(BlockTags.CLIMBABLE) || lv.getBlock() instanceof TrapdoorBlock && lv.get(TrapdoorBlock.OPEN).booleanValue()) {
            return VoxelShapes.empty();
        }
        return lv.getCollisionShape(arg, arg2);
    }

    public static double method_30343(BlockPos arg, int i, Function<BlockPos, VoxelShape> function) {
        BlockPos.Mutable lv = arg.mutableCopy();
        for (int j = 0; j < i; ++j) {
            VoxelShape lv2 = function.apply(lv);
            if (!lv2.isEmpty()) {
                return (double)(arg.getY() + j) + lv2.getMin(Direction.Axis.Y);
            }
            lv.move(Direction.UP);
        }
        return Double.POSITIVE_INFINITY;
    }
}

