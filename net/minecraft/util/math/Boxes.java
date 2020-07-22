/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class Boxes {
    public static Box stretch(Box box, Direction direction, double length) {
        double e = length * (double)direction.getDirection().offset();
        double f = Math.min(e, 0.0);
        double g = Math.max(e, 0.0);
        switch (direction) {
            case WEST: {
                return new Box(box.minX + f, box.minY, box.minZ, box.minX + g, box.maxY, box.maxZ);
            }
            case EAST: {
                return new Box(box.maxX + f, box.minY, box.minZ, box.maxX + g, box.maxY, box.maxZ);
            }
            case DOWN: {
                return new Box(box.minX, box.minY + f, box.minZ, box.maxX, box.minY + g, box.maxZ);
            }
            default: {
                return new Box(box.minX, box.maxY + f, box.minZ, box.maxX, box.maxY + g, box.maxZ);
            }
            case NORTH: {
                return new Box(box.minX, box.minY, box.minZ + f, box.maxX, box.maxY, box.minZ + g);
            }
            case SOUTH: 
        }
        return new Box(box.minX, box.minY, box.maxZ + f, box.maxX, box.maxY, box.maxZ + g);
    }
}

