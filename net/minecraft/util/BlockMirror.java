/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

public enum BlockMirror {
    NONE(DirectionTransformation.IDENTITY),
    LEFT_RIGHT(DirectionTransformation.INVERT_Z),
    FRONT_BACK(DirectionTransformation.INVERT_X);

    private final DirectionTransformation directionTransformation;

    private BlockMirror(DirectionTransformation arg) {
        this.directionTransformation = arg;
    }

    public int mirror(int i, int j) {
        int k = j / 2;
        int l = i > k ? i - j : i;
        switch (this) {
            case FRONT_BACK: {
                return (j - l) % j;
            }
            case LEFT_RIGHT: {
                return (k - l + j) % j;
            }
        }
        return i;
    }

    public BlockRotation getRotation(Direction arg) {
        Direction.Axis lv = arg.getAxis();
        return this == LEFT_RIGHT && lv == Direction.Axis.Z || this == FRONT_BACK && lv == Direction.Axis.X ? BlockRotation.CLOCKWISE_180 : BlockRotation.NONE;
    }

    public Direction apply(Direction arg) {
        if (this == FRONT_BACK && arg.getAxis() == Direction.Axis.X) {
            return arg.getOpposite();
        }
        if (this == LEFT_RIGHT && arg.getAxis() == Direction.Axis.Z) {
            return arg.getOpposite();
        }
        return arg;
    }

    public DirectionTransformation getDirectionTransformation() {
        return this.directionTransformation;
    }
}

