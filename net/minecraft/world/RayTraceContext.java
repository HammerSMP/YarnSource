/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class RayTraceContext {
    private final Vec3d start;
    private final Vec3d end;
    private final ShapeType shapeType;
    private final FluidHandling fluid;
    private final ShapeContext entityPosition;

    public RayTraceContext(Vec3d arg, Vec3d arg2, ShapeType arg3, FluidHandling arg4, Entity arg5) {
        this.start = arg;
        this.end = arg2;
        this.shapeType = arg3;
        this.fluid = arg4;
        this.entityPosition = ShapeContext.of(arg5);
    }

    public Vec3d getEnd() {
        return this.end;
    }

    public Vec3d getStart() {
        return this.start;
    }

    public VoxelShape getBlockShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return this.shapeType.get(arg, arg2, arg3, this.entityPosition);
    }

    public VoxelShape getFluidShape(FluidState arg, BlockView arg2, BlockPos arg3) {
        return this.fluid.handled(arg) ? arg.getShape(arg2, arg3) : VoxelShapes.empty();
    }

    public static enum FluidHandling {
        NONE(arg -> false),
        SOURCE_ONLY(FluidState::isStill),
        ANY(arg -> !arg.isEmpty());

        private final Predicate<FluidState> predicate;

        private FluidHandling(Predicate<FluidState> predicate) {
            this.predicate = predicate;
        }

        public boolean handled(FluidState arg) {
            return this.predicate.test(arg);
        }
    }

    public static interface ShapeProvider {
        public VoxelShape get(BlockState var1, BlockView var2, BlockPos var3, ShapeContext var4);
    }

    public static enum ShapeType implements ShapeProvider
    {
        COLLIDER(AbstractBlock.AbstractBlockState::getCollisionShape),
        OUTLINE(AbstractBlock.AbstractBlockState::getOutlineShape),
        VISUAL(AbstractBlock.AbstractBlockState::getVisualShape);

        private final ShapeProvider provider;

        private ShapeType(ShapeProvider arg) {
            this.provider = arg;
        }

        @Override
        public VoxelShape get(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
            return this.provider.get(arg, arg2, arg3, arg4);
        }
    }
}

