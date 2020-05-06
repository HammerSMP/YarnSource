/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.util.math;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public class GravityField {
    private final List<Point> points = Lists.newArrayList();

    public void addPoint(BlockPos arg, double d) {
        if (d != 0.0) {
            this.points.add(new Point(arg, d));
        }
    }

    public double calculate(BlockPos arg, double d) {
        if (d == 0.0) {
            return 0.0;
        }
        double e = 0.0;
        for (Point lv : this.points) {
            e += lv.getGravityFactor(arg);
        }
        return e * d;
    }

    static class Point {
        private final BlockPos pos;
        private final double mass;

        public Point(BlockPos arg, double d) {
            this.pos = arg;
            this.mass = d;
        }

        public double getGravityFactor(BlockPos arg) {
            double d = this.pos.getSquaredDistance(arg);
            if (d == 0.0) {
                return Double.POSITIVE_INFINITY;
            }
            return this.mass / Math.sqrt(d);
        }
    }
}

