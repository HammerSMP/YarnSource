/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft;

import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.EntityView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.WorldView;

public interface class_5423
extends EntityView,
WorldView,
ModifiableTestableWorld {
    @Override
    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return EntityView.super.getEntityCollisions(arg, arg2, predicate);
    }

    @Override
    default public boolean intersectsEntities(@Nullable Entity arg, VoxelShape arg2) {
        return EntityView.super.intersectsEntities(arg, arg2);
    }

    @Override
    default public BlockPos getTopPosition(Heightmap.Type arg, BlockPos arg2) {
        return WorldView.super.getTopPosition(arg, arg2);
    }
}
