/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import java.util.function.BiPredicate;
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

    default public boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
        return true;
    }

    default public boolean canPlace(BlockState state, BlockPos pos, ShapeContext context) {
        VoxelShape lv = state.getCollisionShape(this, pos, context);
        return lv.isEmpty() || this.intersectsEntities(null, lv.offset(pos.getX(), pos.getY(), pos.getZ()));
    }

    default public boolean intersectsEntities(Entity entity) {
        return this.intersectsEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
    }

    default public boolean doesNotCollide(Box box) {
        return this.doesNotCollide(null, box, arg -> true);
    }

    default public boolean doesNotCollide(Entity entity) {
        return this.doesNotCollide(entity, entity.getBoundingBox(), arg -> true);
    }

    default public boolean doesNotCollide(Entity entity, Box box) {
        return this.doesNotCollide(entity, box, arg -> true);
    }

    default public boolean doesNotCollide(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return this.getCollisions(arg, arg2, predicate).allMatch(VoxelShape::isEmpty);
    }

    public Stream<VoxelShape> getEntityCollisions(@Nullable Entity var1, Box var2, Predicate<Entity> var3);

    default public Stream<VoxelShape> getCollisions(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return Stream.concat(this.getBlockCollisions(arg, arg2), this.getEntityCollisions(arg, arg2, predicate));
    }

    default public Stream<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box) {
        return StreamSupport.stream(new BlockCollisionSpliterator(this, entity, box), false);
    }

    default public Stream<VoxelShape> method_30030(@Nullable Entity arg, Box arg2, BiPredicate<BlockState, BlockPos> biPredicate) {
        return StreamSupport.stream(new BlockCollisionSpliterator(this, arg, arg2, biPredicate), false);
    }
}

