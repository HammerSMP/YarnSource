/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public enum CubeFace {
    DOWN(new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH)),
    UP(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH)),
    NORTH(new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH)),
    SOUTH(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH)),
    WEST(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH)),
    EAST(new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH));

    private static final CubeFace[] DIRECTION_LOOKUP;
    private final Corner[] corners;

    public static CubeFace getFace(Direction arg) {
        return DIRECTION_LOOKUP[arg.getId()];
    }

    private CubeFace(Corner ... args) {
        this.corners = args;
    }

    public Corner getCorner(int i) {
        return this.corners[i];
    }

    static {
        DIRECTION_LOOKUP = Util.make(new CubeFace[6], args -> {
            args[DirectionIds.DOWN] = DOWN;
            args[DirectionIds.UP] = UP;
            args[DirectionIds.NORTH] = NORTH;
            args[DirectionIds.SOUTH] = SOUTH;
            args[DirectionIds.WEST] = WEST;
            args[DirectionIds.EAST] = EAST;
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class Corner {
        public final int xSide;
        public final int ySide;
        public final int zSide;

        private Corner(int i, int j, int k) {
            this.xSide = i;
            this.ySide = j;
            this.zSide = k;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DirectionIds {
        public static final int SOUTH = Direction.SOUTH.getId();
        public static final int UP = Direction.UP.getId();
        public static final int EAST = Direction.EAST.getId();
        public static final int NORTH = Direction.NORTH.getId();
        public static final int DOWN = Direction.DOWN.getId();
        public static final int WEST = Direction.WEST.getId();
    }
}

