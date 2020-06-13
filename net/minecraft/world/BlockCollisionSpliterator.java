/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import java.util.Objects;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
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
import net.minecraft.world.CollisionView;
import net.minecraft.world.border.WorldBorder;

public class BlockCollisionSpliterator
extends Spliterators.AbstractSpliterator<VoxelShape> {
    @Nullable
    private final Entity entity;
    private final Box box;
    private final ShapeContext context;
    private final CuboidBlockIterator blockIterator;
    private final BlockPos.Mutable pos;
    private final VoxelShape boxShape;
    private final CollisionView world;
    private boolean checkEntity;
    private final BiPredicate<BlockState, BlockPos> field_25669;

    public BlockCollisionSpliterator(CollisionView arg3, @Nullable Entity arg22, Box arg32) {
        this(arg3, arg22, arg32, (arg, arg2) -> true);
    }

    public BlockCollisionSpliterator(CollisionView arg, @Nullable Entity arg2, Box arg3, BiPredicate<BlockState, BlockPos> biPredicate) {
        super(Long.MAX_VALUE, 1280);
        this.context = arg2 == null ? ShapeContext.absent() : ShapeContext.of(arg2);
        this.pos = new BlockPos.Mutable();
        this.boxShape = VoxelShapes.cuboid(arg3);
        this.world = arg;
        this.checkEntity = arg2 != null;
        this.entity = arg2;
        this.box = arg3;
        this.field_25669 = biPredicate;
        int i = MathHelper.floor(arg3.minX - 1.0E-7) - 1;
        int j = MathHelper.floor(arg3.maxX + 1.0E-7) + 1;
        int k = MathHelper.floor(arg3.minY - 1.0E-7) - 1;
        int l = MathHelper.floor(arg3.maxY + 1.0E-7) + 1;
        int m = MathHelper.floor(arg3.minZ - 1.0E-7) - 1;
        int n = MathHelper.floor(arg3.maxZ + 1.0E-7) + 1;
        this.blockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
    }

    @Override
    public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
        return this.checkEntity && this.offerEntityShape(consumer) || this.offerEntitylessShape(consumer);
    }

    boolean offerEntitylessShape(Consumer<? super VoxelShape> consumer) {
        while (this.blockIterator.step()) {
            BlockView lv;
            int i = this.blockIterator.getX();
            int j = this.blockIterator.getY();
            int k = this.blockIterator.getZ();
            int l = this.blockIterator.getEdgeCoordinatesCount();
            if (l == 3 || (lv = this.getChunk(i, k)) == null) continue;
            this.pos.set(i, j, k);
            BlockState lv2 = lv.getBlockState(this.pos);
            if (!this.field_25669.test(lv2, this.pos) || l == 1 && !lv2.exceedsCube() || l == 2 && !lv2.isOf(Blocks.MOVING_PISTON)) continue;
            VoxelShape lv3 = lv2.getCollisionShape(this.world, this.pos, this.context);
            if (lv3 == VoxelShapes.fullCube()) {
                if (!this.box.intersects(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) continue;
                consumer.accept(lv3.offset(i, j, k));
                return true;
            }
            VoxelShape lv4 = lv3.offset(i, j, k);
            if (!VoxelShapes.matchesAnywhere(lv4, this.boxShape, BooleanBiFunction.AND)) continue;
            consumer.accept(lv4);
            return true;
        }
        return false;
    }

    @Nullable
    private BlockView getChunk(int i, int j) {
        int k = i >> 4;
        int l = j >> 4;
        return this.world.getExistingChunk(k, l);
    }

    boolean offerEntityShape(Consumer<? super VoxelShape> consumer) {
        boolean bl2;
        Objects.requireNonNull(this.entity);
        this.checkEntity = false;
        WorldBorder lv = this.world.getWorldBorder();
        boolean bl = BlockCollisionSpliterator.isInWorldBorder(lv, this.entity.getBoundingBox().contract(1.0E-7));
        boolean bl3 = bl2 = bl && !BlockCollisionSpliterator.isInWorldBorder(lv, this.entity.getBoundingBox().expand(1.0E-7));
        if (bl2) {
            consumer.accept(lv.asVoxelShape());
            return true;
        }
        return false;
    }

    public static boolean isInWorldBorder(WorldBorder arg, Box arg2) {
        double d = MathHelper.floor(arg.getBoundWest());
        double e = MathHelper.floor(arg.getBoundNorth());
        double f = MathHelper.ceil(arg.getBoundEast());
        double g = MathHelper.ceil(arg.getBoundSouth());
        return arg2.minX > d && arg2.minX < f && arg2.minZ > e && arg2.minZ < g && arg2.maxX > d && arg2.maxX < f && arg2.maxZ > e && arg2.maxZ < g;
    }
}

