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

    public RayTraceContext(Vec3d start, Vec3d end, ShapeType shapeType, FluidHandling fluidHandling, Entity entity) {
        this.start = start;
        this.end = end;
        this.shapeType = shapeType;
        this.fluid = fluidHandling;
        this.entityPosition = ShapeContext.of(entity);
    }

    public Vec3d getEnd() {
        return this.end;
    }

    public Vec3d getStart() {
        return this.start;
    }

    public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
        return this.shapeType.get(state, world, pos, this.entityPosition);
    }

    public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos) {
        return this.fluid.handled(state) ? state.getShape(world, pos) : VoxelShapes.empty();
    }

    public static enum FluidHandling {
        NONE(arg -> false),
        SOURCE_ONLY(FluidState::isStill),
        ANY(arg -> !arg.isEmpty());

        private final Predicate<FluidState> predicate;

        private FluidHandling(Predicate<FluidState> predicate) {
            this.predicate = predicate;
        }

        public boolean handled(FluidState state) {
            return this.predicate.test(state);
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

        private ShapeType(ShapeProvider provider) {
            this.provider = provider;
        }

        @Override
        public VoxelShape get(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
            return this.provider.get(arg, arg2, arg3, arg4);
        }
    }
}

