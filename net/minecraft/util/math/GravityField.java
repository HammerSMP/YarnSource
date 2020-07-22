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

    public void addPoint(BlockPos pos, double mass) {
        if (mass != 0.0) {
            this.points.add(new Point(pos, mass));
        }
    }

    public double calculate(BlockPos pos, double mass) {
        if (mass == 0.0) {
            return 0.0;
        }
        double e = 0.0;
        for (Point lv : this.points) {
            e += lv.getGravityFactor(pos);
        }
        return e * mass;
    }

    static class Point {
        private final BlockPos pos;
        private final double mass;

        public Point(BlockPos pos, double mass) {
            this.pos = pos;
            this.mass = mass;
        }

        public double getGravityFactor(BlockPos pos) {
            double d = this.pos.getSquaredDistance(pos);
            if (d == 0.0) {
                return Double.POSITIVE_INFINITY;
            }
            return this.mass / Math.sqrt(d);
        }
    }
}

