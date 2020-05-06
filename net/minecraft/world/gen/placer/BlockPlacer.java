/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.placer;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.placer.BlockPlacerType;

public abstract class BlockPlacer
implements DynamicSerializable {
    protected final BlockPlacerType<?> type;

    protected BlockPlacer(BlockPlacerType<?> arg) {
        this.type = arg;
    }

    public abstract void method_23403(IWorld var1, BlockPos var2, BlockState var3, Random var4);
}

