/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class class_5275 {
    public static int[][] method_27934(Direction arg) {
        Direction lv = arg.rotateYClockwise();
        Direction lv2 = lv.getOpposite();
        Direction lv3 = arg.getOpposite();
        return new int[][]{{lv.getOffsetX(), lv.getOffsetZ()}, {lv2.getOffsetX(), lv2.getOffsetZ()}, {lv3.getOffsetX() + lv.getOffsetX(), lv3.getOffsetZ() + lv.getOffsetZ()}, {lv3.getOffsetX() + lv2.getOffsetX(), lv3.getOffsetZ() + lv2.getOffsetZ()}, {arg.getOffsetX() + lv.getOffsetX(), arg.getOffsetZ() + lv.getOffsetZ()}, {arg.getOffsetX() + lv2.getOffsetX(), arg.getOffsetZ() + lv2.getOffsetZ()}, {lv3.getOffsetX(), lv3.getOffsetZ()}, {arg.getOffsetX(), arg.getOffsetZ()}};
    }

    public static boolean method_27932(double d) {
        return !Double.isInfinite(d) && d < 1.0;
    }

    public static boolean method_27933(World arg, LivingEntity arg2, Box arg3) {
        return arg.getBlockCollisions(arg2, arg3).allMatch(VoxelShape::isEmpty);
    }
}

