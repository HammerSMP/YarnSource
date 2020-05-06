/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.CroppedVoxelSet;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

public class SlicedVoxelShape
extends VoxelShape {
    private final VoxelShape shape;
    private final Direction.Axis axis;
    private static final DoubleList POINTS = new FractionalDoubleList(1);

    public SlicedVoxelShape(VoxelShape arg, Direction.Axis arg2, int i) {
        super(SlicedVoxelShape.createVoxelSet(arg.voxels, arg2, i));
        this.shape = arg;
        this.axis = arg2;
    }

    private static VoxelSet createVoxelSet(VoxelSet arg, Direction.Axis arg2, int i) {
        return new CroppedVoxelSet(arg, arg2.choose(i, 0, 0), arg2.choose(0, i, 0), arg2.choose(0, 0, i), arg2.choose(i + 1, arg.xSize, arg.xSize), arg2.choose(arg.ySize, i + 1, arg.ySize), arg2.choose(arg.zSize, arg.zSize, i + 1));
    }

    @Override
    protected DoubleList getPointPositions(Direction.Axis arg) {
        if (arg == this.axis) {
            return POINTS;
        }
        return this.shape.getPointPositions(arg);
    }
}

