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

    private BlockMirror(DirectionTransformation directionTransformation) {
        this.directionTransformation = directionTransformation;
    }

    public int mirror(int rotation, int fullTurn) {
        int k = fullTurn / 2;
        int l = rotation > k ? rotation - fullTurn : rotation;
        switch (this) {
            case FRONT_BACK: {
                return (fullTurn - l) % fullTurn;
            }
            case LEFT_RIGHT: {
                return (k - l + fullTurn) % fullTurn;
            }
        }
        return rotation;
    }

    public BlockRotation getRotation(Direction direction) {
        Direction.Axis lv = direction.getAxis();
        return this == LEFT_RIGHT && lv == Direction.Axis.Z || this == FRONT_BACK && lv == Direction.Axis.X ? BlockRotation.CLOCKWISE_180 : BlockRotation.NONE;
    }

    public Direction apply(Direction direction) {
        if (this == FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
            return direction.getOpposite();
        }
        if (this == LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z) {
            return direction.getOpposite();
        }
        return direction;
    }

    public DirectionTransformation getDirectionTransformation() {
        return this.directionTransformation;
    }
}

