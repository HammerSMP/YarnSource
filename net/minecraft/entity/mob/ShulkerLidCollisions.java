/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;

public class ShulkerLidCollisions {
    public static Box getLidCollisionBox(BlockPos arg, Direction arg2) {
        return VoxelShapes.fullCube().getBoundingBox().stretch(0.5f * (float)arg2.getOffsetX(), 0.5f * (float)arg2.getOffsetY(), 0.5f * (float)arg2.getOffsetZ()).shrink(arg2.getOffsetX(), arg2.getOffsetY(), arg2.getOffsetZ()).offset(arg.offset(arg2));
    }
}

