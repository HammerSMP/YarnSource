/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft;

import java.util.Objects;
import java.util.Spliterators;
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

public class class_5329
extends Spliterators.AbstractSpliterator<VoxelShape> {
    @Nullable
    private final Entity field_25168;
    private final Box field_25169;
    private final ShapeContext field_25170;
    private final CuboidBlockIterator field_25171;
    private final BlockPos.Mutable field_25172;
    private final VoxelShape field_25173;
    private final CollisionView field_25174;
    private boolean field_25175;

    public class_5329(CollisionView arg, @Nullable Entity arg2, Box arg3) {
        super(Long.MAX_VALUE, 1280);
        this.field_25170 = arg2 == null ? ShapeContext.absent() : ShapeContext.of(arg2);
        this.field_25172 = new BlockPos.Mutable();
        this.field_25173 = VoxelShapes.cuboid(arg3);
        this.field_25174 = arg;
        this.field_25175 = arg2 != null;
        this.field_25168 = arg2;
        this.field_25169 = arg3;
        int i = MathHelper.floor(arg3.minX - 1.0E-7) - 1;
        int j = MathHelper.floor(arg3.maxX + 1.0E-7) + 1;
        int k = MathHelper.floor(arg3.minY - 1.0E-7) - 1;
        int l = MathHelper.floor(arg3.maxY + 1.0E-7) + 1;
        int m = MathHelper.floor(arg3.minZ - 1.0E-7) - 1;
        int n = MathHelper.floor(arg3.maxZ + 1.0E-7) + 1;
        this.field_25171 = new CuboidBlockIterator(i, k, m, j, l, n);
    }

    @Override
    public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
        return this.field_25175 && this.method_29286(consumer) || this.method_29285(consumer);
    }

    boolean method_29285(Consumer<? super VoxelShape> consumer) {
        while (this.field_25171.step()) {
            BlockView lv;
            int i = this.field_25171.getX();
            int j = this.field_25171.getY();
            int k = this.field_25171.getZ();
            int l = this.field_25171.getEdgeCoordinatesCount();
            if (l == 3 || (lv = this.method_29283(i, k)) == null) continue;
            this.field_25172.set(i, j, k);
            BlockState lv2 = lv.getBlockState(this.field_25172);
            if (l == 1 && !lv2.exceedsCube() || l == 2 && !lv2.isOf(Blocks.MOVING_PISTON)) continue;
            VoxelShape lv3 = lv2.getCollisionShape(this.field_25174, this.field_25172, this.field_25170);
            if (lv3 == VoxelShapes.fullCube()) {
                if (!this.field_25169.intersects(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) continue;
                consumer.accept(lv3.offset(i, j, k));
                return true;
            }
            VoxelShape lv4 = lv3.offset(i, j, k);
            if (!VoxelShapes.matchesAnywhere(lv4, this.field_25173, BooleanBiFunction.AND)) continue;
            consumer.accept(lv4);
            return true;
        }
        return false;
    }

    @Nullable
    private BlockView method_29283(int i, int j) {
        int k = i >> 4;
        int l = j >> 4;
        return this.field_25174.getExistingChunk(k, l);
    }

    boolean method_29286(Consumer<? super VoxelShape> consumer) {
        boolean bl2;
        Objects.requireNonNull(this.field_25168);
        this.field_25175 = false;
        WorldBorder lv = this.field_25174.getWorldBorder();
        boolean bl = class_5329.method_29284(lv, this.field_25168.getBoundingBox().contract(1.0E-7));
        boolean bl3 = bl2 = bl && !class_5329.method_29284(lv, this.field_25168.getBoundingBox().expand(1.0E-7));
        if (bl2) {
            consumer.accept(lv.asVoxelShape());
            return true;
        }
        return false;
    }

    public static boolean method_29284(WorldBorder arg, Box arg2) {
        double d = MathHelper.floor(arg.getBoundWest());
        double e = MathHelper.floor(arg.getBoundNorth());
        double f = MathHelper.ceil(arg.getBoundEast());
        double g = MathHelper.ceil(arg.getBoundSouth());
        return arg2.minX > d && arg2.minX < f && arg2.minZ > e && arg2.minZ < g && arg2.maxX > d && arg2.maxX < f && arg2.maxZ > e && arg2.maxZ < g;
    }
}

