/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface ShapeContext {
    public static ShapeContext absent() {
        return EntityShapeContext.ABSENT;
    }

    public static ShapeContext of(Entity arg) {
        return new EntityShapeContext(arg);
    }

    public boolean isDescending();

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

    public boolean isHolding(Item var1);

    public boolean method_27866(FluidState var1, FlowableFluid var2);
}

