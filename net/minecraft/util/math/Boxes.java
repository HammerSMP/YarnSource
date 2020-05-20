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
                return new Box(arg.minX + f, arg.minY, arg.minZ, arg.minX + g, arg.maxY, arg.maxZ);
            }
            case EAST: {
                return new Box(arg.maxX + f, arg.minY, arg.minZ, arg.maxX + g, arg.maxY, arg.maxZ);
            }
            case DOWN: {
                return new Box(arg.minX, arg.minY + f, arg.minZ, arg.maxX, arg.minY + g, arg.maxZ);
            }
            default: {
                return new Box(arg.minX, arg.maxY + f, arg.minZ, arg.maxX, arg.maxY + g, arg.maxZ);
            }
            case NORTH: {
                return new Box(arg.minX, arg.minY, arg.minZ + f, arg.maxX, arg.maxY, arg.minZ + g);
            }
            case SOUTH: 
        }
        return new Box(arg.minX, arg.minY, arg.maxZ + f, arg.maxX, arg.maxY, arg.maxZ + g);
    }
}

