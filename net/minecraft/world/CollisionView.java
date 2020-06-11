/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.border.WorldBorder;

public interface CollisionView
extends BlockView {
    public WorldBorder getWorldBorder();

    @Nullable
    public BlockView getExistingChunk(int var1, int var2);

    default public boolean intersectsEntities(@Nullable Entity arg, VoxelShape arg2) {
        return true;
    }

    default public boolean canPlace(BlockState arg, BlockPos arg2, ShapeContext arg3) {
        VoxelShape lv = arg.getCollisionShape(this, arg2, arg3);
        return lv.isEmpty() || this.intersectsEntities(null, lv.offset(arg2.getX(), arg2.getY(), arg2.getZ()));
    }

    default public boolean intersectsEntities(Entity arg) {
        return this.intersectsEntities(arg, VoxelShapes.cuboid(arg.getBoundingBox()));
    }

    default public boolean doesNotCollide(Box arg2) {
        return this.doesNotCollide(null, arg2, arg -> true);
    }

    default public boolean doesNotCollide(Entity arg2) {
        return this.doesNotCollide(arg2, arg2.getBoundingBox(), arg -> true);
    }

    default public boolean doesNotCollide(Entity arg2, Box arg22) {
        return this.doesNotCollide(arg2, arg22, arg -> true);
    }

    default public boolean doesNotCollide(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return this.getCollisions(arg, arg2, predicate).allMatch(VoxelShape::isEmpty);
    }

    public Stream<VoxelShape> getEntityCollisions(@Nullable Entity var1, Box var2, Predicate<Entity> var3);

    default public Stream<VoxelShape> getCollisions(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return Stream.concat(this.getBlockCollisions(arg, arg2), this.getEntityCollisions(arg, arg2, predicate));
    }

    default public Stream<VoxelShape> getBlockCollisions(@Nullable Entity arg, Box arg2) {
        return StreamSupport.stream(new BlockCollisionSpliterator(this, arg, arg2), false);
    }
}

