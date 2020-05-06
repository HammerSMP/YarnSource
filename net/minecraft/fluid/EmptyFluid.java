/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class EmptyFluid
extends Fluid {
    @Override
    public Item getBucketItem() {
        return Items.AIR;
    }

    @Override
    public boolean canBeReplacedWith(FluidState arg, BlockView arg2, BlockPos arg3, Fluid arg4, Direction arg5) {
        return true;
    }

    @Override
    public Vec3d getVelocity(BlockView arg, BlockPos arg2, FluidState arg3) {
        return Vec3d.ZERO;
    }

    @Override
    public int getTickRate(WorldView arg) {
        return 0;
    }

    @Override
    protected boolean isEmpty() {
        return true;
    }

    @Override
    protected float getBlastResistance() {
        return 0.0f;
    }

    @Override
    public float getHeight(FluidState arg, BlockView arg2, BlockPos arg3) {
        return 0.0f;
    }

    @Override
    public float getHeight(FluidState arg) {
        return 0.0f;
    }

    @Override
    protected BlockState toBlockState(FluidState arg) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isStill(FluidState arg) {
        return false;
    }

    @Override
    public int getLevel(FluidState arg) {
        return 0;
    }

    @Override
    public VoxelShape getShape(FluidState arg, BlockView arg2, BlockPos arg3) {
        return VoxelShapes.empty();
    }
}

