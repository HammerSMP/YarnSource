/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

public enum BlockRotation {
    NONE(DirectionTransformation.IDENTITY),
    CLOCKWISE_90(DirectionTransformation.ROT_90_Y_NEG),
    CLOCKWISE_180(DirectionTransformation.ROT_180_FACE_XZ),
    COUNTERCLOCKWISE_90(DirectionTransformation.ROT_90_Y_POS);

    private final DirectionTransformation directionTransformation;

    private BlockRotation(DirectionTransformation directionTransformation) {
        this.directionTransformation = directionTransformation;
    }

    public BlockRotation rotate(BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_90: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_180: {
                        return NONE;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_90;
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return NONE;
                    }
                    case CLOCKWISE_180: {
                        return CLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_180: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return NONE;
                    }
                }
            }
        }
        return this;
    }

    public DirectionTransformation getDirectionTransformation() {
        return this.directionTransformation;
    }

    public Direction rotate(Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return direction;
        }
        switch (this) {
            case CLOCKWISE_180: {
                return direction.getOpposite();
            }
            case COUNTERCLOCKWISE_90: {
                return direction.rotateYCounterclockwise();
            }
            case CLOCKWISE_90: {
                return direction.rotateYClockwise();
            }
        }
        return direction;
    }

    public int rotate(int rotation, int fullTurn) {
        switch (this) {
            case CLOCKWISE_180: {
                return (rotation + fullTurn / 2) % fullTurn;
            }
            case COUNTERCLOCKWISE_90: {
                return (rotation + fullTurn * 3 / 4) % fullTurn;
            }
            case CLOCKWISE_90: {
                return (rotation + fullTurn / 4) % fullTurn;
            }
        }
        return rotation;
    }

    public static BlockRotation random(Random random) {
        return Util.getRandom(BlockRotation.values(), random);
    }

    public static List<BlockRotation> randomRotationOrder(Random random) {
        ArrayList list = Lists.newArrayList((Object[])BlockRotation.values());
        Collections.shuffle(list, random);
        return list;
    }
}

