/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class Boxes {
    public static Box stretch(Box arg, Direction arg2, double d) {
        double e = d * (double)arg2.getDirection().offset();
        double f = Math.min(e, 0.0);
        double g = Math.max(e, 0.0);
        switch (arg2) {
            case WEST: {
                return new Box(arg.x1 + f, arg.y1, arg.z1, arg.x1 + g, arg.y2, arg.z2);
            }
            case EAST: {
                return new Box(arg.x2 + f, arg.y1, arg.z1, arg.x2 + g, arg.y2, arg.z2);
            }
            case DOWN: {
                return new Box(arg.x1, arg.y1 + f, arg.z1, arg.x2, arg.y1 + g, arg.z2);
            }
            default: {
                return new Box(arg.x1, arg.y2 + f, arg.z1, arg.x2, arg.y2 + g, arg.z2);
            }
            case NORTH: {
                return new Box(arg.x1, arg.y1, arg.z1 + f, arg.x2, arg.y2, arg.z1 + g);
            }
            case SOUTH: 
        }
        return new Box(arg.x1, arg.y1, arg.z2 + f, arg.x2, arg.y2, arg.z2 + g);
    }
}

