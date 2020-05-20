/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
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

    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return Stream.empty();
    }

    default public Stream<VoxelShape> getCollisions(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return Streams.concat((Stream[])new Stream[]{this.getBlockCollisions(arg, arg2), this.getEntityCollisions(arg, arg2, predicate)});
    }

    default public Stream<VoxelShape> getBlockCollisions(final @Nullable Entity arg, final Box arg2) {
        int i = MathHelper.floor(arg2.minX - 1.0E-7) - 1;
        int j = MathHelper.floor(arg2.maxX + 1.0E-7) + 1;
        int k = MathHelper.floor(arg2.minY - 1.0E-7) - 1;
        int l = MathHelper.floor(arg2.maxY + 1.0E-7) + 1;
        int m = MathHelper.floor(arg2.minZ - 1.0E-7) - 1;
        int n = MathHelper.floor(arg2.maxZ + 1.0E-7) + 1;
        final ShapeContext lv = arg == null ? ShapeContext.absent() : ShapeContext.of(arg);
        final CuboidBlockIterator lv2 = new CuboidBlockIterator(i, k, m, j, l, n);
        final BlockPos.Mutable lv3 = new BlockPos.Mutable();
        final VoxelShape lv4 = VoxelShapes.cuboid(arg2);
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280){
            boolean field_19296;
            {
                super(l, i);
                this.field_19296 = arg == null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
                if (!this.field_19296) {
                    boolean bl2;
                    this.field_19296 = true;
                    WorldBorder lv6 = CollisionView.this.getWorldBorder();
                    boolean bl = CollisionView.method_27087(lv6, arg.getBoundingBox().contract(1.0E-7));
                    boolean bl3 = bl2 = bl && !CollisionView.method_27087(lv6, arg.getBoundingBox().expand(1.0E-7));
                    if (bl2) {
                        consumer.accept(lv6.asVoxelShape());
                        return true;
                    }
                }
                while (lv2.step()) {
                    int n;
                    int m;
                    BlockView lv22;
                    int i = lv2.getX();
                    int j = lv2.getY();
                    int k = lv2.getZ();
                    int l = lv2.getEdgeCoordinatesCount();
                    if (l == 3 || (lv22 = CollisionView.this.getExistingChunk(m = i >> 4, n = k >> 4)) == null) continue;
                    lv3.set(i, j, k);
                    BlockState lv32 = lv22.getBlockState(lv3);
                    if (l == 1 && !lv32.exceedsCube() || l == 2 && !lv32.isOf(Blocks.MOVING_PISTON)) continue;
                    VoxelShape lv42 = lv32.getCollisionShape(CollisionView.this, lv3, lv);
                    if (lv42 == VoxelShapes.fullCube()) {
                        if (!arg2.intersects(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) continue;
                        consumer.accept(lv42.offset(i, j, k));
                        return true;
                    }
                    VoxelShape lv5 = lv42.offset(i, j, k);
                    if (!VoxelShapes.matchesAnywhere(lv5, lv4, BooleanBiFunction.AND)) continue;
                    consumer.accept(lv5);
                    return true;
                }
                return false;
            }
        }, false);
    }

    public static boolean method_27087(WorldBorder arg, Box arg2) {
        double d = MathHelper.floor(arg.getBoundWest());
        double e = MathHelper.floor(arg.getBoundNorth());
        double f = MathHelper.ceil(arg.getBoundEast());
        double g = MathHelper.ceil(arg.getBoundSouth());
        return arg2.minX > d && arg2.minX < f && arg2.minZ > e && arg2.minZ < g && arg2.maxX > d && arg2.maxX < f && arg2.maxZ > e && arg2.maxZ < g;
    }
}

