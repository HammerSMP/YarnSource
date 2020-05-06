/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Direction;

public enum AxisCycleDirection {
    NONE{

        @Override
        public int choose(int i, int j, int k, Direction.Axis arg) {
            return arg.choose(i, j, k);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis arg) {
            return arg;
        }

        @Override
        public AxisCycleDirection opposite() {
            return this;
        }
    }
    ,
    FORWARD{

        @Override
        public int choose(int i, int j, int k, Direction.Axis arg) {
            return arg.choose(k, i, j);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis arg) {
            return AXES[Math.floorMod(arg.ordinal() + 1, 3)];
        }

        @Override
        public AxisCycleDirection opposite() {
            return BACKWARD;
        }
    }
    ,
    BACKWARD{

        @Override
        public int choose(int i, int j, int k, Direction.Axis arg) {
            return arg.choose(j, k, i);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis arg) {
            return AXES[Math.floorMod(arg.ordinal() - 1, 3)];
        }

        @Override
        public AxisCycleDirection opposite() {
            return FORWARD;
        }
    };

    public static final Direction.Axis[] AXES;
    public static final AxisCycleDirection[] VALUES;

    public abstract int choose(int var1, int var2, int var3, Direction.Axis var4);

    public abstract Direction.Axis cycle(Direction.Axis var1);

    public abstract AxisCycleDirection opposite();

    public static AxisCycleDirection between(Direction.Axis arg, Direction.Axis arg2) {
        return VALUES[Math.floorMod(arg2.ordinal() - arg.ordinal(), 3)];
    }

    static {
        AXES = Direction.Axis.values();
        VALUES = AxisCycleDirection.values();
    }
}

