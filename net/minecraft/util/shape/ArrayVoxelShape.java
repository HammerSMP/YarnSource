/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

public final class ArrayVoxelShape
extends VoxelShape {
    private final DoubleList xPoints;
    private final DoubleList yPoints;
    private final DoubleList zPoints;

    protected ArrayVoxelShape(VoxelSet arg, double[] ds, double[] es, double[] fs) {
        this(arg, (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf(ds, arg.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf(es, arg.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf(fs, arg.getZSize() + 1)));
    }

    ArrayVoxelShape(VoxelSet arg, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
        super(arg);
        int i = arg.getXSize() + 1;
        int j = arg.getYSize() + 1;
        int k = arg.getZSize() + 1;
        if (i != doubleList.size() || j != doubleList2.size() || k != doubleList3.size()) {
            throw Util.throwOrPause(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
        }
        this.xPoints = doubleList;
        this.yPoints = doubleList2;
        this.zPoints = doubleList3;
    }

    @Override
    protected DoubleList getPointPositions(Direction.Axis arg) {
        switch (arg) {
            case X: {
                return this.xPoints;
            }
            case Y: {
                return this.yPoints;
            }
            case Z: {
                return this.zPoints;
            }
        }
        throw new IllegalArgumentException();
    }
}

