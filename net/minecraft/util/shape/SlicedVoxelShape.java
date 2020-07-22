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

    public SlicedVoxelShape(VoxelShape shape, Direction.Axis axis, int sliceWidth) {
        super(SlicedVoxelShape.createVoxelSet(shape.voxels, axis, sliceWidth));
        this.shape = shape;
        this.axis = axis;
    }

    private static VoxelSet createVoxelSet(VoxelSet voxelSet, Direction.Axis arg2, int sliceWidth) {
        return new CroppedVoxelSet(voxelSet, arg2.choose(sliceWidth, 0, 0), arg2.choose(0, sliceWidth, 0), arg2.choose(0, 0, sliceWidth), arg2.choose(sliceWidth + 1, voxelSet.xSize, voxelSet.xSize), arg2.choose(voxelSet.ySize, sliceWidth + 1, voxelSet.ySize), arg2.choose(voxelSet.zSize, voxelSet.zSize, sliceWidth + 1));
    }

    @Override
    protected DoubleList getPointPositions(Direction.Axis axis) {
        if (axis == this.axis) {
            return POINTS;
        }
        return this.shape.getPointPositions(axis);
    }
}

